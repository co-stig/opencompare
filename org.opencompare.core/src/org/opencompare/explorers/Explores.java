package org.opencompare.explorers;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// TODO: Use this annotation to automatically register explorers
//TODO: This concept won't work for reusable explorers, e.g. NoExplorer

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Explores {

	Class<?> value();
	
}