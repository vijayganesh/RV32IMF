package org.vricsa
import chisel3._
import chisel3.experimental.ChiselEnum
import chisel3.util._
import chisel3.util.experimental.loadMemoryFromFile
import firrtl.annotations.MemoryLoadFileType


class RV32imf extends  Module{
  val io = IO(new Bundle() {
 val busy = Output(Bool())
    val tout = Output(UInt(32.W))

  })
  object States extends ChiselEnum {
    val sNone, sFetch, sDecode, sExecute,sStore = Value
  }
  /// Need to change as parameter at later stage
  val memDepth = 1024
  val memWidth = 8
  val state = RegInit(States.sNone)

  val  pcWidth = 32 // 32 bit pc register
  val PC = RegInit(0.U(pcWidth.W))
  // Declaration  of modules
  val ProgramMem = Module(new InstROM())
  val decoder = Module(new Decoder())
  ProgramMem.io.en := 0.U
  ProgramMem.io.readBaseAddress := PC
  decoder.io.InstructionData := ProgramMem.io.instData
  decoder.io.en := 0.U
  decoder.io.wrData := 0.U
  decoder.io.ctlRead := 0.U
  decoder.io.ctlWrite := 0.U


 // val xreg  = Module(new xreg())


  /// Section of RAM
  // Depth kept to

  ///


  /// Declaraions for Instructions


  /**
   * State diagram of control unit
   */
  val tBusy = RegInit(0.B)

  io.busy := tBusy
  io.tout := 0.U

  switch(state)
  {
    is(States.sNone){state := States.sFetch
    tBusy := 0.B
    ProgramMem.io.en := 0.U
    }
    is(States.sFetch){
      tBusy := 1.B
      //insructionData :=
      ProgramMem.io.en := 1.U
     // printf("Changing the fetch")
      state := States.sDecode
    } // end of fetch
    is(States.sDecode){
      decoder.io.en := 1.U
      decoder.io.ctlRead := 1.U
      decoder.io.ctlWrite := 0.U
      decoder.io.wrData := 0.U // since going for single cycle  need to change in later stages
      ProgramMem.io.en := 0.U

      state := States.sExecute
    }
    is(States.sExecute){
      decoder.io.en := 0.U
      io.tout := decoder.io.aluOpcode
      state := States.sStore}
    is(States.sStore){
      PC := PC + 4.U
      state := States.sFetch
      io.busy := 0.B
    }


  }



}
