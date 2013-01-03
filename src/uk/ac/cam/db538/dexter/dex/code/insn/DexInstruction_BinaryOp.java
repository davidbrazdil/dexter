package uk.ac.cam.db538.dexter.dex.code.insn;

import java.util.Map;
import java.util.Set;

import lombok.Getter;
import lombok.val;

import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.Format.Instruction12x;
import org.jf.dexlib.Code.Format.Instruction23x;

import uk.ac.cam.db538.dexter.analysis.coloring.ColorRange;
import uk.ac.cam.db538.dexter.dex.code.DexCode;
import uk.ac.cam.db538.dexter.dex.code.DexCode_AssemblingState;
import uk.ac.cam.db538.dexter.dex.code.DexCode_InstrumentationState;
import uk.ac.cam.db538.dexter.dex.code.DexCode_ParsingState;
import uk.ac.cam.db538.dexter.dex.code.DexRegister;
import uk.ac.cam.db538.dexter.dex.code.elem.DexCodeElement;

public class DexInstruction_BinaryOp extends DexInstruction {

  // CAREFUL: produce /addr2 instructions if target and first
  // registers are equal; for commutative instructions,
  // check the second as well

  @Getter private final DexRegister regTarget;
  @Getter private final DexRegister regSourceA;
  @Getter private final DexRegister regSourceB;
  @Getter private final Opcode_BinaryOp insnOpcode;

  public DexInstruction_BinaryOp(DexCode methodCode, DexRegister target, DexRegister sourceA, DexRegister sourceB, Opcode_BinaryOp opcode) {
    super(methodCode);

    regTarget = target;
    regSourceA = sourceA;
    regSourceB = sourceB;
    insnOpcode = opcode;
  }

  public DexInstruction_BinaryOp(DexCode methodCode, Instruction insn, DexCode_ParsingState parsingState) throws InstructionParsingException {
    super(methodCode);

    int regA, regB, regC;

    if (insn instanceof Instruction23x && Opcode_BinaryOp.convert(insn.opcode) != null) {

      val insnBinaryOp = (Instruction23x) insn;
      regA = insnBinaryOp.getRegisterA();
      regB = insnBinaryOp.getRegisterB();
      regC = insnBinaryOp.getRegisterC();

    } else if (insn instanceof Instruction12x && Opcode_BinaryOp.convert(insn.opcode) != null) {

      val insnBinaryOp2addr = (Instruction12x) insn;
      regA = regB = insnBinaryOp2addr.getRegisterA();
      regC = insnBinaryOp2addr.getRegisterB();

    } else
      throw FORMAT_EXCEPTION;

    regTarget = parsingState.getRegister(regA);
    regSourceA = parsingState.getRegister(regB);
    regSourceB = parsingState.getRegister(regC);
    insnOpcode = Opcode_BinaryOp.convert(insn.opcode);
  }

  @Override
  public String getOriginalAssembly() {
    return insnOpcode.getAssemblyName() + " v" + regTarget.getOriginalIndexString() +
           ", v" + regSourceA.getOriginalIndexString() + ", v" + regSourceB.getOriginalIndexString();
  }

  @Override
  public void instrument(DexCode_InstrumentationState mapping) {
    getMethodCode().replace(this,
                            new DexCodeElement[] {
                              this,
                              new DexInstruction_BinaryOp(
                                this.getMethodCode(),
                                mapping.getTaintRegister(regTarget),
                                mapping.getTaintRegister(regSourceA),
                                mapping.getTaintRegister(regSourceB),
                                Opcode_BinaryOp.OrInt)
                            });
  }

  @Override
  public Instruction[] assembleBytecode(DexCode_AssemblingState state) {
    val regAlloc = state.getRegisterAllocation();
    int rTarget = regAlloc.get(regTarget);
    int rSourceA = regAlloc.get(regSourceA);
    int rSourceB = regAlloc.get(regSourceB);

    if (rTarget == rSourceA && fitsIntoBits_Unsigned(rTarget, 4) && fitsIntoBits_Unsigned(rSourceB, 4))
      return new Instruction[] { new Instruction12x(Opcode_BinaryOp.convert2addr(insnOpcode), (byte) rTarget, (byte) rSourceB) };
    else if (fitsIntoBits_Unsigned(rTarget, 8) && fitsIntoBits_Unsigned(rSourceA, 8) && fitsIntoBits_Unsigned(rSourceB, 8))
      return new Instruction[] { new Instruction23x(Opcode_BinaryOp.convert(insnOpcode), (short) rTarget, (short) rSourceA, (short) rSourceB)	};
    else
      return throwNoSuitableFormatFound();
  }

  @Override
  public Set<DexRegister> lvaDefinedRegisters() {
    return createSet(regTarget);
  }

  @Override
  public Set<DexRegister> lvaReferencedRegisters() {
    return createSet(regSourceA, regSourceB);
  }

  @Override
  public Set<GcRangeConstraint> gcRangeConstraints() {
    return createSet(
             new GcRangeConstraint(regTarget, ColorRange.RANGE_8BIT),
             new GcRangeConstraint(regSourceA, ColorRange.RANGE_8BIT),
             new GcRangeConstraint(regSourceB, ColorRange.RANGE_8BIT));
  }

  @Override
  protected DexCodeElement gcReplaceWithTemporaries(Map<DexRegister, DexRegister> mapping) {
    return new DexInstruction_BinaryOp(
             getMethodCode(),
             mapping.get(regTarget),
             mapping.get(regSourceA),
             mapping.get(regSourceB),
             insnOpcode);
  }
}
