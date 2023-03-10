package org.vricsa

import chiseltest.{ChiselScalatestTester, VerilatorBackendAnnotation, WriteVcdAnnotation}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers
import chiseltest._

class RV32imfTester extends AnyFlatSpec with ChiselScalatestTester with Matchers{
  val annos = Seq(VerilatorBackendAnnotation,WriteVcdAnnotation)
  behavior of "RV32IMF is "
  "Initial Run FSM " should "Load move all the state" in {
    test(new RV32imf()).withAnnotations(annos){
      c=>
        c.clock.step(10)
    }


  }

}
