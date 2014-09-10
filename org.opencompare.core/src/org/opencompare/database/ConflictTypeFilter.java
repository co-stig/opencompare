package org.opencompare.database;

import org.opencompare.explorable.Conflict;

public class ConflictTypeFilter implements ConflictFilter {
    
    private boolean includeNew;
    private boolean includeModified;
    private boolean includeMissing;
    private boolean includeIdentical;
    
    public ConflictTypeFilter(boolean includeIdentical, boolean includeNew, boolean includeMissing, boolean includeModified) {
        this.includeIdentical = includeIdentical;
        this.includeNew = includeNew;
        this.includeMissing = includeMissing;
        this.includeModified = includeModified;
    }

    public ConflictTypeFilter() {
        this(true, true, true, true);
    }

    public boolean isIncludeIdentical() {
        return includeIdentical;
    }

    public void setIncludeIdentical(boolean includeIdentical) {
        this.includeIdentical = includeIdentical;
    }

    public boolean isIncludeMissing() {
        return includeMissing;
    }

    public void setIncludeMissing(boolean includeMissing) {
        this.includeMissing = includeMissing;
    }

    public boolean isIncludeModified() {
        return includeModified;
    }

    public void setIncludeModified(boolean includeModified) {
        this.includeModified = includeModified;
    }

    public boolean isIncludeNew() {
        return includeNew;
    }

    public void setIncludeNew(boolean includeNew) {
        this.includeNew = includeNew;
    }
    
    public boolean include(Conflict conflict) {
        switch (conflict.getType()) {
            case Identical: return includeIdentical;
            case New: return includeNew;
            case Missing: return includeMissing;
            case Modified: return includeModified;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ConflictTypeFilter{" + "includeNew=" + includeNew + ", includeModified=" + includeModified + ", includeMissing=" + includeMissing + ", includeIdentical=" + includeIdentical + '}';
    }
    
}
