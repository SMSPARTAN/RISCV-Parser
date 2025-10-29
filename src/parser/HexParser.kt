package parser

import model.Instruction

object HexParser {
  fun hexToBin(hexLine: String): String {
    val cleanHex = hexLine.trim()
    if (cleanHex.length != 8) {
      throw IllegalArgumentException("Hex line must be 8 characters long: $cleanHex")
    }

    val value = cleanHex.toLong(16)
    return String.format("%32s", value.toString(2)).replace(' ', '0')
  }

  fun parseLine(hexLine: String): Instruction {
    val binLine = hexToBin(hexLine)
    return BinaryParser.parseLine(binLine)
  }
}