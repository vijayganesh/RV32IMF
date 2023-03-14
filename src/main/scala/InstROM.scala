package org.vricsa

import chisel3._
import chisel3.util._
import chisel3.util.experimental.{loadMemoryFromFile, loadMemoryFromFileInline}
import firrtl.annotations.MemoryLoadFileType

class InstROM extends  Module{
  val pcWidth = 32
  val memDepth = 256
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
  loadMemoryFromFileInline(programMemory, "additionals/ROM.hex", MemoryLoadFileType.Hex)


  //////// ENd of Rom Logic
  val PC = WireInit(0.U(pcWidth.W))
  PC := io.readBaseAddress
  val tInsData = RegInit(0.U(pcWidth.W))
  io.instData := tInsData
  //printf("The Value is dd = %d \n",programMemory(11))
  //when(io.en === 1.U){

    // printf("The Value is%d \n",programMemory(10))
    tInsData := Cat(programMemory(PC + 3.U),programMemory(PC + 2.U),programMemory(PC + 1.U),programMemory(PC + 0.U))

  //}
  //  .otherwise{
      // Do Nothing

   // }
}
