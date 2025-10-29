import hazard.*
import model.Instruction
import parser.RiscVParser
import util.FileManager
import java.io.File

fun main(argv: Array<String>) {
  if (argv.isEmpty()) {
    println("Usage: java -jar riscv-parser.jar <input_file> [output_prefix]")
    return
  }

  val inputFile = argv[0]
  val outputPrefix = if (argv.size > 1) argv[1] else "output"

  val lines = FileManager.readFileAsLines(inputFile)
  val parser = RiscVParser()
  parser.parseLines(lines)

  println("Found ${parser.instructions.size} instructions")

  val detector = HazardDetector()
  val addressRecalculator = AddressRecalculator()

  // Detect hazards
  val dataHazardsWithoutForwarding = detector.detectDataHazardWithoutForwarding(parser.instructions)
  val dataHazardsWithForwarding = detector.detectDataHazardWithForwarding(parser.instructions)
  val controlHazards = detector.detectControlHazard(parser.instructions)

  println("\n=== HAZARD DETECTION ===")
  println("Data hazards without forwarding: ${dataHazardsWithoutForwarding.size}")
  println("Data hazards with forwarding: ${dataHazardsWithForwarding.size}")
  println("Control hazards: ${controlHazards.size}")

  // Resolve hazards and calculate overhead
  val resolvers = mapOf(
    "data_no_forwarding" to DataHazardResolverWithoutForwarding(),
    "data_with_forwarding" to DataHazardResolverWithForwarding(),
    "control" to ControlHazardResolver(),
    "integrated_no_forwarding" to IntegratedHazardResolver(false),
    "integrated_with_forwarding" to IntegratedHazardResolver(true)
  )

  println("\n=== HAZARD RESOLUTION OVERHEAD ===")

  val results = mutableMapOf<String, Pair<List<Instruction>, Int>>()
  for ((name, resolver) in resolvers) {
    val (resolvedInstructions, nopCount) = resolver.resolve(parser.instructions)
    results[name] = resolvedInstructions to nopCount

    val overhead = addressRecalculator.calculateOverhead(
      parser.instructions.size,
      resolvedInstructions.size
    )

    println("$name:")
    println("  Original instructions: ${overhead.originalInstructionCount}")
    println("  Resolved instructions: ${overhead.resolvedInstructionCount}")
    println("  Additional NOPs: ${overhead.additionalInstructions}")
    println("  Overhead: ${"%.2f".format(overhead.overheadPercentage)}%")

    // Save to file
    val outputFile = "${outputPrefix}_${name}.txt"
    saveInstructionsToFile(resolvedInstructions, outputFile)
    println("  Saved to: $outputFile")
  }

  // Display recalculated addresses for integrated solution
  println("\n=== RECALCULATED ADDRESSES (Integrated with Forwarding) ===")
  val integratedSolution = results["integrated_with_forwarding"]!!.first
  val addresses = addressRecalculator.recalculateAddresses(integratedSolution)

  addresses.take(10).forEach { (addr, inst) ->
    println("0x${addr.toString(16).padStart(8, '0')}: ${inst.summary()}")
  }
  if (addresses.size > 10) {
    println("... and ${addresses.size - 10} more instructions")
  }
}

fun saveInstructionsToFile(instructions: List<Instruction>, filename: String) {
  val file = File(filename)
  file.bufferedWriter().use { writer ->
    instructions.forEach { instruction ->
      writer.write(instruction.rawBinary)
      writer.newLine()
    }
  }
}