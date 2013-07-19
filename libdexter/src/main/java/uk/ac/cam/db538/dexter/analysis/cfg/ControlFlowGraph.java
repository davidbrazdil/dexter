package uk.ac.cam.db538.dexter.analysis.cfg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import uk.ac.cam.db538.dexter.dex.code.DexCode;
import uk.ac.cam.db538.dexter.dex.code.InstructionList;
import uk.ac.cam.db538.dexter.dex.code.elem.DexCodeElement;

public class ControlFlowGraph {
	private final DexCode code;
	private CfgStartBlock startBlock;
	private CfgExitBlock exitBlock;
	private List<CfgBasicBlock> basicBlocks;
	
	public ControlFlowGraph(DexCode code) {
		
		this.code = code;
		this.startBlock = new CfgStartBlock();
		this.exitBlock = new CfgExitBlock();
		this.basicBlocks = new LinkedList<CfgBasicBlock>();
		generateCFG();
	}
	
	private CfgBlock getBlockByFirstInsn(DexCodeElement firstInsn, HashMap<DexCodeElement, CfgBasicBlock> insnBlockMap) {
		final uk.ac.cam.db538.dexter.analysis.cfg.CfgBasicBlock block = insnBlockMap.get(firstInsn);
		if (block == null) throw new CfgException("Successor of a block doesn\'t point to a different block"); else return block;
	}
	
	private void generateCFG() {
		final uk.ac.cam.db538.dexter.dex.code.InstructionList insns = code.getInstructionList();
		// this is a map going from the first instruction
		// of a block to its reference; used later to create
		// edges between blocks
		final java.util.HashMap<uk.ac.cam.db538.dexter.dex.code.elem.DexCodeElement, uk.ac.cam.db538.dexter.analysis.cfg.CfgBasicBlock> insnBlockMap = new HashMap<DexCodeElement, CfgBasicBlock>();
		// split instruction list into basic blocks
		List<DexCodeElement> currentBlock = new ArrayList<DexCodeElement>();
		for (final uk.ac.cam.db538.dexter.dex.code.elem.DexCodeElement insn : insns) {
			if (insn.cfgStartsBasicBlock() && !currentBlock.isEmpty()) {
				final uk.ac.cam.db538.dexter.analysis.cfg.CfgBasicBlock block = new CfgBasicBlock(new InstructionList(currentBlock));
				basicBlocks.add(block);
				insnBlockMap.put(block.getFirstInstruction(), block);
				currentBlock = new ArrayList<DexCodeElement>();
			}
			currentBlock.add(insn);
			if ((insn.cfgEndsBasicBlock() || insn.cfgExitsMethod(insns) || insn.cfgGetSuccessors(insns).size() > 1) && !currentBlock.isEmpty()) {
				final uk.ac.cam.db538.dexter.analysis.cfg.CfgBasicBlock block = new CfgBasicBlock(new InstructionList(currentBlock));
				basicBlocks.add(block);
				insnBlockMap.put(block.getFirstInstruction(), block);
				currentBlock = new ArrayList<DexCodeElement>();
			}
		}
		if (!currentBlock.isEmpty()) {
			final uk.ac.cam.db538.dexter.analysis.cfg.CfgBasicBlock block = new CfgBasicBlock(new InstructionList(currentBlock));
			basicBlocks.add(block);
			insnBlockMap.put(block.getFirstInstruction(), block);
		}
		// connect blocks together
		if (basicBlocks.isEmpty()) {
			// no basic blocks => just connect START and EXIT
			CfgBlock.createEdge(startBlock, exitBlock);
		} else {
			// connect first block with START
			CfgBlock.createEdge(startBlock, basicBlocks.get(0));
			// connect blocks together using the list of successors
			// provided by each instruction
			for (final uk.ac.cam.db538.dexter.analysis.cfg.CfgBasicBlock block : basicBlocks) {
				final uk.ac.cam.db538.dexter.dex.code.elem.DexCodeElement lastInsn = block.getLastInstruction();
				final java.util.Set<uk.ac.cam.db538.dexter.dex.code.elem.DexCodeElement> lastInsnSuccs = lastInsn.cfgGetSuccessors(insns);
				for (final uk.ac.cam.db538.dexter.dex.code.elem.DexCodeElement succ : lastInsnSuccs) CfgBlock.createEdge(block, getBlockByFirstInsn(succ, insnBlockMap));
				// if a block ends with a returning instruction connect it to EXIT
				if (lastInsn.cfgExitsMethod(insns) || lastInsnSuccs.isEmpty()) CfgBlock.createEdge(block, exitBlock);
			}
		}
	}
	
	public List<CfgBasicBlock> getBasicBlocks() {
		return Collections.unmodifiableList(basicBlocks);
	}
	
	public CfgBasicBlock getStartingBasicBlock() {
		final java.util.Set<uk.ac.cam.db538.dexter.analysis.cfg.CfgBlock> startBlockSucc = getStartBlock().getSuccessors();
		if (startBlockSucc.size() != 1) throw new RuntimeException("ControlFlowGraph has multiple starting points");
		final uk.ac.cam.db538.dexter.analysis.cfg.CfgBlock startBlockCandidate = startBlockSucc.iterator().next();
		if (startBlockCandidate instanceof CfgBasicBlock) return (CfgBasicBlock)startBlockCandidate; else return null;
	}
	
	@java.lang.SuppressWarnings("all")
	public DexCode getCode() {
		return this.code;
	}
	
	@java.lang.SuppressWarnings("all")
	public CfgStartBlock getStartBlock() {
		return this.startBlock;
	}
	
	@java.lang.SuppressWarnings("all")
	public CfgExitBlock getExitBlock() {
		return this.exitBlock;
	}
}