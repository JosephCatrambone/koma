@file:koma.internal.JvmName("NDArray")
@file:koma.internal.JvmMultifileClass

/**
 * THIS FILE IS AUTOGENERATED, DO NOT MODIFY. EDIT THE FILES IN templates/
 * AND RUN ./gradlew :codegen INSTEAD!
 */

package koma.extensions

import koma.internal.default.utils.reduceArrayAxis
import koma.internal.default.utils.argMinByte
import koma.internal.default.utils.argMaxByte
import koma.internal.default.utils.wrapIndex
import koma.ndarray.NDArray
import koma.ndarray.NumericalNDArrayFactory
import koma.pow
import koma.matrix.Matrix
import koma.util.IndexIterator



@koma.internal.JvmName("fillByte")
inline fun  NDArray<Byte>.fill(f: (idx: IntArray) -> Byte) = apply {
    for ((nd, linear) in this.iterateIndices())
        this.setByte(linear, f(nd))
}

@koma.internal.JvmName("fillByteBoth")
inline fun  NDArray<Byte>.fillBoth(f: (nd: IntArray, linear: Int) -> Byte) = apply {
    for ((nd, linear) in this.iterateIndices())
        this.setByte(linear, f(nd, linear))
}

@koma.internal.JvmName("fillByteLinear")
inline fun  NDArray<Byte>.fillLinear(f: (idx: Int) -> Byte) = apply {
    for (idx in 0 until size)
        this.setByte(idx, f(idx))
}

@koma.internal.JvmName("createByte")
inline fun  NumericalNDArrayFactory<Byte>.create(vararg lengths: Int, filler: (idx: IntArray) -> Byte)
    = NDArray.byteFactory.zeros(*lengths).fill(filler)

/**
 * Returns a new NDArray with the given shape, populated with the data in this array.
 *
 * @param dims Desired dimensions of the output array.
 *
 * @returns A copy of the elements in this array, shaped to the given number of rows and columns,
 *          such that `this.toList() == this.reshape(*dims).toList()`
 *
 * @throws IllegalArgumentException when the product of all of the given `dims` does not equal [size]
 */
@koma.internal.JvmName("reshapeByte")
fun  NDArray<Byte>.reshape(vararg dims: Int): NDArray<Byte> {
    if (dims.reduce { a, b -> a * b } != size)
        throw IllegalArgumentException("$size items cannot be reshaped to ${dims.toList()}")
    var idx = 0
    return NDArray.byteFactory.zeros(*dims).fill { _ -> getByte(idx++) }
}


/**
 * Takes each element in a NDArray, passes them through f, and puts the output of f into an
 * output NDArray.
 *
 * @param f A function that takes in an element and returns an element
 *
 * @return the new NDArray after each element is mapped through f
 */
@koma.internal.JvmName("mapByte")
inline fun <reified R> NDArray<Byte>.map(crossinline f: (Byte) -> R)
    = NDArray.createLinear(*shape().toIntArray(), filler={ f(this.getByte(it)) } )

/**
 * Takes each element in a NDArray, passes them through f, and puts the output of f into an
 * output NDArray. Index given to f is a linear index, depending on the underlying storage
 * major dimension.
 *
 * @param f A function that takes in an element and returns an element. Function also takes
 *      in the linear index of the element's location.
 *
 * @return the new NDArray after each element is mapped through f
 */
@koma.internal.JvmName("mapIndexedByte")
inline fun  NDArray<Byte>.mapIndexed(f: (idx: Int, ele: Byte) -> Byte)
    = NDArray.byteFactory.zeros(*shape().toIntArray()).fillLinear { f(it, this.getByte(it)) }


/**
 * Takes each element in a NDArray and passes them through f.
 *
 * @param f A function that takes in an element
 *
 */
