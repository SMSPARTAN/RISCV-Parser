package parser

import model.Instruction

/**
 * Controla o parseamento das linhas e guarda as instruções
 */
class RiscVParser {
  val instructions: MutableList<Instruction> = mutableListOf()

  fun String.isBinary(): Boolean {
    if (this.isEmpty()) return false
    return all { it == '0' || it == '1' }
  }

  fun String.isHex(): Boolean {
    if (this.isEmpty()) return false
    val clean = this.trim()
    return clean.all {
      it in '0'..'9' ||
          it in 'A'..'F' ||
          it in 'a'..'f'
    }
  }

  fun parseLines(lines: MutableList<String>) {
    for (line in lines) {
      if (line.isEmpty()) continue
      else if (line.isBinary()) instructions.add(BinaryParser.parseLine(line))
      else if (line.isHex()) instructions.add(HexParser.parseLine(line))
      else println("Line ignored, not a valid 32-bit bin/hex string: $line")
    }
  }
}