package net.chrisdolan.pcgen.drools;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;

import org.drools.FactHandle;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.compiler.DroolsError;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderErrors;
import org.drools.rule.EntryPoint;
import org.drools.runtime.ObjectFilter;

public class Engine {
    private WorkingMemory workingMemory;

    public void create() throws IOException, DroolsParserException {
        InputStream rulesStream = getClass().getResourceAsStream("rules.drl");
        try {
            PackageBuilder packageBuilder = new PackageBuilder();
            packageBuilder.addPackageFromDrl(new InputStreamReader(rulesStream));
            assertNoRuleErrors(packageBuilder);
            RuleBase ruleBase = RuleBaseFactory.newRuleBase();
            ruleBase.addPackage(packageBuilder.getPackage());
            workingMemory = ruleBase.newStatefulSession();
//            for (Object o : CoreFacts.get())
//                workingMemory.insert(o);
        } finally {
            rulesStream.close();
        }
    }

    public void insert(Object obj) {
        workingMemory.insert(obj);
    }
    public void remove(Object obj) {
    	FactHandle handle = workingMemory.getFactHandle(obj);
        workingMemory.retract(handle);
    }

    public void run() {
    	System.out.println("--Engine.run--");
        workingMemory.fireAllRules();
    }

    public Collection<Object> query(ObjectFilter filter) {
        return workingMemory.getWorkingMemoryEntryPoint(EntryPoint.DEFAULT.getEntryPointId()).getObjects(filter);
    }

    public void destroy() {
        workingMemory.dispose();
        workingMemory = null;
    }
    
    private void assertNoRuleErrors(PackageBuilder packageBuilder) {
        PackageBuilderErrors errors = packageBuilder.getErrors();

        if (errors.getErrors().length > 0) {
            StringBuilder errorMessages = new StringBuilder();
            errorMessages.append("Found errors in package builder\n");
            for (int i = 0; i < errors.getErrors().length; i++) {
                DroolsError errorMessage = errors.getErrors()[i];
                errorMessages.append(errorMessage);
                errorMessages.append("\n");
            }
            errorMessages.append("Could not parse knowledge");

            throw new IllegalArgumentException(errorMessages.toString());
        }
    }
}
