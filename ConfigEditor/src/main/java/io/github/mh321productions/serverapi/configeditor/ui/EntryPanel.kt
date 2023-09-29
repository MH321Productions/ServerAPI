package io.github.mh321productions.serverapi.configeditor.ui

import io.github.mh321productions.serverapi.configio.ConfigEntry
import java.awt.Component
import javax.swing.JList
import javax.swing.JPanel
import javax.swing.ListCellRenderer

class EntryPanel : JPanel(), ListCellRenderer<ConfigEntry> {

    init {

    }

    override fun getListCellRendererComponent(
        list: JList<out ConfigEntry>,
        value: ConfigEntry,
        index: Int,
        isSelected: Boolean,
        cellHasFocus: Boolean
    ): Component {
        TODO("Not yet implemented")
    }
}