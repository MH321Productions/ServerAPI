package io.github.mh321productions.serverapi.configeditor.ui.frame

import io.github.mh321productions.serverapi.configeditor.ui.entry.ArrayEntryPanel
import io.github.mh321productions.serverapi.configio.EntryArray
import io.github.mh321productions.serverapi.configio.EntryType
import net.miginfocom.swing.MigLayout
import java.util.*
import javax.swing.*
import javax.swing.border.LineBorder
import javax.swing.border.TitledBorder

class ArrayDialog(private val main: MainFrame, entry: EntryArray) : JDialog(main) {

    private val lblType = JLabel("Entry type:")
    private val cbType = JComboBox(EntryType.entries.toTypedArray())
    private val btnAdd = JButton("Add entry")
    private val spEntries = JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED)
    private val listEntries = JPanel()
    private val btnOk = JButton("OK")
    val entries = mutableListOf<ArrayEntryPanel>()

    val entryType : EntryType
        get() = cbType.selectedItem as EntryType

    init {
        setBounds(100, 100, 500, 300)
        setLocationRelativeTo(main)
        defaultCloseOperation = DISPOSE_ON_CLOSE

        contentPane.layout = MigLayout("", "[][grow][grow]", "[][grow][]")

        contentPane.add(lblType, "cell 0 0, grow")

        cbType.selectedItem = entry.type
        cbType.addActionListener { onChangeType() }
        add(cbType, "cell 1 0, grow")

        btnAdd.addActionListener { onAdd() }
        contentPane.add(btnAdd, "cell 2 0, grow")

        listEntries.layout = BoxLayout(listEntries, BoxLayout.Y_AXIS)
        spEntries.setViewportView(listEntries)
        spEntries.border = TitledBorder(
            LineBorder(MainFrame.titleBorderColor),
            "Entries", TitledBorder.LEADING, TitledBorder.TOP, null, MainFrame.titleBorderColor
        )
        contentPane.add(spEntries, "cell 0 1 3 1, grow")

        btnOk.addActionListener { dispose() }
        contentPane.add(btnOk, "cell 0 2 3 1, grow")

        //Add entries
        val loaded = when(entry.type) {
            EntryType.Boolean -> entry.boolEntries
            EntryType.Byte -> entry.byteEntries
            EntryType.Int -> entry.intEntries
            EntryType.Long -> entry.longEntries
            EntryType.Float -> entry.floatEntries
            EntryType.Double -> entry.doubleEntries
            EntryType.String -> entry.stringEntries
            EntryType.UUID -> entry.uuidEntries
            EntryType.Array -> entry.arrayEntries
        }

        loaded.forEachIndexed { index, v ->
            val e = ArrayEntryPanel(main, this, entry.type, index, v)

            entries.add(e)
            listEntries.add(e)
        }
    }

    fun recalculateIndices() = entries.forEachIndexed { index, entry -> entry.index = index }

    fun writeToEntry() : EntryArray {
        val arr = EntryArray.emptyArray(entryType)

        val encoded = entries.map { it.writeToEntry() }

        when (entryType) {
            EntryType.Boolean -> arr.boolEntries.addAll(encoded.map { it as Boolean })
            EntryType.Byte -> arr.byteEntries.addAll(encoded.map { it as Byte })
            EntryType.Int -> arr.intEntries.addAll(encoded.map { it as Int })
            EntryType.Long -> arr.longEntries.addAll(encoded.map { it as Long })
            EntryType.Float -> arr.floatEntries.addAll(encoded.map { it as Float })
            EntryType.Double -> arr.doubleEntries.addAll(encoded.map { it as Double })
            EntryType.String -> arr.stringEntries.addAll(encoded.map { it as String })
            EntryType.UUID -> arr.uuidEntries.addAll(encoded.map { it as UUID })
            EntryType.Array -> arr.arrayEntries.addAll(encoded.map { it as EntryArray })
        }

        return arr
    }

    private fun onChangeType() {
        entries.forEach { it.type = entryType }
    }

    private fun onAdd() {
        val entry = ArrayEntryPanel(main, this, entryType, entries.size, entryType.defaultValue)
        entries.add(entry)
        listEntries.add(entry)

        validate()
        repaint()
        main.markDirty()
    }
}