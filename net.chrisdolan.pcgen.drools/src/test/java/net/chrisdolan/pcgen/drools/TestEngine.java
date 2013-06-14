package net.chrisdolan.pcgen.drools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import net.chrisdolan.pcgen.drools.input.AttributeInput;
import net.chrisdolan.pcgen.drools.input.Condition;
import net.chrisdolan.pcgen.drools.input.Input;
import net.chrisdolan.pcgen.drools.type.ArmorClass;

import org.drools.compiler.DroolsParserException;
import org.drools.runtime.ObjectFilter;
import org.junit.Assert;
import org.junit.Test;

public class TestEngine {

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
        Engine engine = new Engine();
        engine.create();
        engine.insert(ac(ArmorClass.SUBTYPE_ARMOR, 2));
        engine.insert(ac(ArmorClass.SUBTYPE_DEFLECTION, 2));
        engine.insert(ac(ArmorClass.SUBTYPE_DEFLECTION, 1));
        engine.run();
        assertAc(engine, ArmorClass.ACTYPE_NORMAL, 14);
        assertAc(engine, ArmorClass.ACTYPE_TOUCH, 12);
        engine.destroy();
    }

    @Test
    public void testACOther() throws DroolsParserException, IOException {
        Engine engine = new Engine();
        engine.create();
        engine.insert(ac(ArmorClass.SUBTYPE_ARMOR, 2));
        engine.insert(ac(ArmorClass.SUBTYPE_OTHER, 2));
        engine.insert(ac(ArmorClass.SUBTYPE_OTHER, 1));
        engine.run();
        assertAc(engine, ArmorClass.ACTYPE_NORMAL, 15);
        assertAc(engine, ArmorClass.ACTYPE_TOUCH, 13);
        engine.destroy();
    }

    @Test
    public void testACDex() throws DroolsParserException, IOException {
        Engine engine = new Engine();
        engine.create();
        engine.insert(new AttributeInput(AttributeInput.DEX, 18));
        engine.run();
        assertAc(engine, ArmorClass.ACTYPE_NORMAL, 14);
        engine.destroy();
    }

    @Test
    public void testACFlatFooted() throws DroolsParserException, IOException {
        Engine engine = new Engine();
        engine.create();
        engine.insert(ac(ArmorClass.SUBTYPE_ARMOR, 1));
        engine.insert(ac(ArmorClass.SUBTYPE_SHIELD, 5));
        engine.insert(ac(ArmorClass.SUBTYPE_DEXTERITY, 10));
        engine.insert(ac(ArmorClass.SUBTYPE_DODGE, 7));
        engine.insert(new Condition(Condition.TYPE_FLATFOOTED));
        engine.run();
        assertAc(engine, ArmorClass.ACTYPE_NORMAL, 16);
        assertAc(engine, ArmorClass.ACTYPE_TOUCH, 10);
        engine.destroy();
    }

    @Test
    public void testACMonk() throws DroolsParserException, IOException {
        Engine engine = new Engine();
        engine.create();
        engine.insert(new AttributeInput(AttributeInput.DEX, 12));
        engine.insert(new AttributeInput(AttributeInput.WIS, 18));
        engine.insert(new Input("ClassLevel", "Monk", 11));
        engine.run();
        assertAc(engine, ArmorClass.ACTYPE_NORMAL, 17);
        assertAc(engine, ArmorClass.ACTYPE_TOUCH, 17);
        engine.destroy();
    }

    @Test
    public void testACChargeLunge() throws DroolsParserException, IOException {
        Engine engine = new Engine();
        engine.create();
        engine.insert(new Condition(Condition.TYPE_CHARGE));
        engine.insert(new Condition(Condition.TYPE_LUNGE));
        engine.run();
        assertAc(engine, ArmorClass.ACTYPE_NORMAL, 6);
        assertAc(engine, ArmorClass.ACTYPE_TOUCH, 6);
        engine.destroy();
    }

    @Test
    public void testACSize() throws DroolsParserException, IOException {
        Engine engine = new Engine();
        engine.create();
        Input lastSize;
        engine.insert(lastSize = new Input("Size", "Colossal", 1));
        engine.run();
        assertAc(engine, ArmorClass.ACTYPE_NORMAL, 2);
        engine.remove(lastSize);
        engine.insert(lastSize = new Input("Size", "Huge", 1));
        engine.run();
        assertAc(engine, ArmorClass.ACTYPE_NORMAL, 8);
        engine.remove(lastSize);
        engine.insert(lastSize = new Input("Size", "Small", 1));
        engine.run();
        assertAc(engine, ArmorClass.ACTYPE_NORMAL, 11);
        engine.destroy();
    }

    private void assertAc(Engine engine, String actype, int ac) {
        Collection<Object> acs = engine.query(new ACFilter(actype));
        Assert.assertEquals(Arrays.asList(new ArmorClass(actype, ac)), new ArrayList<Object>(acs));
    }

    private Input ac(String subtype, int value) {
        return new Input(ArmorClass.TYPE, subtype, value);
    }
}
