package org.vricsa

import chisel3._
import chisel3.util._
import chisel3.util.experimental.{loadMemoryFromFile, loadMemoryFromFileInline}
import firrtl.annotations.MemoryLoadFileType

import scala.collection.mutable.ArrayBuffer
import scala.io.Source

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

  //val programMemory = Mem(memDepth, UInt(memWidth.W))
  // Load the ROM value from Hex file
  //loadMemoryFromFileInline(programMemory, "additionals/ROM.hex", MemoryLoadFileType.Hex)
   //val programMemory = VecInit(Seq.fill(128)(0.U(memWidth.W)))
   val k = Tools.readmemh("additionals/ROM.hex")
   val programMemory = VecInit(k.map(_.U(8.W)))

  println(s"The Length is ${k.length}")
  //////// ENd of Rom Logic
  val PC = WireInit(0.U(pcWidth.W))
  PC := io.readBaseAddress
  val tInsData = RegInit(0.U(pcWidth.W))
  io.instData := tInsData
  //printf("The Value is dd = %d \n",programMemory(11))
  //when(io.en === 1.U){
 // printf(p"\n the data is ${Hexadecimal(programMemory1(PC))} and PC=${Hexadecimal(PC)} \n")
    // printf("The Value is%d \n",programMemory(10))
    tInsData := Cat(programMemory(PC + 3.U),programMemory(PC + 2.U),programMemory(PC + 1.U),programMemory(PC + 0.U))

  //}
  //  .otherwise{
      // Do Nothing

   // }
}


// Solution to load from File from Stackoverflow
object Tools{
  def readmemh(path: String): Array[BigInt] = {
    val buffer = new ArrayBuffer[BigInt]
    for (line <- Source.fromFile(path).getLines) {
      //val tokens: Array[String] = line.split("(//)").map(_.trim)
      if (line != "") {
        val i = Integer.parseInt(line, 16)
        buffer.append(i)
      }
    }
    buffer.toArray
  }

}