@koma.internal.JvmName("forEachByte")
inline fun  NDArray<Byte>.forEach(f: (ele: Byte) -> Unit) {
    // TODO: Change this back to iteration once there are non-boxing iterators
    for (idx in 0 until size)
        f(getByte(idx))
}
/**
 * Takes each element in a NDArray and passes them through f. Index given to f is a linear
 * index, depending on the underlying storage major dimension.
 *
 * @param f A function that takes in an element. Function also takes
 *      in the linear index of the element's location.
 *
 */
@koma.internal.JvmName("forEachIndexedByte")
inline fun  NDArray<Byte>.forEachIndexed(f: (idx: Int, ele: Byte) -> Unit) {
    // TODO: Change this back to iteration once there are non-boxing iterators
    for (idx in 0 until size)
        f(idx, getByte(idx))
}

/**
 * Takes each element in a NDArray, passes them through f, and puts the output of f into an
 * output NDArray. Index given to f is the full ND index of the element.
 *
 * @param f A function that takes in an element and returns an element. Function also takes
 *      in the ND index of the element's location.
 *
 * @return the new NDArray after each element is mapped through f
 */
@koma.internal.JvmName("mapIndexedNByte")
inline fun  NDArray<Byte>.mapIndexedN(f: (idx: IntArray, ele: Byte) -> Byte): NDArray<Byte>
    = NDArray.byteFactory.zeros(*shape().toIntArray()).fillBoth { nd, linear -> f(nd, getByte(linear)) }


/**
 * Takes each element in a NDArray and passes them through f. Index given to f is the full
 * ND index of the element.
 *
 * @param f A function that takes in an element. Function also takes
 *      in the ND index of the element's location.
 *
 */
@koma.internal.JvmName("forEachIndexedNByte")
inline fun  NDArray<Byte>.forEachIndexedN(f: (idx: IntArray, ele: Byte) -> Unit) {
    for ((nd, linear) in iterateIndices())
        f(nd, getByte(linear))
}

/**
 * Converts this NDArray into a one-dimensional ByteArray in row-major order.
 */
fun  NDArray<Byte>.toByteArray() = ByteArray(size) { getByte(it) }

fun <T> NDArray<T>.getSliceByte(vararg indices: Any): NDArray<Byte> {
    if (indices.size != shape().size)
        throw IllegalArgumentException("Specified ${indices.size} indices for an array with ${shape().size} dimensions")
    val indexArrays = mutableListOf<IntArray>()
    val outputShape = mutableListOf<Int>()
    val outputDims = mutableListOf<Int>()
    val inputIndex = kotlin.IntArray(indices.size)

    // Convert the inputs to arrays of integer indices.

    for (i in 0 until indices.size) {
        val index = indices[i]
        val size = shape()[i]
        if (index is Int) {
            inputIndex[i] = index
            indexArrays.add(kotlin.intArrayOf(wrapIndex(index, size)))
        }
        else if (index is Iterable<*>) {
            outputDims.add(i)
            indexArrays.add(index.map { wrapIndex(it as Int, size) }.toIntArray())
            outputShape.add(indexArrays.last().size)
        }
        else
            throw IllegalArgumentException("All indices must be Int or Iterable<Int>")
    }
    if (outputShape.size == 0)
        throw IllegalArgumentException("A slice must have at least one dimension")

    // Create the output array.

    val lengths = outputShape.toIntArray()
    val filler = { index: IntArray ->
        for (i in 0 until outputDims.size)
            inputIndex[outputDims[i]] = indexArrays[outputDims[i]][index[i]]
        getByte(*inputIndex)
    }
    return NDArray.byteFactory.zeros(*lengths).fill(filler)
}

fun <T> NDArray<T>.setSliceByte(vararg indices: Any, value: Byte) {
    if (indices.size != shape().size)
        throw IllegalArgumentException("Specified ${indices.size} indices for an array with ${shape().size} dimensions")
    val indexArrays = mutableListOf<IntArray>()

    // Convert the inputs to arrays of integer indices.

    for (i in 0 until indices.size) {
        val index = indices[i]
        val size = shape()[i]
        if (index is Int)
            indexArrays.add(kotlin.intArrayOf(wrapIndex(index, size)))
        else if (index is Iterable<*>)
            indexArrays.add(index.map { wrapIndex(it as Int, size) }.toIntArray())
        else
            throw IllegalArgumentException("All indices must be Int or Iterable<Int>")
    }

    // Set the elements.

    val lengths = IntArray(indices.size, { indexArrays[it].size })
    val element = IntArray(lengths.size)
    for ((nd, linear) in IndexIterator(lengths)) {
        for (i in 0 until element.size)
            element[i] = indexArrays[i][nd[i]]
        setByte(*element, v=value)
    }
}

