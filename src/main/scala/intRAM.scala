package org.vricsa
import chisel3._
import chisel3.util._
class intRAM extends Module{
  val memDepth = 1024
  val memWidth = 8

  val io = IO(new Bundle() {
    val address = Input(UInt(log2Ceil(memDepth).W))
    val wrData = Input(SInt(32.W))
    val wrOp = Input(Bits(1.W))
    val rdOp = Input(Bits(1.W))
    val RAMDataOut = Output(SInt(32.W))
    val datatype = Input(WordType())

   // val en = Input(Bits(1.W))

  })


  io.RAMDataOut := 0.S
   //val iRAM = SyncReadMem(memDepth, SInt(memWidth.W))
  val iRAM = Mem(memDepth, SInt(memWidth.W))

  val tValue = WireInit(0.S(32.W))
  val firstByte = WireInit(0.S(8.W))
  val secondByte = WireInit(0.S(8.W))
  val thirdByte = WireInit(0.S(8.W))
  val fourthByte = WireInit(0.S(8.W))

  firstByte := io.wrData(7, 0).asSInt
  secondByte := io.wrData(15, 8).asSInt
  thirdByte := io.wrData(23, 16).asSInt
  fourthByte := io.wrData(31, 24).asSInt

  io.RAMDataOut := tValue

 // Update

  when(io.rdOp === 1.U ){
   // io.RAMDataOut := iRAM(io.address)

    switch(io.datatype){
      is(WordType.wByte){
        tValue := iRAM.read(io.address).asSInt
      }
      is(WordType.wHalfWord){
        //tValue := //Cat(iRAM.read(io.address+1.U),iRAM.read(io.address)).asSInt
        tValue := Cat(iRAM.read(io.address+1.U),iRAM.read(io.address)).asSInt
      }
      is(WordType.wWord){
        tValue := Cat(iRAM.read(io.address+3.U),iRAM.read(io.address+2.U),iRAM.read(io.address+1.U),iRAM.read(io.address)).asSInt
      }


    }

    //io.RAMDataOut:= iRAM.read(io.address)

  }
  when(io.wrOp === 1.U ) {
    // read the data
    // iRAM(io.address) := io.wrData
    tValue := io.wrData


    // iRAM.write(io.address,firstByte)
    //io.RAMDataOut := io.wrData
    switch(io.datatype) {

      is(WordType.wByte) {
        // iRAM.write(io.address,io.wrData(7,0))
        iRAM.write(io.address, firstByte)
      }
      is(WordType.wHalfWord) {
        iRAM.write(io.address, firstByte)
        iRAM.write(io.address + 1.U, secondByte)

      }
      is(WordType.wWord) {
        iRAM.write(io.address, firstByte)
        iRAM.write(io.address + 1.U, secondByte)
        iRAM.write(io.address + 2.U, thirdByte)
        iRAM.write(io.address + 3.U, fourthByte)

      }


    }

  }


}
