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
        public String type = "Bludgeoning";
        public boolean lethal = true;
        public int value = 0;
        public int critvalue = 0;
    }

    DamageDice[] dice;
    int rolls = 0;
    int sides = 0;
    int plus = 0;
    String damageType = "Bludgeoning";
    boolean lethal = true;
    int critMult = 1;
    String size = "Medium";

    public DamageDice() {
    }
    public DamageDice(String asString) throws IllegalArgumentException {
        parse(asString);
    }
    public DamageDice(String damageType, String asString) throws IllegalArgumentException {
        setDamageType(damageType);
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
                d.lethal = lethal;
                types.put(damageType, d);
            }
            int roll = plus;
            if (sides > 0) {
                for (int i=0; i<rolls; ++i) {
                    roll += randomizer.roll(sides);
                }
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
        if (damageType.startsWith("NonLethal.")) {
            this.damageType = damageType.substring("NonLethal.".length());
            this.lethal = false;
        }
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

            Matcher m = Pattern.compile("^(\\d*)\\s*d\\s*(\\d+)\\s*(.*)").matcher(s);
            if (m.matches()) {
                int rl = m.group(1).isEmpty() ? 1 : Integer.parseInt(m.group(1));
                int sd = Integer.parseInt(m.group(2));
                s = m.group(3);
                if (sides == 0 || rolls == 0) {
                    sides = sd;
                    rolls = rl;
                } else {
                    DamageDice d = new DamageDice();
                    d.damageType = damageType;
                    d.lethal = lethal;
                    d.size = size;
                    d.critMult = critMult;
                    d.rolls = rl;
                    d.sides = sd; 
                    if (dice == null) {
                        dice = new DamageDice[] {d};
                    } else {
                        DamageDice[] dd = new DamageDice[dice.length + 1];
                        System.arraycopy(dice, 0, dd, 0, dice.length);
                        dd[dice.length] = d;
                        dice = dd;
                    }
                }
            } else {
                m = Pattern.compile("^(-?\\d+)\\s*(.*)").matcher(s);
                if (m.matches()) {
                plus += Integer.parseInt(m.group(1));
                s = m.group(2);
                } else {
                    throw new IllegalArgumentException("Syntax error at " + asString.substring(0, asString.length() - s.length()) + " >> " + s);
                }
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
    
    private static DamageDice d(String asString) {
        return new DamageDice(asString);
    }
    private static Map<String,DamageDice[]> sizes = new HashMap<String, DamageDice[]>();
    static {
        sizes.put("Tiny",   new DamageDice[]{d("0d1"),d("1d1"),d("1d2"),d("1d3"),d("1d4"),d("1d6"), d("1d8"), d("1d4"),d("1d8"), d("1d10"),d("2d6") });
        sizes.put("Small",  new DamageDice[]{d("1d1"),d("1d2"),d("1d3"),d("1d4"),d("1d6"),d("1d8"), d("1d10"),d("1d6"),d("1d10"),d("2d6"), d("2d8") });
        sizes.put("Medium", new DamageDice[]{d("1d2"),d("1d3"),d("1d4"),d("1d6"),d("1d8"),d("1d10"),d("1d12"),d("2d4"),d("2d6"), d("2d8"), d("2d10")});
        sizes.put("Large",  new DamageDice[]{d("1d3"),d("1d4"),d("1d6"),d("1d8"),d("2d6"),d("2d8"), d("3d6"), d("2d6"),d("3d6"), d("3d8"), d("4d8") });
    }

    public DamageDice adjustToSize(String sz) {
        if (this.size.equals(sz))
            return this;

        DamageDice d = new DamageDice();
        d.damageType = damageType;
        d.lethal = lethal;
        d.size = sz;
        d.critMult = critMult;
        d.plus = plus;
        if (this.dice != null) {
            d.dice = new DamageDice[dice.length];
            for (int i=0; i<dice.length; ++i)
                d.dice[i] = dice[i].adjustToSize(d.size);
        }
        int r = rolls;
        while (r > 0) {
            DamageDice[] sl1 = sizes.get(this.size);
            DamageDice[] sl2 = sizes.get(d.size);
            if (sl1 == null)
                throw new IllegalStateException("Unknown current size " + this.size);
            if (sl2 == null)
                throw new IllegalArgumentException("Unknown desired size " + d.size);
            int n = -1;
            for (int i=sl1.length-1; i>=0; --i) {
                if (sl1[i].sides == sides && sl1[i].rolls <= r) {
                    n = i;
                    break;
                }
            }
            if (n < 0)
                throw new IllegalStateException("Cannot convert unknown damage dice to new size " + d.size + " -- " + this + "/" + this.size);

            r -= sl1[n].rolls;
            if (sl1[n].rolls < 1)
                r = 0;
            if (d.sides == 0) {
                d.sides = sl2[n].sides;
                d.rolls = sl2[n].rolls;
            } else if (d.sides == sl2[n].sides) {
                d.rolls += sl2[n].rolls;
            } else {
                DamageDice[] ds = new DamageDice[d.dice == null ? 1 : d.dice.length + 1];
                for (int i=0; i<ds.length-1; ++i)
                    ds[i] = d.dice[i];
                d.dice = ds;
                DamageDice nd = d.dice[d.dice.length-1] = new DamageDice();
                nd.damageType = damageType;
                nd.lethal = lethal;
                nd.size = d.size;
                nd.critMult = critMult;
                nd.plus = 0;
                nd.sides = sl2[n].sides;
                nd.rolls = sl2[n].rolls;
            }
        }
        return d;
    }
}
