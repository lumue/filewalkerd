package net.lumue.filewalkerd.scanner

import java.io.File

interface FileHandler {
    fun handleFile(file: File)
}