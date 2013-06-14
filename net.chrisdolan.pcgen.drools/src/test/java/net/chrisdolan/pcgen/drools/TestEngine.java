package net.chrisdolan.pcgen.drools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import net.chrisdolan.pcgen.drools.input.AbilityInput;
import net.chrisdolan.pcgen.drools.input.ConditionInput;
import net.chrisdolan.pcgen.drools.input.Input;
import net.chrisdolan.pcgen.drools.type.ArmorClass;

import org.drools.compiler.DroolsParserException;
import org.drools.runtime.ObjectFilter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestEngine {
    private Engine engine;
    
    @Before
    public void before() throws DroolsParserException, IOException {
        engine = new Engine();
    }
    
    private final class ACFilter implements ObjectFilter {
        private final String actype;

        public ACFilter(String actype) {
            this.actype = actype;
        }

        public boolean accept(Object object) {
            return object instanceof ArmorClass && ((ArmorClass) object).getAcType().equals(actype);
        }
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

    private void assertAc(Session session, String actype, int ac) {
        Collection<Object> acs = session.query(new ACFilter(actype));
        Assert.assertEquals(Arrays.asList(new ArmorClass(actype, ac)), new ArrayList<Object>(acs));
    }

    private Input ac(String subtype, int value) {
        return new Input(ArmorClass.TYPE, subtype, value);
    }
}
