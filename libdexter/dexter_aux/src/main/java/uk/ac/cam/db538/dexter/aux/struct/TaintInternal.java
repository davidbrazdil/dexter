package uk.ac.cam.db538.dexter.aux.struct;

import java.util.HashSet;

import uk.ac.cam.db538.dexter.aux.TaintConstants;

public class TaintInternal implements Taint {

	private final InternalDataStructure obj;
	private final TaintExternal t_super;
	
	public TaintInternal(InternalDataStructure obj, Taint t_super) {
		this.obj = obj;
		
		if (t_super instanceof TaintExternal)
			this.t_super = (TaintExternal) t_super;
		else
			this.t_super = null;
	}
	
	public int get() {
		HashSet<TaintInternal> visited = visitedSet.get();
		if (visited.contains(this))
			return TaintConstants.TAINT_EMPTY;
		else
			visited.add(this);

		if (this.t_super != null)
			return this.obj.getTaint() | this.t_super.get();
		else
			return this.obj.getTaint();
	}
	
	public void set(int taint) {
		HashSet<TaintInternal> visited = visitedSet.get();
		if (!visited.contains(this)) {
			visited.add(this);
			
			this.obj.setTaint(taint);
			if (this.t_super != null)
				this.t_super.set(taint);
		}
	}
	
	// THREAD-LOCAL SET OF VISITED NODES
	// prevents infinite looping
	
	private static class VisitedSet extends ThreadLocal<HashSet<TaintInternal>> {
		@Override
		protected HashSet<TaintInternal> initialValue() {
			return new HashSet<TaintInternal>();
		}

		@Override
		public void set(HashSet<TaintInternal> value) {
			throw new UnsupportedOperationException();
		}
	}

	private static final VisitedSet visitedSet;
	static { visitedSet = new VisitedSet(); }
	
	// MUST BE CALLED BEFORE A TRAVERSAL IS INITIATED !!!
	// traversal is every call of Taint.get or Taint.set
	// but that can be initiated even from Assigner.lookup* methods! 
	
	public static void clearVisited() {
		visitedSet.get().clear();
	}
}
