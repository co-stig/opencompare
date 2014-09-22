package org.opencompare.explorers.files.java;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javassist.bytecode.ClassFile;
import javassist.bytecode.MethodInfo;

import org.opencompare.database.Database;
import org.opencompare.explorable.Configuration;
import org.opencompare.explorable.Explorable;
import org.opencompare.explorable.RootFactory;
import org.opencompare.explorable.files.SimpleFile;
import org.opencompare.explore.ExplorationException;
import org.opencompare.explorers.Explorer;
import org.opencompare.explorers.Explores;

@Explores(SimpleFile.class)
public class ClassFileExplorer implements Explorer {

    @Override
	public void explore(Database threadDatabase, Explorable what) throws ExplorationException, InterruptedException {
    	File file = ((SimpleFile) what).getPath();
    	if (!file.isFile() || !file.getName().endsWith(".class")) {
    		return;
    	}
    	
    	InputStream is = null;
		try {
			is = new BufferedInputStream(new FileInputStream(file));
			DataInputStream dstream = new DataInputStream(is);
			ClassFile clazz = new ClassFile(dstream);
			for (Object raw: clazz.getMethods()) {
				MethodInfo method = (MethodInfo) raw;
				Configuration.enqueue(threadDatabase, what, RootFactory.TYPE_PROPERTY, method.toString(), method.getName());
			}
		} catch (Exception e) {
			throw new ExplorationException(e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					throw new ExplorationException(e);
				}
			}
		}
	}
    
}
