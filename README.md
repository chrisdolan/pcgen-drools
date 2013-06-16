Synopsis
--------

Drools is a high-performance, generic *knowledge engine* with a
concise syntax for defining rules. A knowledge engine is a collection
of rules and facts that can infer downstream facts with minimal
effort. This project takes advantage of Drools' statful mode, where
one add and remove facts from the knowledge base, and see what
changes.

For example, add a level of Monk and see how the Armor Class changes,
or add the Shaken condition and see how attack bonuses change. We just
need to make a bunch of if/then rules and Drools takes care of *how*
to propagate the facts.

Motivation
----------

In early 2013, I (Chris Dolan) tried to port PCGen 5.x to Android. It
technically worked, but the startup performance was about 100x too
slow to be usable, largely due to the effort involved to parse the
.LST rules. With some folks on the list, I discussed pre-compiling the
rules and storing them in a database but that sounded like a lot of
work.

I'd previously used Drools and Drools Planner in my day job, so I was
familiar with the rules syntax (and knew their limitations...) I
announced my initial work here:
http://tech.groups.yahoo.com/group/pcgen_developers/message/3363

Architecture
------------

This initial implementation only has Pathfinder rules, but I've
architected it to hopefully be compatible with any d20 system, but I
mostly play D&D 3.5 and Pathfinder these days, so I may have
hard-coded some parts too tightly.

In the near future, PCs will be XML (or JSON) files that will be
loaded via JAXB and injected into the knowledge base, and UI code will
query that kbase or subscribe for changes in that kbase. It's really
early in the project, but my hope would be that the existing PCGen UI
could be ported to sit on top of the kbase.

Here's what the API currently looks like:

        Session session = Engine.createSession("pathfinder");
        
        session.insert(new AbilityInput(AbilityInput.STR, 10));
        session.insert(new AbilityInput(AbilityInput.CON, 8));
        session.insert(new AbilityInput(AbilityInput.DEX, 16));
        session.insert(new AbilityInput(AbilityInput.INT, 10));
        session.insert(new AbilityInput(AbilityInput.WIS, 11));
        session.insert(new AbilityInput(AbilityInput.CHA, 12));
        FactHandle stun = session.insert(new ConditionInput(ConditionInput.TYPE_STUNNED));
        session.run();
        int ac = session.querySingle(Integer.class, "Query.ArmorClass");
        // should be 8 = 10 - 2 (stunned), and no dex bonus to AC due to stun
        System.out.println("AC is " + ac);
        
        session.retract(stun);
        session.run();
        ac = session.querySingle(Integer.class, "Query.ArmorClass");
        // should be 13 = 10 + 3 (dex)
        System.out.println("AC is " + ac);
        
        session.destroy();

License
-------

Following the lead of PCGen itself, I permit redistribution of this
project's code under the terms of the LGPL v3
(http://www.gnu.org/licenses/lgpl.html)

The initial implementation of the rules include my interpretation of
the rules and data from Paizo's Pathfinder game. Following the lead of
the Open Gaming License, Paizo has generously allowed people like me
to extend those rules via their Community Use Policy
(http://paizo.com/paizo/about/communityuse). I permit reuse of my
rules and data under the same terms.

Have thoughts or opinions on this licensing? Please contact me.
