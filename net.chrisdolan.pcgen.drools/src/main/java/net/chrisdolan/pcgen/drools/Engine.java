package net.chrisdolan.pcgen.drools;

import java.io.IOException;
import java.io.PrintStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.KnowledgeBase;
import org.drools.rule.EntryPoint;
import org.drools.runtime.ObjectFilter;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;
import org.drools.runtime.rule.QueryResults;
import org.drools.runtime.rule.QueryResultsRow;

public class Engine {
    private KnowledgeBase kbase;
    private Ruleset rs;
    private static final Ruleset.Reader rulesetReader = new XStreamRulesetReader();

    private static int cacheLimit = 20;
    private static boolean useCache = true;

    public static void setCacheLimit(int cacheLimit) {
        Engine.cacheLimit = cacheLimit;
    }
    public static void setUseCache(boolean useCache) {
        Engine.useCache = useCache;
    }

    
    private static class EngineSession implements Session {
        private StatefulKnowledgeSession ksession;

        private EngineSession(StatefulKnowledgeSession ksession) {
            this.ksession = ksession;
        }
        public FactHandle insert(Object obj) {
            return ksession.insert(obj);
        }
        public void retract(Object obj) {
            FactHandle handle = obj instanceof FactHandle ? (FactHandle) obj : ksession.getFactHandle(obj);
            ksession.retract(handle);
        }

        public void run() {
            ksession.fireAllRules();
        }

        public QueryResults query(String queryname, Object... args) {
            return ksession.getQueryResults(queryname, args);
        }
        public List<Object> queryAll(String queryname, Object... args) {
            QueryResults queryResults = ksession.getQueryResults(queryname, args);
            List<Object> out = new ArrayList<Object>();
            String[] cols = queryResults.getIdentifiers();
            for (Iterator<QueryResultsRow> it = queryResults.iterator(); it.hasNext();) {
                QueryResultsRow row = it.next();
                for (String id : cols) {
                    out.add(row.get(id));
                }
            }
            return out;
        }
        public <T> Map<String, T> queryToMap(Class<T> cls, String queryname,  Object... args) {
            QueryResults queryResults = ksession.getQueryResults(queryname, args);
            Map<String, T> out = new HashMap<String, T>();
            String[] cols = queryResults.getIdentifiers();
            QueryResultsRow row = queryResults.iterator().next();
            for (String id : cols) {
                Object object = row.get(id);
                if (!cls.isAssignableFrom(object.getClass()))
                    throw new ClassCastException(cls.getName() + " <- " + object.getClass());
                @SuppressWarnings("unchecked")
                T t = (T) object;
                out.put(id, t);
            }
            return out;
        }
        public <T> Map<String, T> queryPairs(Class<T> cls, String queryname, Object... args) {
            if (cls == String.class)
                throw new IllegalArgumentException("Sorry, String is ambiguous in this method...");
            QueryResults queryResults = ksession.getQueryResults(queryname, args);
            Map<String, T> out = new HashMap<String, T>();
            String[] cols = queryResults.getIdentifiers();
            for (Iterator<QueryResultsRow> it = queryResults.iterator(); it.hasNext();) {
                QueryResultsRow row = it.next();
                String key = null;
                T value = null;
                for (String id : cols) {
                    Object o = row.get(id);
                    if (o instanceof String) {
                        if (key == null)
                            key = (String) o;
                        if (value != null)
                            break;
                    } else if (cls.isAssignableFrom(o.getClass())) {
                        if (value == null) {
                            @SuppressWarnings("unchecked")
                            T t = (T) o;
                            value = t;
                        }
                        if (key != null)
                            break;
                    }
                }
                if (key == null)
                    throw new RuntimeException("query did not return a String column: " + queryname);
                if (value == null)
                    throw new RuntimeException("query did not return a " + cls.getSimpleName() + " column: " + queryname);
                out.put(key, value);
            }
            return out;
        }
        public <T> List<T> queryColumn(Class<T> cls, String queryname, Object... args) {
            QueryResults queryResults = ksession.getQueryResults(queryname, args);
            List<T> out = new ArrayList<T>();
            String[] cols = queryResults.getIdentifiers();
            for (Iterator<QueryResultsRow> it = queryResults.iterator(); it.hasNext();) {
                QueryResultsRow row = it.next();
                Object object = row.get(cols[args.length]);
                if (!cls.isAssignableFrom(object.getClass()))
                    throw new ClassCastException(cls.getName() + " <- " + object.getClass());
                @SuppressWarnings("unchecked")
                T t = (T) object;
                out.add(t);
            }
            return out;
        }
        public <T> T querySingle(Class<T> cls, String queryname, Object... args) {
            QueryResults queryResults = ksession.getQueryResults(queryname, args);
            Iterator<QueryResultsRow> iterator = queryResults.iterator();
            if (!iterator.hasNext())
                return null;
            Object object = iterator.next().get(queryResults.getIdentifiers()[args.length]);
            if (!cls.isAssignableFrom(object.getClass()))
                throw new ClassCastException(cls.getName() + " <- " + object.getClass());
            @SuppressWarnings("unchecked")
            T t = (T) object;
            return t;
        }
        
