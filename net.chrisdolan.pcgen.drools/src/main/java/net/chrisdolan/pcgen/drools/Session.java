package net.chrisdolan.pcgen.drools;

import java.util.Collection;

import org.drools.runtime.ObjectFilter;
import org.drools.runtime.rule.FactHandle;

public interface Session {
    FactHandle insert(Object obj);
    void retract(Object obj);
    void run();
    <T> Collection<T> queryByClass(Class<T> cls);
    Collection<Object> query(ObjectFilter filter);
    void destroy();
}