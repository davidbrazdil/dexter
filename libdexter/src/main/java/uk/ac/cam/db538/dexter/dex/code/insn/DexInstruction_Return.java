package uk.ac.cam.db538.dexter.dex.code.insn;

import java.util.Collections;
import java.util.Set;

import lombok.Getter;
import lombok.val;

import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.Opcode;
import org.jf.dexlib.Code.Format.Instruction11x;

import uk.ac.cam.db538.dexter.dex.code.CodeParserState;
import uk.ac.cam.db538.dexter.dex.code.InstructionList;
import uk.ac.cam.db538.dexter.dex.code.elem.DexCodeElement;
import uk.ac.cam.db538.dexter.dex.code.reg.DexRegister;
import uk.ac.cam.db538.dexter.dex.code.reg.DexSingleRegister;
import uk.ac.cam.db538.dexter.dex.code.reg.DexWideRegister;
import uk.ac.cam.db538.dexter.dex.code.reg.RegisterType;
import uk.ac.cam.db538.dexter.hierarchy.RuntimeHierarchy;

import com.google.common.collect.Sets;

public class DexInstruction_Return extends DexInstruction {

    @Getter private final DexRegister regFrom;
    @Getter private final RegisterType opcode;

    public DexInstruction_Return(DexSingleRegister regFrom, boolean objectMoving, RuntimeHierarchy hierarchy) {
        super(hierarchy);

        this.regFrom = regFrom;
        this.opcode = objectMoving ? RegisterType.REFERENCE : RegisterType.SINGLE_PRIMITIVE;
    }

    public DexInstruction_Return(DexWideRegister regFrom, RuntimeHierarchy hierarchy) {
        super(hierarchy);

        this.regFrom = regFrom;
        this.opcode = RegisterType.WIDE_PRIMITIVE;
    }

    public static DexInstruction_Return parse(Instruction insn, CodeParserState parsingState) {
        if (insn instanceof Instruction11x &&
                (insn.opcode == Opcode.RETURN || insn.opcode == Opcode.RETURN_WIDE || insn.opcode == Opcode.RETURN_OBJECT)) {

            val insnReturn = (Instruction11x) insn;
            val opcode = RegisterType.fromOpcode(insn.opcode);
            if (opcode == RegisterType.WIDE_PRIMITIVE)
                return new DexInstruction_Return(
                           parsingState.getWideRegister(insnReturn.getRegisterA()),
                           parsingState.getHierarchy());
            else
                return new DexInstruction_Return(
                           parsingState.getSingleRegister(insnReturn.getRegisterA()),
                           opcode == RegisterType.REFERENCE,
                           parsingState.getHierarchy());

        } else
            throw FORMAT_EXCEPTION;
    }

    @Override
    public String toString() {
        return "return" + opcode.getAsmSuffix() + " " + regFrom.toString();
    }

    @Override
    public boolean cfgEndsBasicBlock() {
        return true;
    }

    @Override
    public Set<? extends DexRegister> lvaReferencedRegisters() {
        return Sets.newHashSet(regFrom);
    }

    @Override
    public Set<? extends DexCodeElement> cfgJumpTargets(InstructionList code) {
        return Collections.emptySet();
    }

    @Override
    public void accept(DexInstructionVisitor visitor) {
        visitor.visit(this);
    }
}
