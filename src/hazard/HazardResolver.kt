package hazard

import model.Instruction
import model.makeNOP

interface HazardResolver {
  fun resolve(instructions: List<Instruction>): Pair<List<Instruction>, Int>
}

/**
 * Resolve conflitos sem forwarding
 */
class DataHazardResolverWithoutForwarding : HazardResolver {

  override fun resolve(instructions: List<Instruction>): Pair<List<Instruction>, Int> {
    val newInstructions = mutableListOf<Instruction>()
    var nopCount = 0

    for (i in instructions.indices) {
      newInstructions.add(instructions[i])

      // Check for data hazard with next instruction
      if (i < instructions.size - 1) {
        val current = instructions[i]
        val next = instructions[i + 1]

        val currentRd = current.getRd()
        if (currentRd != null) {
          if (next.getRs1() == currentRd || next.getRs2() == currentRd) {
            // Insert 1 NOP for non-load, 2 for load
            val nopsToInsert = if (current.isLoad()) 2 else 1
            repeat(nopsToInsert) {
              newInstructions.add(makeNOP())
              nopCount++
            }
          }
        }
      }
    }

    return newInstructions to nopCount
  }
}

/**
 * Resolve conflitos com forwarding
 */
class DataHazardResolverWithForwarding : HazardResolver {

  override fun resolve(instructions: List<Instruction>): Pair<List<Instruction>, Int> {
    val newInstructions = mutableListOf<Instruction>()
    var nopCount = 0

    for (i in instructions.indices) {
      newInstructions.add(instructions[i])

      // Check for data hazard with next instruction (only for loads)
      if (i < instructions.size - 1) {
        val current = instructions[i]
        val next = instructions[i + 1]

        if (current.isLoad()) {
          val currentRd = current.getRd()
          if (currentRd != null && (next.getRs1() == currentRd || next.getRs2() == currentRd)) {
            newInstructions.add(makeNOP())
            nopCount++
          }
        }
      }
    }

    return newInstructions to nopCount
  }
}

/**
 * Controla o resolvimento de conflitos
 */
class ControlHazardResolver : HazardResolver {

  override fun resolve(instructions: List<Instruction>): Pair<List<Instruction>, Int> {
    val newInstructions = mutableListOf<Instruction>()
    var nopCount = 0

    for (i in instructions.indices) {
      newInstructions.add(instructions[i])

      val current = instructions[i]
      if (current.isBranch() || current.isJump()) {
        // Insert NOPs after control instructions
        newInstructions.add(makeNOP())
        nopCount++

        // For jumps, insert additional NOP if needed
        if (current.isJump() && !current.isBranch()) {
          newInstructions.add(makeNOP())
          nopCount++
        }
      }
    }

    return newInstructions to nopCount
  }
}

class IntegratedHazardResolver(useForwarding: Boolean) : HazardResolver {
  private val dataResolver = if (useForwarding) DataHazardResolverWithForwarding()
  else DataHazardResolverWithoutForwarding()
  private val controlResolver = ControlHazardResolver()

  override fun resolve(instructions: List<Instruction>): Pair<List<Instruction>, Int> {
    // First resolve data hazards
    val (dataResolved, dataNops) = dataResolver.resolve(instructions)
    // Then resolve control hazards on the result
    val (fullyResolved, controlNops) = controlResolver.resolve(dataResolved)

    return fullyResolved to (dataNops + controlNops)
  }
}