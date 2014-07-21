package org.opencompare.explorable.files;

import java.io.File;

import org.opencompare.explore.ExplorationException;

public class XConfFile extends SimpleFile {

    public XConfFile(int id, int parentId, File path, long checksum, String sha) throws ExplorationException {
        super(id, parentId, path, checksum, sha);
    }

    public XConfFile(int id, int parentId, File path) throws ExplorationException {
        super(id, parentId, path);
    }

    /**
     * XConf file value is defined by contained elements, similarly to
     * file-folder relationship.
     */
    public String getValue() {
        return "";
    }

    @Override
    public String getUserFriendlyValue() {
        return "[XConf]";
    }
}
