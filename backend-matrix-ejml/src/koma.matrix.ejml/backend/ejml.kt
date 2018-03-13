/**
 * Some functions to make ejml matrices more usable directly in Kotlin.
 * If raw performance isnt needed, try using koma.matrix.Matrix facade instead,
 * which wraps this class and allows you to swap out matrix implementations.
 *
 * In particular, this enables you to write e.g.
 *
 *      var one = zeros(4,5)    // 4x5 zero matrix
 *      var two = one*one.T()
 *      println(two.repr())     // A nice representation of a square matrix
 *
 * In addition, a shorthand for matrix construction is
 *
 *      var foo = mat[1,2,3 end
 *                    4,5,6 end
 *                    7,8,9]
 */
@file:JvmName("RawEJML")

package koma.matrix.ejml.backend

import org.ejml.factory.DecompositionFactory
import org.ejml.ops.CommonOps
import org.ejml.simple.SimpleMatrix

// Algebraic Operators (Already has plus, minus, set(i,v:Float), set(i,j,v:Float), toString)
operator fun SimpleMatrix.times(other: SimpleMatrix) = this.mult(other)

operator fun SimpleMatrix.times(other: Int) = this.scale(other.toDouble())
operator fun SimpleMatrix.times(other: Double) = this.scale(other)
operator fun SimpleMatrix.rem(other: SimpleMatrix) = this.elementMult(other)
operator fun SimpleMatrix.unaryMinus() = this.scale(-1.0)
operator fun SimpleMatrix.div(other: Int) = this.divide(other.toDouble())
operator fun SimpleMatrix.div(other: Double) = this.divide(other)
val SimpleMatrix.T: SimpleMatrix
    get() = this.transpose()

// Index syntax (already has get)
operator fun SimpleMatrix.set(i: Int, v: Int) = this.set(i, v.toDouble())

operator fun SimpleMatrix.set(i: Int, j: Int, v: Int) = this.set(i, j, v.toDouble())

// Override operators TODO: Lose this when Kotlin auto annotates operator java classes
operator fun SimpleMatrix.get(i: Int) = if (this.numCols() == 1) this.get(i, 0) else this.get(0, i)

operator fun SimpleMatrix.get(i: Int, j: Int) = this.get(i, j)
operator fun SimpleMatrix.set(i: Int, j: Int, v: Double) = this.set(i, j, v)

// Scalar Arithmetic
operator fun SimpleMatrix.plus(other: Int) = this.plus(other.toDouble(), this)

operator fun SimpleMatrix.plus(other: Double) = this.plus(other, this)

// Decompositions (Already has eig, svd) [expm,schur not available]
fun SimpleMatrix.chol() = DecompositionFactory.chol(this.numCols(), true).also {
    it.decompose(this.matrix.copy())
}
fun SimpleMatrix.LU() = DecompositionFactory.lu(this.numRows(), this.numCols()).also {
    it.decompose(this.matrix.copy())
}
fun SimpleMatrix.QR() = DecompositionFactory.qr(this.numRows(), this.numCols()).also {
    it.decompose(this.matrix.copy())
}

// Basic Functions
fun SimpleMatrix.inv() = this.invert()
// Already existing: det, pinv, normf, elementSum

// Matrix Generators
fun zeros(rows: Int, cols: Int) = SimpleMatrix(rows, cols)

fun eye(size: Int) = SimpleMatrix.identity(size)
fun ones(rows: Int, cols: Int): SimpleMatrix {
    val out = SimpleMatrix(rows, cols)
    CommonOps.fill(out.matrix, 1.0)
    return out
}

fun rand(rows: Int, cols: Int, seed: Long): SimpleMatrix {
    if (seed != curSeed) {
        random.setSeed(seed)
        curSeed = seed
    }
    return SimpleMatrix.random(rows, cols, 0.0, 1.0, random)
}
fun rand(rows: Int, cols: Int) = rand(rows, cols, curSeed)
fun rand(len: Int) = rand(1, len, curSeed)

fun randn(rows: Int, cols: Int, seed: Long): SimpleMatrix {
    if (seed != curSeed) {
        random.setSeed(seed)
        curSeed = seed
    }
    val out = SimpleMatrix(rows, cols)
    for (i in 0..rows - 1)
        for (j in 0..cols - 1)
            out[i, j] = random.nextGaussian()
    return out
}
fun randn(len: Int) = randn(len, len, curSeed)
fun randn(rows: Int, cols: Int) = randn(rows, cols, curSeed)

fun SimpleMatrix.map(f: (Double) -> Double): SimpleMatrix {
    val out = SimpleMatrix(this.numRows(), this.numCols())
    for (row in 0..this.numRows() - 1)
        for (col in 0..this.numCols() - 1)
            out[row, col] = f(this[row, col])
    return out
}

object arr {
    operator fun get(vararg ts: Any): DoubleArray {
        // Todo: check for malformed inputs to avoid ambiguous out of bounds exceptions

        val len = ts.count()
        val out = DoubleArray(len)
        for (i in (0..len - 1)) {
            // Smart cast doesn't work on indexed collection
            val ele = ts[i]
            if (ele is Double)
                out[i] = ele
            else if (ele is Int)
                out[i] = ele.toDouble()
            else throw Exception("Invalid initial value to internal construction")
        }
        return out
    }
}

object mat {
    operator fun get(vararg ts: Any): SimpleMatrix {
        // Todo: check for malformed inputs to avoid ambiguous out of bounds exceptions

        val numStops = (ts.filter { it is Pair<*, *> }).count()
        val numRows = numStops + 1
        val numElements = ts.count() - numStops + 2 * numStops
        val numCols = numElements / numRows

        val out = SimpleMatrix(numRows, numCols)
        var curRow = 0
        var curCol = 0

        for (ele in ts) {
            when (ele) {
                is Double -> {
                    out[curRow, curCol] = ele
                    curCol += 1
                }
                is Int -> {
                    out[curRow, curCol] = ele.toDouble()
                    curCol += 1
                }
                is Pair<*, *> -> {
                    out[curRow, curCol] = ele.first as Double
                    out[curRow + 1, 0] = ele.second as Double
                    curRow += 1
                    curCol = 1
                }
                else -> throw Exception("Invalid initial value to internal construction")
            }
        }
        return out
    }
}

fun Double.end(other: Double) = Pair(this, other)
fun Double.end(other: Int) = Pair(this, other.toDouble())
fun Int.end(other: Double) = Pair(this.toDouble(), other)
fun Int.end(other: Int) = Pair(this.toDouble(), other.toDouble())
