package org.vricsa

import chisel3._
import chisel3.util._
import chisel3.util.experimental.loadMemoryFromFile
import firrtl.annotations.MemoryLoadFileType

class InstROM extends  Module{
  val pcWidth = 32
  val memDepth = 1024
  val memWidth = 8
  val io = IO(new Bundle{
    val en = Input(Bits(1.W))
    val readBaseAddress = Input(Bits(pcWidth.W))
    val instData = Output(UInt(32.W))

  })
  io.instData := 10.U

  ///// Section for ROM

  val programMemory = Mem(memDepth, UInt(memWidth.W))
  // Load the ROM value from Hex file
  loadMemoryFromFile(programMemory, "additionals/ROM.hex", MemoryLoadFileType.Hex)

  //////// ENd of Rom Logic
  val PC = WireInit(0.U(pcWidth.W))
  PC := io.readBaseAddress
  when(io.en === 1.U){

    io.instData := Cat(programMemory(PC + 3.U),programMemory(PC + 2.U),programMemory(PC + 1.U),programMemory(PC + 0.U))

  }
    .otherwise{
      // Do Nothing
    }
}
