package io.github.mh321productions.serverapi.configio

/**
 * A small wrapper around a [MutableList], with a callback function, which
 * is called, when the list is changed
 */
@Suppress("UNCHECKED_CAST")
class WrappedCallbackMutableList<E: Any>(var callback: () -> Unit, private val impl: MutableList<Any>) : MutableList<E> {

    override val size: Int
        get() = impl.size

    override fun add(element: E): Boolean {
        callback()
        return impl.add(element)
    }

    override fun add(index: Int, element: E) {
        callback()
        impl.add(index, element)
    }

    override fun addAll(index: Int, elements: Collection<E>): Boolean {
        callback()
        return impl.addAll(index, elements)
    }

    override fun addAll(elements: Collection<E>): Boolean {
        callback()
        return impl.addAll(elements)
    }

    override fun clear() {
        callback()
        impl.clear()
    }

    override fun get(index: Int): E = impl[index] as E

    override fun isEmpty() = impl.isEmpty()

    override fun iterator() = impl.iterator() as MutableIterator<E>

    override fun listIterator() = impl.listIterator() as MutableListIterator<E>

    override fun listIterator(index: Int) = impl.listIterator(index) as MutableListIterator<E>

    override fun lastIndexOf(element: E) = impl.lastIndexOf(element)

    override fun indexOf(element: E) = impl.indexOf(element)

    override fun containsAll(elements: Collection<E>) = impl.containsAll(elements)

    override fun contains(element: E) = impl.contains(element)

    override fun removeAt(index: Int): E {
        callback()
        return impl.removeAt(index) as E
    }

    override fun subList(fromIndex: Int, toIndex: Int): WrappedCallbackMutableList<E> {
        return WrappedCallbackMutableList(callback, impl.subList(fromIndex, toIndex))
    }

    override fun set(index: Int, element: E): E {
        callback()
        return impl.set(index, element) as E
    }

    override fun retainAll(elements: Collection<E>): Boolean {
        callback()
        return impl.retainAll(elements)
    }

    override fun removeAll(elements: Collection<E>): Boolean {
        callback()
        return impl.removeAll(elements)
    }

    override fun remove(element: E): Boolean {
        callback()
        return impl.remove(element)
    }
}