package net.chrisdolan.pcgen.drools;

import java.io.IOException;
import java.util.Arrays;

import net.chrisdolan.pcgen.drools.input.ActionInput;
import net.chrisdolan.pcgen.drools.input.ConditionInput;
import net.chrisdolan.pcgen.drools.input.LanguageInput;
import net.chrisdolan.pcgen.drools.input.LevelInput;
import net.chrisdolan.pcgen.drools.input.RaceInput;
import net.chrisdolan.pcgen.drools.input.SkillInput;
import net.chrisdolan.pcgen.drools.input.StatInput;
import net.chrisdolan.pcgen.drools.test.PCAssert;
import net.chrisdolan.pcgen.drools.test.SpecialTestInput;
import net.chrisdolan.pcgen.drools.test.TestInput;
import net.chrisdolan.pcgen.drools.type.ArmorClass;

import org.junit.Test;

public class TestEngine {
    private static final String[] RULESETS = {"pathfinder", "test_pathfinder"};
    static {
        Engine.setUseCache(false);
    }

    /**
     * Fires up an empty engine and just prints all of the injected facts. This
     * is just for curiosity and diagnostics, not an actual test.
     */
    //@Test
    public void testDump() throws ParseException, IOException {
        Session session = Engine.createSession(RULESETS);
        session.run();
        for (String s : session.dump())
            System.out.println(s);
        session.destroy();
    }

    @Test
    public void testAC() throws ParseException, IOException {
        Session session = Engine.createSession(RULESETS);
        session.insert(ac(ArmorClass.SUBTYPE_ARMOR, 2));
        session.insert(ac(ArmorClass.SUBTYPE_DEFLECTION, 2));
        session.insert(ac(ArmorClass.SUBTYPE_DEFLECTION, 1));
        session.run();
        PCAssert.assertAc(session, ArmorClass.ACTYPE_NORMAL, 14);
        PCAssert.assertAc(session, ArmorClass.ACTYPE_TOUCH, 12);
        PCAssert.assertConditions(session); // none
        session.destroy();
    }

    @Test
    public void testACStack() throws ParseException, IOException {
        Session session = Engine.createSession(RULESETS);
        session.insert(ac(ArmorClass.SUBTYPE_ARMOR, 2));
        session.insert(ac(ArmorClass.SUBTYPE_ARMOR, 2));
        session.run();
        PCAssert.assertAc(session, ArmorClass.ACTYPE_NORMAL, 12);
        PCAssert.assertConditions(session); // none
        session.destroy();
    }

    @Test
    public void testACOther() throws ParseException, IOException {
        Session session = Engine.createSession(RULESETS);
        session.insert(ac(ArmorClass.SUBTYPE_ARMOR, 2));
        session.insert(ac(ArmorClass.SUBTYPE_OTHER, 2));
        session.insert(ac(ArmorClass.SUBTYPE_OTHER, 1));
        session.run();
        PCAssert.assertAc(session, ArmorClass.ACTYPE_NORMAL, 15);
        PCAssert.assertAc(session, ArmorClass.ACTYPE_TOUCH, 13);
        PCAssert.assertConditions(session); // none
        session.destroy();
    }

    @Test
    public void testACDex() throws ParseException, IOException {
        Session session = Engine.createSession(RULESETS);
        session.insert(new StatInput(StatInput.DEX, 18));
        session.run();
        PCAssert.assertAc(session, ArmorClass.ACTYPE_NORMAL, 14);
        PCAssert.assertConditions(session); // none
        session.destroy();
    }

    @Test
    public void testACFlatFooted() throws ParseException, IOException {
        Session session = Engine.createSession(RULESETS);
        session.insert(ac(ArmorClass.SUBTYPE_ARMOR, 1));
        session.insert(ac(ArmorClass.SUBTYPE_SHIELD, 5));
        session.insert(ac(ArmorClass.SUBTYPE_DEXTERITY, 10));
        session.insert(ac(ArmorClass.SUBTYPE_DODGE, 7));
        session.insert(new ConditionInput(ConditionInput.TYPE_FLATFOOTED));
        session.run();
        PCAssert.assertAc(session, ArmorClass.ACTYPE_NORMAL, 16);
        PCAssert.assertAc(session, ArmorClass.ACTYPE_TOUCH, 10);
        session.destroy();
    }

    @Test
    public void testACMonk() throws ParseException, IOException {
        Session session = Engine.createSession(RULESETS);
        session.insert(new StatInput(StatInput.DEX, 12));
        session.insert(new StatInput(StatInput.WIS, 18));
        for (int i=0;i<11;++i)
            session.insert(new LevelInput("Monk"));
        session.run();
        PCAssert.assertAc(session, ArmorClass.ACTYPE_NORMAL, 17);
        PCAssert.assertAc(session, ArmorClass.ACTYPE_TOUCH, 17);
        PCAssert.assertConditions(session); // none
        session.destroy();
    }

