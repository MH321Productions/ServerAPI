package io.github.mh321productions.serverapi.configeditor.ui

import javax.swing.AbstractListModel

class ArrayListModel<E> : AbstractListModel<E>() {
    private val elements = ArrayList<E>()
    override fun getSize(): Int {
        return elements.size
    }

    override fun getElementAt(index: Int): E {
        return elements[index]
    }

    fun clear() {
        if (elements.isNotEmpty()) {
            fireIntervalRemoved(this, 0, elements.size)
            elements.clear()
        }
    }

    fun setElements(list: List<E>) {
        clear()
        elements.addAll(list)
        fireIntervalAdded(this, 0, list.size - 1)
    }

    fun addElement(element: E) {
        elements.add(element)
        fireIntervalAdded(this, elements.size - 2, elements.size - 1)
    }

    fun removeElement(element: E) {
        val index = elements.indexOf(element)
        if (index == -1) return
        removeElement(index)
    }

    fun removeElement(index: Int) {
        if (index == -1) return
        elements.removeAt(index)
        fireIntervalRemoved(this, index, index)
    }
}
