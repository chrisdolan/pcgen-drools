package net.chrisdolan.pcgen.drools;

import junit.framework.Assert;
import net.chrisdolan.pcgen.drools.type.DamageDice;
import net.chrisdolan.pcgen.drools.type.DamageDice.Randomizer;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestDice {
    @BeforeClass
    public static void setup() {
        DamageDice.setRandomizer(new Randomizer() {
            public int roll(int sides) {
                return sides/2;
            }
        });
    }
    @AfterClass
    public static void teardown() {
        DamageDice.setRandomizer(null);
    }

    @Test
    public void test() {
        Assert.assertEquals(1, roll("1"));
        Assert.assertEquals(3, roll("d6"));
        Assert.assertEquals(6, roll("2d6"));
        Assert.assertEquals(6, roll(" 2 d 6 "));
        Assert.assertEquals(9, roll("1d8+5"));
        Assert.assertEquals(20+6-4+50+36, roll("10d5+6-4+d100+9d9"));
    }

    private static int roll(String dice) {
        DamageDice d = new DamageDice("Foo", dice);
        Assert.assertEquals(dice.replaceAll("\\s+", "").replaceAll("^1d", "d"), d.toString());
        return d.roll()[0].value;
    }
}
