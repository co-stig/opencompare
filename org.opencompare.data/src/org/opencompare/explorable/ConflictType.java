package org.opencompare.explorable;

public enum ConflictType {

	Identical,	// No conflict
	New, 		// Object doesn't exist in the reference
	Missing, 	// Object is missing
	Modified;	// Object is modified
	
	public short toShort() {
		switch (this) {
			case Identical: return 0;
			case New: return 1;
			case Missing: return 2;
			case Modified: return 3;
		}
		return -1;
	}
	
	public static ConflictType valueOf(short s) {
		switch (s) {
			case 0: return Identical;
			case 1: return New;
			case 2: return Missing;
			case 3: return Modified;
		}
		return null;
	}
	
}
