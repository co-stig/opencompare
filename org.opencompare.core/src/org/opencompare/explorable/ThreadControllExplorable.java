package org.opencompare.explorable;

public class ThreadControllExplorable extends Explorable {

	public ThreadControllExplorable() {
		super(0, 0, null);
	}

	@Override
	public String getValue() {
		throw new UnsupportedOperationException("ThreadControllExplorable does not support any of the Explorable operations, including getValue()");
	}

	@Override
	public long getValueHashCode() {
		throw new UnsupportedOperationException("ThreadControllExplorable does not support any of the Explorable operations, including getValueHashCode()");
	}

	@Override
	public String getRelativeId() {
		throw new UnsupportedOperationException("ThreadControllExplorable does not support any of the Explorable operations, including getRelativeId()");
	}

	@Override
	public String getUserFriendlyValue() {
		throw new UnsupportedOperationException("ThreadControllExplorable does not support any of the Explorable operations, including getUserFriendlyValue()");
	}
}
