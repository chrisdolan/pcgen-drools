package net.chrisdolan.pcgen.drools;

import static net.chrisdolan.pcgen.drools.test.PCAssert.assertAc;
import static net.chrisdolan.pcgen.drools.test.PCAssert.assertBABFirst;
import static net.chrisdolan.pcgen.drools.test.PCAssert.assertCMB;
import static net.chrisdolan.pcgen.drools.test.PCAssert.assertCMD;
import static net.chrisdolan.pcgen.drools.test.PCAssert.assertEncumbrance;
import static net.chrisdolan.pcgen.drools.test.PCAssert.assertInitiative;
import static net.chrisdolan.pcgen.drools.test.PCAssert.assertLoadLimits;
import static net.chrisdolan.pcgen.drools.test.PCAssert.assertSaves;

import java.io.IOException;

import net.chrisdolan.pcgen.drools.input.ActionInput;
import net.chrisdolan.pcgen.drools.input.ConditionInput;
import net.chrisdolan.pcgen.drools.input.LevelInput;
import net.chrisdolan.pcgen.drools.input.StatInput;
import net.chrisdolan.pcgen.drools.test.SpecialTestInput;
import net.chrisdolan.pcgen.drools.test.TestInput;
import net.chrisdolan.pcgen.drools.type.ArmorClass;

import org.drools.compiler.DroolsParserException;
import org.junit.Test;

public class TestEngine {
    private static final String[] RULESETS = {"pathfinder", "test_pathfinder"};

    /**
     * Fires up an empty engine and just prints all of the injected facts. This
     * is just for curiosity and diagnostics, not an actual test.
     */
    //@Test
    public void testDump() throws DroolsParserException, IOException {
        Session session = Engine.createSession(RULESETS);
        session.run();
        for (String s : session.dump())
            System.out.println(s);
        session.destroy();
    }

    @Test
    public void testAC() throws DroolsParserException, IOException {
        Session session = Engine.createSession(RULESETS);
        session.insert(ac(ArmorClass.SUBTYPE_ARMOR, 2));
        session.insert(ac(ArmorClass.SUBTYPE_DEFLECTION, 2));
        session.insert(ac(ArmorClass.SUBTYPE_DEFLECTION, 1));
        session.run();
        assertAc(session, ArmorClass.ACTYPE_NORMAL, 14);
        assertAc(session, ArmorClass.ACTYPE_TOUCH, 12);
        session.destroy();
    }

    @Test
    public void testACStack() throws DroolsParserException, IOException {
        Session session = Engine.createSession(RULESETS);
        session.insert(ac(ArmorClass.SUBTYPE_ARMOR, 2));
        session.insert(ac(ArmorClass.SUBTYPE_ARMOR, 2));
        session.run();
        assertAc(session, ArmorClass.ACTYPE_NORMAL, 12);
        session.destroy();
    }

    @Test
    public void testACOther() throws DroolsParserException, IOException {
        Session session = Engine.createSession(RULESETS);
        session.insert(ac(ArmorClass.SUBTYPE_ARMOR, 2));
        session.insert(ac(ArmorClass.SUBTYPE_OTHER, 2));
        session.insert(ac(ArmorClass.SUBTYPE_OTHER, 1));
        session.run();
        assertAc(session, ArmorClass.ACTYPE_NORMAL, 15);
        assertAc(session, ArmorClass.ACTYPE_TOUCH, 13);
        session.destroy();
    }

    @Test
    public void testACDex() throws DroolsParserException, IOException {
        Session session = Engine.createSession(RULESETS);
        session.insert(new StatInput(StatInput.DEX, 18));
        session.run();
        assertAc(session, ArmorClass.ACTYPE_NORMAL, 14);
        session.destroy();
    }

    @Test
    public void testACFlatFooted() throws DroolsParserException, IOException {
        Session session = Engine.createSession(RULESETS);
        session.insert(ac(ArmorClass.SUBTYPE_ARMOR, 1));
        session.insert(ac(ArmorClass.SUBTYPE_SHIELD, 5));
        session.insert(ac(ArmorClass.SUBTYPE_DEXTERITY, 10));
        session.insert(ac(ArmorClass.SUBTYPE_DODGE, 7));
        session.insert(new ConditionInput(ConditionInput.TYPE_FLATFOOTED));
        session.run();
        assertAc(session, ArmorClass.ACTYPE_NORMAL, 16);
        assertAc(session, ArmorClass.ACTYPE_TOUCH, 10);
        session.destroy();
    }

    @Test
    public void testACMonk() throws DroolsParserException, IOException {
        Session session = Engine.createSession(RULESETS);
        session.insert(new StatInput(StatInput.DEX, 12));
        session.insert(new StatInput(StatInput.WIS, 18));
        for (int i=0;i<11;++i)
            session.insert(new LevelInput("Monk"));
        session.run();
        assertAc(session, ArmorClass.ACTYPE_NORMAL, 17);
        assertAc(session, ArmorClass.ACTYPE_TOUCH, 17);
        session.destroy();
    }

