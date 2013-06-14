package net.chrisdolan.pcgen.drools;

import java.io.IOException;
import java.util.Collection;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
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
        
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.batch()
            .add(new ClassPathResource("rules.drl", getClass()), ResourceType.DRL)
            .build();
        if (kbuilder.hasErrors())
            throw new DroolsParserException(kbuilder.getErrors().toString());

        KnowledgeBaseConfiguration config = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        config.setOption(EventProcessingOption.STREAM);
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(config);
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        ksession = kbase.newStatefulKnowledgeSession();
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
