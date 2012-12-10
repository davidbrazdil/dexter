package uk.ac.cam.db538.dexter.dex.code.insn;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import lombok.Getter;
import lombok.val;

import org.jf.dexlib.FieldIdItem;
import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.Opcode;
import org.jf.dexlib.Code.Format.Instruction22c;

import uk.ac.cam.db538.dexter.analysis.coloring.ColorRange;
import uk.ac.cam.db538.dexter.dex.DexField;
import uk.ac.cam.db538.dexter.dex.code.DexCode;
import uk.ac.cam.db538.dexter.dex.code.DexCodeElement;
import uk.ac.cam.db538.dexter.dex.code.DexCode_AssemblingState;
import uk.ac.cam.db538.dexter.dex.code.DexCode_ParsingState;
import uk.ac.cam.db538.dexter.dex.code.DexRegister;
import uk.ac.cam.db538.dexter.dex.type.DexClassType;
import uk.ac.cam.db538.dexter.dex.type.DexRegisterType;
import uk.ac.cam.db538.dexter.dex.type.UnknownTypeException;

public class DexInstruction_InstancePutWide extends DexInstruction {

  @Getter private final DexRegister regFrom1;
  @Getter private final DexRegister regFrom2;
  @Getter private final DexRegister regObject;
  @Getter private final DexClassType fieldClass;
  @Getter private final DexRegisterType fieldType;
  @Getter private final String fieldName;

  public DexInstruction_InstancePutWide(DexCode methodCode, DexRegister from1, DexRegister from2, DexRegister obj, DexClassType fieldClass, DexRegisterType fieldType, String fieldName) {
    super(methodCode);

    this.regFrom1 = from1;
    this.regFrom2 = from2;
    this.regObject = obj;
    this.fieldClass = fieldClass;
    this.fieldType = fieldType;
    this.fieldName = fieldName;

    Opcode_GetPutWide.checkTypeIsWide(this.fieldType);
  }

  public DexInstruction_InstancePutWide(DexCode methodCode, DexRegister from1, DexRegister from2, DexRegister obj, DexField field) {
    super(methodCode);

    if (field.isStatic())
      throw new InstructionArgumentException("Expected instance field");

    this.regFrom1 = from1;
    this.regFrom2 = from2;
    this.regObject = obj;
    this.fieldClass = field.getParentClass().getType();
    this.fieldType = field.getType();
    this.fieldName = field.getName();

    Opcode_GetPutWide.checkTypeIsWide(this.fieldType);
  }

  public DexInstruction_InstancePutWide(DexCode methodCode, Instruction insn, DexCode_ParsingState parsingState) throws InstructionParsingException, UnknownTypeException {
    super(methodCode);

    if (insn instanceof Instruction22c && insn.opcode == Opcode.IPUT_WIDE) {

      val insnStaticPut = (Instruction22c) insn;
      val refItem = (FieldIdItem) insnStaticPut.getReferencedItem();
      regFrom1 = parsingState.getRegister(insnStaticPut.getRegisterA());
      regFrom2 = parsingState.getRegister(insnStaticPut.getRegisterA() + 1);
      regObject = parsingState.getRegister(insnStaticPut.getRegisterB());
      fieldClass = DexClassType.parse(
                     refItem.getContainingClass().getTypeDescriptor(),
                     parsingState.getCache());
      fieldType = DexRegisterType.parse(
                    refItem.getFieldType().getTypeDescriptor(),
                    parsingState.getCache());
      fieldName = refItem.getFieldName().getStringValue();

    } else
      throw new InstructionParsingException("Unknown instruction format or opcode");

    Opcode_GetPutWide.checkTypeIsWide(this.fieldType);
  }

  @Override
  public String getOriginalAssembly() {
    return "iput-wide v" + regFrom1.getOriginalIndexString() + ", {v" + regObject.getOriginalIndexString() + "}" + fieldClass.getPrettyName() + "." + fieldName;
  }

  @Override
  public Set<DexRegister> lvaReferencedRegisters() {
    val referencedRegs = new HashSet<DexRegister>();
    referencedRegs.add(regFrom1);
    referencedRegs.add(regFrom2);
    referencedRegs.add(regObject);
    return referencedRegs;
  }

  @Override
  public Set<GcFollowConstraint> gcFollowConstraints() {
    val constraints = new HashSet<GcFollowConstraint>();
    constraints.add(new GcFollowConstraint(regFrom1, regFrom2));
    return constraints;
  }

  @Override
  public Set<GcRangeConstraint> gcRangeConstraints() {
    val constraints = new HashSet<GcRangeConstraint>();
    constraints.add(new GcRangeConstraint(regFrom1, ColorRange.RANGE_4BIT));
    constraints.add(new GcRangeConstraint(regObject, ColorRange.RANGE_4BIT));
    return constraints;
  }

  @Override
  protected DexCodeElement gcReplaceWithTemporaries(Map<DexRegister, DexRegister> mapping) {
    return new DexInstruction_InstancePutWide(getMethodCode(), mapping.get(regFrom1), mapping.get(regFrom2), mapping.get(regObject), fieldClass, fieldType, fieldName);
  }

  @Override
  public Instruction[] assembleBytecode(DexCode_AssemblingState state) {
    val regAlloc = state.getRegisterAllocation();
    int rTo1 = regAlloc.get(regFrom1);
    int rTo2 = regAlloc.get(regFrom2);
    int rObject = regAlloc.get(regObject);

    if (!formWideRegister(rTo1, rTo2))
      return throwWideRegistersExpected();

    if (fitsIntoBits_Unsigned(rTo1, 4) && fitsIntoBits_Unsigned(rObject, 4)) {
      return new Instruction[] {
               new Instruction22c(Opcode.IPUT_WIDE, (byte) rTo1, (byte) rObject, state.getCache().getField(fieldClass, fieldType, fieldName))
             };
    } else
      return throwNoSuitableFormatFound();
  }
}