    @Test
    public void testACEncumberedMonk() throws ParseException, IOException {
        Session session = Engine.createSession(RULESETS);
        session.insert(new StatInput(StatInput.DEX, 18));
        session.insert(new TestInput("Encumbrance", "Heavy", 1));
        session.run();
        PCAssert.assertAc(session, ArmorClass.ACTYPE_NORMAL, 11);
        PCAssert.assertAc(session, ArmorClass.ACTYPE_TOUCH, 11);
        PCAssert.assertConditions(session); // none
        session.destroy();
    }

    @Test
    public void testACChargeLunge() throws ParseException, IOException {
        Session session = Engine.createSession(RULESETS);
        session.insert(new ActionInput(ActionInput.TYPE_CHARGE));
        session.insert(new ActionInput(ActionInput.TYPE_LUNGE));
        session.run();
        PCAssert.assertAc(session, ArmorClass.ACTYPE_NORMAL, 6);
        PCAssert.assertAc(session, ArmorClass.ACTYPE_TOUCH, 6);
        PCAssert.assertConditions(session); // none
        session.destroy();
    }

    @Test
    public void testACHelpless() throws ParseException, IOException {
        Session session = Engine.createSession(RULESETS);
        session.insert(ac(ArmorClass.SUBTYPE_ARMOR, 3));
        session.insert(new StatInput(StatInput.DEX, 12));
        session.insert(new ConditionInput(ConditionInput.TYPE_HELPLESS));
        session.run();
        PCAssert.assertAc(session, ArmorClass.ACTYPE_NORMAL, 8);
        PCAssert.assertAc(session, ArmorClass.ACTYPE_TOUCH, 5);
        PCAssert.assertConditions(session, ConditionInput.TYPE_HELPLESS); // just the one
        session.destroy();
    }

    @Test
    public void testACProne() throws ParseException, IOException {
        Session session = Engine.createSession(RULESETS);
        session.insert(new ConditionInput(ConditionInput.TYPE_PRONE));
        session.run();
        PCAssert.assertAc(session, ArmorClass.ACTYPE_NORMAL, 10);
        PCAssert.assertAc(session, ArmorClass.ACTYPE_TOUCH, 10);
        PCAssert.assertAc(session, ArmorClass.ACTYPE_MELEE, 6);
        PCAssert.assertAc(session, ArmorClass.ACTYPE_MELEE_TOUCH, 6);
        PCAssert.assertAc(session, ArmorClass.ACTYPE_RANGE, 14);
        PCAssert.assertAc(session, ArmorClass.ACTYPE_RANGE_TOUCH, 14);
        PCAssert.assertConditions(session, ConditionInput.TYPE_PRONE); // just the one
        session.destroy();
    }

    @Test
    public void testTwoConditions() throws ParseException, IOException {
        Session session = Engine.createSession(RULESETS);
        session.insert(new ConditionInput(ConditionInput.TYPE_STUNNED));
        session.insert(new ConditionInput(ConditionInput.TYPE_STUNNED));
        session.run();
        PCAssert.assertAc(session, ArmorClass.ACTYPE_NORMAL, 8);
        PCAssert.assertAc(session, ArmorClass.ACTYPE_TOUCH, 8);
        session.destroy();
    }

    @Test
    public void testACSize() throws ParseException, IOException {
        Session session = Engine.createSession(RULESETS);
        SpecialTestInput lastSize;
        session.insert(lastSize = new SpecialTestInput("Size", "Colossal", 1));
        session.run();
        PCAssert.assertAc(session, ArmorClass.ACTYPE_NORMAL, 2);
        session.retract(lastSize);
        session.insert(lastSize = new SpecialTestInput("Size", "Huge", 1));
        session.run();
        PCAssert.assertAc(session, ArmorClass.ACTYPE_NORMAL, 8);
        session.retract(lastSize);
        session.insert(lastSize = new SpecialTestInput("Size", "Small", 1));
        session.run();
        PCAssert.assertAc(session, ArmorClass.ACTYPE_NORMAL, 11);
        PCAssert.assertConditions(session); // none
        session.destroy();
    }

