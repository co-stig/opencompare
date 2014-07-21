package org.opencompare.explorers.files;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map.Entry;
import java.util.Properties;

import org.opencompare.explorable.Configuration;
import org.opencompare.explorable.Explorable;
import org.opencompare.explorable.ExplorableFactory;
import org.opencompare.explorable.RootFactory;
import org.opencompare.explorable.files.PropertiesFile;
import org.opencompare.explore.ExplorationException;
import org.opencompare.explorers.Explorer;

public class PropertiesFileExplorer implements Explorer {

    @Override
	public void explore(Explorable what, ExplorableFactory factory) throws ExplorationException, InterruptedException {
    	PropertiesFile file = (PropertiesFile) what;
    	
		Properties p = new Properties();
		
		try {
			InputStream is = new BufferedInputStream(new FileInputStream(file.getPath()));
			try {
				p.load(is);
			} finally {
				is.close();
			}
		} catch (IOException e) {
			throw new ExplorationException("Unable to read properties file: " + file.getPath(), e);
		}
		
		for (Entry<Object, Object> e: p.entrySet()) {
			// Here cast to String is perfectly safe, according to Properties specification
			Configuration.enqueue(file, RootFactory.TYPE_PROPERTY, (String) e.getKey(), (String) e.getValue());
		}
	}
    
}
