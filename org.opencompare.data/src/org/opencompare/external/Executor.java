package org.opencompare.external;

import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("rawtypes")
public abstract class Executor {
    
    private static final Class[] EMPTY_CLASSES_ARRAY = new Class[] {};
    private static final Object[] EMPTY_VALUES_ARRAY = new Object[] {};
    
    public class ExecutionBuilder {
        
        private List<Class> classes = null;
        private List<Object> values = null;
        private final String clazz;
        private final String method;
        
        private ExecutionBuilder(String clazz, String method) {
            this.clazz = clazz;
            this.method = method;
        }
        
        public ExecutionBuilder withArgument(Class clazz, Object value) {
            if (classes == null || values == null) {
                classes = new LinkedList<Class>();
                values = new LinkedList<Object>();
            }
            classes.add(clazz);
            values.add(value);
            return this;
        }
        
        public Object executeForTarget(Object target) throws ExternalException {
            return executeRemotely(
                    clazz, 
                    method, 
                    classes == null ? EMPTY_CLASSES_ARRAY : classes.toArray(EMPTY_CLASSES_ARRAY), 
                    values == null ? EMPTY_VALUES_ARRAY : values.toArray(EMPTY_VALUES_ARRAY), 
                    target
                );
        }
        
        public Object executeStatically() throws ExternalException {
            return executeForTarget(null);
        }
    }

    public ExecutionBuilder getRemoteMethod(String clazz, String method) {
        return new ExecutionBuilder(clazz, method);
    }
    
    /**
     * This method should handle multiple threads correctly
     */
    protected abstract Object executeRemotely(String clazz, String method, Class[] argTypes, Object[] argValues, Object target) throws ExternalException;
    
}
