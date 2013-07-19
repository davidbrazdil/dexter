package uk.ac.cam.db538.dexter.dex.code.insn;

public enum Opcode_Compare {
	CmplFloat("cmpl-float"),
	CmpgFloat("cmpg-float"),
	CmplDouble("cmpl-double"),
	CmpgDouble("cmpg-double"),
	CmpLong("cmp-long");
	private final String AsmName;
	
	private Opcode_Compare(String assemblyName) {
		
		AsmName = assemblyName;
	}
	
	@java.lang.SuppressWarnings("all")
	public String getAsmName() {
		return this.AsmName;
	}
}