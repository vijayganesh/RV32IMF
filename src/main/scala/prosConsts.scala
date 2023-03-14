package org.vricsa

import chisel3.experimental.ChiselEnum

object WordType extends ChiselEnum {
  val wByte,wHalfWord,wWord = Value
}