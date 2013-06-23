package net.chrisdolan.pcgen.drools.test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.chrisdolan.pcgen.drools.Session;

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

    private static void assertInteger(Session session, String query, int expected) {
        int got = session.querySingle(Integer.class, query);
        Assert.assertEquals(expected, got);
    }
}
