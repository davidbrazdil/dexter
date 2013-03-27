package com.rx201.dx.translator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

import org.jf.dexlib.CodeItem;
import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.Analysis.AnalyzedInstruction;
import org.jf.dexlib.Code.Analysis.MethodAnalyzer;
import org.jf.dexlib.Util.AccessFlags;

import com.android.dx.cf.code.ConcreteMethod;
import com.android.dx.cf.code.Ropper;
import com.android.dx.dex.DexOptions;
import com.android.dx.dex.cf.CodeStatistics;
import com.android.dx.dex.cf.OptimizerOptions;
import com.android.dx.dex.code.DalvCode;
import com.android.dx.dex.code.PositionList;
import com.android.dx.dex.code.RopTranslator;
import com.android.dx.rop.code.BasicBlock;
import com.android.dx.rop.code.BasicBlockList;
import com.android.dx.rop.code.DexTranslationAdvice;
import com.android.dx.rop.code.Insn;
import com.android.dx.rop.code.InsnList;
import com.android.dx.rop.code.LocalVariableExtractor;
import com.android.dx.rop.code.LocalVariableInfo;
import com.android.dx.rop.code.RopMethod;
import com.android.dx.rop.code.TranslationAdvice;
import com.android.dx.rop.cst.CstMethodRef;
import com.android.dx.ssa.Optimizer;
import com.android.dx.util.IntList;

public class Translator {

	public static RopMethod toRop(CodeItem method) {
		MethodAnalyzer analyzer = new MethodAnalyzer(method.getParent(), false, null);
		analyzer.analyze();
        HashMap<Instruction, AnalyzedInstruction> analysisResult = new HashMap<Instruction, AnalyzedInstruction>();
        for(AnalyzedInstruction analyzedInst : analyzer.getInstructions()) {
                analysisResult.put(analyzedInst.getInstruction(), analyzedInst);
        }
        
        // Build basic blocks
        ArrayList<ArrayList<AnalyzedInstruction>> basicBlocks = buildBasicBlocks(analyzer);
        
        // Convert to ROP's BasicBlockList form
        BasicBlockList ropBasicBlocks = new BasicBlockList(basicBlocks.size());
        
        for(ArrayList<AnalyzedInstruction> basicBlock : basicBlocks) {
        	InsnList insns = new InsnList(basicBlock.size());
        	
        	for(AnalyzedInstruction inst : basicBlock)
        		insns.set(inst.getInstructionIndex(), convert(inst));
        	
        	int label = basicBlock.get(0).getInstructionIndex();
        	AnalyzedInstruction last = basicBlock.get(basicBlock.size() - 1);
        	IntList successors = new IntList();
        	for(AnalyzedInstruction s : last.getSuccesors()) 
        		successors.add(s.getInstructionIndex());
        	
        	//TODO: Primary successor
        	BasicBlock ropBasicBlock = new BasicBlock(label, insns, successors, successors.size() == 0 ? -1 : successors.get(0));
        	
        	ropBasicBlocks.set(label, ropBasicBlock);
        }

        return new RopMethod(ropBasicBlocks, analyzer.getStartOfMethod().getInstructionIndex());
	}
	
	private static ArrayList<ArrayList<AnalyzedInstruction>> buildBasicBlocks(MethodAnalyzer analyzer) {
        ArrayList<ArrayList<AnalyzedInstruction>> basicBlocks = new ArrayList<ArrayList<AnalyzedInstruction>>();
        
        Stack<AnalyzedInstruction> leads = new Stack<AnalyzedInstruction>();
        leads.push(analyzer.getStartOfMethod());
        HashSet<Integer> visited = new HashSet<Integer>();
        
        while(!leads.empty()) {
        	AnalyzedInstruction first = leads.pop();
        	int id = first.getInstructionIndex();
        	if (visited.contains(id)) continue; // Already visited this basic block before.
        	visited.add(id);
        	
        	ArrayList<AnalyzedInstruction> block = new ArrayList<AnalyzedInstruction>(); 
        	// Extend this basic block as far as possible
        	AnalyzedInstruction current = first; // Always refer to latest-added instruction in the bb
        	block.add(current);
        	while(current.getSuccessorCount() == 1) { 
        		// Condition 1: current has only one successor
        		// Condition 2: next instruction has only one predecessor
        		AnalyzedInstruction next = current.getSuccesors().get(0);
        		if (next.getPredecessorCount() == 1) {
        			block.add(next);
        			next = current;
        		} else
        			break;
        	}
        	
        	// Add successors of current to the to-be-visit stack
        	for(AnalyzedInstruction i : current.getSuccesors())
        		leads.push(i);
        	
        	basicBlocks.add(block);
       }
        
        return basicBlocks;
	}

	private static Insn convert(AnalyzedInstruction inst) {
		return null;
	}
	
	public static void translate(CodeItem method) {
		int paramSize = method.getInWords();
		boolean isStatic = (method.getParent().accessFlags & AccessFlags.STATIC.getValue()) != 0;
		DexOptions dexOptions = new DexOptions();
        dexOptions.targetApiLevel = 10;

		RopMethod rmeth = toRop(method);

        rmeth = Optimizer.optimize(rmeth, paramSize, isStatic, false, DexTranslationAdvice.THE_ONE);
		
        DalvCode code = RopTranslator.translate(rmeth, PositionList.NONE, null, paramSize, dexOptions);
        
	}
	
}

