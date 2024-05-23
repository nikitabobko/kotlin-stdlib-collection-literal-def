interface MyList<T> {
    companion object {
        /*operator*/ fun <T> createListBuilder(size: Int): MutableList<T> = ArrayList(size)
        /*operator*/ fun <T> freeze(a: MutableList<T>, size: Int): List<T> = a/*.apply { it.readonly = true }*/
    }
}

fun main() {
    // val s = [1, 2]
    // desugared to
    val size = 2
    val builder = MyList.createListBuilder<Int>(size)
    builder.plusAssign(1) // !!! plusAssign is an extension, not a member
    builder.plusAssign(2)
    val s = MyList.freeze(builder, size)

    // mapOf(1, 2, 3)
    //
    // java.util.Map.ofEntries<Int>(java.util.Map.entry(1, 2))
}

// Implementing 3 operators for List is harder
// 1. It's impossible to return different types based on the `size` (idea: add `of(T)` and `of()` overloads)
//    How are you going to implement `EmptyList` for `List`? How are you going to implement singletonList?
// 2. The builder type becomes exposed in API
// 3. 3 operators become more scattered. plusAssign is an extension, not a member, which makes the resolve problems harder
//    (idea: make plusAssign a mapped member?)
// 4. We should go with simplier `of` if possible.
// 5. We can do `inline vararg` in future Kotlin updates.
// Idea:
// - createListBuilder, plusAssign, freeze // Still introduces one more MutableList implementation, which makes devirtualization worse
// - of(T) // Strictly one argument
// - of() // Strictly zero arguments
