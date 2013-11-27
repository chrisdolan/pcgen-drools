package net.chrisdolan.pcgen.drools;

import java.io.IOException;
import java.util.Collection;

import org.drools.KnowledgeBase;
import org.drools.definition.KnowledgePackage;

final class CompatibleEngineBuilder implements EngineBuilder {
    private EngineBuilder other;

    CompatibleEngineBuilder() {
        try {
            String clsName = getClass().getPackage().getName() + "." + "EngineBuilderV6";
            Class<?> cls = getClass().getClassLoader().loadClass(clsName);
            this.other = (EngineBuilder) cls.newInstance();
        } catch (Throwable t){
            this.other = new EngineBuilderV5();
        }
    }

    @Override
    public Collection<KnowledgePackage> CreateKnowledgePkgs(Ruleset rs) throws IOException, ParseException {
        return other.CreateKnowledgePkgs(rs);
    }

    @Override
    public KnowledgeBase CreateKnowledgeBase(Collection<KnowledgePackage> kpkgs) {
        return other.CreateKnowledgeBase(kpkgs);
    }
}