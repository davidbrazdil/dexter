package uk.ac.cam.db538.dexter.dex.code.reg;

public class DexSingleOriginalRegister extends DexSingleRegister {
	private final int id;
	private final DexTaintRegister taintRegister;
	
	public DexSingleOriginalRegister(int id) {
		
		this.id = id;
		this.taintRegister = new DexTaintRegister(this);
	}
	
	@Override
	String getPlainId() {
		return Integer.toString(id);
	}
	
	@Override
	public DexTaintRegister getTaintRegister() {
		return this.taintRegister;
	}
	
	@java.lang.SuppressWarnings("all")
	public int getId() {
		return this.id;
	}
}