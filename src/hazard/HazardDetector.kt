package hazard

import model.Instruction

/**
 * Detecta conflitos nas instruções
 */
class HazardDetector {

  // Data hazard detection without forwarding
  fun detectDataHazardWithoutForwarding(instructions: List<Instruction>): List<Int> {
    val hazards = mutableListOf<Int>()

    for (i in 0 until instructions.size - 1) {
      val current = instructions[i]
      val next = instructions[i + 1]

      val currentRd = current.getRd()
      if (currentRd != null) {
        // Check if next instruction uses the register written by current
        if (next.getRs1() == currentRd || next.getRs2() == currentRd) {
          hazards.add(i)
        }
      }
    }

    return hazards
  }

  // Data hazard detection with forwarding
  fun detectDataHazardWithForwarding(instructions: List<Instruction>): List<Int> {
    val hazards = mutableListOf<Int>()

    for (i in 0 until instructions.size - 1) {
      val current = instructions[i]
      val next = instructions[i + 1]

      val currentRd = current.getRd()
      if (currentRd != null && current.isLoad()) {
        // With forwarding, only loads cause hazards that need stalls
        if (next.getRs1() == currentRd || next.getRs2() == currentRd) {
          hazards.add(i)
        }
      }
    }

    return hazards
  }

  // Control hazard detection
  fun detectControlHazard(instructions: List<Instruction>): List<Int> {
    val hazards = mutableListOf<Int>()

    for (i in instructions.indices) {
      val current = instructions[i]
      if (current.isBranch() || current.isJump()) {
        hazards.add(i)
      }
    }

    return hazards
  }
}