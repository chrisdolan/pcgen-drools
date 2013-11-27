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
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

import net.chrisdolan.pcgen.drools.Ruleset.Reader;

import org.drools.KnowledgeBase;
import org.drools.definition.KnowledgePackage;

final class CachingEngineBuilder implements EngineBuilder {
    private EngineBuilder other;
    private boolean useCache;
    private int cacheLimit;
    private Reader rulesetReader;

    CachingEngineBuilder(boolean useCache, int cacheLimit, Reader rulesetReader, EngineBuilder other) {
        this.useCache = useCache;
        this.cacheLimit = cacheLimit;
        this.rulesetReader = rulesetReader;
        this.other = other;
    }

    @Override
    public Collection<KnowledgePackage> CreateKnowledgePkgs(Ruleset rs) throws IOException, ParseException {
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
            kpkgs = other.CreateKnowledgePkgs(flattened);
            if (useCache)
                cacheWrite(rsHash, kpkgs);
        }
        return kpkgs;
    }

    @Override
    public KnowledgeBase CreateKnowledgeBase(Collection<KnowledgePackage> kpkgs) {
        return other.CreateKnowledgeBase(kpkgs);
    }

    private String cacheHash(Ruleset ruleset) throws IOException {
        StringWriter sw = new StringWriter();
        rulesetReader.write(ruleset, sw);
        return Ruleset.hash(sw.toString());
    }
    
    private Collection<KnowledgePackage> cacheRead(String rsHash) {
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

    private File getCacheDir() throws IOException {
        File f = new File(System.getProperty("user.home"), ".pcgen-drools/cache");
        f.mkdirs();
        if (!f.exists())
            throw new IOException("Failed to create cache dir");
        return f;
    }
}