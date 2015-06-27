package org.opencompare.explorable;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class ProcessConfiguration {

	private final List<OptionValue> processOptions = ApplicationConfiguration.getInstance().initializeAllOptions();
	private final List<Closeable> toBeClosed = new ArrayList<Closeable>();
	
	public void appendConfiguration(Properties to) {
		for (OptionValue opt: processOptions) {
			to.setProperty("option-" + opt.getDefinition().getName(), opt.getValue().toString());
		}
	}

	public OptionValue getOption(String key) {
		for (OptionValue opt: processOptions) {
			if (opt.getDefinition().getName().equals(key)) {
				return opt;
			}
		}
		return null;
	}
	
	public void closeOnFinish(Closeable closeable) {
		synchronized(toBeClosed) {
			toBeClosed.add(closeable);
		}
	}
	
	public Map<Closeable, IOException> close() {
		Map<Closeable, IOException> res = new HashMap<Closeable, IOException>();
		
		List<Closeable> copy;	// Don't lock the thread while closing stuff
		synchronized(toBeClosed) {
			copy = new ArrayList<Closeable>(toBeClosed);
		}
		
		for (Closeable c: copy) {
			try {
				System.out.println("Closing " + c);
				c.close();
			} catch (IOException e) {
				e.printStackTrace();
				res.put(c, e);
			}
		}
		
		return res;
	}

	@Override
	public String toString() {
		return "ProcessConfiguration [processOptions=" + processOptions + "]";
	}
	
}
