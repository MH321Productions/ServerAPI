package io.github.mh321productions.serverapi.configeditor.ui.frame

import io.github.mh321productions.serverapi.configeditor.io.EntryWrapper
import io.github.mh321productions.serverapi.configeditor.ui.entry.EntryPanel
import io.github.mh321productions.serverapi.configio.ConfigIO
import io.github.mh321productions.serverapi.configio.EntryType
import net.miginfocom.swing.MigLayout
import java.awt.Color
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.io.File
import java.io.IOException
import javax.swing.*
import javax.swing.border.LineBorder
import javax.swing.border.TitledBorder
import javax.swing.filechooser.FileNameExtensionFilter

class MainFrame : JFrame() {

    private class WindowClosingListener(val closeOp: () -> Unit) : WindowAdapter() {
        override fun windowClosing(e: WindowEvent?) {
            closeOp()
        }
    }

    companion object {
        val titleBorderColor = Color(51, 51, 51)
        const val FRAME_TITLE = "ServerAPI Config Editor"
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
        private val listEntries = JPanel()
        val entries = mutableListOf<EntryPanel>()

    private var dirty = false
    private var saveFile: File? = null

    init {
        setBounds(100, 100, 750, 500)
        //size = Dimension(750, 500)
        title = FRAME_TITLE
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
            miSaveAs.addActionListener { onSave(true) }
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

        listEntries.layout = BoxLayout(listEntries, BoxLayout.Y_AXIS)
        spEntries.setViewportView(listEntries)
        spEntries.viewportBorder = TitledBorder(LineBorder(Color.BLACK), "Entries", TitledBorder.LEADING, TitledBorder.TOP, null, titleBorderColor)
        spEntries.verticalScrollBar.unitIncrement = 10
        contentPane.add(spEntries, "cell 0 1, grow")
    }

    fun markDirty() {
        dirty = true
        updateTitle()
    }

    private fun updateTitle() {
        title = "$FRAME_TITLE${if (dirty) "*" else ""}"
    }

    private fun onAdd() {
        val entry = EntryWrapper("", EntryType.Boolean, false)
        val panel = EntryPanel(this, entry)
        entries.add(panel)
        listEntries.add(panel)
        validate()
        markDirty()
    }

    private fun onOpen() {
        if (dirty) {
            val res = JOptionPane.showConfirmDialog(this,
                "You have unsaved changes.\nDo you want to save them\nbefore opening a new file?", "Unsaved changes",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE
            )

            when (res) {
                JOptionPane.YES_OPTION -> if (!onSave(false)) return
                JOptionPane.CANCEL_OPTION -> return
            }
        }

        val fc = JFileChooser(System.getProperty("user.dir"))
        fc.dialogType = JFileChooser.OPEN_DIALOG
        fc.fileSelectionMode = JFileChooser.FILES_ONLY
        fc.fileFilter = FileNameExtensionFilter("ServerAPI Config Files", "sac")
        fc.dialogTitle = "Open"

        if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return

        val readFile = fc.selectedFile

        try {
            val loaded = ConfigIO
                .loadFile(readFile)
                .map { EntryPanel(this, EntryWrapper(it)) }

            listEntries.removeAll()
            entries.clear()

            entries.addAll(loaded)
            loaded.forEach { listEntries.add(it) }

            validate()
            repaint()
            dirty = false
            updateTitle()

            saveFile = readFile
        } catch (ex: Exception) {
            ex.printStackTrace()
            JOptionPane.showMessageDialog(this, "Couldn't open file\n${ex.message}", "Saving error", JOptionPane.ERROR_MESSAGE)
        }
    }

    private fun onSave(saveAs: Boolean) : Boolean {
        if (saveFile == null || saveAs) {
            val fc = JFileChooser(System.getProperty("user.dir"))
            fc.dialogType = JFileChooser.SAVE_DIALOG
            fc.fileSelectionMode = JFileChooser.FILES_ONLY
            fc.fileFilter = FileNameExtensionFilter("ServerAPI Config Files", "sac")
            fc.dialogTitle = "Save as"

            if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return false

            saveFile = fc.selectedFile
            if (saveFile?.extension != "sac") saveFile = File(fc.selectedFile.parentFile, fc.selectedFile.name + ".sac")
        }

        var errors = 0
        val res = entries
            .map { it.writeToEntry() }
            .filter {
                if (it.isFailure) {
                    errors++
                    false
                } else true
            }
            .map { it.getOrThrow().toEntry() }

        try {
            ConfigIO.saveFile(saveFile!!, res)
        } catch (ex: IOException) {
            ex.printStackTrace()
            JOptionPane.showMessageDialog(this, "Couldn't save file\n${ex.message}", "Saving error", JOptionPane.ERROR_MESSAGE)
            return false
        }

        dirty = errors != 0
        updateTitle()

        return errors == 0
    }

    private fun onClose() {
        if (dirty) {
            val res = JOptionPane.showConfirmDialog(this,
                "You have unsaved changes.\nDo you want to save them before quitting?", "Unsaved changes",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE
            )

            when (res) {
                JOptionPane.YES_OPTION -> if (!onSave(false)) return
                JOptionPane.CANCEL_OPTION -> return
            }
        }

        dispose()
        //exitProcess(0)
    }

    private fun onHelp() {

    }

    private fun onAbout() {

    }
}