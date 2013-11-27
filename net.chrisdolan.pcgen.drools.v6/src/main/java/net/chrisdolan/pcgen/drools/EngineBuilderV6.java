package net.chrisdolan.pcgen.drools;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.regex.Pattern;

import net.chrisdolan.pcgen.drools.EngineBuilder;
import net.chrisdolan.pcgen.drools.ParseException;
import net.chrisdolan.pcgen.drools.Ruleset;
import net.chrisdolan.pcgen.drools.Ruleset.Rule;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.CompositeKnowledgeBuilder;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.core.io.impl.ClassPathResource;
import org.drools.core.io.impl.ReaderResource;
import org.drools.core.io.impl.UrlResource;
import org.drools.definition.KnowledgePackage;
import org.drools.impl.adapters.ResourceAdapter;
import org.kie.api.io.Resource;

final class EngineBuilderV6 implements EngineBuilder {
        public Collection<KnowledgePackage> CreateKnowledgePkgs(Ruleset rs) throws IOException, ParseException {
            KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
            CompositeKnowledgeBuilder batch = kbuilder.batch();
            for (Rule rule : rs.getRules()) {
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
                batch.add(new ResourceAdapter(r), ResourceType.getResourceType(rule.getType()));
            }
            batch.build();
            if (kbuilder.hasErrors())
                throw new ParseException(kbuilder.getErrors().toString());

            return kbuilder.getKnowledgePackages();
        }

        public KnowledgeBase CreateKnowledgeBase(Collection<KnowledgePackage> kpkgs) {
            //KnowledgeBaseConfiguration config = KnowledgeBaseFactory.newKnowledgeBaseConfiguration(null, getClass().getClassLoader());
            KnowledgeBaseConfiguration config = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
            //config.setOption(EventProcessingOption.STREAM);
            KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(config);
            kbase.addKnowledgePackages(kpkgs);

//          KieServices ksvcs = KieServices.Factory.get();
//          KieFileSystem kfs = ksvcs.newKieFileSystem();
//          //ksvcs.getKieClasspathContainer().newKieSession();
//          ksvcs.getRepository().addKieModule(resource, dependencies)
//          KieBuilder builder = ksvcs.newKieBuilder(new File("."));

            return kbase;
        }
    }