fun <T> NDArray<T>.setSliceByte(vararg indices: Any, value: NDArray<Byte>) {
    if (indices.size != shape().size)
        throw IllegalArgumentException("Specified ${indices.size} indices for an array with ${shape().size} dimensions")
    val indexArrays = mutableListOf<IntArray>()

    // Convert the inputs to arrays of integer indices.

    for (i in 0 until indices.size) {
        val index = indices[i]
        val size = shape()[i]
        if (index is Int)
            indexArrays.add(kotlin.intArrayOf(wrapIndex(index, size)))
        else if (index is Iterable<*>)
            indexArrays.add(index.map { wrapIndex(it as Int, size) }.toIntArray())
        else
            throw IllegalArgumentException("All indices must be Int or Iterable<Int>")
    }

    // Make sure the shapes match, after eliminating dimensions of size 1.

    val lengths = IntArray(indices.size, { indexArrays[it].size })
    val outputDims = lengths.filter { it != 1 }
    val inputDims = value.shape().filter { it != 1}
    if (!(outputDims.toIntArray() contentEquals inputDims.toIntArray()))
        throw IllegalArgumentException("Cannot assign a value of shape ${inputDims.toList()} to a slice of shape ${outputDims.toList()}")

    // Set the elements.

    val element = IntArray(lengths.size)
    for ((nd, linear) in IndexIterator(lengths)) {
        for (i in 0 until element.size)
            element[i] = indexArrays[i][nd[i]]
        setByte(*element, v=value.getByte(linear))
    }
}

@koma.internal.JvmName("setByte")
operator fun  NDArray<Byte>.set(vararg indices: Int, value: NDArray<Byte>) {
    val lastIndex = indices.mapIndexed { i, range -> range + value.shape()[i] }
    val outOfBounds = lastIndex.withIndex().any { it.value > shape()[it.index] }
    if (outOfBounds)
        throw IllegalArgumentException("NDArray with shape ${shape()} cannot be " +
                "set at ${indices.toList()} by a ${value.shape()} array " +
                "(out of bounds)")

    val offset = indices.map { it }.toIntArray()
    value.forEachIndexedN { idx, ele ->
        val newIdx = offset.zip(idx).map { it.first + it.second }.toIntArray()
        this.setGeneric(indices=*newIdx, v=ele)
    }
}

/**
 * Find the linear index of the minimum element in this array.
 */
@koma.internal.JvmName("argMinByte")
fun  NDArray<Byte>.argMin(): Int = argMinInternal()

/**
 * Find the linear index of the minimum element along one axis of this array,  returning the result in a new array.
 * If the array contains non-comparable values, this throws an exception.
 * 
 * @param axis      the axis to compute the minimum over
 * @param keepdims  if true, the output array has the same number of dimensions as the original one,
 *                  with [axis] having size 1.  If false, the output array has one fewer dimensions
 *                  than the original one.
 */
@koma.internal.JvmName("argMinAxisByte")
fun  NDArray<Byte>.argMin(axis: Int, keepdims: Boolean): NDArray<Int> =
    reduceArrayAxis(this, { length: Int, get: (Int) -> Byte -> argMinByte(length, get) }, axis, keepdims)

/**
 * Find the value of the minimum element in this array.
 */
@koma.internal.JvmName("minByte")
fun  NDArray<Byte>.min(): Byte = minInternal()

