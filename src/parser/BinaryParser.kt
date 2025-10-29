package parser

import model.Instruction
import model.Opcode

/**
 * Parseia uma string binária e define uma instrução RISCV-32i
 */
object BinaryParser {
  fun binToUInt(binline: String): UInt {
    var v = 0u

    for (c in binline) {
      v = (v shl 1) or (c.digitToInt().toUInt())
    }

    return v
  }

  // Helper functions for bitwise
  fun UInt.slice(offset: Int, length: Int): UInt =
    (this shr offset) and ((1u shl length) - 1u)

  fun UInt.signExtend(width: Int): Int {
    val shift = 32 - width
    return (toInt() shl shift) shr shift
  }

  fun buildR(bin: UInt, binline: String): Instruction.R {
    val rd = bin.slice(7, 5).toInt()
    val funct3 = bin.slice(12, 3).toInt()
    val rs1 = bin.slice(15, 5).toInt()
    val rs2 = bin.slice(20, 5).toInt()
    val funct7 = bin.slice(25, 7).toInt()

    return Instruction.R(binline, rd, rs1, rs2, funct3, funct7)
  }

  fun buildI(bin: UInt, binline: String): Instruction.I {
    val rd = bin.slice(7, 5).toInt()
    val funct3 = bin.slice(12, 3).toInt()
    val rs1 = bin.slice(15, 5).toInt()
    val imm = bin.slice(20, 12).signExtend(12)

    return Instruction.I(binline, rd, rs1, funct3, imm)
  }

  fun buildS(bin: UInt, binline: String): Instruction.S {
    val funct3 = bin.slice(12, 3).toInt()
    val rs1 = bin.slice(15, 5).toInt()
    val rs2 = bin.slice(20, 5).toInt()

    val immLow = bin.slice(7, 5)
    val immHigh = bin.slice(25, 7)
    val uImm = (immHigh shl 5) or immLow
    val imm = uImm.signExtend(12)

    return Instruction.S(binline, rs1, rs2, funct3, imm)
  }

  fun buildB(bin: UInt, binline: String): Instruction.B {
    val funct3 = bin.slice(12, 3).toInt()
    val rs1 = bin.slice(15, 5).toInt()
    val rs2 = bin.slice(20, 5).toInt()

    val imm11 = bin.slice(7, 1)
    val imm4_1 = bin.slice(8, 4)
    val imm10_5 = bin.slice(25, 6)
    val imm12 = bin.slice(31, 1)

    val uImm = (imm12 shl 12) or
        (imm11 shl 11) or
        (imm10_5 shl 5) or
        (imm4_1 shl 1)

    val imm = uImm.signExtend(13)

    return Instruction.B(binline, rs1, rs2, funct3, imm)
  }

  fun buildU(bin: UInt, binline: String): Instruction.U {
    val rd = bin.slice(7, 5).toInt()
    val raw20 = bin.slice(12, 20)
    val imm = (raw20.toInt() shl 12)

    return Instruction.U(binline, rd, imm)
  }

  fun buildJ(bin: UInt, binline: String): Instruction.J {
    val rd = bin.slice(7, 5).toInt()

    val imm10_1 = bin.slice(21, 10)
    val imm11 = bin.slice(20, 1)
    val imm19_12 = bin.slice(12, 8)
    val imm20 = bin.slice(31, 1)
    val uImm = (imm20 shl 20) or
        (imm19_12 shl 12) or
        (imm11 shl 11) or
        (imm10_1 shl 1)

    val imm = uImm.signExtend(21)

    return Instruction.J(binline, rd, imm)
  }

  fun parseLine(binline: String): Instruction {
    if (binline.length != 32) {
      return Instruction.Unknown(binline)
    }

    val bin = binToUInt(binline)
    val opcode = (bin and 0x7Fu).toInt()

    /**
     * Pesquisa em model.Opcode por um enum correspondente à [opcode] e o atribui para fmt
     * apos, usa-se fmt para invocar a operação de construção correspondente ao opcode
     * retorna a instrução construída pelas funçoes build* ou um model.Instruction.Unknown caso ocorra algum erro
     */
    val fmt = Opcode.entries.firstOrNull { opcode in it.opcodes }
    return fmt?.builder?.invoke(bin, binline) ?: Instruction.Unknown(binline)
  }
}