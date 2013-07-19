package uk.ac.cam.db538.dexter.analysis.cfg;

import java.util.HashSet;
import java.util.Set;
import uk.ac.cam.db538.dexter.dex.code.InstructionList;
import uk.ac.cam.db538.dexter.dex.code.elem.DexCodeElement;
import uk.ac.cam.db538.dexter.dex.code.reg.DexRegister;

public class CfgBasicBlock extends CfgBlock {
	private final InstructionList instructions;
	
	public CfgBasicBlock(InstructionList instructions) {
		
		if (instructions == null || instructions.isEmpty()) throw new UnsupportedOperationException("BasicBlock must contain at least one instruction");
		this.instructions = instructions;
	}
	
	public DexCodeElement getFirstInstruction() {
		return instructions.peekFirst();
	}
	
	public DexCodeElement getLastInstruction() {
		return instructions.peekLast();
	}
	
	public Set<DexRegister> getAllDefinedRegisters() {
		final java.util.HashSet<uk.ac.cam.db538.dexter.dex.code.reg.DexRegister> set = new HashSet<DexRegister>();
		for (final uk.ac.cam.db538.dexter.dex.code.elem.DexCodeElement insn : instructions) set.addAll(insn.lvaDefinedRegisters());
		return set;
	}
	
	@java.lang.SuppressWarnings("all")
	public InstructionList getInstructions() {
		return this.instructions;
	}
}