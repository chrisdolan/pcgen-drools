package net.chrisdolan.pcgen.drools;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;

import net.chrisdolan.pcgen.drools.input.PC;
import net.chrisdolan.pcgen.drools.input.PCDerived;
import net.chrisdolan.pcgen.drools.input.StatInput;
import net.chrisdolan.pcgen.drools.test.PCAssert;
import net.chrisdolan.pcgen.drools.test.TestInput;
import net.chrisdolan.pcgen.drools.type.ArmorClass;
import net.chrisdolan.pcgen.drools.type.SavingThrow;

import org.junit.Assert;
import org.junit.Test;


public class TestPC {
    static {
        Engine.setUseCache(false);
    }

    @Test
    public void testMonkLevel1() throws ParseException, IOException {
        PCReader pcReader = new PCReader();
        pcReader.setIgnoreAllUnknowns(true);
        PC pc = pcReader.read(getClass().getResource("testmonk.xml"));
        Assert.assertNotNull(pc);

        Session session = Engine.createSession(new Ruleset(pc.getRulesets()));
        session.insert(pc.getInput());
        session.run();

        for (String s : session.dump())
            System.out.println(s);

        PCAssert.assertNoViolations(session);

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
        PCAssert.assertSkill(session, "Acrobatics", 6);
        PCAssert.assertSkill(session, "Acrobatics.Jump", 6);
        PCAssert.assertSkill(session, "Appraise", 0);
        PCAssert.assertSkill(session, "Bluff", -1);
        PCAssert.assertSkill(session, "Climb", 7);
        PCAssert.assertSkill(session, "Craft", 0);
        PCAssert.assertSkill(session, "Craft.UnderwaterBasketWeaving", 4);
        PCAssert.assertSkill(session, "Diplomacy", -1);
        PCAssert.assertSkill(session, "Disguise", -1);
        PCAssert.assertSkill(session, "EscapeArtist", 6);
        PCAssert.assertSkill(session, "Fly", 2);
        PCAssert.assertSkill(session, "Heal", 2);
        PCAssert.assertSkill(session, "Intimidate", -1);
        PCAssert.assertSkill(session, "Perception", 2);
        PCAssert.assertSkill(session, "Perform", -1);
        PCAssert.assertSkill(session, "Ride", 2);
        PCAssert.assertSkill(session, "SenseMotive", 7);
        PCAssert.assertSkill(session, "Stealth", 2);
        PCAssert.assertSkill(session, "Survival", 2);
        PCAssert.assertSkill(session, "Swim", 3);
        PCAssert.assertNoSkill(session, "UseMagicDevice");
        PCAssert.assertSpeed(session, 30);

        PCAssert.assertCircumstances(session, Collections.<String>emptySet());

        // test DarkFear custom rule:
        PCAssert.assertHasAbility(session, "LowLightVision");
        PCAssert.assertHasLanguage(session, "Undercommon");

        PCDerived out = new PCDerived();
        session.insert(out);
        session.run();
        pc.setDerived(out);
        StringWriter sw = new StringWriter();
        pcReader.write(pc, sw);
        System.out.print(sw.toString());

        session.destroy();
    }

    @Test
    public void testMonkLevel20() throws ParseException, IOException {
        PCReader pcReader = new PCReader();
        PC pc = pcReader.read(getClass().getResource("testmonk-lvl20.xml"));
        Assert.assertNotNull(pc);

        Session session = Engine.createSession(new Ruleset(pc.getRulesets()));
        session.insert(pc.getInput());

        // Hacks for unsupported features...
        session.insert(new TestInput(StatInput.TYPE + StatInput.STR, "MagicItem", 6)); // belt
        session.insert(new TestInput(StatInput.TYPE + StatInput.DEX, "MagicItem", 6)); // belt
        session.insert(new TestInput(StatInput.TYPE + StatInput.CON, "MagicItem", 6)); // belt
        session.insert(new TestInput(StatInput.TYPE + StatInput.WIS, "MagicItem", 6)); // headband
        session.insert(new TestInput(ArmorClass.TYPE, ArmorClass.SUBTYPE_ARMOR, 8)); // bracers
        session.insert(new TestInput(ArmorClass.TYPE, ArmorClass.SUBTYPE_DEFLECTION, 5)); // ring
        session.insert(new TestInput(SavingThrow.TYPE + SavingThrow.ALL, "MagicItem", 5)); // cloak
        
        session.run();

        for (String s : session.dump())
            System.out.println(s);

        PCAssert.assertNoViolations(session);

        PCAssert.assertProperty(session, "CharacterName", "Aarn");

        PCAssert.assertStats(session, 23, 20, 20, 10, 22, 8); // str, dex, con, int, wis, cha

        PCAssert.assertAc(session, ArmorClass.ACTYPE_NORMAL, 39);
        PCAssert.assertAc(session, ArmorClass.ACTYPE_TOUCH, 31);
        PCAssert.assertBABFirst(session, 15);
        PCAssert.assertCMB(session, "Grapple", 26);
        PCAssert.assertCMD(session, "Grapple", 52);
        PCAssert.assertClassLevel(session, "Monk", 20);
        PCAssert.assertConditions(session); // none...
        PCAssert.assertEncumbrance(session, "Light");
        PCAssert.assertFavoredClasses(session, "Monk");
        PCAssert.assertHitpoints(session, 233);
        PCAssert.assertInitiative(session, 9);
        PCAssert.assertPCLevel(session, 20);
        PCAssert.assertSaves(session, 22, 22, 23); // fort, refl, will
        PCAssert.assertSpeed(session, 90);

        PCAssert.assertSkill(session, "Acrobatics", 28); // 20 + 5 + 3
        PCAssert.assertSkill(session, "Acrobatics.Jump", 72);
        PCAssert.assertSkill(session, "Appraise", 0);
        PCAssert.assertSkill(session, "Bluff", -1);
        PCAssert.assertSkill(session, "Climb", 29); // 20 + 6 + 3
        PCAssert.assertSkill(session, "Craft", 0);
        PCAssert.assertNoSkill(session, "Craft.UnderwaterBasketWeaving");
        PCAssert.assertSkill(session, "Diplomacy", -1);
        PCAssert.assertSkill(session, "Disguise", -1);
        PCAssert.assertSkill(session, "EscapeArtist", 28); // 20 + 5 + 3
        PCAssert.assertSkill(session, "Fly", 5);
        PCAssert.assertSkill(session, "Heal", 6);
        PCAssert.assertSkill(session, "Intimidate", -1);
        PCAssert.assertNoSkill(session, "Knowledge.Dungeoneering");
        PCAssert.assertNoSkill(session, "Knowledge.Local");
        PCAssert.assertSkill(session, "Perception", 29); // 20 + 6 + 3
        PCAssert.assertSkill(session, "Perform", -1);
        PCAssert.assertSkill(session, "Ride", 5);
        PCAssert.assertSkill(session, "SenseMotive", 30); // TODO: should be 32 due to +2 from SnakeStyle
        PCAssert.assertSkill(session, "Stealth", 5);
        PCAssert.assertSkill(session, "Survival", 6);
        PCAssert.assertSkill(session, "Swim", 6);
        PCAssert.assertNoSkill(session, "UseMagicDevice");

        PCAssert.assertCircumstances(session, Collections.<String>emptySet());

        session.destroy();
    }
}