/**
 * Find the minimum element along one axis of this array, returning the result in a new array.
 * If the array contains non-comparable values, this throws an exception.
 *
 * @param axis      the axis to compute the minimum over
 * @param keepdims  if true, the output array has the same number of dimensions as the original one,
 *                  with [axis] having size 1.  If false, the output array has one fewer dimensions
 *                  than the original one.
 */
@koma.internal.JvmName("minAxisByte")
inline fun  NDArray<Byte>.min(axis: Int, keepdims: Boolean): NDArray<Byte> =
    reduceArrayAxis(this, { length: Int, get: (Int) -> Byte -> get(argMinByte(length, get)) }, axis, keepdims)

/**
 * Find the linear index of the maximum element in this array.
 */
@koma.internal.JvmName("argMaxByte")
fun  NDArray<Byte>.argMax(): Int = argMaxInternal()

/**
 * Find the linear index of the maximum element along one axis of this array, returning the result in a new array.
 * If the array contains non-comparable values, this throws an exception.
 * 
 * @param axis      the axis to compute the maximum over
 * @param keepdims  if true, the output array has the same number of dimensions as the original one,
 *                  with [axis] having size 1.  If false, the output array has one fewer dimensions
 *                  than the original one.
 */
@koma.internal.JvmName("argMaxAxisByte")
fun  NDArray<Byte>.argMax(axis: Int, keepdims: Boolean): NDArray<Int> =
    reduceArrayAxis(this, { length: Int, get: (Int) -> Byte -> argMaxByte(length, get) }, axis, keepdims)

/**
 * Find the value of the maximum element in this array.
 */
@koma.internal.JvmName("maxByte")
fun  NDArray<Byte>.max(): Byte = maxInternal()

/**
 * Find the maximum element along one axis of this array, returning the result in a new array.
 * If the array contains non-comparable values, this throws an exception.
 *
 * @param axis      the axis to compute the maximum over
 * @param keepdims  if true, the output array has the same number of dimensions as the original one,
 *                  with [axis] having size 1.  If false, the output array has one fewer dimensions
 *                  than the original one.
 */
@koma.internal.JvmName("maxAxisByte")
inline fun  NDArray<Byte>.max(axis: Int, keepdims: Boolean): NDArray<Byte> =
    reduceArrayAxis(this, { length: Int, get: (Int) -> Byte -> get(argMaxByte(length, get)) }, axis, keepdims)

operator fun  NDArray<Byte>.get(vararg indices: Int) = getByte(*indices)
operator fun  NDArray<Byte>.set(vararg indices: Int, value: Byte) = setByte(indices=*indices, v=value)


@koma.internal.JvmName("divByte")
operator fun NDArray<Byte>.div(other: Byte) = map { (it/other).toByte() }
@koma.internal.JvmName("timesArrByte")
operator fun NDArray<Byte>.times(other: NDArray<Byte>) = mapIndexedN { idx, ele -> (ele*other.get(*idx)).toByte() }
@koma.internal.JvmName("timesByte")
operator fun NDArray<Byte>.times(other: Byte) = map { (it * other).toByte() }
@koma.internal.JvmName("unaryMinusByte")
operator fun NDArray<Byte>.unaryMinus() = map { (-it).toByte() }
@koma.internal.JvmName("minusByte")
operator fun NDArray<Byte>.minus(other: Byte) = map { (it - other).toByte() }
@koma.internal.JvmName("minusArrByte")
operator fun NDArray<Byte>.minus(other: NDArray<Byte>) = mapIndexedN { idx, ele -> (ele - other.get(*idx)).toByte() }
@koma.internal.JvmName("plusByte")
operator fun NDArray<Byte>.plus(other: Byte) = map { (it + other).toByte() }
@koma.internal.JvmName("plusArrByte")
operator fun NDArray<Byte>.plus(other: NDArray<Byte>) = mapIndexedN { idx, ele -> (ele + other.get(*idx)).toByte() }
@koma.internal.JvmName("powByte")
infix fun NDArray<Byte>.pow(exponent: Int) = map { pow(it.toDouble(), exponent).toByte() }
