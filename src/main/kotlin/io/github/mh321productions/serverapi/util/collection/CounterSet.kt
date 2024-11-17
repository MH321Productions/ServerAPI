package io.github.mh321productions.serverapi.util.collection

/**
 * Interne Klasse: Wrapper für Set, die zählt, wie oft ein Objekt hinzugefügt wurde
 *
 * @author 321Productions
 *
 * @param <E> Die Art von Objekt
</E> */
class CounterSet<E> : MutableSet<E> {

    private val entries = HashMap<E, Int?>()
    override val size = entries.size

    override fun clear() = entries.clear()
    override fun isEmpty() = entries.isEmpty()
    override fun iterator() = entries.keys.iterator()
    override fun containsAll(elements: Collection<E>) = entries.keys.containsAll(elements)
    override fun contains(element: E) = entries.keys.contains(element)

    override fun retainAll(elements: Collection<E>): Boolean {
        val remove = entries.keys
            .filter { !elements.contains(it) }

        if (remove.isNotEmpty()) {
            removeAll(remove)
            return true
        }
        return false
    }

    override fun removeAll(elements: Collection<E>): Boolean {
        elements.forEach { remove(it) }

        return elements.isNotEmpty()
    }

    override fun remove(element: E): Boolean {
        if (!entries.containsKey(element)) return false
        else if (entries[element] == 1) entries.remove(element)
        else entries[element] = entries[element]!! - 1

        return true
    }

    override fun addAll(elements: Collection<E>): Boolean {
        var changed = false

        elements.forEach { changed = add(it) || changed }

        return changed
    }

    override fun add(element: E): Boolean {
        if (entries.containsKey(element)) {
            entries[element] = entries[element]!! + 1
            return false
        }

        entries[element] = 1
        return true
    }

    fun getCountOf(e: E): Int {
        val value = entries[e]
        return value ?: 0
    }
}
