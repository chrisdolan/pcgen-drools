package net.chrisdolan.pcgen.drools;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

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
import org.drools.io.Resource;
import org.drools.io.impl.ClassPathResource;
import org.drools.io.impl.ReaderResource;
import org.drools.io.impl.UrlResource;
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

    public Engine(Ruleset rs) throws IOException, DroolsParserException {
        this.rs = rs;

        Ruleset flattened;
        Collection<KnowledgePackage> kpkgs = null;
        String rsHash = null;
        if (useCache) {
            flattened = rulesetReader.inline(rs);
            rsHash = cacheHash(flattened);
            kpkgs = cacheRead(rsHash);
        } else {
            flattened = rulesetReader.flatten(rs);
        }

        if (null == kpkgs) {
//        if (this.names.size() == 1 && this.names.get(0).endsWith(".ser")) {
//            kpkgs = readSerializedRules(this.names.get(0));
            KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
            CompositeKnowledgeBuilder batch = kbuilder.batch();
            for (Rule rule : flattened.getRules()) {
                Resource r;
                if (rule.getBody() != null && !Pattern.matches("^\\s*$", rule.getBody())) {
                    r = new ReaderResource(new StringReader(rule.getBody()));
                } else if (rule.getUri() == null) {
                    r = new ClassPathResource(rule.getName(), getClass());
                } else if (rule.getUri().isAbsolute()) {
                    r = new UrlResource(rule.getUri().toURL());
                } else {
                    r = new ClassPathResource(rule.getUri().toString(), getClass());
                }
                batch.add(r, ResourceType.getResourceType(rule.getType()));
            }
            batch.build();
            if (kbuilder.hasErrors())
                throw new DroolsParserException(kbuilder.getErrors().toString());

            kpkgs = kbuilder.getKnowledgePackages();
            if (useCache)
                cacheWrite(rsHash, kpkgs);
        }
        //KnowledgeBaseConfiguration config = KnowledgeBaseFactory.newKnowledgeBaseConfiguration(null, getClass().getClassLoader());
        KnowledgeBaseConfiguration config = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        config.setOption(EventProcessingOption.STREAM);
        kbase = KnowledgeBaseFactory.newKnowledgeBase(config);
        kbase.addKnowledgePackages(kpkgs);
        System.out.println("Created engine for ruleset " + rs);
    }

    private static String cacheHash(Ruleset ruleset) throws IOException {
        StringWriter sw = new StringWriter();
        rulesetReader.write(ruleset, sw);
        return Ruleset.hash(sw.toString());
    }
    
    private static Collection<KnowledgePackage> cacheRead(String rsHash) {
//        return null;
        try {
            File file = new File(getCacheDir(), rsHash + ".ser");
            InputStream is = new FileInputStream(file);
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
        } catch (IOException e) {
            return null;
        }
    }

    private void cacheWrite(String rsHash, Collection<KnowledgePackage> kpkgs) throws IOException {
        File cacheDir = getCacheDir();

        if (cacheLimit > 0) {
            File[] cachedFiles = cacheDir.listFiles(new FileFilter() {
                public boolean accept(File f) {
                    return f.getName().endsWith(".ser");
                }
            });
            if (cachedFiles.length >= cacheLimit) {
                // need to trim oldest files from cache
                Arrays.sort(cachedFiles, new Comparator<File>() {
                    public int compare(File f1, File f2) {
                        long m1 = f1.lastModified();
                        long m2 = f2.lastModified();
                        return m1 < m2 ? -1 : m2 > m1 ? 1 : 0;
                    }
                });
                for (int i = 0; i < cachedFiles.length - cacheLimit; ++i)
                    cachedFiles[i].delete();
            }
        }

        File file = new File(cacheDir, rsHash + ".ser");
        OutputStream os = new FileOutputStream(file);
        try {
            ObjectOutputStream oos = new ObjectOutputStream(os);
            try {
                oos.writeObject(kpkgs);
            } finally {
                oos.close();
            }
        } catch (RuntimeException e) {
            throw new IOException(e);
        } finally {
            os.close();
        }
    }

    private static File getCacheDir() throws IOException {
        File f = new File(System.getProperty("user.home"), ".pcgen-drools/cache");
        f.mkdirs();
        if (!f.exists())
            throw new IOException("Failed to create cache dir");
        return f;
    }

    public static Session createSession(Ruleset rs) throws DroolsParserException, IOException {
        return getEngine(rs).createSession();
    }
    public static Session createSession(String... names) throws DroolsParserException, IOException {
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

    public static Engine getEngine(Ruleset rs) throws DroolsParserException, IOException {
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
