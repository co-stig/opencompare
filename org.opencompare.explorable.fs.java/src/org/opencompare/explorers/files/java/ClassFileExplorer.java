package org.opencompare.explorers.files.java;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javassist.bytecode.ClassFile;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.MethodInfo;

import org.opencompare.explorable.Explorable;
import org.opencompare.explorable.ProcessConfiguration;
import org.opencompare.explorable.RootFactory;
import org.opencompare.explorable.files.SimpleFile;
import org.opencompare.explore.ExplorationException;
import org.opencompare.explore.ExploringThread;
import org.opencompare.explorers.Explorer;
import org.opencompare.explorers.Explores;

@Explores(SimpleFile.class)
public class ClassFileExplorer implements Explorer {

	public final static String OPTION_EXPLORE_METHODS = ClassFileExplorer.class.getName() + "/explore.methods";
	public final static String OPTION_EXPLORE_FIELDS = ClassFileExplorer.class.getName() + "/explore.fields";
	
    @Override
	public void explore(ProcessConfiguration config, ExploringThread thread, Explorable what) throws ExplorationException, InterruptedException {
    	File file = ((SimpleFile) what).getPath();
    	if (!file.isFile() || !file.getName().endsWith(".class")) {
    		return;
    	}

    	boolean exploreFields = config.getOption(OPTION_EXPLORE_FIELDS).getBooleanValue();
		boolean exploreMethods = config.getOption(OPTION_EXPLORE_METHODS).getBooleanValue();
		if (!exploreFields && !exploreMethods) {
    		return;
    	}
    	
    	InputStream is = null;
		try {
			is = new BufferedInputStream(new FileInputStream(file));
			DataInputStream dstream = new DataInputStream(is);
			ClassFile clazz = new ClassFile(dstream);
			if (exploreMethods) {
				for (Object raw: clazz.getMethods()) {
					MethodInfo method = (MethodInfo) raw;
					thread.enqueue(what, RootFactory.TYPE_PROPERTY, method.toString(), method.getName());
				}
			}
			if (exploreFields) {
				for (Object raw: clazz.getFields()) {
					FieldInfo field = (FieldInfo) raw;
					thread.enqueue(what, RootFactory.TYPE_PROPERTY, field.toString(), field.getName());
				}
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
