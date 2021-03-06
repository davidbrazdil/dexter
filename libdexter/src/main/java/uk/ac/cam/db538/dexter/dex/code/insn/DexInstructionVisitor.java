package uk.ac.cam.db538.dexter.dex.code.insn;


public interface DexInstructionVisitor {

    void visit(DexInstruction_Move instruction);

    void visit(DexInstruction_MoveResult instruction);

    void visit(DexInstruction_MoveException instruction);

    void visit(DexInstruction_ReturnVoid instruction);

    void visit(DexInstruction_Return instruction);

    void visit(DexInstruction_Const instruction);

    void visit(DexInstruction_ConstString instruction);

    void visit(DexInstruction_ConstClass instruction);

    void visit(DexInstruction_Monitor instruction);

    void visit(DexInstruction_CheckCast instruction);

    void visit(DexInstruction_InstanceOf instruction);

    void visit(DexInstruction_ArrayLength instruction);

    void visit(DexInstruction_NewInstance instruction);

    void visit(DexInstruction_NewArray instruction);

    void visit(DexInstruction_FilledNewArray instruction);

    void visit(DexInstruction_FillArrayData instruction);

    void visit(DexInstruction_Throw instruction);

    void visit(DexInstruction_Goto instruction);

    void visit(DexInstruction_Switch instruction);

    void visit(DexInstruction_Compare instruction);

    void visit(DexInstruction_IfTest instruction);

    void visit(DexInstruction_IfTestZero instruction);

    void visit(DexInstruction_ArrayGet instruction);

    void visit(DexInstruction_ArrayPut instruction);

    void visit(DexInstruction_InstanceGet instruction);

    void visit(DexInstruction_InstancePut instruction);

    void visit(DexInstruction_StaticGet instruction);

    void visit(DexInstruction_StaticPut instruction);

    void visit(DexInstruction_Invoke instruction);

    void visit(DexInstruction_UnaryOp instruction);

    void visit(DexInstruction_Convert instruction);

    void visit(DexInstruction_BinaryOp instruction);

    void visit(DexInstruction_BinaryOpLiteral instruction);

    void visit(DexInstruction_Unknown instruction);
}
