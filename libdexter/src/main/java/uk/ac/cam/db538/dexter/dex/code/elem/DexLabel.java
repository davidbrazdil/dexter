package uk.ac.cam.db538.dexter.dex.code.elem;

public class DexLabel extends DexCodeElement {
	private final int id;
	
	public DexLabel(int id) {
		
		this.id = id;
	}
	
	@Override
	public String toString() {
		return "L" + Integer.toString(id);
	}
	
	@Override
	public boolean cfgStartsBasicBlock() {
		return true;
	}
	
	@java.lang.SuppressWarnings("all")
	public int getId() {
		return this.id;
	}
}