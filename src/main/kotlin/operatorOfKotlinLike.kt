@file:OptIn(ExperimentalSubclassOptIn::class)

package foo

import java.util.Collections

// Observation: we don't loop over vararg elements, we pass the vararg further
// Observation: listOf provides 3 overloads
interface MyList<T> {
    companion object {
        /*operator*/ fun <T> of(): List<T> = Collections.emptyList()
        /*operator*/ fun <T> of(element: T): List<T> = java.util.Collections.singletonList(element)
        /*operator*/ fun <T> of(vararg elements: T): List<T> =
            when (elements.size) {
                0 -> Collections.emptyList()
                1 -> Collections.singletonList(elements[0])
                else -> elements.asList() /*.asList() doesn't copy the array*/
            }
    }
}

// Observation: we don't loop over vararg elements, we pass the vararg further
// Observation: mutableListOf provides only 2 overloads
interface MyMutableList<T> {
    companion object {
        /*operator*/ fun <T> of(): MutableList<T> = ArrayList()
        /*operator*/ fun <T> of(element: T): MutableList<T> = ArrayList<T>(1).apply { add(element) }
        @Suppress("INVISIBLE_REFERENCE")
        /*operator*/ fun <T> of(vararg elements: T): MutableList<T> =
            when (elements.size) {
                0 -> ArrayList<T>()
                1 -> ArrayList<T>(1).apply { add(elements[0]) }
                // ArrayList(ArrayAsCollection(...)) avoids copying the vararg array. ArrayList copies the array inside the constructor
                else -> ArrayList<T>(ArrayAsCollection(elements, isVarargs = true))
            }
    }
}

public class MyIntArray {
    companion object { /*operator*/ fun of(vararg elements: Int): IntArray = error("generated code") }
}

public class MyLongArray {
    companion object { /*operator*/ fun of(vararg elements: Long): LongArray = error("generated code") }
}

public class MyArray<T> {
    companion object { /*operator*/ inline fun <reified T> of(vararg elements: T): Array<T> = error("generated code") }
}

public interface MySet<T> {
    companion object {
        /*operator*/ fun <T> of(): Set<T> = Collections.emptySet()
        /*operator*/ fun <T> of(element: T): Set<T> = Collections.singleton(element)
        /*operator*/ fun <T> of(vararg elements: T): Set<T> =
            when (elements.size) {
                0 -> Collections.emptySet()
                1 -> Collections.singleton(elements[0])
                else -> TODO("Custom unmodifiable Set implementation is needed :(")
            }
    }
}

public interface MyMutableSet<T> {
    companion object {
        /*operator*/ fun <T> of(): MutableSet<T> = Collections.emptySet()
        /*operator*/ fun <T> of(element: T): MutableSet<T> = Collections.singleton(element)
        /*operator*/ fun <T> of(vararg elements: T): MutableSet<T> =
            when (elements.size) {
                0 -> Collections.emptySet()
                1 -> Collections.singleton(elements[0])
                else -> elements.toMutableSet()
            }
    }
}

public interface MyMap<K, V> {
    companion object {
        /*operator*/ fun <K, V> of(): Map<K, V> = Collections.emptyMap()
        /*operator*/ fun <K, V> of(pair: Pair<K, V>): Map<K, V> = Collections.singletonMap(pair.first, pair.second)
        /*operator*/ fun <K, V> of(vararg pairs: Pair<K, V>): Map<K, V> =
            when (pairs.size) {
                0 -> Collections.emptyMap()
                1 -> Collections.singletonMap(pairs[0].first, pairs[0].second)
                else -> TODO("Custom unmodifiable Map implementation is needed :(")
            }
    }
}

public interface MyMutableMap<K, V> {
    companion object {
        /*operator*/ fun <K, V> of(): MutableMap<K, V> = Collections.emptyMap()
        /*operator*/ fun <K, V> of(pair: Pair<K, V>): MutableMap<K, V> = Collections.singletonMap(pair.first, pair.second)
        /*operator*/ fun <K, V> of(vararg pairs: Pair<K, V>): MutableMap<K, V> =
            when (pairs.size) {
                0 -> Collections.emptyMap()
                1 -> Collections.singletonMap(pairs[0].first, pairs[0].second)
                else -> pairs.toMap(LinkedHashMap(mapCapacity(pairs.size)))
            }
    }
}

@PublishedApi
internal fun mapCapacity(expectedSize: Int): Int = when {
    // We are not coercing the value to a valid one and not throwing an exception. It is up to the caller to
    // properly handle negative values.
    expectedSize < 0 -> expectedSize
    expectedSize < 3 -> expectedSize + 1
    expectedSize < INT_MAX_POWER_OF_TWO -> ((expectedSize / 0.75F) + 1.0F).toInt()
    // any large value
    else -> Int.MAX_VALUE
}
private const val INT_MAX_POWER_OF_TWO: Int = 1 shl (Int.SIZE_BITS - 2)
