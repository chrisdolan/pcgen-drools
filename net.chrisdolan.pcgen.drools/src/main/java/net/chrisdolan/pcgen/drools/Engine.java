package net.chrisdolan.pcgen.drools;

import java.io.IOException;
import java.util.Collection;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

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
import org.drools.io.impl.ClassPathResource;
import org.drools.rule.EntryPoint;
import org.drools.runtime.ObjectFilter;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;

public class Engine {
    private StatefulKnowledgeSession ksession;

    public void create() throws IOException, DroolsParserException {
        Ruleset ruleset = readRuleset();
        
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        CompositeKnowledgeBuilder batch = kbuilder.batch();
        for (Rule rule : ruleset.getRules()) {
            batch.add(new ClassPathResource(rule.getName(), getClass()), ResourceType.getResourceType(rule.getType()));
        }
        batch.build();
        if (kbuilder.hasErrors())
            throw new DroolsParserException(kbuilder.getErrors().toString());

        KnowledgeBaseConfiguration config = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        config.setOption(EventProcessingOption.STREAM);
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(config);
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        ksession = kbase.newStatefulKnowledgeSession();
    }

    private Ruleset readRuleset() throws IOException {
        try {
            Object o = JAXBContext.newInstance(Ruleset.class).createUnmarshaller().unmarshal(getClass().getResourceAsStream("ruleset.xml"));
            if (!(o instanceof Ruleset))
                throw new IOException("Unmarshalled XML is not a Ruleset, but is: " + o.getClass());
            Ruleset rs = (Ruleset) o;
            if (rs.getRules().isEmpty())
                throw new IOException("No rules found in the ruleset");
            return rs;
        } catch (JAXBException e) {
            throw new IOException(e);
        }
    }

    public FactHandle insert(Object obj) {
        return ksession.insert(obj);
    }
    public void remove(Object obj) {
        FactHandle handle = obj instanceof FactHandle ? (FactHandle) obj : ksession.getFactHandle(obj);
        ksession.retract(handle);
    }

    public void run() {
        //System.out.println("--Engine.run--");
        ksession.fireAllRules();
    }

    public Collection<Object> query(ObjectFilter filter) {
        return ksession.getWorkingMemoryEntryPoint(EntryPoint.DEFAULT.getEntryPointId()).getObjects(filter);
    }
    public <T> Collection<T> queryByClass(final Class<T> cls) {
        @SuppressWarnings("unchecked")
        Collection<T> c = (Collection<T>) query(new ObjectFilter() {
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
