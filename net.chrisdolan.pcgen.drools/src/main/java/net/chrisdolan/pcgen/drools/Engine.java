package net.chrisdolan.pcgen.drools;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.chrisdolan.pcgen.drools.Ruleset.Rule;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.CompositeKnowledgeBuilder;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.compiler.DroolsParserException;
import org.drools.conf.EventProcessingOption;
import org.drools.definition.KnowledgePackage;
import org.drools.io.impl.ClassPathResource;
import org.drools.rule.EntryPoint;
import org.drools.runtime.ObjectFilter;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;
import org.drools.runtime.rule.QueryResults;
import org.drools.runtime.rule.QueryResultsRow;

public class Engine {
    private KnowledgeBase kbase;
    private ArrayList<String> names;
    private final Ruleset.Reader rulesetReader;

    {
        Ruleset.Reader r;
        try {
            r = new JAXBRulesetReader(); // this will fail on Android
        } catch (Throwable t) {
            r = new XStreamRulesetReader();
        }
        rulesetReader = r;
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
//            int nFired = ksession.fireAllRules();
//            System.out.println("Fired " + nFired + " rules");
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
            Object object = queryResults.iterator().next().get(queryResults.getIdentifiers()[args.length]);
            if (!cls.isAssignableFrom(object.getClass()))
                throw new ClassCastException(cls.getName() + " <- " + object.getClass());
            @SuppressWarnings("unchecked")
            T t = (T) object;
            return t;
        }
        
        public Collection<Object> search(ObjectFilter filter) {
            return ksession.getWorkingMemoryEntryPoint(EntryPoint.DEFAULT.getEntryPointId()).getObjects(filter);
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

        public void destroy() {
            ksession.dispose();
            ksession = null;
        }
    }

    public Engine(String... names) throws IOException, DroolsParserException {
        this.names = new ArrayList<String>(Arrays.asList(names));

        Collection<KnowledgePackage> kpkgs = null;
        if (this.names.size() == 1 && this.names.get(0).endsWith(".ser")) {
            kpkgs = readSerializedRules(this.names.get(0));
        } else {
            KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
            CompositeKnowledgeBuilder batch = kbuilder.batch();
            for (String name : names)
                for (Rule rule : readRuleset(name).getRules())
                    batch.add(new ClassPathResource(rule.getName(), getClass()), ResourceType.getResourceType(rule.getType()));
            batch.build();
            if (kbuilder.hasErrors())
                throw new DroolsParserException(kbuilder.getErrors().toString());

            kpkgs = kbuilder.getKnowledgePackages();
        }
        //KnowledgeBaseConfiguration config = KnowledgeBaseFactory.newKnowledgeBaseConfiguration(null, getClass().getClassLoader());
        KnowledgeBaseConfiguration config = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        config.setOption(EventProcessingOption.STREAM);
        kbase = KnowledgeBaseFactory.newKnowledgeBase(config);
        kbase.addKnowledgePackages(kpkgs);
    }
    
    private Collection<KnowledgePackage> readSerializedRules(String path) throws IOException {
        InputStream is = getClass().getResourceAsStream(path);
        try {
            ObjectInputStream ois = new ObjectInputStream(is);
            try {
                Object o = ois.readObject();
                if (!(o instanceof Collection))
                    throw new IOException("deserialization expected a Collection, got: " + o.getClass());
                @SuppressWarnings({ "rawtypes" })
                Collection c = (Collection)o;
                if (c.isEmpty())
                    throw new IOException("deserialization got an empty collection");
                Object item = c.iterator().next();
                if (!(item instanceof KnowledgePackage))
                    throw new IOException("deserialization expected a Collection of KnowledgePackage, got: " + item.getClass());
                @SuppressWarnings("unchecked")
                Collection<KnowledgePackage> ck = c;
                return ck;
            } catch (ClassNotFoundException e) {
                throw new IOException(e);
            } finally {
                ois.close();
            }
        } catch (RuntimeException e) {
            throw new IOException(e);
        } finally {
            is.close();
        }
    }

    public static Session createSession(String... names) throws DroolsParserException, IOException {
        return getEngine(names).createSession();
    }

    private static String lastUsedName;
    private static Engine lastUsed;
    private static Map<String, WeakReference<Engine>> engines = new HashMap<String, WeakReference<Engine>>();
    public static Engine getEngine(String... names) throws DroolsParserException, IOException {
        String key = Arrays.toString(names);
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
                    engine = new Engine(names);
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

    private Ruleset readRuleset(String name) throws IOException {
        return rulesetReader.read(getClass().getResource(name + ".xml"));
    }

    @Override
    public String toString() {
        return "Engine" + names;
    }

    // Just for Compile action
    KnowledgeBase getKbase() {
        return kbase;
    }
    void setKbase(KnowledgeBase kbase) {
        this.kbase = kbase;
    }
}
