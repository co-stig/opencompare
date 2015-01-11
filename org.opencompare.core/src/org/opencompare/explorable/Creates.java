package org.opencompare.explorable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// TODO: Use this annotation to automatically register explorable factories

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Creates {

	Class<?>[] value();
	
}