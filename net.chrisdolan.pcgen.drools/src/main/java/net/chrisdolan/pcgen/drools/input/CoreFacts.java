package net.chrisdolan.pcgen.drools.input;

import java.util.Arrays;
import java.util.List;

import net.chrisdolan.pcgen.drools.type.ArmorClass;
import net.chrisdolan.pcgen.drools.type.StackRule;

public class CoreFacts {
    public static List<Object> get() {
        return Arrays.<Object>asList(
                new Input(ArmorClass.TYPE, ArmorClass.SUBTYPE_BASE, 10),
                new StackRule(ArmorClass.TYPE, ArmorClass.SUBTYPE_DODGE, "Sum"),
                new StackRule(ArmorClass.TYPE, ArmorClass.SUBTYPE_OTHER, "Sum")
                );
    }
}
