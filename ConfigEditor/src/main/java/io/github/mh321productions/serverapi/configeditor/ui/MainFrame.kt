package io.github.mh321productions.serverapi.configeditor.ui

import io.github.mh321productions.serverapi.configio.ConfigEntry
import net.miginfocom.swing.MigLayout
import java.awt.Color
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.*
import javax.swing.border.LineBorder
import javax.swing.border.TitledBorder

class MainFrame : JFrame() {

    class WindowClosingListener(val closeOp: () -> Unit) : WindowAdapter() {
        override fun windowClosing(e: WindowEvent?) {
            closeOp()
        }
    }

    companion object {
        val titleBorderColor = Color(51, 51, 51)
    }

    private val menuBar = JMenuBar()
    private val menuFile = JMenu("File")
        private val miOpen = JMenuItem("Open")
        private val miSave = JMenuItem("Save")
        private val miSaveAs = JMenuItem("Save As")
        private val miQuit = JMenuItem("Quit")
    private val menuHelp = JMenu("Help")
        private val miHelp = JMenuItem("Help")
        private val miAbout = JMenuItem("About")

    private val contentPane = JPanel()
        private val btnAddEntry = JButton("Add entry")
        private val spEntries = JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED)
        private val listEntries = JList<ConfigEntry>()
        private val entries = ArrayListModel<ConfigEntry>()

    init {
        setBounds(100, 100, 750, 500)
        //size = Dimension(750, 500)
        title = "ServerAPI Config Editor"
        defaultCloseOperation = DO_NOTHING_ON_CLOSE
        addWindowListener(WindowClosingListener(::onClose))

        jMenuBar = menuBar

        menuBar.add(menuFile)
            miOpen.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK)
            miOpen.addActionListener { onOpen() }
            menuFile.add(miOpen)
            menuFile.add(JSeparator())
            miSave.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK)
            miSave.addActionListener { onSave(false) }
            menuFile.add(miSave)
            miSaveAs.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK or InputEvent.SHIFT_DOWN_MASK)
            miSave.addActionListener { onSave(true) }
            menuFile.add(miSaveAs)
            menuFile.add(JSeparator())
            miQuit.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK)
            miQuit.addActionListener { onClose() }
            menuFile.add(miQuit)
        menuBar.add(menuHelp)
            miHelp.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0)
            miHelp.addActionListener { onHelp() }
            menuHelp.add(miHelp)
            miAbout.addActionListener { onAbout() }
            menuHelp.add(miAbout)

        setContentPane(contentPane)
        layout = MigLayout("", "[grow]", "[][grow]")

        btnAddEntry.addActionListener { onAdd() }
        contentPane.add(btnAddEntry, "cell 0 0, grow")
        listEntries.model = entries
        listEntries.setCellRenderer(EntryPanel())
        spEntries.setViewportView(listEntries)
        spEntries.viewportBorder = TitledBorder(LineBorder(Color.BLACK), "Entries", TitledBorder.LEADING, TitledBorder.TOP, null, titleBorderColor)
        contentPane.add(spEntries, "cell 0 1, grow")
    }

    private fun onAdd() {

    }

    private fun onOpen() {

    }

    private fun onSave(saveAs: Boolean) {

    }

    private fun onClose() {
        dispose()
    }

    private fun onHelp() {

    }

    private fun onAbout() {

    }
}