package org.vricsa
import chisel3._
import chisel3.util._

class Decoder extends Module{
  val io = IO (new Bundle{

    val InstructionData = Input(UInt(32.W))
    val rs1Data = Output(UInt(32.W))
    val rs2Data = Output(UInt(32.W))
   // val ctlRead = Input(Bits(1.W))
   // val ctlWrite = Input(Bits(1.W))
    val en = Input(Bits(1.W))
    val wrData = Input(UInt(32.W))
    val rs1Execute = Output(UInt(32.W))
    val rs2Execute = Output(UInt(32.W))
    val aluOpcode = Output(UInt(5.W))
    val wrDataRAM = Output(UInt(32.W))


    // Additional Control unit

    val isDataRAMWrite = Output(Bits(1.W))
    val isDataRAMRead = Output(Bits(1.W))
    val isXregwr = Output(Bits(1.W))


  })
 // at reset value
  io.rs2Execute := 0.U
  io.rs1Execute := 0.U
  io.wrDataRAM := 0.U

  io.rs2Data := 0.U
  io.rs1Data := 0.U
  io.isDataRAMWrite := 0.B
  io.isDataRAMRead := 0.B
  io.isXregwr := 0.B

  val ItypeImmediateData = WireInit(0.U(12.W))
  val StypeImmediateData = WireInit(0.U(12.W))
  //val regRd = WireInit(0.U(5.W))
  val regRs1 = WireInit(0.U(5.W))
  val regRs2 = WireInit(0.U(5.W))
  val regWr = WireInit(0.U(5.W))
  val func3 = WireInit(0.U(3.W))
  val func7 = WireInit(0.U(7.W))
  val opcode = WireInit(0.U(5.W))
  val tInstruction = WireInit(0.U(32.W))
  val aluOperation = WireInit(7.U(5.W))
  val ctlRead = WireInit(0.U(1.W))
  val ctlWrite = WireInit(0.U(1.W))
  io.aluOpcode := aluOperation
  val xregs  = Module(new xreg())
  xregs.io.en := io.en
  xregs.io.ctlReadMem := ctlRead //io.ctlRead
  xregs.io.ctlWriteMem := ctlWrite //io.ctlWrite
  xregs.io.RegWrData := io.wrData
  xregs.io.RegRs2 := regRs2
  xregs.io.RegRs1 := regRs1
  xregs.io.RegWR := regWr
  io.rs2Data := xregs.io.RegRs2Data
  io.rs1Data := xregs.io.RegRs1Data
  io.isXregwr := ctlWrite




  //////


  tInstruction := io.InstructionData

  ////
 // when(io.en===1.U){

    // Decode based on Types
    // 6-0 values
    // R - type -
    // I - Type
    // S - Type
    // B - Type
    // J - type
    // U - Type
    opcode := tInstruction(6, 2)
    func3 := tInstruction(14, 12)
    func7 := tInstruction(31, 25) // last 7 bits

   // regRd := tInstruction(11, 7)
    ItypeImmediateData := tInstruction(31,20)
    StypeImmediateData := Cat(tInstruction(31,25),tInstruction(11,7))

    regRs1 := tInstruction(19, 15)
    regRs2 := tInstruction(24,20)
    regWr := tInstruction(11,7)
    io.isDataRAMRead := 0.B
      io.isDataRAMRead := 0.B
   // printf(p" \n The opcode is $opcode and instdata is ${tInstruction(6,0)}")
    switch(opcode){
      is(0.U){
        // Required to add immediate and rs1 value to find the location
        // so RS1 = xreg(rs1)
        // RS2 = immediate value
        // set ALU mode = addition
        io.rs1Execute := xregs.io.RegRs1Data
        io.rs2Execute := ItypeImmediateData
        //io.aluOpcode := 0.U
        aluOperation := 0.U
        xregs.io.RegWR := regWr
        //io.isXregwr := 1.U
        io.isDataRAMRead := 1.B
        ctlRead := 1.U
        ctlWrite := 1.U // This may lead to conflict

        switch(func3){
          is(0.U){         }
          is(1.U){          }
          is(2.U){          }
          is(4.U){          }
          is(5.U){          }

        }
       // printf(p"\n reached for load instruction and ${io.rs2Execute}")
      } //End of Load statement
      is(8.U){
        // Store copy from rs2 to Memory
        // Check for function
        io.rs1Execute := xregs.io.RegRs1Data
        io.rs2Execute := StypeImmediateData//Cat(ItypeImmediateData,regWr)
        //io.aluOpcode := 0.U
        aluOperation := 0.U
        //xregs.io.RegWR := regRs2
        io.wrDataRAM := xregs.io.RegRs2Data

        io.isDataRAMWrite := 1.B
        ctlRead := 1.U
      }
      // Imm - Ops
      is(4.U){
        io.rs1Execute := xregs.io.RegRs1Data
        io.rs2Execute := ItypeImmediateData
        ctlRead := 1.U
        ctlWrite := 1.U


        switch(func3){
          is(0.U){
            // ADDI instruction
            aluOperation := 0.U
            printf("Reached ADDI instr \n ")
          }
          is(2.U){
            // SLTI set  Less than Immediate
            aluOperation := 8.U

          }
          is(3.U){
            // SLTIU set  Less than Immediate Unsigned
            aluOperation := 9.U
          }
          is(4.U)
          {
            // XorI
            aluOperation := 7.U
          }
          is(6.U){
            // ORI
            aluOperation := 6.U
          }
          is(7.U){
            // AND I
            aluOperation := 5.U
          }
          is(1.U){
            // SLLI
            io.rs2Execute := ItypeImmediateData(5,0)
            aluOperation := 2.U
          }
          is(5.U){
            // need to use function  7 to determine the data
            io.rs2Execute := ItypeImmediateData(5,0)
            switch(func7){
              is(0.U){
                // SRLI
                aluOperation := 3.U
              }
              is("h20".U){
                // SRAI
                aluOperation := 4.U
              }
            }
          }
        }

      }

      // OP of type R require to extract RD,RS1 and RS2, func3 and func7 decides
      is("b01100".U){
        // Both data from the Xregs
        io.rs1Execute := xregs.io.RegRs1Data
        io.rs1Execute := xregs.io.RegRs2Data
        xregs.io.RegWR := regWr
        ctlRead := 1.U
        ctlWrite := 1.U

        switch(func3){
          is(0.U){
            // ADD and SUB
            switch(func7) {
              is(0.U) {
                // add
                aluOperation := 0.U
              }
              is("b0100000".U) {
                // Sub
                aluOperation := 1.U

              }
            }
          }
          is(1.U){
            // SLL shift left logical requires only 5 bits values
            io.rs2Execute := xregs.io.RegRs2Data(4,0)
            aluOperation := 2.U
          }
          is(2.U){
            // SLT set less than
            aluOperation := 8.U
          }
          is(3.U){
            // Set less than for Unsigned number
            aluOperation := 9.U
          }
          is(4.U)
          { // XOR Gate
            aluOperation := 7.U
          }
          is(5.U){
            // SRL and SRA
            io.rs2Execute := xregs.io.RegRs2Data(4,0)
            switch(func7){
              is(0.U){
                // SRL
                aluOperation := 3.U

              }
              is("h20".U){
                aluOperation := 4.U
              }
            }
          }
          is(6.U){
            // OR operation
            aluOperation := 6.U
          }
          is(7.U){
            // AND Operation Bitwise
            aluOperation := 7.U
          }


        }


      }
    }


  //}
 //   .otherwise{
      // Do nothing
//    }

}