    @Test
    public void testWeightLimits() throws ParseException, IOException {
        Session session = Engine.createSession(RULESETS);
        StatInput ability;
        session.insert(ability = new StatInput(StatInput.STR, 11));
        session.run();
        PCAssert.assertLoadLimits(session, 38, 76, 115);
        session.retract(ability);
        session.insert(ability = new StatInput(StatInput.STR, 31));
        session.run();
        PCAssert.assertLoadLimits(session, 153*4, 306*4, 460*4);
        session.retract(ability);
        session.insert(ability = new StatInput(StatInput.STR, 64));
        session.run();
        PCAssert.assertLoadLimits(session, 233*4*4*4*4, 466*4*4*4*4, 700*4*4*4*4);
        session.destroy();
    }

    @Test
    public void testEncumbrance() throws ParseException, IOException {
        Session session = Engine.createSession(RULESETS);
        TestInput last;
        session.insert(new StatInput(StatInput.STR, 11));
        session.insert(last = new TestInput("Weight", "PC", 1));
        session.run();
        PCAssert.assertEncumbrance(session, "Light");
        session.retract(last);
        session.insert(last = new TestInput("Weight", "PC", 38));
        session.run();
        PCAssert.assertEncumbrance(session, "Light");
        session.retract(last);
        session.insert(last = new TestInput("Weight", "PC", 39));
        session.run();
        PCAssert.assertEncumbrance(session, "Medium");
        session.retract(last);
        session.insert(last = new TestInput("Weight", "PC", 76));
        session.run();
        PCAssert.assertEncumbrance(session, "Medium");
        session.retract(last);
        session.insert(last = new TestInput("Weight", "PC", 77));
        session.run();
        PCAssert.assertEncumbrance(session, "Heavy");
        session.retract(last);
        session.insert(last = new TestInput("Weight", "PC", 115));
        session.run();
        PCAssert.assertEncumbrance(session, "Heavy");
        session.retract(last);
        session.insert(last = new TestInput("Weight", "PC", 116));
        session.run();
        PCAssert.assertEncumbrance(session, "Overloaded");
        session.destroy();
    }

    @Test
    public void testInitiative() throws ParseException, IOException {
        Session session = Engine.createSession(RULESETS);
        session.run();
        PCAssert.assertInitiative(session, 0);
        StatInput ability;
        session.insert(ability = new StatInput(StatInput.DEX, 10));
        session.run();
        PCAssert.assertInitiative(session, 0);
        session.retract(ability);
        session.insert(ability = new StatInput(StatInput.DEX, 4));
        session.run();
        PCAssert.assertInitiative(session, -3);
        session.retract(ability);
        session.insert(ability = new StatInput(StatInput.DEX, 64));
        session.run();
        PCAssert.assertInitiative(session, 27);
        PCAssert.assertConditions(session); // none
        session.destroy();
    }

    @Test
    public void testSaves() throws ParseException, IOException {
        Session session = Engine.createSession(RULESETS);
        session.insert(new StatInput(StatInput.CON, 14));
        session.insert(new StatInput(StatInput.DEX, 12));
        session.insert(new StatInput(StatInput.WIS, 8));
        session.run();
        PCAssert.assertSaves(session, 2, 1, -1);
        TestInput last;
        session.insert(last = new TestInput("ClassLevel", "Monk", 1));
        session.run();
        PCAssert.assertSaves(session, 4, 3, 1);
        session.retract(last);
        session.insert(last = new TestInput("ClassLevel", "Monk", 2));
        session.run();
        PCAssert.assertSaves(session, 5, 4, 2);
        session.retract(last);
        session.insert(last = new TestInput("ClassLevel", "Monk", 20));
        session.run();
        PCAssert.assertSaves(session, 14, 13, 11);
        session.insert(new ConditionInput(ConditionInput.TYPE_SHAKEN));
        session.run();
        PCAssert.assertSaves(session, 12, 11, 9);
        session.insert(last = new TestInput("ClassLevel", "Fighter", 1));
        session.run();
        PCAssert.assertSaves(session, 14, 11, 9);
        session.retract(last);
        session.insert(last = new TestInput("ClassLevel", "Fighter", 20));
        session.run();
        PCAssert.assertSaves(session, 24, 17, 15);
        PCAssert.assertConditions(session, ConditionInput.TYPE_SHAKEN); // just the one
        session.destroy();
    }

