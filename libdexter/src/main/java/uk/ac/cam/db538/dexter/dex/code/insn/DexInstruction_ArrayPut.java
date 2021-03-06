package uk.ac.cam.db538.dexter.dex.code.insn;

import java.util.Set;

import lombok.Getter;
import lombok.val;

import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.Format.Instruction23x;

import uk.ac.cam.db538.dexter.dex.code.CodeParserState;
import uk.ac.cam.db538.dexter.dex.code.reg.DexRegister;
import uk.ac.cam.db538.dexter.dex.code.reg.DexSingleRegister;
import uk.ac.cam.db538.dexter.dex.code.reg.DexStandardRegister;
import uk.ac.cam.db538.dexter.dex.type.DexClassType;
import uk.ac.cam.db538.dexter.hierarchy.RuntimeHierarchy;

import com.google.common.collect.Sets;

public class DexInstruction_ArrayPut extends DexInstruction {

    @Getter private final DexRegister regFrom;
    @Getter private final DexSingleRegister regArray;
    @Getter private final DexSingleRegister regIndex;
    @Getter private final Opcode_GetPut opcode;

    public DexInstruction_ArrayPut(DexRegister from, DexSingleRegister array, DexSingleRegister index, Opcode_GetPut opcode, RuntimeHierarchy hierarchy) {
        super(hierarchy);

        this.regFrom = from;
        this.regArray = array;
        this.regIndex = index;
        this.opcode = opcode;

        Opcode_GetPut.checkRegisterWidth(regFrom, this.opcode);
    }

    public static DexInstruction_ArrayPut parse(Instruction insn, CodeParserState parsingState) {
        val opcode = Opcode_GetPut.convert_APUT(insn.opcode);
        if (insn instanceof Instruction23x && opcode != null) {

            val insnArrayGet = (Instruction23x) insn;

            DexStandardRegister regFrom;
            if (opcode == Opcode_GetPut.Wide)
                regFrom = parsingState.getWideRegister(insnArrayGet.getRegisterA());
            else
                regFrom = parsingState.getSingleRegister(insnArrayGet.getRegisterA());

            return new DexInstruction_ArrayPut(
                       regFrom,
                       parsingState.getSingleRegister(insnArrayGet.getRegisterB()),
                       parsingState.getSingleRegister(insnArrayGet.getRegisterC()),
                       opcode,
                       parsingState.getHierarchy());

        } else
            throw FORMAT_EXCEPTION;
    }

    @Override
    public String toString() {
        return "aput" + opcode.getAsmSuffix() + " " + regFrom.toString() + ", {" + regArray.toString() + "}[" + regIndex.toString() + "]";
    }

    @Override
    public Set<? extends DexRegister> lvaReferencedRegisters() {
        return Sets.newHashSet(regFrom, regArray, regIndex);
    }

    @Override
    public void accept(DexInstructionVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    protected DexClassType[] throwsExceptions() {
        if (opcode == Opcode_GetPut.Object)
            return this.hierarchy.getTypeCache().LIST_Error_Null_ArrayIndex_ArrayStore;
        else
            return this.hierarchy.getTypeCache().LIST_Error_Null_ArrayIndexOutOfBounds;
    }

}
