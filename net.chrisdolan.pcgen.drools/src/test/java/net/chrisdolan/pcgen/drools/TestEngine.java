package net.chrisdolan.pcgen.drools;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.chrisdolan.pcgen.drools.input.AbilityInput;
import net.chrisdolan.pcgen.drools.input.ActionInput;
import net.chrisdolan.pcgen.drools.input.ConditionInput;
import net.chrisdolan.pcgen.drools.input.Input;
import net.chrisdolan.pcgen.drools.type.ArmorClass;

import org.drools.compiler.DroolsParserException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestEngine {
    private static Engine engine;
    
    @BeforeClass
    public static void before() throws DroolsParserException, IOException {
        engine = new Engine("pathfinder"); // this is by far the most expensive part
    }

    @Test
    public void testAC() throws DroolsParserException, IOException {
        Session session = engine.createSession();
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
        Session session = engine.createSession();
        session.insert(ac(ArmorClass.SUBTYPE_ARMOR, 2));
        session.insert(ac(ArmorClass.SUBTYPE_ARMOR, 2));
        session.run();
        assertAc(session, ArmorClass.ACTYPE_NORMAL, 12);
        session.destroy();
    }

    @Test
    public void testACOther() throws DroolsParserException, IOException {
        Session session = engine.createSession();
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
        Session session = engine.createSession();
        session.insert(new AbilityInput(AbilityInput.DEX, 18));
        session.run();
        assertAc(session, ArmorClass.ACTYPE_NORMAL, 14);
        session.destroy();
    }

    @Test
    public void testACFlatFooted() throws DroolsParserException, IOException {
        Session session = engine.createSession();
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
        Session session = engine.createSession();
        session.insert(new AbilityInput(AbilityInput.DEX, 12));
        session.insert(new AbilityInput(AbilityInput.WIS, 18));
        session.insert(new Input("ClassLevel", "Monk", 11));
        session.run();
        assertAc(session, ArmorClass.ACTYPE_NORMAL, 17);
        assertAc(session, ArmorClass.ACTYPE_TOUCH, 17);
        session.destroy();
    }

    @Test
    public void testACEncumberedMonk() throws DroolsParserException, IOException {
        Session session = engine.createSession();
        session.insert(new AbilityInput(AbilityInput.DEX, 18));
        session.insert(new Input("Encumbrance", "Heavy", 1));
        session.run();
        assertAc(session, ArmorClass.ACTYPE_NORMAL, 11);
        assertAc(session, ArmorClass.ACTYPE_TOUCH, 11);
        session.destroy();
    }

    @Test
    public void testACChargeLunge() throws DroolsParserException, IOException {
        Session session = engine.createSession();
        session.insert(new ActionInput(ActionInput.TYPE_CHARGE));
        session.insert(new ActionInput(ActionInput.TYPE_LUNGE));
        session.run();
        assertAc(session, ArmorClass.ACTYPE_NORMAL, 6);
        assertAc(session, ArmorClass.ACTYPE_TOUCH, 6);
        session.destroy();
    }

    @Test
    public void testACHelpless() throws DroolsParserException, IOException {
        Session session = engine.createSession();
        session.insert(ac(ArmorClass.SUBTYPE_ARMOR, 3));
        session.insert(new AbilityInput(AbilityInput.DEX, 12));
        session.insert(new ConditionInput(ConditionInput.TYPE_HELPLESS));
        session.run();
        assertAc(session, ArmorClass.ACTYPE_NORMAL, 8);
        assertAc(session, ArmorClass.ACTYPE_TOUCH, 5);
        session.destroy();
    }

    @Test
    public void testACProne() throws DroolsParserException, IOException {
        Session session = engine.createSession();
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
        Session session = engine.createSession();
        session.insert(new ConditionInput(ConditionInput.TYPE_STUNNED));
        session.insert(new ConditionInput(ConditionInput.TYPE_STUNNED));
        session.run();
        assertAc(session, ArmorClass.ACTYPE_NORMAL, 8);
        assertAc(session, ArmorClass.ACTYPE_TOUCH, 8);
        session.destroy();
    }

    @Test
    public void testACSize() throws DroolsParserException, IOException {
        Session session = engine.createSession();
        Input lastSize;
        session.insert(lastSize = new Input("Size", "Colossal", 1));
        session.run();
        assertAc(session, ArmorClass.ACTYPE_NORMAL, 2);
        session.retract(lastSize);
        session.insert(lastSize = new Input("Size", "Huge", 1));
        session.run();
        assertAc(session, ArmorClass.ACTYPE_NORMAL, 8);
        session.retract(lastSize);
        session.insert(lastSize = new Input("Size", "Small", 1));
        session.run();
        assertAc(session, ArmorClass.ACTYPE_NORMAL, 11);
        session.destroy();
    }

    @Test
    public void testWeightLimits() throws DroolsParserException, IOException {
        Session session = engine.createSession();
        AbilityInput ability;
        session.insert(ability = new AbilityInput(AbilityInput.STR, 11));
        session.run();
        assertLoadLimits(session, 38, 76, 115);
        session.retract(ability);
        session.insert(ability = new AbilityInput(AbilityInput.STR, 31));
        session.run();
        assertLoadLimits(session, 153*4, 306*4, 460*4);
        session.retract(ability);
        session.insert(ability = new AbilityInput(AbilityInput.STR, 64));
        session.run();
        assertLoadLimits(session, 233*4*4*4*4, 466*4*4*4*4, 700*4*4*4*4);
        session.destroy();
    }

    @Test
    public void testEncumbrance() throws DroolsParserException, IOException {
        Session session = engine.createSession();
        Input last;
        session.insert(new AbilityInput(AbilityInput.STR, 11));
        session.insert(last = new Input("Weight", "PC", 1));
        session.run();
        assertEncumbrance(session, "Light");
        session.retract(last);
        session.insert(last = new Input("Weight", "PC", 38));
        session.run();
        assertEncumbrance(session, "Light");
        session.retract(last);
        session.insert(last = new Input("Weight", "PC", 39));
        session.run();
        assertEncumbrance(session, "Medium");
        session.retract(last);
        session.insert(last = new Input("Weight", "PC", 76));
        session.run();
        assertEncumbrance(session, "Medium");
        session.retract(last);
        session.insert(last = new Input("Weight", "PC", 77));
        session.run();
        assertEncumbrance(session, "Heavy");
        session.retract(last);
        session.insert(last = new Input("Weight", "PC", 115));
        session.run();
        assertEncumbrance(session, "Heavy");
        session.retract(last);
        session.insert(last = new Input("Weight", "PC", 116));
        session.run();
        assertEncumbrance(session, "Overloaded");
        session.destroy();
    }

    @Test
    public void testInitiative() throws DroolsParserException, IOException {
        Session session = engine.createSession();
        session.run();
        assertInitiative(session, 0);
        AbilityInput ability;
        session.insert(ability = new AbilityInput(AbilityInput.DEX, 10));
        session.run();
        assertInitiative(session, 0);
        session.retract(ability);
        session.insert(ability = new AbilityInput(AbilityInput.DEX, 4));
        session.run();
        assertInitiative(session, -3);
        session.retract(ability);
        session.insert(ability = new AbilityInput(AbilityInput.DEX, 64));
        session.run();
        assertInitiative(session, 27);
        session.destroy();
    }

    @Test
    public void testSaves() throws DroolsParserException, IOException {
        Session session = engine.createSession();
        session.insert(new AbilityInput(AbilityInput.CON, 14));
        session.insert(new AbilityInput(AbilityInput.DEX, 12));
        session.insert(new AbilityInput(AbilityInput.WIS, 8));
        session.run();
        assertSaves(session, 2, 1, -1);
        session.destroy();
    }

    private void assertSaves(Session session, int fort, int refl, int will) {
        Map<String,Integer> got = session.queryToMap(Integer.class, "Query.SavingThrows");
        Map<String,Integer> expected = new HashMap<String, Integer>();
        expected.put("fortitude", fort);
        expected.put("reflex", refl);
        expected.put("will", will);
        Assert.assertEquals(expected, got);
    }

    private void assertInitiative(Session session, int initExpected) {
        int initGot = session.querySingle(Integer.class, "Query.Initiative");
        Assert.assertEquals(initExpected, initGot);
    }

    private void assertEncumbrance(Session session, String encExpected) {
        String encGot = session.querySingle(String.class, "Query.Encumbrance");
        Assert.assertEquals(encExpected, encGot);
    }

    private void assertLoadLimits(Session session, int light, int medium, int heavy) {
        Map<String,Integer> got = session.queryToMap(Integer.class, "Query.LoadLimits");
        Map<String,Integer> expected = new HashMap<String, Integer>();
        expected.put("light", light);
        expected.put("medium", medium);
        expected.put("heavy", heavy);
        Assert.assertEquals(expected, got);
    }

    private void assertAc(Session session, String actype, int acExpected) {
        int acGot = session.querySingle(Integer.class, "Query.ArmorClass.ByType", actype);
        Assert.assertEquals(acExpected, acGot);
    }

    private Input ac(String subtype, int value) {
        return new Input(ArmorClass.TYPE, subtype, value);
    }
}
