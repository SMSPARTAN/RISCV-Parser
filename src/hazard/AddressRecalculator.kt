package hazard

import model.Instruction

/**
 * Calcula o overhead e o novo endereço de instruções à serem reordenadas
 */
class AddressRecalculator {
  fun recalculateAddresses(instructions: List<Instruction>): List<Pair<Int, Instruction>> {
    val result = mutableListOf<Pair<Int, Instruction>>()
    var currentAddress = 0

    for (instruction in instructions) {
      result.add(currentAddress to instruction)
      currentAddress += 4 // Each instruction is 4 bytes
    }

    return result
  }

  fun calculateOverhead(originalCount: Int, resolvedCount: Int): OverheadInfo {
    val additionalInstructions = resolvedCount - originalCount
    val overheadPercentage = (additionalInstructions.toDouble() / originalCount) * 100

    return OverheadInfo(originalCount, resolvedCount, additionalInstructions, overheadPercentage)
  }
}

data class OverheadInfo(
  val originalInstructionCount: Int,
  val resolvedInstructionCount: Int,
  val additionalInstructions: Int,
  val overheadPercentage: Double
)