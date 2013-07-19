package uk.ac.cam.db538.dexter.dex.code.insn;

import java.util.Set;
import org.jf.dexlib.TypeIdItem;
import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.Opcode;
import org.jf.dexlib.Code.Format.Instruction21c;
import uk.ac.cam.db538.dexter.dex.code.CodeParserState;
import uk.ac.cam.db538.dexter.dex.code.reg.DexRegister;
import uk.ac.cam.db538.dexter.dex.code.reg.DexSingleRegister;
import uk.ac.cam.db538.dexter.dex.type.DexClassType;
import uk.ac.cam.db538.dexter.dex.type.DexReferenceType;
import uk.ac.cam.db538.dexter.dex.type.UnknownTypeException;
import uk.ac.cam.db538.dexter.hierarchy.RuntimeHierarchy;
import com.google.common.collect.Sets;

public class DexInstruction_ConstClass extends DexInstruction {
	private final DexSingleRegister regTo;
	private final DexReferenceType value;
	
	public DexInstruction_ConstClass(DexSingleRegister to, DexReferenceType value, RuntimeHierarchy hierarchy) {
		super(hierarchy);
		this.regTo = to;
		this.value = value;
	}
	
	public static DexInstruction_ConstClass parse(Instruction insn, CodeParserState parsingState) throws InstructionParseError, UnknownTypeException {
		if (insn instanceof Instruction21c && insn.opcode == Opcode.CONST_CLASS) {
			final uk.ac.cam.db538.dexter.hierarchy.RuntimeHierarchy hierarchy = parsingState.getHierarchy();
			final org.jf.dexlib.Code.Format.Instruction21c insnConstClass = (Instruction21c)insn;
			return new DexInstruction_ConstClass(parsingState.getSingleRegister(insnConstClass.getRegisterA()), DexReferenceType.parse(((TypeIdItem)insnConstClass.getReferencedItem()).getTypeDescriptor(), hierarchy.getTypeCache()), hierarchy);
		} else throw FORMAT_EXCEPTION;
	}
	
	@Override
	public String toString() {
		return "const-class " + regTo.toString() + ", " + value.getDescriptor();
	}
	
	@Override
	public void instrument() {
	}
	
	@Override
	public Set<? extends DexRegister> lvaDefinedRegisters() {
		return Sets.newHashSet(regTo);
	}
	
	@Override
	public void accept(DexInstructionVisitor visitor) {
		visitor.visit(this);
	}
	
	@Override
	protected DexClassType[] throwsExceptions() {
		return this.hierarchy.getTypeCache().LIST_Error;
	}
	
	@java.lang.SuppressWarnings("all")
	public DexSingleRegister getRegTo() {
		return this.regTo;
	}
	
	@java.lang.SuppressWarnings("all")
	public DexReferenceType getValue() {
		return this.value;
	}
}