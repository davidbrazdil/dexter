package uk.ac.cam.db538.dexter.aux.struct;

public class TaintImmutable extends TaintExternal {

	TaintImmutable() {
		super();
	}
	
	TaintImmutable(int taint) {
		super(taint);
	}

	@Override
	public void set(int taint) {
		// do nothing => it's immutable, stupid
	}

	@Override
	public void setExternal(int taint) {
		// do nothing => it's immutable, stupid
	}

}
