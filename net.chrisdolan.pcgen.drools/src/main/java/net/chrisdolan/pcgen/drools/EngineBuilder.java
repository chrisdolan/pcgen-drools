package net.chrisdolan.pcgen.drools;

import java.io.IOException;
import java.util.Collection;

import org.drools.KnowledgeBase;
import org.drools.definition.KnowledgePackage;

public interface EngineBuilder {
    Collection<KnowledgePackage> CreateKnowledgePkgs(Ruleset rs) throws IOException, ParseException;
    KnowledgeBase CreateKnowledgeBase(Collection<KnowledgePackage> kpkgs);
}