    @Test
    public void testACEncumberedMonk() throws DroolsParserException, IOException {
        Session session = Engine.createSession(RULESETS);
        session.insert(new StatInput(StatInput.DEX, 18));
        session.insert(new TestInput("Encumbrance", "Heavy", 1));
        session.run();
        assertAc(session, ArmorClass.ACTYPE_NORMAL, 11);
        assertAc(session, ArmorClass.ACTYPE_TOUCH, 11);
        session.destroy();
    }

    @Test
    public void testACChargeLunge() throws DroolsParserException, IOException {
        Session session = Engine.createSession(RULESETS);
        session.insert(new ActionInput(ActionInput.TYPE_CHARGE));
        session.insert(new ActionInput(ActionInput.TYPE_LUNGE));
        session.run();
        assertAc(session, ArmorClass.ACTYPE_NORMAL, 6);
        assertAc(session, ArmorClass.ACTYPE_TOUCH, 6);
        session.destroy();
    }

    @Test
    public void testACHelpless() throws DroolsParserException, IOException {
        Session session = Engine.createSession(RULESETS);
        session.insert(ac(ArmorClass.SUBTYPE_ARMOR, 3));
        session.insert(new StatInput(StatInput.DEX, 12));
        session.insert(new ConditionInput(ConditionInput.TYPE_HELPLESS));
        session.run();
        assertAc(session, ArmorClass.ACTYPE_NORMAL, 8);
        assertAc(session, ArmorClass.ACTYPE_TOUCH, 5);
        session.destroy();
    }

    @Test
    public void testACProne() throws DroolsParserException, IOException {
        Session session = Engine.createSession(RULESETS);
        session.insert(new ConditionInput(ConditionInput.TYPE_PRONE));
        session.run();
        assertAc(session, ArmorClass.ACTYPE_NORMAL, 10);
        assertAc(session, ArmorClass.ACTYPE_TOUCH, 10);
        assertAc(session, ArmorClass.ACTYPE_MELEE, 6);
        assertAc(session, ArmorClass.ACTYPE_MELEE_TOUCH, 6);
        assertAc(session, ArmorClass.ACTYPE_RANGE, 14);
        assertAc(session, ArmorClass.ACTYPE_RANGE_TOUCH, 14);
        session.destroy();
    }

    @Test
    public void testTwoConditions() throws DroolsParserException, IOException {
        Session session = Engine.createSession(RULESETS);
        session.insert(new ConditionInput(ConditionInput.TYPE_STUNNED));
        session.insert(new ConditionInput(ConditionInput.TYPE_STUNNED));
        session.run();
        assertAc(session, ArmorClass.ACTYPE_NORMAL, 8);
        assertAc(session, ArmorClass.ACTYPE_TOUCH, 8);
        session.destroy();
    }

    @Test
    public void testACSize() throws DroolsParserException, IOException {
        Session session = Engine.createSession(RULESETS);
        SpecialTestInput lastSize;
        session.insert(lastSize = new SpecialTestInput("Size", "Colossal", 1));
        session.run();
        assertAc(session, ArmorClass.ACTYPE_NORMAL, 2);
        session.retract(lastSize);
        session.insert(lastSize = new SpecialTestInput("Size", "Huge", 1));
        session.run();
        assertAc(session, ArmorClass.ACTYPE_NORMAL, 8);
        session.retract(lastSize);
        session.insert(lastSize = new SpecialTestInput("Size", "Small", 1));
        session.run();
        assertAc(session, ArmorClass.ACTYPE_NORMAL, 11);
        session.destroy();
    }

    @Test
    public void testWeightLimits() throws DroolsParserException, IOException {
        Session session = Engine.createSession(RULESETS);
        StatInput ability;
        session.insert(ability = new StatInput(StatInput.STR, 11));
        session.run();
        assertLoadLimits(session, 38, 76, 115);
        session.retract(ability);
        session.insert(ability = new StatInput(StatInput.STR, 31));
        session.run();
        assertLoadLimits(session, 153*4, 306*4, 460*4);
        session.retract(ability);
        session.insert(ability = new StatInput(StatInput.STR, 64));
        session.run();
        assertLoadLimits(session, 233*4*4*4*4, 466*4*4*4*4, 700*4*4*4*4);
        session.destroy();
    }