    @Test
    public void testBAB() throws ParseException, IOException {
        Session session = Engine.createSession(RULESETS);
        session.insert(new StatInput(StatInput.STR, 18));
        session.insert(new StatInput(StatInput.DEX, 12));
        session.run();
        PCAssert.assertBABFirst(session, 0);
        PCAssert.assertCMB(session, "Grapple", 4);
        PCAssert.assertCMD(session, "Grapple", 15);
        TestInput last;
        session.insert(last = new TestInput("ClassLevel", "Monk", 1));
        session.run();
        PCAssert.assertBABFirst(session, 0);
        PCAssert.assertCMB(session, "Grapple", 4);
        PCAssert.assertCMD(session, "Grapple", 15);
        session.retract(last);
        session.insert(last = new TestInput("ClassLevel", "Monk", 2));
        session.run();
        PCAssert.assertBABFirst(session, 1);
        PCAssert.assertCMB(session, "Grapple", 5);
        PCAssert.assertCMD(session, "Grapple", 16);
        PCAssert.assertSpeed(session, 30);
        session.retract(last);
        session.insert(last = new TestInput("ClassLevel", "Monk", 3));
        session.run();
        PCAssert.assertBABFirst(session, 2);
        PCAssert.assertCMB(session, "Grapple", 7); // +3 for maneuver training instead of +2 for BAB
        PCAssert.assertCMD(session, "Grapple", 17);
        PCAssert.assertSpeed(session, 40);
        session.retract(last);
        session.insert(last = new TestInput("ClassLevel", "Monk", 19));
        session.run();
        PCAssert.assertBABFirst(session, 14);
        PCAssert.assertCMB(session, "Grapple", 23); // +19 for maneuver training instead of +14 for BAB
        PCAssert.assertCMD(session, "Grapple", 33); // +4 for monk level CMD bonus
        session.retract(last);
        session.insert(last = new TestInput("ClassLevel", "Monk", 20));
        session.run();
        PCAssert.assertBABFirst(session, 15);
        PCAssert.assertCMB(session, "Grapple", 24); // +20 for maneuver training instead of +15 for BAB
        PCAssert.assertCMD(session, "Grapple", 35); // +5 for monk level CMD bonus
        PCAssert.assertSpeed(session, 90);
        session.insert(last = new TestInput("ClassLevel", "Fighter", 20));
        session.run();
        //session.dump(System.out);
        PCAssert.assertBABFirst(session, 35);
        PCAssert.assertCMB(session, "Grapple", 44);
        PCAssert.assertCMD(session, "Grapple", 55);
        PCAssert.assertSpeed(session, 90);
        session.insert(new RaceInput("Human", 70));
        session.run();
        PCAssert.assertBABFirst(session, 35);
        PCAssert.assertCMB(session, "Grapple", 44); // would be -6 STR for age => -3 CMB, except TimelessBody => +0
        PCAssert.assertCMD(session, "Grapple", 56); // would be -6 STR, -6 DEX, +3 WIS for age => net -5 CMD, except TimelessBody => +1
        PCAssert.assertConditions(session, "Age.Venerable"); // just the one
        session.destroy();
    }

    @Test
    public void testSkills() throws ParseException, IOException {
        Session session = Engine.createSession(RULESETS);
        session.insert(new TestInput("ClassLevel", "Rogue", 1));
        session.insert(new SkillInput("Stealth", null, 1, "Rogue"));
        session.run();
        PCAssert.assertSkill(session, "Stealth", 4);
        session.insert(new TestInput("Weight", "PC", 90));
        session.run();
        PCAssert.assertSkill(session, "Stealth", -2);
        PCAssert.assertSkill(session, "Intimidate", 0);
        session.insert(new RaceInput(RaceInput.HALFORC));
        session.run();
        PCAssert.assertSkill(session, "Intimidate", 2);
        PCAssert.assertConditions(session); // none
        session.destroy();
    }

    @Test
    public void testLanguages() throws ParseException, IOException {
        Session session = Engine.createSession(RULESETS);
        LevelInput lvl = new LevelInput("Monk");
        lvl.setLanguages(Arrays.asList(new LanguageInput("Baz"))); // this should not affect the racial violations at all
        session.insert(lvl);
        RaceInput last;
        session.insert(last = new RaceInput(RaceInput.HUMAN));
        session.insert(new StatInput(StatInput.INT, 12));
        session.run();
        PCAssert.assertViolation(session, "Language.Racial.Missing");
        PCAssert.assertViolation(session, "!Language.Racial.Excess");
        session.retract(last);
        last.setLanguages(Arrays.asList(new LanguageInput("Foo"), new LanguageInput("Bar")));
        session.insert(last);
        session.run();
        PCAssert.assertViolation(session, "!Language.Racial.Missing");
        PCAssert.assertViolation(session, "Language.Racial.Excess");
        session.retract(last);
        last.setLanguages(Arrays.asList(new LanguageInput("Foo")));
        session.insert(last);
        session.run();
        PCAssert.assertViolation(session, "!Language.Racial.Missing");
        PCAssert.assertViolation(session, "!Language.Racial.Excess");
        session.destroy();
    }

    private static TestInput ac(String subtype, int value) {
        return new TestInput(ArmorClass.TYPE, subtype, value);
    }
}
