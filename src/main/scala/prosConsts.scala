package org.vricsa

import chisel3.experimental.ChiselEnum
import chisel3._

object WordType extends ChiselEnum {
  val wByte,wHalfWord,wWord = Value
}
// Based on example from chisel cookbook
// for our purpose remove the least two bits
object Opcodes extends ChiselEnum {
  val load  = Value(0x00.U) // i "load"  -> 0_0000_11
  val imm   = Value(0x04.U) // i "imm"   -> 0_0100_11
  val auipc = Value(0x05.U) // u "auipc" -> 0_0101_11
  val store = Value(0x08.U) // s "store" -> 0_1000_11
  val reg   = Value(0x0C.U) // r "reg"   -> 0_1100_11
  val lui   = Value(0x0D.U) // u "lui"   -> 0_1101_11
  val br    = Value(0x18.U) // b "br"    -> 1_1000_11
  val jalr  = Value(0x19.U) // i "jalr"  -> 1_1001_11
  val jal   = Value(0x1D.U) // j "jal"   -> 1_1011_11
  val none = Value(0x1f.U) // just for initial value
}

object ALUOperation extends  ChiselEnum {
  val ADD = Value(0x00.U)
  val SUB = Value(1.U)
  val SHIFTLEFT = Value(2.U)
  val SHIFTRIGHT = Value(3.U)
  val SHIFTRIGHTArthi = Value(4.U)
  val AND = Value(5.U)
  val OR = Value(6.U)
  val XOR = Value(7.U)
  val LessThanS = Value(8.U)
  val LessThanU = Value(9.U)

}
object  Consts {
  val XLEN = 32

}