    @Test
    public void testEncumbrance() throws DroolsParserException, IOException {
        Session session = Engine.createSession(RULESETS);
        TestInput last;
        session.insert(new StatInput(StatInput.STR, 11));
        session.insert(last = new TestInput("Weight", "PC", 1));
        session.run();
        assertEncumbrance(session, "Light");
        session.retract(last);
        session.insert(last = new TestInput("Weight", "PC", 38));
        session.run();
        assertEncumbrance(session, "Light");
        session.retract(last);
        session.insert(last = new TestInput("Weight", "PC", 39));
        session.run();
        assertEncumbrance(session, "Medium");
        session.retract(last);
        session.insert(last = new TestInput("Weight", "PC", 76));
        session.run();
        assertEncumbrance(session, "Medium");
        session.retract(last);
        session.insert(last = new TestInput("Weight", "PC", 77));
        session.run();
        assertEncumbrance(session, "Heavy");
        session.retract(last);
        session.insert(last = new TestInput("Weight", "PC", 115));
        session.run();
        assertEncumbrance(session, "Heavy");
        session.retract(last);
        session.insert(last = new TestInput("Weight", "PC", 116));
        session.run();
        assertEncumbrance(session, "Overloaded");
        session.destroy();
    }

    @Test
    public void testInitiative() throws DroolsParserException, IOException {
        Session session = Engine.createSession(RULESETS);
        session.run();
        assertInitiative(session, 0);
        StatInput ability;
        session.insert(ability = new StatInput(StatInput.DEX, 10));
        session.run();
        assertInitiative(session, 0);
        session.retract(ability);
        session.insert(ability = new StatInput(StatInput.DEX, 4));
        session.run();
        assertInitiative(session, -3);
        session.retract(ability);
        session.insert(ability = new StatInput(StatInput.DEX, 64));
        session.run();
        assertInitiative(session, 27);
        session.destroy();
    }

    @Test
    public void testSaves() throws DroolsParserException, IOException {
        Session session = Engine.createSession(RULESETS);
        session.insert(new StatInput(StatInput.CON, 14));
        session.insert(new StatInput(StatInput.DEX, 12));
        session.insert(new StatInput(StatInput.WIS, 8));
        session.run();
        assertSaves(session, 2, 1, -1);
        TestInput last;
        session.insert(last = new TestInput("ClassLevel", "Monk", 1));
        session.run();
        assertSaves(session, 4, 3, 1);
        session.retract(last);
        session.insert(last = new TestInput("ClassLevel", "Monk", 2));
        session.run();
        assertSaves(session, 5, 4, 2);
        session.retract(last);
        session.insert(last = new TestInput("ClassLevel", "Monk", 20));
        session.run();
        assertSaves(session, 14, 13, 11);
        session.insert(new ConditionInput("Shaken"));
        session.run();
        assertSaves(session, 12, 11, 9);
        session.insert(last = new TestInput("ClassLevel", "Fighter", 1));
        session.run();
        assertSaves(session, 14, 11, 9);
        session.retract(last);
        session.insert(last = new TestInput("ClassLevel", "Fighter", 20));
        session.run();
        assertSaves(session, 24, 17, 15);
        session.destroy();
    }

    @Test
    public void testBAB() throws DroolsParserException, IOException {
        Session session = Engine.createSession(RULESETS);
        session.insert(new StatInput(StatInput.STR, 18));
        session.insert(new StatInput(StatInput.DEX, 12));
        session.run();
        assertBABFirst(session, 0);
        assertCMB(session, "Grapple", 4);
        assertCMD(session, "Grapple", 15);
        TestInput last;
        session.insert(last = new TestInput("ClassLevel", "Monk", 1));
        session.run();
        assertBABFirst(session, 0);
        assertCMB(session, "Grapple", 4);
        assertCMD(session, "Grapple", 15);
        session.retract(last);
        session.insert(last = new TestInput("ClassLevel", "Monk", 2));
        session.run();
        assertBABFirst(session, 1);
        assertCMB(session, "Grapple", 5);
        assertCMD(session, "Grapple", 16);
        session.retract(last);
        session.insert(last = new TestInput("ClassLevel", "Monk", 3));
        session.run();
        assertBABFirst(session, 2);
        assertCMB(session, "Grapple", 6);
        assertCMD(session, "Grapple", 17);
        session.retract(last);
        session.insert(last = new TestInput("ClassLevel", "Monk", 19));
        session.run();
        assertBABFirst(session, 14);
        assertCMB(session, "Grapple", 18);
        assertCMD(session, "Grapple", 29);
        session.retract(last);
        session.insert(last = new TestInput("ClassLevel", "Monk", 20));
        session.run();
        assertBABFirst(session, 15);
        assertCMB(session, "Grapple", 19);
        assertCMD(session, "Grapple", 30);
        session.insert(last = new TestInput("ClassLevel", "Fighter", 20));
        session.run();
        assertBABFirst(session, 35);
        assertCMB(session, "Grapple", 39);
        assertCMD(session, "Grapple", 50);
        session.destroy();
    }

    private static TestInput ac(String subtype, int value) {
        return new TestInput(ArmorClass.TYPE, subtype, value);
    }
}
