package io.github.mh321productions.serverapi.configeditor.ui.entry

import io.github.mh321productions.serverapi.configeditor.io.EntryWrapper
import io.github.mh321productions.serverapi.configeditor.ui.frame.ArrayDialog
import io.github.mh321productions.serverapi.configeditor.ui.frame.MainFrame
import io.github.mh321productions.serverapi.configio.EntryArray
import io.github.mh321productions.serverapi.configio.EntryType
import net.miginfocom.swing.MigLayout
import java.awt.CardLayout
import java.awt.Dialog
import java.awt.Dimension
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.util.*
import javax.imageio.ImageIO
import javax.swing.*
import javax.swing.border.LineBorder

class EntryPanel(private val main: MainFrame, private val entry: EntryWrapper) : JPanel() {

    private val lblName = JLabel("Name:")
    private val lblType = JLabel("Type:")
    private val txtName = JTextField()
    private val cbType = JComboBox(EntryType.entries.toTypedArray())
    private val cbBool = JCheckBox("Bool value")
    private val cardLayout = CardLayout()
    private val panelValue = JPanel(cardLayout)
    private val spValue = JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED)
    private val txtValue = JTextArea()
    private val btnArray = JButton("Open Array Manager")
    private val btnDelete = JButton(ImageIcon(ImageIO.read(EntryPanel::class.java.getResource("/icon/garbage.png"))))
    private val array = ArrayDialog(main, if (entry.type == EntryType.Array) entry.value as EntryArray else EntryType.Array.defaultValue as EntryArray)

    companion object {
        const val TYPE_BOOL = "Card Bool Value"
        const val TYPE_ARRAY = "Card Array Value"
        const val TYPE_OTHER = "Card Other Value"
    }

    class KeyTypedListener(val action: () -> Unit) : KeyAdapter() {
        override fun keyTyped(e: KeyEvent) {
            if (!(e.isControlDown && e.keyChar.code == 19)) action()
        }
    }

    val type: EntryType
        get() = cbType.selectedItem as EntryType

    init {
        size = Dimension(730, 100)
        layout = MigLayout("", "[][grow][grow][]", "[][]")
        border = LineBorder(MainFrame.titleBorderColor)

        add(lblName, "cell 0 0, grow")
        add(lblType, "cell 0 1, grow")

        txtName.text = entry.name
        txtName.addKeyListener(KeyTypedListener { main.markDirty() })
        add(txtName, "cell 1 0, grow")

        cbType.selectedItem = entry.type
        cbType.addActionListener { onChangeType() }
        add(cbType, "cell 1 1, grow")

        cbBool.isSelected = if (entry.type == EntryType.Boolean) entry.value as Boolean else false
        cbBool.addActionListener{ main.markDirty() }
        panelValue.add(cbBool, TYPE_BOOL)

        txtValue.text = if (entry.type != EntryType.Boolean && entry.type != EntryType.Array) entry.value.toString() else ""
        txtValue.addKeyListener(KeyTypedListener { main.markDirty() })
        spValue.setViewportView(txtValue)
        spValue.viewportBorder = LineBorder(MainFrame.titleBorderColor)
        panelValue.add(spValue, TYPE_OTHER)

        btnArray.addActionListener { onSelectArray() }
        panelValue.add(btnArray, TYPE_ARRAY)

        val card = when(entry.type) {
            EntryType.Array -> ArrayEntryPanel.TYPE_ARRAY
            EntryType.Boolean -> ArrayEntryPanel.TYPE_BOOL
            else -> ArrayEntryPanel.TYPE_OTHER
        }
        cardLayout.show(panelValue, card)
        add(panelValue, "cell 2 0 1 2, grow")

        btnDelete.addActionListener { onDelete() }
        add(btnDelete, "cell 4 0 1 2, center")

        array.modalityType = Dialog.ModalityType.APPLICATION_MODAL
    }

    fun writeToEntry() : Result<EntryWrapper> {
        val entry = EntryWrapper()
        //println("Writing entry ${txtName.text}")
        entry.name = txtName.text
        entry.type = cbType.selectedItem as EntryType

        try {
            entry.value = when (entry.type) {
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
        } catch (ex: NumberFormatException) {
            ex.printStackTrace()
            JOptionPane.showMessageDialog(main,
                "Couldn't parse entry ${entry.name}\n${ex.message}", "Parsing error", JOptionPane.ERROR_MESSAGE)
            return Result.failure(ex)
        } catch (ex: IllegalArgumentException) {
            ex.printStackTrace()
            JOptionPane.showMessageDialog(main,
                "Couldn't parse entry ${entry.name}\n${ex.message}", "Parsing error", JOptionPane.ERROR_MESSAGE)
            return Result.failure(ex)
        }
        return Result.success(entry)
    }

    private fun onDelete() {
        main.entries.remove(this)
        parent.remove(this)
        main.validate()
        main.repaint()
        main.markDirty()
    }

    private fun onChangeType() {
        val card = when (type) {
            EntryType.Boolean -> TYPE_BOOL
            EntryType.Array -> TYPE_ARRAY
            else -> TYPE_OTHER
        }

        cardLayout.show(panelValue, card)
        main.markDirty()
    }

    private fun onSelectArray() {
        array.title = txtName.text.ifEmpty { "<unnamed>" }
        array.isVisible = true
    }
}