        public Collection<Object> search(ObjectFilter filter) {
            return ksession.getWorkingMemoryEntryPoint(EntryPoint.DEFAULT.getEntryPointId()).getObjects(filter);
            //return ksession.getObjects(filter); // I think this is right for Drools v6
        }
        public <T> Collection<T> searchByClass(final Class<T> cls) {
            @SuppressWarnings("unchecked")
            Collection<T> c = (Collection<T>) search(new ObjectFilter() {
                public boolean accept(Object object) {
                    return cls.isAssignableFrom(object.getClass());
                }
            });
            return c;
        }
        public List<String> dump() {
            ObjectFilter filter = new ObjectFilter() {
                public boolean accept(Object object) {
                    return true;
//                    return !object.getClass().getSimpleName().startsWith("Stack");
                }
            };
            ArrayList<String> list = new ArrayList<String>();
            for (Object o : search(filter))
                list.add(o.toString());
            Collections.sort(list);
            return list;
        }

        public void dump(PrintStream stream) {
            for (String s : dump())
                stream.println(s);
        }

        public void destroy() {
            ksession.dispose();
            ksession = null;
        }
    }

    public Engine(Ruleset rs) throws IOException, ParseException {
        this.rs = rs;
        EngineBuilder builder = new CachingEngineBuilder(useCache, cacheLimit, rulesetReader, new CompatibleEngineBuilder());
        this.kbase = builder.CreateKnowledgeBase(builder.CreateKnowledgePkgs(rs));
        if (kbase == null)
            throw new ParseException("Failed to make a kbase");
        System.out.println("Created engine for ruleset " + rs);
    }


    public static Session createSession(Ruleset rs) throws ParseException, IOException {
        return getEngine(rs).createSession();
    }
    public static Session createSession(String... names) throws ParseException, IOException {
        Ruleset ruleset = new Ruleset();
        //ruleset.setUrl(new URL("."));
        List<Ruleset> rulesets = new ArrayList<Ruleset>();
        for (String name : names)
            rulesets.add(new Ruleset(name));
        ruleset.setRulesets(rulesets);
            
        return createSession(ruleset);
    }

    private static String lastUsedName;
    private static Engine lastUsed;
    private static Map<String, WeakReference<Engine>> engines = new HashMap<String, WeakReference<Engine>>();

    public static Engine getEngine(Ruleset rs) throws ParseException, IOException {
        String key = rs.toString();
        synchronized (engines) {
            if (key.equals(lastUsedName) && lastUsed != null)
                return lastUsed;
            Engine engine = null;
            WeakReference<Engine> engineRef = engines.get(key);
            if (engineRef != null) {
                engine = engineRef.get();
            }
            if (engine == null) {
                try {
                    engine = new Engine(rs);
                } catch (RuntimeException e) {
                    throw new IOException(e);
                }
                engines.put(key, new WeakReference<Engine>(engine));
                lastUsedName = key;
                lastUsed = engine;
            }
            return engine;
        }
    }

    public Session createSession() {
        return new EngineSession(kbase.newStatefulKnowledgeSession());
    }

    public String toString() {
        return "Engine[" + rs + "]";
    }

    // Just for Compile action
    KnowledgeBase getKbase() {
        return kbase;
    }
    void setKbase(KnowledgeBase kbase) {
        this.kbase = kbase;
    }
}
