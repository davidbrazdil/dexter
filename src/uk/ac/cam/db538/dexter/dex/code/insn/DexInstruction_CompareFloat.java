package uk.ac.cam.db538.dexter.dex.code.insn;

import java.util.Map;
import java.util.Set;

import lombok.Getter;
import lombok.val;

import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.Opcode;
import org.jf.dexlib.Code.Format.Instruction23x;

import uk.ac.cam.db538.dexter.analysis.coloring.ColorRange;
import uk.ac.cam.db538.dexter.dex.code.DexCode;
import uk.ac.cam.db538.dexter.dex.code.DexCode_AssemblingState;
import uk.ac.cam.db538.dexter.dex.code.DexCode_ParsingState;
import uk.ac.cam.db538.dexter.dex.code.DexRegister;
import uk.ac.cam.db538.dexter.dex.code.elem.DexCodeElement;

public class DexInstruction_CompareFloat extends DexInstruction {

  @Getter private final DexRegister regTarget;
  @Getter private final DexRegister regSourceA;
  @Getter private final DexRegister regSourceB;
  @Getter private final boolean ltBias;

  public DexInstruction_CompareFloat(DexCode methodCode, DexRegister target, DexRegister sourceA, DexRegister sourceB, boolean ltBias) {
    super(methodCode);

    this.regTarget = target;
    this.regSourceA = sourceA;
    this.regSourceB = sourceB;
    this.ltBias = ltBias;
  }

  public DexInstruction_CompareFloat(DexCode methodCode, Instruction insn, DexCode_ParsingState parsingState) throws InstructionParsingException {
    super(methodCode);

    if (insn instanceof Instruction23x &&
        (insn.opcode == Opcode.CMPL_FLOAT || insn.opcode == Opcode.CMPG_FLOAT)) {

      val insnCompare = (Instruction23x) insn;
      this.regTarget = parsingState.getRegister(insnCompare.getRegisterA());
      this.regSourceA = parsingState.getRegister(insnCompare.getRegisterB());
      this.regSourceB = parsingState.getRegister(insnCompare.getRegisterC());
      this.ltBias = insnCompare.opcode == Opcode.CMPL_FLOAT;

    } else
      throw FORMAT_EXCEPTION;
  }

  @Override
  public String getOriginalAssembly() {
    return (ltBias ? "cmpl" : "cmpg") + "-float v" + regTarget.getOriginalIndexString() +
           ", v" + regSourceA.getOriginalIndexString() + ", v" + regSourceB.getOriginalIndexString();
  }

  @Override
  public Instruction[] assembleBytecode(DexCode_AssemblingState state) {
    val regAlloc = state.getRegisterAllocation();
    int rTarget = regAlloc.get(regTarget);
    int rSourceA = regAlloc.get(regSourceA);
    int rSourceB = regAlloc.get(regSourceB);

    if (fitsIntoBits_Unsigned(rTarget, 8) && fitsIntoBits_Unsigned(rSourceA, 8) && fitsIntoBits_Unsigned(rSourceB, 8))
      return new Instruction[] {
               new Instruction23x((ltBias ? Opcode.CMPL_FLOAT : Opcode.CMPG_FLOAT), (short) rTarget, (short) rSourceA, (short) rSourceB)
             };
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
    return new DexInstruction_CompareFloat(
             getMethodCode(),
             mapping.get(regTarget),
             mapping.get(regSourceA),
             mapping.get(regSourceB),
             ltBias);
  }
}
