package net.chrisdolan.pcgen.drools.type;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DamageDice {

    public interface Randomizer {
        int roll(int sides);
    }

    private static Randomizer randomizer;
    static { setRandomizer(null); }

    /**
     * This is for testing, you can inject a non-random randomizer!
     * @param randomizer
     */
    public static void setRandomizer(Randomizer randomizer) {
        DamageDice.randomizer = randomizer == null ? new Randomizer() {
            public int roll(int sides) {
                return (int) (Math.random() * (double)sides);
            }
        } : randomizer;
    }
    
    public static class Damage {
        public String type;
        public int value;
        public int critvalue;
    }

    DamageDice[] dice;
    int rolls = 0;
    int sides = 6;
    int plus = 0;
    String damageType = "Bludgeoning";
    int critMult = 1;

    public DamageDice() {
    }
    public DamageDice(String damageType, String asString) throws IllegalArgumentException {
        this.damageType = damageType;
        parse(asString);
    }

    public Damage[] roll() {
        Map<String,Damage> types = new HashMap<String, DamageDice.Damage>();
        if (dice != null) {
            for (DamageDice d : dice) {
                Damage[] dmgs = d.roll();
                for (Damage dmg : dmgs) {
                    Damage old = types.get(dmg.type);
                    if (old == null) {
                        types.put(dmg.type, dmg);
                    } else {
                        old.value += dmg.value;
                        old.critvalue += dmg.critvalue;
                    }
                }
            }
        }
        if (damageType != null) {
            Damage d = types.get(damageType);
            if (d == null) {
                d = new Damage();
                d.type = damageType;
                types.put(damageType, d);
            }
            int roll = plus;
            for (int i=0; i<rolls; ++i) {
                roll += randomizer.roll(sides);
            }
            d.value += roll;
            d.critvalue += roll * critMult;
        }
        return types.values().toArray(new Damage[]{});
    }

    public DamageDice[] getDice() {
        return dice;
    }
    public void setDice(DamageDice[] dice) {
        this.dice = dice;
    }
    public int getRolls() {
        return rolls;
    }
    public void setRolls(int rolls) {
        this.rolls = rolls;
    }
    public int getSides() {
        return sides;
    }
    public void setSides(int sides) {
        this.sides = sides;
    }
    public int getPlus() {
        return plus;
    }
    public void setPlus(int plus) {
        this.plus = plus;
    }
    public String getDamageType() {
        return damageType;
    }
    public void setDamageType(String damageType) {
        this.damageType = damageType;
    }
    public int getCritMult() {
        return critMult;
    }
    public void setCritMult(int critMult) {
        this.critMult = critMult;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (rolls > 0) {
            if (rolls != 1)
                sb.append(rolls);
            sb.append('d').append(sides);
        }
        if (plus != 0) {
            if (plus > 0 && sb.length() > 0)
                sb.append("+");
            sb.append(plus);
        }
        if (dice != null) {
            for (DamageDice d : dice) {
                String s = d.toString();
                if (!s.startsWith("-") && sb.length() > 0)
                    sb.append("+");
                sb.append(s);
            }
        }
        return sb.toString();
    }
    public void parse(String asString) throws IllegalArgumentException {
        String s = asString.replaceFirst("^\\s*", "");
        while (true) {
            DamageDice d = new DamageDice();
            d.damageType = damageType;
            d.critMult = critMult;

            Matcher m = Pattern.compile("^(\\d*)\\s*d\\s*(\\d+)\\s*(.*)").matcher(s);
            if (m.matches()) {
                d.rolls = m.group(1).isEmpty() ? 1 : Integer.parseInt(m.group(1));
                d.sides = Integer.parseInt(m.group(2));
                s = m.group(3);
            } else {
                m = Pattern.compile("^(-?\\d+)\\s*(.*)").matcher(s);
                if (m.matches()) {
                d.plus = Integer.parseInt(m.group(1));
                s = m.group(2);
                } else {
                    throw new IllegalArgumentException("Syntax error at " + asString.substring(0, asString.length() - s.length()) + " >> " + s);
                }
            }

            if (dice == null)
                dice = new DamageDice[] {d};
            else {
                DamageDice[] dd = new DamageDice[dice.length + 1];
                System.arraycopy(dice, 0, dd, 0, dice.length);
                dd[dice.length] = d;
                dice = dd;
            }

            if (s.isEmpty())
                break;
            else if (s.startsWith("+"))
                s = s.replaceFirst("^\\+\\s*", "");
            else if (s.startsWith("-"))
                continue;
            else
                throw new IllegalArgumentException("Syntax error at " + asString.substring(0, asString.length() - s.length()) + " >> " + s);
        }
    }
}
