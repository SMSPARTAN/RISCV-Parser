package util

import java.io.File

object FileManager {
  /**
   *  Lê as linhas e as armazena no vetor `lineList`
   */
  fun readFileAsLines(path: String): MutableList<String> {
    val lineList: MutableList<String> = mutableListOf()
    val file = File(path)
    val inputStream = file.inputStream()

    inputStream.bufferedReader().forEachLine { lineList.add(it) }

    return lineList
  }
}