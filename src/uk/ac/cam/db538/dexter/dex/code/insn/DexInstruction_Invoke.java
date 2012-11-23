package uk.ac.cam.db538.dexter.dex.code.insn;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.val;

import org.jf.dexlib.MethodIdItem;
import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.Format.Instruction35c;
import org.jf.dexlib.Code.Format.Instruction3rc;

import uk.ac.cam.db538.dexter.dex.DexAssemblingCache;
import uk.ac.cam.db538.dexter.dex.code.DexCode;
import uk.ac.cam.db538.dexter.dex.code.DexCode_ParsingState;
import uk.ac.cam.db538.dexter.dex.code.DexRegister;
import uk.ac.cam.db538.dexter.dex.method.DexPrototype;
import uk.ac.cam.db538.dexter.dex.type.DexClassType;

public class DexInstruction_Invoke extends DexInstruction {

  @Getter private final DexClassType ClassType;
  @Getter private final String MethodName;
  @Getter private final DexPrototype MethodPrototype;
  @Getter private final List<DexRegister> ArgumentRegisters;
  @Getter private final Opcode_Invoke CallType;

  public DexInstruction_Invoke(DexCode methodCode, DexClassType classType, String methodName, DexPrototype prototype, List<DexRegister> argumentRegisters, Opcode_Invoke callType) {
    super(methodCode);
    ClassType = classType;
    MethodName = methodName;
    MethodPrototype = prototype;
    ArgumentRegisters = argumentRegisters;
    CallType = callType;

    checkArguments();
  }

  public DexInstruction_Invoke(DexCode methodCode, Instruction insn, DexCode_ParsingState parsingState) {
    super(methodCode);

    val cache = parsingState.getCache();

    MethodIdItem methodInfo;
    ArgumentRegisters = new LinkedList<DexRegister>();

    if (insn instanceof Instruction35c && Opcode_Invoke.convert(insn.opcode) != null) {

      val insnInvoke = (Instruction35c) insn;
      methodInfo = (MethodIdItem) insnInvoke.getReferencedItem();

      switch (insnInvoke.getRegCount()) {
      case 5:
        ArgumentRegisters.add(0, parsingState.getRegister(insnInvoke.getRegisterA()));
      case 4:
        ArgumentRegisters.add(0, parsingState.getRegister(insnInvoke.getRegisterG()));
      case 3:
        ArgumentRegisters.add(0, parsingState.getRegister(insnInvoke.getRegisterF()));
      case 2:
        ArgumentRegisters.add(0, parsingState.getRegister(insnInvoke.getRegisterE()));
      case 1:
        ArgumentRegisters.add(0, parsingState.getRegister(insnInvoke.getRegisterD()));
      case 0:
        break;
      default:
        throw new InstructionParsingException("Unexpected number of method argument registers");
      }

    } else if (insn instanceof Instruction3rc && Opcode_Invoke.convert(insn.opcode) != null) {

      val insnInvokeRange = (Instruction3rc) insn;
      methodInfo = (MethodIdItem) insnInvokeRange.getReferencedItem();

      val startRegister = insnInvokeRange.getStartRegister();
      for (int i = 0; i < insnInvokeRange.getRegCount(); ++i)
        ArgumentRegisters.add(parsingState.getRegister(startRegister + i));

    } else
      throw new InstructionParsingException("Unknown instruction format or opcode");

    ClassType = parsingState.getCache().getClassType(methodInfo.getContainingClass().getTypeDescriptor());

    MethodName = methodInfo.getMethodName().getStringValue();
    MethodPrototype = new DexPrototype(methodInfo.getPrototype(), cache);

    CallType = Opcode_Invoke.convert(insn.opcode);

    checkArguments();
  }

  private void checkArguments() {
    // check that the number of registers is correct
    int expectedRegisterCount = (CallType == Opcode_Invoke.Static) ? 0 : 1;
    for (val argType : MethodPrototype.getArgumentTypes())
      expectedRegisterCount += argType.getRegisters();
    if (expectedRegisterCount != ArgumentRegisters.size())
      throw new InstructionArgumentException("Wrong number of arguments given to a method call");
  }

  @Override
  public String getOriginalAssembly() {
    val str = new StringBuilder();
    str.append("invoke-");
    str.append(CallType.name().toLowerCase());
    str.append(" ");
    str.append(ClassType.getPrettyName());
    str.append(".");
    str.append(MethodName);

    if (CallType == Opcode_Invoke.Static) {

      str.append("(");
      boolean first = true;
      for (val reg : ArgumentRegisters) {
        if (first) first = false;
        else str.append(", ");
        str.append("v" + reg.getId());
      }
      str.append(")");

    } else {
      str.append("{");

      boolean first = true;
      boolean second = false;
      for (val reg : ArgumentRegisters) {
        if (second) second = false;
        else if (!first) str.append(", ");

        str.append("v" + reg.getId());

        if (first) {
          first = false;
          second = true;
          str.append("}(");
        }
      }

      str.append(")");
    }

    return str.toString();
  }

  @Override
  public Instruction[] assembleBytecode(Map<DexRegister, Integer> regAlloc, DexAssemblingCache cache) {
    int[] r = new int[ArgumentRegisters.size()];
    for (int i = 0; i < r.length; ++i)
      r[i] = regAlloc.get(ArgumentRegisters.get(i));

    val methodItem = cache.getMethod(ClassType, MethodPrototype, MethodName);

    if (r.length <= 5) {
      for (int regNum : r)
        if (!fitsIntoBits_Unsigned(regNum, 4))
          return throwCannotAssembleException();

      return new Instruction[] {
               new Instruction35c(Opcode_Invoke.convertStandard(CallType),
                                  r.length,
                                  (byte) ((r.length >= 1) ? r[0] : 0),
                                  (byte) ((r.length >= 2) ? r[1] : 0),
                                  (byte) ((r.length >= 3) ? r[2] : 0),
                                  (byte) ((r.length >= 4) ? r[3] : 0),
                                  (byte) ((r.length >= 5) ? r[4] : 0),
                                  methodItem)
             };
    } else {
      return null;
    }

  }

}
