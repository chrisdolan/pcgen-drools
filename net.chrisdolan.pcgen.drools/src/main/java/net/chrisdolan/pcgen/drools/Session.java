package net.chrisdolan.pcgen.drools;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.drools.runtime.ObjectFilter;
import org.drools.runtime.rule.FactHandle;
import org.drools.runtime.rule.QueryResults;

public interface Session {
    void run();
    void destroy();

    FactHandle insert(Object obj);
    void retract(Object obj);
    
    QueryResults query(String queryname, Object... args);
    List<Object> queryAll(String queryname, Object... args);
    <T> Map<String,T> queryToMap(Class<T> cls, String queryname, Object... args);
    <T> List<T> queryColumn(Class<T> cls, String queryname, Object... args);
    <T> T querySingle(Class<T> cls, String queryname, Object... args);

    <T> Collection<T> searchByClass(Class<T> cls);
    Collection<Object> search(ObjectFilter filter);

}