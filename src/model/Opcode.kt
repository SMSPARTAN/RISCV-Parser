package model

import parser.BinaryParser.buildB
import parser.BinaryParser.buildI
import parser.BinaryParser.buildJ
import parser.BinaryParser.buildR
import parser.BinaryParser.buildS
import parser.BinaryParser.buildU

/**
 * Contêm os OPCODEs para os diversos tipos de instruções da ISA RISCV-32i
 */
enum class Opcode(
  val opcodes: Set<Int>,
  val builder: (UInt, String) -> Instruction
) {
  R(setOf(0b0110011), ::buildR),
  I(setOf(0b0010011, 0b0011011, 0b0000011, 0b0001111, 0b1100111, 0b1110011), ::buildI),
  S(setOf(0b0100011), ::buildS),
  B(setOf(0b1100011), ::buildB),
  U(setOf(0b0110111, 0b0010111), ::buildU),
  J(setOf(0b1101111), ::buildJ),
}
