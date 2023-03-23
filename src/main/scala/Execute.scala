package org.vricsa
import chisel3._
import chisel3.util._

class Execute extends Module{
 val io= IO(new Bundle() {
   val valueA = Input(SInt(32.W))
   val valueB = Input(SInt(32.W))
   //val exeOpcode = Input(UInt(5.W))
   val exeOpcode = Input(ALUOperation())
   val aluResult = Output(SInt(32.W))
   val en = Input(Bits(1.W))



 })

  io.aluResult := 0.S
  //when(io.en === 1.U) {

    printf(p"\n -- The en is on and inputs are ${io.exeOpcode}")
    printf(p"\n in Exe data is ${io.valueA} and ${io.valueB}")

    switch(io.exeOpcode) {
      is(ALUOperation.ADD) {
        // Add the values
        io.aluResult := io.valueA + io.valueB
        printf(p"\n in Exe data is ${io.valueA} and ${io.valueB}")
      }
      //is(1.U) {
      is(ALUOperation.SUB){

        // Subtract A - B
        io.aluResult := io.valueA - io.valueB
      }
     // is(2.U){
      is(ALUOperation.SHIFTLEFT){
        // Shift left
        // Left shift value B must be 5 bits
        io.aluResult := (io.valueA.asUInt << io.valueB(4,0).asUInt).asSInt
      }
      //is(3.U){
      is(ALUOperation.SHIFTRIGHT){
        // shift right
        /// Shift right value B must be 5 bit
        io.aluResult := (io.valueA.asUInt >> io.valueB(4,0).asUInt).asSInt
      }
      //is(4.U){
      is(ALUOperation.SHIFTRIGHTArthi){
        // Shift Right Arthimatic
        // shift right arithmetic
        io.aluResult := io.valueA >> io.valueB(4,0).asUInt

      }
      //is(5.U){
      is(ALUOperation.AND){
        // AND
        io.aluResult := io.valueA & io.valueB
      }
     // is(6.U)
      is(ALUOperation.OR)
      {
        // OR
        io.aluResult := io.valueA | io.valueB
      }
     // is(7.U)
      is(ALUOperation.XOR)
      {
        // XOR
        io.aluResult := io.valueA ^ io.valueB
      }
      // Comparator logic
      //is(8.U)
      is(ALUOperation.LessThanS)
      {
        io.aluResult := (io.valueA < io.valueB).asSInt
      }
      //is(9.U)
      is(ALUOperation.LessThanU)
      {
        // For Unsigned number compare
        io.aluResult := (io.valueA.asUInt < io.valueB.asUInt).asSInt
      }
      // For Branch we need to work out
      is(ALUOperation.brEQL)
      {
        io.aluResult := (io.valueA === io.valueB).asSInt

      }
      is(ALUOperation.brNEQL)
      {
        io.aluResult := (io.valueA =/= io.valueB).asSInt
      }
      is(ALUOperation.brLTS)
      {
        io.aluResult := (io.valueA < io.valueB).asSInt
      }
      is(ALUOperation.brLTU)
      {
        io.aluResult := (io.valueA.asUInt < io.valueB.asUInt).asSInt
      }
      is(ALUOperation.brGRES)
      {
        io.aluResult := (io.valueA >= io.valueB).asSInt

      }
      is(ALUOperation.brGREU)
      {
        io.aluResult := (io.valueA.asUInt >= io.valueB.asUInt).asSInt
      }
      }

  //}
  //  .otherwise{
      // No operation
  //  }
}
