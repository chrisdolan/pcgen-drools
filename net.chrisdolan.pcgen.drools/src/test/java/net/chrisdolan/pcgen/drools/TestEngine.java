package net.chrisdolan.pcgen.drools;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import net.chrisdolan.pcgen.drools.input.AbilityInput;
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
        engine = new Engine(); // this is by far the most expensive part
    }

    @Test
    public void testAC() throws DroolsParserException, IOException {
//        long start = System.currentTimeMillis();
        Session session = engine.createSession();
//        System.out.println("elapsed " + (System.currentTimeMillis() - start));
//        session.run();
//        System.out.println("elapsed " + (System.currentTimeMillis() - start));
        session.insert(ac(ArmorClass.SUBTYPE_ARMOR, 2));
        session.insert(ac(ArmorClass.SUBTYPE_DEFLECTION, 2));
        session.insert(ac(ArmorClass.SUBTYPE_DEFLECTION, 1));
//        System.out.println("elapsed " + (System.currentTimeMillis() - start));
        session.run();
//        System.out.println("elapsed " + (System.currentTimeMillis() - start));
        assertAc(session, ArmorClass.ACTYPE_NORMAL, 14);
        assertAc(session, ArmorClass.ACTYPE_TOUCH, 12);
//        System.out.println("elapsed " + (System.currentTimeMillis() - start));
        session.destroy();
//        System.out.println("elapsed " + (System.currentTimeMillis() - start));
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
        session.insert(new ConditionInput(ConditionInput.TYPE_CHARGE));
        session.insert(new ConditionInput(ConditionInput.TYPE_LUNGE));
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

    private void assertEncumbrance(Session session, String encExpected) {
        String encGot = session.querySingle(String.class, "Query.Encumbrance");
        Assert.assertEquals(encExpected, encGot);
//        Collection<Object> enc = session.search(new ObjectFilter() {
//            @Override
//            public boolean accept(Object object) {
//                return object instanceof Input && ((Input)object).getType().equals("Encumbrance");
//            }
//        });
//        List<String> got = new ArrayList<String>();
//        for (Object o : enc)
//            got.add(((Input)o).getSubtype());
//        Assert.assertEquals(Arrays.asList(enclevel), got);
    }

    private void assertLoadLimits(Session session, int light, int medium, int heavy) {
        List<Object> got = session.queryAll("Query.LoadLimits");
        Assert.assertEquals(Arrays.asList(heavy, light, medium), got);
//        Collection<Object> limits = session.search(new ObjectFilter() {
//            @Override
//            public boolean accept(Object object) {
//                return object instanceof Input && ((Input)object).getType().equals("LoadLimit");
//            }
//        });
//        Map<String,Integer> expected = new HashMap<String, Integer>();
//        expected.put("Light", light);
//        expected.put("Medium", medium);
//        expected.put("Heavy", heavy);
//        Map<String,Integer> got = new HashMap<String, Integer>();
//        for (Object limit : limits)
//            got.put(((Input)limit).getSubtype(), ((Input)limit).getValue());
//        Assert.assertEquals(expected, got);
    }

    private void assertAc(Session session, String actype, int acExpected) {
        int acGot = session.querySingle(Integer.class, "Query.ArmorClass.ByType", actype);
        Assert.assertEquals(acExpected, acGot);
//        Collection<Object> acs = session.search(new ACFilter(actype));
//        Assert.assertEquals(Arrays.asList(new ArmorClass(actype, acExpected)), new ArrayList<Object>(acs));
    }

    private Input ac(String subtype, int value) {
        return new Input(ArmorClass.TYPE, subtype, value);
    }
    
//    private final class ACFilter implements ObjectFilter {
//        private final String actype;
//
//        public ACFilter(String actype) {
//            this.actype = actype;
//        }
//
//        public boolean accept(Object object) {
//            return object instanceof ArmorClass && ((ArmorClass) object).getActype().equals(actype);
//        }
//    }
}
