package org.vricsa
import chisel3._
import chisel3.experimental.ChiselEnum
import chisel3.util._
import chisel3.util.experimental.loadMemoryFromFile
import firrtl.annotations.MemoryLoadFileType

/**
 *
 * Single cycle RV32IMF design for Embedded applications
 */

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
  val exealu = Module(new Execute())
  val internalRam = Module(new intRAM())


  ProgramMem.io.en := 0.U
  ProgramMem.io.readBaseAddress := PC
  decoder.io.InstructionData := ProgramMem.io.instData
  decoder.io.en := 0.U
 decoder.io.wrData := 0.U // Initial state
  decoder.io.pcValue := PC
  // Store the data from ALU result or from RAM
  //decoder.io.wrData := exealu.io.aluResult.asUInt //0.U // This value should be updated based on signal passed
 // decoder.io.ctlRead := 0.U
 // decoder.io.ctlWrite := 0.U
   /// Connect decoder and execute unit
   exealu.io.valueA := decoder.io.rs1Execute.asSInt
   exealu.io.valueB := decoder.io.rs2Execute.asSInt
   exealu.io.en := 0.U
   exealu.io.exeOpcode := (decoder.io.aluOpcode)

  //val RamData = WireInit(0.S(SInt(32.W)))
 /// internal RAM
 // wr to RAM comes only from RS2 of xregs->decoder
 internalRam.io.wrData := decoder.io.wrDataRAM.asSInt
 internalRam.io.address := exealu.io.aluResult.asUInt
 internalRam.io.wrOp := decoder.io.isDataRAMWrite
 internalRam.io.rdOp := decoder.io.isDataRAMRead
  internalRam.io.datatype := decoder.io.ramDataType
// RamData := internalRam.io.RAMDataOut

 // val xreg  = Module(new xreg())


  /// Section of RAM
  // Depth kept to

  ///


  /// Declaraions for Instructions


  /**
   * State diagram of control unit
   */
  val tBusy = RegInit(0.B)
  /// Control unit signals declaration

  /// need the value from Decoder
  val cuIsDataRAMWrite = RegInit(0.U(1.W))
  val cuIsDataRAMRead = RegInit(0.U((1.W)))


  cuIsDataRAMRead := decoder.io.isDataRAMRead
  cuIsDataRAMWrite := decoder.io.isDataRAMWrite



  io.busy := tBusy
 io.tout :=0.U
  //io.tout := exealu.io.aluResult.asUInt

  when(decoder.io.isDataRAMRead === 1.U){
   // This is for load instruction
   io.tout := internalRam.io.RAMDataOut.asUInt
   decoder.io.wrData := internalRam.io.RAMDataOut.asUInt
  }
    .elsewhen(decoder.io.isXregwr === 1.U){
     // This is for op-imm, op and R -Type
     io.tout := exealu.io.aluResult.asUInt
     decoder.io.wrData := exealu.io.aluResult.asUInt
    }
/*
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
      //state := States.sDecode
     state := States.sStore // To ensure data is stored
    } // end of fetch
    is(States.sDecode){
      decoder.io.en := 1.U
     // decoder.io.ctlRead := 1.U
      //decoder.io.ctlWrite := 0.U
      decoder.io.wrData := 0.U // since going for single cycle  need to change in later stages
      ProgramMem.io.en := 0.U
        // Here it needs to know, is the data to be written in RAM or retrieve from RAM?


      state := States.sExecute
    }
    is(States.sExecute){
      decoder.io.en := 0.U
      //decoder.io.ctlRead := 0.U
      exealu.io.en := 1.U

      //exealu.io.exeOpcode
      //io.tout := exealu.io.aluResult//decoder.io.aluOpcode
      printf(p"\n <----- Execute state ------> \n aluopcode is ${decoder.io.aluOpcode} \n")
      state := States.sStore}
    is(States.sStore){
      PC := PC + 4.U
      state := States.sFetch
      io.busy := 0.B
    }


  }
*/
 // Pc logic depends on
  // deocoder isBranchInst and result is true then change the new value
  val pcAddreTemp = WireInit(0.S(Consts.pcWidth.W))
  when(decoder.io.isBrancInst === true.B){
    when(exealu.io.aluResult(0) === 1.U){
      pcAddreTemp := decoder.io.brRelativeAddress -4.S // since 4 is added at begining
    }
      .otherwise{
        pcAddreTemp := 4.S
      }

  }.elsewhen(decoder.io.isJumpInst ===true.B){
    pcAddreTemp := decoder.io.brRelativeAddress - 4.S
  }
    .otherwise{
      pcAddreTemp := 4.S
    }
 PC := PC+pcAddreTemp.asUInt
}
