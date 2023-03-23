package org.vricsa

import chisel3._
import chisel3.util._

/**
 *  ctlReadMem and ctlWriteMem are mutual exclusive
 */
class xreg extends  Module{
  val xregDepth = 32
  val xregWidth = 32
  val io = IO(new Bundle() {
    val ctlReadMem = Input(Bits(1.W))
    val ctlWriteMem = Input(Bits(1.W))
    val RegRs1 = Input(UInt(5.W))
    val RegRs2 = Input(UInt(5.W))
    val RegWR = Input(UInt(5.W))
    val en = Input(Bits(1.W))
    val RegWrData = Input(UInt(32.W))
    val RegRs1Data = Output(UInt(32.W))
    val RegRs2Data = Output(UInt(32.W))

  })

 io.RegRs1Data := 0.U
  io.RegRs2Data := 0.U
  /// Declaraion of Xregister
  val X = Mem(xregDepth, UInt(xregWidth.W))
  X(0) := 0.U // Hardwired to zero
  //when(io.en === 1.U)
  //{

    when(io.ctlReadMem === 1.U){
      // read the data from the memory
      io.RegRs1Data := X(io.RegRs1)
      io.RegRs2Data := X(io.RegRs2)
    }

    when(io.ctlWriteMem === 1.U){
      when(io.RegWR =/= 0.U) {
        X(io.RegWR) := io.RegWrData
      }
    }

 // }
   /* .otherwise{

      // Do nothing
      //io.RegRs1Data := 0.U
      //io.RegRs2Data := 0.U

    }
*/

}
