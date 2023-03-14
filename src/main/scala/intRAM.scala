package org.vricsa
import chisel3._
import chisel3.util._
class intRAM extends Module{
  val memDepth = 1024
  val memWidth = 8

  val io = IO(new Bundle() {
    val address = Input(UInt(memWidth.W))
    val wrData = Input(SInt(32.W))
    val wrOp = Input(Bits(1.W))
    val rdOp = Input(Bits(1.W))
    val RAMDataOut = Output(SInt(32.W))
    val wordtype = Input(Bits(2.W))

   // val en = Input(Bits(1.W))

  })


  io.RAMDataOut := 0.S
   val iRAM = SyncReadMem(memDepth, SInt(memWidth.W))
  val tValue = WireInit(0.S(32.W))

 // Update

  when(io.rdOp === 1.U ){
   // io.RAMDataOut := iRAM(io.address)

    switch(io.wordtype){
      is(WordType.wByte){
        tValue := iRAM.read(io.address)
      }


    }

    io.RAMDataOut:= iRAM.read(io.address)

  }
  when(io.wrOp === 1.U )
  {
    // read the data
   // iRAM(io.address) := io.wrData
    iRAM.write(io.address,io.wrData)
    io.RAMDataOut := io.wrData
  }



}
