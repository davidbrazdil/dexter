package uk.ac.cam.db538.dexter.aux.struct;

public final class Assigner {

	private Assigner() { }

	public static final TaintExternal newExternal(Object obj, int initialTaint) {
		TaintExternal tobj = new TaintExternal(initialTaint);
		Cache.insert(obj, tobj);
		return tobj;
	}
	
	public static final TaintExternal lookupExternal(Object obj, int addTaint) {
		TaintExternal tobj = (TaintExternal) Cache.get(obj);
		if (tobj == null) {
			tobj = new TaintExternal(addTaint);
			Cache.insert(obj, tobj);
		} else
			tobj.set(addTaint);
		return tobj;
	}
	
	public static final TaintInternal lookupInternal(InternalDataStructure obj, int addTaint) {
		TaintInternal tobj = (TaintInternal) Cache.get(obj);
		if (tobj == null) {
			tobj = new TaintInternal(obj);
			Cache.insert(obj, tobj);
		}
		
		tobj.set(addTaint);
		return tobj;
	}

	public static final Taint lookupUndecidable(Object obj, int addTaint) {
		if (obj instanceof InternalDataStructure)
			return lookupInternal((InternalDataStructure) obj, addTaint);
		else
			return lookupExternal(obj, addTaint);
	}
	
//	private static final boolean isImmutable(Object obj) {
//      LIST OF IMMUTABLES IN TaintConstants CLASS
//		return
//			obj instanceof String ||
//			obj instanceof Integer ||
//			obj instanceof Boolean ||
//			obj instanceof Byte ||
//			obj instanceof Character ||
//			obj instanceof Double ||
//			obj instanceof Float ||
//			obj instanceof Long ||
//			obj instanceof Short ||
//			obj instanceof Void ||
//			obj instanceof BigDecimal ||
//			obj instanceof BigInteger;
//	}
}
