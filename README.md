== Código produzido para a M2 das aulas de Organização de Computadores com o professor Thiago Felski da Universidade do Vale do Itajaí ==

**Requisitos**
· Kotlin 2.2.20-release-333
· Java openjdk 25.0.1

**Building**
kotlinc Main.kt */*.kt -include-runtime -d RISCV-Parser.jar

**Como Usar**
kotlin RISCV-Parser.jar <input_file> <output_prefix> ou java -jar RISCV-Parser.jar <input_file> <output_prefix>
Ex:. Kotlin RISCV-Paser.jar fib_recursivo_bin.txt prefixo_legal ou java -jar RISCV-Parser.jar fib_rescursivo_hex.txt prefixo_bem_legal

**Alunos**
· Samuel Sarno De Almeida
· Lucas André Alexandre

**¡AVISO!**
O projeto foi feito em sistemas Linux e, também, utilizando do WSL do Windows.
Ao buildar em sistemas Windows fora do WSL garanta que os requisitos do SDK de Java e Kotlin estejam satisfeitos e no seu PATH.
Também é possível buildar o projeto utilizando uma IDE como o IntelliJ
