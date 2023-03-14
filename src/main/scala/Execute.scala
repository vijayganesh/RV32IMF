package org.vricsa
import chisel3._
import chisel3.util._

class Execute extends Module{
 val io= IO(new Bundle() {
   val valueA = Input(SInt(32.W))
   val valueB = Input(SInt(32.W))
   val exeOpcode = Input(UInt(5.W))
   val aluResult = Output(SInt(32.W))
   val en = Input(Bits(1.W))



 })

  io.aluResult := 0.S
  //when(io.en === 1.U) {

    printf(p"\n -- The en is on and inputs are ${io.exeOpcode}")
    printf(p"\n in Exe data is ${io.valueA} and ${io.valueB}")

    switch(io.exeOpcode) {
      is(0.U) {
        // Add the values
        io.aluResult := io.valueA + io.valueB
        printf(p"\n in Exe data is ${io.valueA} and ${io.valueB}")
      }
      is(1.U) {
        // Subtract A - B
        io.aluResult := io.valueA - io.valueB
      }
      is(2.U){
        // Shift left
        // Left shift value B must be 5 bits
        io.aluResult := (io.valueA.asUInt << io.valueB(4,0).asUInt).asSInt
      }
      is(3.U){
        // shift right
        /// Shift right value B must be 5 bit
        io.aluResult := (io.valueA.asUInt >> io.valueB(4,0).asUInt).asSInt
      }
      is(4.U){
        // Shift Right Arthimatic
        // shift right arithmetic
        io.aluResult := io.valueA >> io.valueB(4,0).asUInt

      }
      is(5.U){
        // AND
        io.aluResult := io.valueA & io.valueB
      }
      is(6.U)
      {
        // OR
        io.aluResult := io.valueA | io.valueB
      }
      is(7.U)
      {
        // XOR
        io.aluResult := io.valueA ^ io.valueB
      }
      // Comparator logic
      is(8.U){
        io.aluResult := (io.valueA < io.valueB).asSInt
      }
      is(9.U){
        // For Unsigned number compare
        io.aluResult := (io.valueA.asUInt < io.valueB.asUInt).asSInt
      }
      // For Branch we need to work out
      is(10.U){

      }
      is(11.U){

      }
      is(12.U){

      }


    }

  //}
  //  .otherwise{
      // No operation
  //  }
}
