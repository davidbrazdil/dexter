package uk.ac.cam.db538.dexter.dex.code;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_Invoke;
import uk.ac.cam.db538.dexter.dex.code.reg.DexRegister;
import uk.ac.cam.db538.dexter.dex.code.reg.DexStandardRegister;
import uk.ac.cam.db538.dexter.dex.type.DexRegisterType;
import uk.ac.cam.db538.dexter.hierarchy.RuntimeHierarchy;
import uk.ac.cam.db538.dexter.utils.Utils;

public class DexCode {
	private final RuntimeHierarchy hierarchy;
	private final InstructionList instructionList;
	private final List<Parameter> parameters;
	
	public DexCode(InstructionList instructionList, List<Parameter> parameters, RuntimeHierarchy hierarchy) {
		
		this.instructionList = instructionList;
		this.parameters = Utils.finalList(parameters);
		this.hierarchy = hierarchy;
	}
	
	public Set<DexRegister> getUsedRegisters() {
		final java.util.HashSet<uk.ac.cam.db538.dexter.dex.code.reg.DexRegister> set = new HashSet<DexRegister>();
		for (final uk.ac.cam.db538.dexter.dex.code.elem.DexCodeElement elem : instructionList) set.addAll(elem.lvaUsedRegisters());
		for (final uk.ac.cam.db538.dexter.dex.code.DexCode.Parameter param : parameters) set.add(param.getRegister());
		return set;
	}
	
	public int getOutWords() {
		// outWords is the max of all inWords of methods in the code
		int maxWords = 0;
		for (final uk.ac.cam.db538.dexter.dex.code.elem.DexCodeElement insn : this.instructionList) {
			if (insn instanceof DexInstruction_Invoke) {
				final uk.ac.cam.db538.dexter.dex.code.insn.DexInstruction_Invoke insnInvoke = (DexInstruction_Invoke)insn;
				int insnOutWords = insnInvoke.getMethodId().getPrototype().countParamWords(insnInvoke.getCallType().isStatic());
				if (insnOutWords > maxWords) maxWords = insnOutWords;
			}
		}
		return maxWords;
	}
	
	public static class Parameter {
		private final DexRegisterType type;
		private final DexStandardRegister register;
		
		@java.beans.ConstructorProperties({"type", "register"})
		@java.lang.SuppressWarnings("all")
		public Parameter(final DexRegisterType type, final DexStandardRegister register) {
			
			this.type = type;
			this.register = register;
		}
		
		@java.lang.SuppressWarnings("all")
		public DexRegisterType getType() {
			return this.type;
		}
		
		@java.lang.SuppressWarnings("all")
		public DexStandardRegister getRegister() {
			return this.register;
		}
	}
	
	@java.lang.SuppressWarnings("all")
	public RuntimeHierarchy getHierarchy() {
		return this.hierarchy;
	}
	
	@java.lang.SuppressWarnings("all")
	public InstructionList getInstructionList() {
		return this.instructionList;
	}
	
	@java.lang.SuppressWarnings("all")
	public List<Parameter> getParameters() {
		return this.parameters;
	}
}