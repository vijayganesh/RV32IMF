package org.vricsa
import chisel3._
import chisel3.util._

class Decoder extends Module{
  val io = IO (new Bundle{

    val InstructionData = Input(UInt(32.W))
    val rs1Data = Output(UInt(32.W))
    val rs2Data = Output(UInt(32.W))
    val ctlRead = Input(Bits(1.W))
    val ctlWrite = Input(Bits(1.W))
    val en = Input(Bits(1.W))
    val wrData = Input(UInt(32.W))
    val rs1Execute = Output(UInt(32.W))
    val rs2Execute = Output(UInt(32.W))
    val aluOpcode = Output(UInt(5.W))


  })
 // at reset value
  io.rs2Execute := 0.U
  io.rs1Execute := 0.U
  io.aluOpcode := 7.U
  io.rs2Data := 0.U
  io.rs1Data := 0.U

  val ItypeImmediateData = WireInit(0.U(12.W))
  //val regRd = WireInit(0.U(5.W))
  val regRs1 = WireInit(0.U(5.W))
  val regRs2 = WireInit(0.U(5.W))
  val regWr = WireInit(0.U(5.W))
  val func3 = WireInit(0.U(3.W))
  val func7 = WireInit(0.U(7.W))
  val opcode = WireInit(0.U(5.W))
  val tInstruction = WireInit(0.U(32.W))
  val xregs  = Module(new xreg())
  xregs.io.en := io.en
  xregs.io.ctlReadMem := io.ctlRead
  xregs.io.ctlWriteMem := io.ctlWrite
  xregs.io.RegWrData := io.wrData
  xregs.io.RegRs2 := regRs2
  xregs.io.RegRs1 := regRs1
  xregs.io.RegWR := regWr
  io.rs2Data := xregs.io.RegRs2Data
  io.rs1Data := xregs.io.RegRs1Data




  //////


  tInstruction := io.InstructionData

  ////
  when(io.en===1.U){

    // Decode based on Types
    // 6-0 values
    // R - type -
    // I - Type
    // S - Type
    // B - Type
    // J - type
    // U - Type
    opcode := tInstruction(6, 2)
    func3 := tInstruction(14, 12)
    func7 := tInstruction(31, 25) // last 7 bits
   // regRd := tInstruction(11, 7)
    ItypeImmediateData := tInstruction(31,20)
    regRs1 := tInstruction(19, 15)
    regRs2 := tInstruction(24,20)
    regWr := tInstruction(11,7)

    switch(opcode){
      is(0.U){
        // Required to add immediate and rs1 value to find the location
        // so RS1 = xreg(rs1)
        // RS2 = immediate value
        // set ALU mode = addition
        io.rs1Execute := xregs.io.RegRs1Data
        io.rs2Execute := ItypeImmediateData
        io.aluOpcode := 0.U
        xregs.io.RegWR := regWr
        switch(func3){
          is(0.U){         }
          is(1.U){          }
          is(2.U){          }
          is(4.U){          }
          is(5.U){          }

        }
      } //End of Load statement
      is(8.U){
        // Store to Memory
        // Check for function
      }

    }


  }
    .otherwise{
      // Do nothing
    }

}
