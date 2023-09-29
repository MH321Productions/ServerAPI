package io.github.mh321productions.serverapi.configeditor

import io.github.mh321productions.serverapi.configeditor.ui.MainFrame
import java.awt.EventQueue

fun main() {
    println("Hello World")
    EventQueue.invokeLater {
        val frame = MainFrame()

        frame.setLocationRelativeTo(null)
        frame.isVisible = true
    }
}