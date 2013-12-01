package net.chrisdolan.pcgen.drools.test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.chrisdolan.pcgen.drools.Session;
import net.chrisdolan.pcgen.drools.input.StatInput;

import org.junit.Assert;

public class PCAssert {

    public static void assertBABFirst(Session session, int expected) {
        assertInteger(session, "Query.BaseAttackBonus.Highest", expected);
    }
    public static void assertCMB(Session session, String type, int expected) {
        assertInteger(session, "Query.CMB."+type, expected);
    }
    public static void assertCMD(Session session, String type, int expected) {
        assertInteger(session, "Query.CMD."+type, expected);
    }

    public static void assertSaves(Session session, int fort, int refl, int will) {
        Map<String,Integer> got = session.queryToMap(Integer.class, "Query.SavingThrows");
        Map<String,Integer> expected = new HashMap<String, Integer>();
        expected.put("fortitude", fort);
        expected.put("reflex", refl);
        expected.put("will", will);
        Assert.assertEquals(expected, got);
    }

    public static void assertInitiative(Session session, int expected) {
        assertInteger(session, "Query.Initiative", expected);
    }

    public static void assertEncumbrance(Session session, String encExpected) {
        String encGot = session.querySingle(String.class, "Query.Encumbrance");
        Assert.assertEquals(encExpected, encGot);
    }

    public static void assertLoadLimits(Session session, int light, int medium, int heavy) {
        Map<String,Integer> got = session.queryToMap(Integer.class, "Query.LoadLimits");
        Map<String,Integer> expected = new HashMap<String, Integer>();
        expected.put("light", light);
        expected.put("medium", medium);
        expected.put("heavy", heavy);
        Assert.assertEquals(expected, got);
    }

    public static void assertAc(Session session, String actype, int acExpected) {
        int acGot = session.querySingle(Integer.class, "Query.ArmorClass.ByType", actype);
        Assert.assertEquals(acExpected, acGot);
    }

    public static void assertSpeed(Session session, int speedExpected) {
        assertInteger(session, "Query.Speed", speedExpected);
    }

    public static void assertConditions(Session session, String... expectedConditions) {
        List<String> gotConditions = session.queryColumn(String.class, "Query.Conditions");
        Assert.assertEquals(Arrays.asList(expectedConditions), gotConditions);
        
    }
    public static void assertHitpoints(Session session, int hitpoints) {
        assertInteger(session, "Query.Hitpoints", hitpoints);
    }

    public static void assertProperty(Session session, String key, String valueExpected) {
        String valueGot = session.querySingle(String.class, "Query.Property", key);
        Assert.assertEquals(valueExpected, valueGot);
    }

    public static void assertRace(Session session, String raceExpected) {
        String raceGot = session.querySingle(String.class, "Query.Race.Name");
        Assert.assertEquals(raceExpected, raceGot);
    }

    public static void assertPCLevel(Session session, int level) {
        assertInteger(session, "Query.Class.PCLevel", level);
    }

    public static void assertClassLevel(Session session, String className, int levelExpected) {
        int levelGot = session.querySingle(Integer.class, "Query.Class.Level", className);
        Assert.assertEquals(levelExpected, levelGot);
    }
    public static void assertClassLevels(Session session, Map<String,Integer> expected) {
        Map<String,Integer> got = session.queryPairs(Integer.class, "Query.Class.Level.All");
        Assert.assertEquals(expected, got);
    }

    public static void assertFavoredClasses(Session session, String... expectedClasses) {
        List<String> gotClasses = session.queryColumn(String.class, "Query.FavoredClass.All");
        Assert.assertEquals(Arrays.asList(expectedClasses), gotClasses);
    }

    public static void assertStats(Session session, int str, int dex, int con, int int_, int wis, int cha) {
        Map<String,Integer> got = session.queryPairs(Integer.class, "Query.Stat.All");
        Map<String,Integer> expected = new HashMap<String, Integer>();
        expected.put(StatInput.STR, str);
        expected.put(StatInput.DEX, dex);
        expected.put(StatInput.CON, con);
        expected.put(StatInput.INT, int_);
        expected.put(StatInput.WIS, wis);
        expected.put(StatInput.CHA, cha);
        Assert.assertEquals(expected, got);
    }

    public static void assertStatBonuses(Session session, int str, int dex, int con, int int_, int wis, int cha) {
        Map<String,Integer> got = session.queryPairs(Integer.class, "Query.Stat.Bonus.All");
        Map<String,Integer> expected = new HashMap<String, Integer>();
        expected.put(StatInput.STR, str);
        expected.put(StatInput.DEX, dex);
        expected.put(StatInput.CON, con);
        expected.put(StatInput.INT, int_);
        expected.put(StatInput.WIS, wis);
        expected.put(StatInput.CHA, cha);
        Assert.assertEquals(expected, got);
    }

    public static void assertHasAbility(Session session, String name) {
        List<String> got = session.queryColumn(String.class, "Query.Ability.All");
        Assert.assertTrue(got.contains(name));
    }

    public static void assertHasLanguage(Session session, String language) {
        List<String> got = session.queryColumn(String.class, "Query.Language.All");
        Assert.assertTrue(got.contains(language));
    }

    public static void assertNoViolations(Session session) {
        List<String> got = session.queryColumn(String.class, "Query.Violations.Descriptions");
        Assert.assertEquals(Collections.<String>emptyList(), got);
    }

    public static void assertViolations(Session session, String... codes) {
        List<String> got = session.queryColumn(String.class, "Query.Violations.Codes");
        Assert.assertEquals(new HashSet<String>(Arrays.asList(codes)), new HashSet<String>(got));
    }
    public static void assertViolation(Session session, String code) {
        List<String> got = session.queryColumn(String.class, "Query.Violations.Codes");
        if (code.startsWith("!")) {
            Assert.assertFalse("expected '"+code+"', got: " + got,
                    got.contains(code.substring(1)));
        } else {
            Assert.assertTrue("expected '"+code+"', got: " + got,
                    got.contains(code));
        }
    }

    public static void assertNoSkill(Session session, String name) {
        Integer gotObj = session.querySingle(Integer.class, "Query.Skills.SkillBonus", name);
        Assert.assertNull(gotObj);
    }
    public static void assertSkill(Session session, String name, int expected) {
        Integer gotObj = session.querySingle(Integer.class, "Query.Skills.SkillBonus", name);
        Assert.assertNotNull(gotObj);
        int got = gotObj;
        Assert.assertEquals(expected, got);
    }

    private static void assertInteger(Session session, String query, int expected) {
        int got = session.querySingle(Integer.class, query);
        Assert.assertEquals(expected, got);
    }

    public static void assertCircumstances(Session session, Set<String> expected) {
        @SuppressWarnings("unchecked")
        Set<String> got = session.querySingle(Set.class, "Query.KnownCircumstances");
        Assert.assertEquals(expected, got);
    }
}
