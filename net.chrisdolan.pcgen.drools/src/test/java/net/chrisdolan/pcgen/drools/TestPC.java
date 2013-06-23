package net.chrisdolan.pcgen.drools;

import java.io.IOException;

import net.chrisdolan.pcgen.drools.input.PC;
import net.chrisdolan.pcgen.drools.test.PCAssert;
import net.chrisdolan.pcgen.drools.type.ArmorClass;

import org.drools.compiler.DroolsParserException;
import org.junit.Assert;
import org.junit.Test;


public class TestPC {
    private static final String[] RULESETS = {"pathfinder", "test_pathfinder"};

    @Test
    public void testMonk() throws DroolsParserException, IOException {
        PCReader pcReader = new PCReader();
        PC pc = pcReader.read(getClass().getResource("testmonk.xml"));
        Assert.assertNotNull(pc);

        Session session = Engine.createSession(RULESETS);
        session.insert(pc.getInput());
        session.run();

        for (String s : session.dump())
            System.out.println(s);

        PCAssert.assertAc(session, ArmorClass.ACTYPE_NORMAL, 14);
        PCAssert.assertAc(session, ArmorClass.ACTYPE_TOUCH, 14);
        PCAssert.assertSaves(session, 3, 4, 4);
        PCAssert.assertSpeed(session, 30);

        session.destroy();
    }
}
