package org.vricsa
import chisel3._
import chisel3.util._
import firrtl.Utils.True

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
   // val aluOpcode = Output(UInt(5.W))
    val aluOpcode = Output(ALUOperation())
    val wrDataRAM = Output(UInt(32.W))
    val brRelativeAddress = Output(SInt(32.W))
    val pcValue = Input(UInt(32.W))


    // Additional Control unit

    val isDataRAMWrite = Output(Bits(1.W))
    val isDataRAMRead = Output(Bits(1.W))
    val isXregwr = Output(Bits(1.W))
    val ramDataType = Output(WordType())
    val isBrancInst = Output(Bits(1.W))
    val isJumpInst = Output(Bits(1.W))


  })
 // at reset value
  io.rs2Execute := 0.U
  io.rs1Execute := 0.U
  io.wrDataRAM := 0.U
  io.ramDataType := WordType.wWord

  io.rs2Data := 0.U
  io.rs1Data := 0.U
  io.isDataRAMWrite := 0.B
  io.isDataRAMRead := 0.B
  io.isXregwr := 0.B
  io.isBrancInst := 0.B
  io.isJumpInst := false.B
  io.brRelativeAddress := 0.S

  val ItypeImmediateData = WireInit(0.U(12.W))
  val brtypeImmediateData = WireInit(0.U(13.W))
  val StypeImmediateData = WireInit(0.U(12.W))
  //val regRd = WireInit(0.U(5.W))
  val regRs1 = WireInit(0.U(5.W))
  val regRs2 = WireInit(0.U(5.W))
  val regWr = WireInit(0.U(5.W))
  val func3 = WireInit(0.U(3.W))
  val func7 = WireInit(0.U(7.W))
  //val opcode = WireInit(0.U(5.W))
 // val opcode = WireInit(Opcodes.none)
  val tInstruction = WireInit(0.U(32.W))
  //val aluOperation = WireInit(7.U(5.W))
  //val aluOperation = WireInit(ALUOperation())
 val  aluOperation = Wire(ALUOperation())

  val ctlRead = WireInit(0.U(1.W))
  val ctlWrite = WireInit(0.U(1.W))
  aluOperation := ALUOperation.ADD
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



  tInstruction := io.InstructionData

  ////
 // when(io.en===1.U){


  val (opcode,valid) = Opcodes.safe(tInstruction(6, 2))
  // Need to assert it if fault
  assert(valid, "Enum state must be valid, got %d!", tInstruction(6, 2))
    func3 := tInstruction(14, 12)
    func7 := tInstruction(31, 25) // last 7 bits

   // regRd := tInstruction(11, 7)
    ItypeImmediateData := tInstruction(31,20)
    brtypeImmediateData := Cat(tInstruction(31),tInstruction(7),tInstruction(30,25),tInstruction(11,8),0.B)
    StypeImmediateData := Cat(tInstruction(31,25),tInstruction(11,7))

    regRs1 := tInstruction(19, 15)
    regRs2 := tInstruction(24,20)
    regWr := tInstruction(11,7)
    io.isDataRAMRead := 0.B
      io.isDataRAMRead := 0.B
   // printf(p" \n The opcode is $opcode and instdata is ${tInstruction(6,0)}")
    switch(opcode){
      // Reset all the control reg and update it later
      /*
      io.isBrancInst := false.B
      io.isDataRAMRead := false.B
      io.isDataRAMWrite := false.B
      io.isXregwr := false.B
*/
     // is(0.U){
      is(Opcodes.load){
        // Required to add immediate and rs1 value to find the location
        // so RS1 = xreg(rs1)
        // RS2 = immediate value
        // set ALU mode = addition
        io.rs1Execute := xregs.io.RegRs1Data
        io.rs2Execute := ItypeImmediateData
        //io.aluOpcode := 0.U
        aluOperation := ALUOperation.ADD
        xregs.io.RegWR := regWr
        //io.isXregwr := 1.U
        io.isDataRAMRead := 1.B
        ctlRead := 1.U
        ctlWrite := 1.U // This may lead to conflict
        io.isXregwr := false.B
        io.isBrancInst := false.B
        io.isJumpInst := false.B

        switch(func3){
          is(0.U){      io.ramDataType:= WordType.wByte   }
          is(1.U){      io.ramDataType := WordType.wHalfWord    }
          is(2.U){       io.ramDataType := WordType.wWord   }
          is(4.U){          }
          is(5.U){          }

        }
       // printf(p"\n reached for load instruction and ${io.rs2Execute}")
      } //End of Load statement
      //is(8.U){
        is(Opcodes.store){
        // Store copy from rs2 to Memory
        // Check for function
        io.rs1Execute := xregs.io.RegRs1Data
        io.rs2Execute := StypeImmediateData//Cat(ItypeImmediateData,regWr)
        //io.aluOpcode := 0.U
        aluOperation := ALUOperation.ADD //0.U
        //xregs.io.RegWR := regRs2
        io.wrDataRAM := xregs.io.RegRs2Data
          io.isXregwr := false.B
        io.isDataRAMWrite := true.B
          io.isJumpInst := false.B
        ctlRead := 1.U

        switch(func3) {
          is(0.U) {
            io.ramDataType := WordType.wByte
          }
          is(1.U) {
            io.ramDataType := WordType.wHalfWord
          }
          is(2.U) {
            io.ramDataType := WordType.wWord
          }


        }
      }
      // Imm - Ops
      //is(4.U){
      is(Opcodes.imm){
        io.rs1Execute := xregs.io.RegRs1Data
        io.rs2Execute := ItypeImmediateData
        ctlRead := 1.U
        ctlWrite := 1.U
        io.isBrancInst := false.B
        io.isJumpInst := false.B
        io.isXregwr := true.B


        switch(func3){
          is(0.U){
            // ADDI instruction
            aluOperation := ALUOperation.ADD //0.U
            printf("Reached ADDI instr \n ")
          }
          is(2.U){
            // SLTI set  Less than Immediate
            aluOperation := ALUOperation.LessThanS //8.U

          }
          is(3.U){
            // SLTIU set  Less than Immediate Unsigned
            aluOperation := ALUOperation.LessThanU //9.U
          }
          is(4.U)
          {
            // XorI
            aluOperation := ALUOperation.XOR //7.U
          }
          is(6.U){
            // ORI
            aluOperation := ALUOperation.OR //6.U
          }
          is(7.U){
            // AND I
            aluOperation := ALUOperation.AND //5.U
          }
          is(1.U){
            // SLLI
            io.rs2Execute := ItypeImmediateData(5,0)
            aluOperation := ALUOperation.SHIFTLEFT //2.U
          }
          is(5.U){
            // need to use function  7 to determine the data
            io.rs2Execute := ItypeImmediateData(5,0)
            switch(func7){
              is(0.U){
                // SRLI
                aluOperation := ALUOperation.SHIFTRIGHT//3.U
              }
              is("h20".U){
                // SRAI
                aluOperation := ALUOperation.SHIFTRIGHTArthi //4.U
              }
            }
          }
        }

      }

      // OP of type R require to extract RD,RS1 and RS2, func3 and func7 decides

      //is("b01100".U){
      is(Opcodes.reg){
        // Both data from the Xregs
        io.isBrancInst := false.B
        io.isJumpInst := false.B
        io.rs1Execute := xregs.io.RegRs1Data
        io.rs2Execute := xregs.io.RegRs2Data
        xregs.io.RegWR := regWr
        ctlRead := 1.U
        ctlWrite := 1.U
        //io.isXregwr := true.B

        switch(func3){
          is(0.U){
            // ADD and SUB
            switch(func7) {
              is(0.U) {
                // add
                aluOperation := ALUOperation.ADD //0.U
              }
              is("b0100000".U) {
                // Sub
                aluOperation := ALUOperation.SUB //1.U

              }
            }
          }
          is(1.U){
            // SLL shift left logical requires only 5 bits values
            io.rs2Execute := xregs.io.RegRs2Data(4,0)
            aluOperation := ALUOperation.SHIFTLEFT //2.U
          }
          is(2.U){
            // SLT set less than
            aluOperation := ALUOperation.LessThanS//8.U
          }
          is(3.U){
            // Set less than for Unsigned number
            aluOperation := ALUOperation.LessThanU //9.U
          }
          is(4.U)
          { // XOR Gate
            aluOperation := ALUOperation.XOR //7.U
          }
          is(5.U){
            // SRL and SRA
            io.rs2Execute := xregs.io.RegRs2Data(4,0)
            switch(func7){
              is(0.U){
                // SRL
                aluOperation := ALUOperation.SHIFTRIGHT //3.U

              }
              is("h20".U){
                aluOperation := ALUOperation.SHIFTRIGHTArthi //4.U
              }
            }
          }
          is(6.U){
            // OR operation
            aluOperation := ALUOperation.OR //6.U
          }
          is(7.U){
            // AND Operation Bitwise
            aluOperation := ALUOperation.AND //7.U
          }


        }


      }
       /// For Branch Decoding
       is(Opcodes.br)
       {
         io.isBrancInst := true.B
         io.isDataRAMRead := false.B
         io.isDataRAMWrite := false.B
         io.isXregwr := false.B
         io.isJumpInst := false.B

         io.rs1Execute := xregs.io.RegRs1Data
         io.rs2Execute := xregs.io.RegRs2Data
          io.brRelativeAddress := brtypeImmediateData.asSInt
         ctlRead := 1.U // Read from Xreg
         ctlWrite := 0.U // Not writing in  XREG

         // Set the ALU OPeration based on Function 3
         switch(func3){
           is(0.U)
           {
             aluOperation := ALUOperation.brEQL
           }
           is(1.U)
           {
             aluOperation := ALUOperation.brNEQL
           }
           is(4.U)
           {
             aluOperation := ALUOperation.brLTS
           }
           is(5.U)
           {
             aluOperation := ALUOperation.brGRES
           }
           is(6.U)
           {
             aluOperation := ALUOperation.brLTU
           }
           is(7.U)
           {
             aluOperation := ALUOperation.brGREU
           }
         }


       }

      is(Opcodes.lui)
      {
        // Need to load the immediate 20 bits value to upper side of register leaving zeros at lower bits
        ctlRead := false.B
        io.isBrancInst := false.B
        ctlWrite := true.B
        xregs.io.RegWR := regWr
        io.isDataRAMRead := false.B
        io.isDataRAMWrite := false.B
        io.rs1Execute := tInstruction(31,12) << 12.U
        io.rs2Execute := 0.U


      }
      is(Opcodes.auipc){
        ctlRead := false.B
        io.isBrancInst := false.B
        ctlWrite := true.B
        xregs.io.RegWR := regWr
        io.isDataRAMRead := false.B
        io.isDataRAMWrite := false.B
        io.rs1Execute := tInstruction(31, 12) << 12.U
        io.rs2Execute := io.pcValue - 4.U

      }

      // Jump Instruction
      // Execute this in online simulator
      is(Opcodes.jal){
        val imm = Cat(tInstruction(31),tInstruction(19,12),tInstruction(20),tInstruction(30,21)) << 1.U
        ctlWrite := true.B
        io.isJumpInst := true.B
        xregs.io.RegWR := regWr
        io.wrData := io.pcValue
        io.brRelativeAddress := io.pcValue +imm  //brtypeImmediateData.asSInt


      }
      is(Opcodes.jalr){
        val imm = ItypeImmediateData
        ctlRead := true.B
        ctlWrite := true.B
        io.isJumpInst := true.B
        io.rs1Execute := imm
        io.rs2Execute := xregs.io.RegRs2Data
        aluOperation := ALUOperation.ADD
        xregs.io.RegWR := regWr
        io.wrData := io.pcValue
        io.brRelativeAddress := io.pcValue + imm + xregs.io.RegRs1Data


      }
    }


  //}
 //   .otherwise{
      // Do nothing
//    }

}
