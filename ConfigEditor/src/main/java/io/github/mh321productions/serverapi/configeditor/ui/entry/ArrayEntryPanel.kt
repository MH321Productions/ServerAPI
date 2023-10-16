package io.github.mh321productions.serverapi.configeditor.ui.entry

import io.github.mh321productions.serverapi.configeditor.ui.frame.ArrayDialog
import io.github.mh321productions.serverapi.configeditor.ui.frame.MainFrame
import io.github.mh321productions.serverapi.configio.EntryArray
import io.github.mh321productions.serverapi.configio.EntryType
import net.miginfocom.swing.MigLayout
import java.awt.CardLayout
import java.awt.Dialog
import java.util.*
import javax.imageio.ImageIO
import javax.swing.*
import javax.swing.border.LineBorder

class ArrayEntryPanel(private val main: MainFrame, private val topDialog: ArrayDialog, type: EntryType, index: Int, private var value: Any) : JPanel() {

    private val lblIndex = JLabel("[$index]")
    private val panelValue = JPanel()
    private val cardLayout = CardLayout()
    private val cbBool = JCheckBox("Bool value")
    private val spValue = JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED)
    private val txtValue = JTextArea()
    private val btnArray = JButton("Open Array Manager")
    private val btnUp = JButton(ImageIcon(ImageIO.read(ArrayEntryPanel::class.java.getResource("/icon/arrow-up.png"))))
    private val btnDown = JButton(ImageIcon(ImageIO.read(ArrayEntryPanel::class.java.getResource("/icon/arrow-down.png"))))
    private val btnDelete = JButton(ImageIcon(ImageIO.read(EntryPanel::class.java.getResource("/icon/garbage.png"))))
    private val array = ArrayDialog(main, if (type == EntryType.Array) value as EntryArray else EntryType.Array.defaultValue as EntryArray)

    var type = type
        set(value) {
            field = value

            val card = when(value) {
                EntryType.Array -> TYPE_ARRAY
                EntryType.Boolean -> TYPE_BOOL
                else -> TYPE_OTHER
            }
            cardLayout.show(panelValue, card)
        }

    var index = index
        set(value) {
            field = value
            lblIndex.text = "[$value]"
        }

    companion object {
        const val TYPE_BOOL = "Card Bool Value"
        const val TYPE_ARRAY = "Card Array Value"
        const val TYPE_OTHER = "Card Other Value"
    }

    init {
        layout = MigLayout("", "[][grow][][]", "[][]")
        border = LineBorder(MainFrame.titleBorderColor)

        add(lblIndex, "cell 0 0 1 2, center")

        panelValue.layout = cardLayout

        cbBool.isSelected = if (type == EntryType.Boolean) value as Boolean else false
        cbBool.addActionListener { main.markDirty() }
        panelValue.add(cbBool, TYPE_BOOL)

        txtValue.text = if (type != EntryType.Boolean && type != EntryType.Array) value.toString() else ""
        txtValue.addKeyListener(EntryPanel.KeyTypedListener { main.markDirty() })
        spValue.setViewportView(txtValue)
        panelValue.add(spValue, TYPE_OTHER)

        btnArray.addActionListener { onSelectArray() }
        panelValue.add(btnArray, TYPE_ARRAY)

        val card = when(type) {
            EntryType.Array -> TYPE_ARRAY
            EntryType.Boolean -> TYPE_BOOL
            else -> TYPE_OTHER
        }
        cardLayout.show(panelValue, card)
        add(panelValue, "cell 1 0 1 2, grow")

        btnUp.addActionListener { onChangeIndex(true) }
        add(btnUp, "cell 2 0, grow")

        btnDown.addActionListener { onChangeIndex(false) }
        add(btnDown, "cell 2 1, grow")

        btnDelete.addActionListener { onDelete() }
        add(btnDelete, "cell 3 0 1 2, grow")

        array.modalityType = Dialog.ModalityType.APPLICATION_MODAL
    }

    fun writeToEntry() : Any = when (type) {
        EntryType.Boolean -> cbBool.isSelected
        EntryType.Byte -> txtValue.text.toByte()
        EntryType.Int -> txtValue.text.toInt()
        EntryType.Long -> txtValue.text.toLong()
        EntryType.Float -> txtValue.text.toFloat()
        EntryType.Double -> txtValue.text.toDouble()
        EntryType.String -> txtValue.text
        EntryType.UUID -> UUID.fromString(txtValue.text)
        EntryType.Array -> array.writeToEntry()
    }

    private fun onSelectArray() {
        array.title = "${topDialog.title}[$index]"
        array.isVisible = true
    }

    private fun onChangeIndex(up: Boolean) {
        val newIndex = if (up) index - 1 else index + 1
        if (newIndex < 0 || newIndex >= topDialog.entries.size) return

        val other = parent.getComponent(newIndex) as ArrayEntryPanel
        other.index = index

        val p = parent
        p.remove(this)
        p.add(this, newIndex)

        topDialog.entries.remove(this)
        topDialog.entries.add(newIndex, this)

        index = newIndex
        topDialog.validate()
        topDialog.repaint()
        main.markDirty()
    }

    private fun onDelete() {
        topDialog.entries.remove(this)
        parent.remove(this)
        topDialog.validate()
        topDialog.recalculateIndices()
        topDialog.repaint()
        main.markDirty()
    }
}