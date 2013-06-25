package net.chrisdolan.pcgen.drools;

import java.io.IOException;

import net.chrisdolan.pcgen.drools.input.PC;
import net.chrisdolan.pcgen.drools.test.PCAssert;
import net.chrisdolan.pcgen.drools.type.ArmorClass;

import org.drools.compiler.DroolsParserException;
import org.junit.Assert;
import org.junit.Test;


public class TestPC {

    @Test
    public void testMonk() throws DroolsParserException, IOException {
        PCReader pcReader = new PCReader();
        PC pc = pcReader.read(getClass().getResource("testmonk.xml"));
        Assert.assertNotNull(pc);

        Session session = Engine.createSession(new Ruleset(pc.getRulesets()));
        session.insert(pc.getInput());
        session.run();

        for (String s : session.dump())
            System.out.println(s);

        PCAssert.assertProperty(session, "NoSuchProperty", null);
        PCAssert.assertProperty(session, "CharacterName", "Aarn");
        PCAssert.assertProperty(session, "RandomUnicode", "éö¢");

        PCAssert.assertStats(session, 16, 14, 12, 10, 14, 8); // str, dex, con, int, wis, cha
        PCAssert.assertStatBonuses(session, 3, 2, 1, 0, 2, -1); // str, dex, con, int, wis, cha

        PCAssert.assertAc(session, ArmorClass.ACTYPE_NORMAL, 14);
        PCAssert.assertAc(session, ArmorClass.ACTYPE_TOUCH, 14);
        PCAssert.assertBABFirst(session, 0);
        PCAssert.assertCMB(session, "Grapple", 3);
        PCAssert.assertCMD(session, "Grapple", 17);
        PCAssert.assertClassLevel(session, "Monk", 1);
        PCAssert.assertConditions(session); // none...
        PCAssert.assertEncumbrance(session, "Light");
        PCAssert.assertFavoredClasses(session, "Monk");
        PCAssert.assertHasAbility(session, "Feat.Toughness");
        PCAssert.assertHasLanguage(session, "Common");
        PCAssert.assertHitpoints(session, 13);
        PCAssert.assertInitiative(session, 2);
        PCAssert.assertLoadLimits(session, 76, 153, 230);
        PCAssert.assertPCLevel(session, 1);
        PCAssert.assertRace(session, "Human");
        PCAssert.assertSaves(session, 3, 4, 4); // fort, refl, will
        PCAssert.assertSpeed(session, 30);

        // test DarkFear custom rule:
        PCAssert.assertHasAbility(session, "LowLightVision");
        PCAssert.assertHasLanguage(session, "Undercommon");

        session.destroy();
    }
}
