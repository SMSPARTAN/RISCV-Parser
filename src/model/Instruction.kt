package model

/**
 * Representa uma instrução RISCV-32i
 */
sealed class Instruction {
  abstract val rawBinary: String
  abstract fun summary(): String

  // Helper methods for hazard detection
  abstract fun getRd(): Int?
  abstract fun getRs1(): Int?
  abstract fun getRs2(): Int?
  abstract fun isBranch(): Boolean
  abstract fun isJump(): Boolean
  abstract fun isLoad(): Boolean
  abstract fun isStore(): Boolean

  data class R(
    override val rawBinary: String,
    val rd: Int,
    val rs1: Int,
    val rs2: Int,
    val funct3: Int,
    val funct7: Int,
  ) : Instruction() {
    override fun summary() = "R: bin=$rawBinary rd=$rd f3=$funct3 rs1=$rs1 rs2=$rs2 f7=$funct7"
    override fun getRd() = if (rd != 0) rd else null
    override fun getRs1() = if (rs1 != 0) rs1 else null
    override fun getRs2() = if (rs2 != 0) rs2 else null
    override fun isBranch() = false
    override fun isJump() = false
    override fun isLoad() = false
    override fun isStore() = false
  }

  data class I(
    override val rawBinary: String,
    val rd: Int,
    val rs1: Int,
    val funct3: Int,
    val imm: Int
  ) : Instruction() {
    override fun summary() = "I: bin=$rawBinary rd=$rd f3=$funct3 rs1=$rs1 imm=$imm"
    override fun getRd() = if (rd != 0) rd else null
    override fun getRs1() = if (rs1 != 0) rs1 else null
    override fun getRs2() = null
    override fun isBranch() = false
    override fun isJump() = rawBinary.slice(25..31) == "1100111" // JALR
    override fun isLoad() = rawBinary.slice(25..31) == "0000011" // LOAD
    override fun isStore() = false
  }

  data class S(
    override val rawBinary: String,
    val rs1: Int,
    val rs2: Int,
    val funct3: Int,
    val imm: Int
  ) : Instruction() {
    override fun summary() = "S: bin=$rawBinary f3=$funct3 rs1=$rs1 rs2=$rs2 imm=$imm"
    override fun getRd() = null
    override fun getRs1() = if (rs1 != 0) rs1 else null
    override fun getRs2() = if (rs2 != 0) rs2 else null
    override fun isBranch() = false
    override fun isJump() = false
    override fun isLoad() = false
    override fun isStore() = true
  }

  data class B(
    override val rawBinary: String,
    val rs1: Int,
    val rs2: Int,
    val funct3: Int,
    val imm: Int
  ) : Instruction() {
    override fun summary() = "B: bin=$rawBinary f3=$funct3 rs1=$rs1 rs2=$rs2 imm=$imm"
    override fun getRd() = null
    override fun getRs1() = if (rs1 != 0) rs1 else null
    override fun getRs2() = if (rs2 != 0) rs2 else null
    override fun isBranch() = true
    override fun isJump() = false
    override fun isLoad() = false
    override fun isStore() = false
  }

  data class U(
    override val rawBinary: String,
    val rd: Int,
    val imm: Int,
  ) : Instruction() {
    override fun summary() = "U: bin=$rawBinary rd=$rd imm=$imm"
    override fun getRd() = if (rd != 0) rd else null
    override fun getRs1() = null
    override fun getRs2() = null
    override fun isBranch() = false
    override fun isJump() = false
    override fun isLoad() = false
    override fun isStore() = false
  }

  data class J(
    override val rawBinary: String,
    val rd: Int,
    val imm: Int,
  ) : Instruction() {
    override fun summary() = "J: bin=$rawBinary rd=$rd imm=$imm"
    override fun getRd() = if (rd != 0) rd else null
    override fun getRs1() = null
    override fun getRs2() = null
    override fun isBranch() = false
    override fun isJump() = true
    override fun isLoad() = false
    override fun isStore() = false
  }

  data class Unknown(override val rawBinary: String) : Instruction() {
    override fun summary() = "Unknown: bin=$rawBinary"
    override fun getRd() = null
    override fun getRs1() = null
    override fun getRs2() = null
    override fun isBranch() = false
    override fun isJump() = false
    override fun isLoad() = false
    override fun isStore() = false
  }
}

fun makeNOP(): Instruction {
  return Instruction.I("00000000000000000000000000010011", 0, 0, 0, 0)
}