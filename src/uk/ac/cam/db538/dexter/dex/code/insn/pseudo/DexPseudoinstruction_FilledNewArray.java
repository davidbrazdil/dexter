package uk.ac.cam.db538.dexter.dex.code.insn.pseudo;

import java.util.LinkedList;
import java.util.List;

import lombok.Getter;
import lombok.val;
import uk.ac.cam.db538.dexter.dex.code.DexCode;
import uk.ac.cam.db538.dexter.dex.code.DexCode_InstrumentationState;
import uk.ac.cam.db538.dexter.dex.code.DexRegister;
import uk.ac.cam.db538.dexter.dex.code.elem.DexCodeElement;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_BinaryOp;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_Const;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_FilledNewArray;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_MoveResult;
import uk.ac.cam.db538.dexter.dex.code.insn.Opcode_BinaryOp;
import uk.ac.cam.db538.dexter.dex.type.DexPrimitiveType;

public class DexPseudoinstruction_FilledNewArray extends DexPseudoinstruction {

  @Getter private final DexInstruction_FilledNewArray instructionFilledNewArray;
  @Getter private final DexInstruction_MoveResult instructionMoveResult;

  public DexPseudoinstruction_FilledNewArray(DexCode methodCode, DexInstruction_FilledNewArray instructionFilledNewArray, DexInstruction_MoveResult insnMoveResult) {
    super(methodCode);

    this.instructionFilledNewArray = instructionFilledNewArray;
    this.instructionMoveResult = insnMoveResult;
  }

  @Override
  public List<DexCodeElement> unwrap() {
    return createList(
             (DexCodeElement) instructionFilledNewArray,
             (DexCodeElement) instructionMoveResult);
  }

  @Override
  public void instrument(DexCode_InstrumentationState state) {
    val code = getMethodCode();
    val replacement = new LinkedList<DexCodeElement>();

    val regCombinedTaint = new DexRegister();
    replacement.add(new DexInstruction_Const(code, regCombinedTaint, 0));
    if (instructionFilledNewArray.getArrayType().getElementType() instanceof DexPrimitiveType) {
      for (val regArg : instructionFilledNewArray.getArgumentRegisters())
        replacement.add(new DexInstruction_BinaryOp(code, regCombinedTaint, regCombinedTaint, state.getTaintRegister(regArg), Opcode_BinaryOp.OrInt));
    } else {
      val regObjectTaint = new DexRegister();
      for (val regArg : instructionFilledNewArray.getArgumentRegisters()) {
        replacement.add(new DexPseudoinstruction_GetObjectTaint(code, regObjectTaint, regArg));
        replacement.add(new DexInstruction_BinaryOp(code, regCombinedTaint, regCombinedTaint, regObjectTaint, Opcode_BinaryOp.OrInt));
      }
    }

    replacement.add(this);
    replacement.add(new DexPseudoinstruction_SetObjectTaint(code, instructionMoveResult.getRegTo(), regCombinedTaint));

    code.replace(this, replacement);
  }
}