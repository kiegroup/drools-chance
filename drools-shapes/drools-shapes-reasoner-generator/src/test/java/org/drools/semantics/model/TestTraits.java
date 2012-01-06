/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.semantics.model;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.impl.ClassPathResource;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.semantics.model.domain.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.*;

public class TestTraits {



    @Test
    /**
     * In this test, a dynamic Trait is applied to an Object
     *
     * A dynamic Trait is
     *  - an interface
     *  - actually, a partially implemented interface
     *  - applied to an INSTANCE
     *  - which can be removed as necessary
     *
     *  When an object wears a "Trait", it is expected to, and thus does,
     *  exhibit some properties (fields) and behaviour (methods).
     *  These additional features might not be exposed/available
     *  in the original interface the object was implementing.
     *
     *  A "Trait" is then composed by an interface (aka "Role")
     *  and an optional implementing Strategy.
     *  -- The Role adds to the object's interface
     *     The wrapped object might actually implement some of
     *     the methods required by the Role
     *  -- IF NOT, the Strategy is used to fill in any missing
     *     behaviour
     *
     *  So, the entities involved:
     *  -- The core object
     *  ---- and its initial interface, if any
     *  -- The Trait
     *  ---- the Role interface
     *  ---- the Strategy implementation
     *
     *  In this implementation, Roles add on top of the existing
     *  core object interface, instead of hiding it. This also
     *  means that several traits can be added to the same
     *  core object at the same time.
     *
     *  Notice that, in practice, this implies MULTIPLE INHERITANCE,
     *  with all related problems, as two traits might add the
     *  same method (i.e. name and signature)
     *  This problem is solved taking inspiration from Scala:
     *  in case of ambiguities, the last trait applied determines
     *  which strategy method will be eventually called.
     *  Notice that a trait can be "reapplied" to an object
     *  already having that trait.
     *
     *  The trait approach, useful when methods are involved,
     *  has also been enhanced for dynamic properties (fields).
     *  In canonical traits, additional properties are expected
     *  to be supplied by the Strategy implementors.
     *  Here, a traited object is automatically given a property
     *  Map&lt;String,Objet&gt; which is automatically used
     *  to implement getters/setters defined by Roles but
     *  for some reason not provided by either the core object
     *  or any Strategy implementor.
     *
     *  This map can also be accessed directly, to add/access
     *  dynamic properties even when they're not part of a formal
     *  interface.
     *
     */
    public void testTrait() {
        Object o = new Object();
        Map<String, Object> m = new HashMap<String,Object>();
            m.put("echoDynaProp","echo");

        IEcho echo = TraitMantle.wrap(o, m, IEcho.class);

        // this method is provided by the echo trait
        String rev = echo.echo("Hail Word");
        assertEquals("droW liaH", rev);

        // this is a property provided by the echo trait
        assertEquals("droW liaH", echo.getLastEchoMessage());

        // this is a dynamic property exposed by the Role
        // and provided using the dynamic map
        assertEquals("echo", echo.getEchoDynaProp());


    }









    @Test
    /**
     * In order to define a Trait, three entities need to be defined,
     * bottom up
     *
     * - A Trait Strategy implementor
     *   --- could likely be a (stateless) singleton
     * - A Trait interface which the implementor implements.
     * - A Role interface extending the Trait interface
     *
     * ( roughly speaking, the Role interface exposes methods/properties
     *   in the inner object; the Trait interface adds methods; the
     *   implementor and the dynamic map provide any missing implementation )
     *
     *  Another reason for having two interfaces is, the Role interface
     *  usually has a clear name, domain dependent. So, the Role
     *  is applied when some logic (or the programmer) decides that an
     *  object can have that role and behave accordingly.
     *
     *
     *  This becomes interesting when we have several dynamic properties,
     *  e.g. provided as triples, and we want to run a recognition
     *  algorithm. Assuming that the Roles are (automatically) derived
     *  from the concepts defined in an ontology...
     *
     *  Out-of-the box, we provide the IThing interface, which is a Trait.
     *  When a trait is applied to an object, the resulting object can
     *  be cast to the Trait Role interface and used as such, but that's
     *  where the story ends, i.e. the Role+Trait interfaces define the
     *  (new) methods that can be used.
     *
     *  When the IThing trait is applied, additional meta-traiting power
     *  is provided. IThing allows multiple traits to be applied to the
     *  same object at the same time, move from one trait to the other
     *  (i.e. "rewiring" to avoid the multiple inheritance problem,
     *  while keeping all trait interfaces), and access the dynamic properties.
     *
     *  All "semantic" (i.e. concept-defined) traits naturally extend
     *  the IThing interface, though nothing prevents user-defined traits
     *  from doing so.
     *  Notice, then, that if you apply a "semantic" trait, you also
     *  provide the IThing trait.
     *  (impl. note : the IThing just EXPOSES some internal functionalities
     *  which are added whenever any trait is applied)
     */
    public void testSemanticTrait() {
        Object o = new Object();
        Map<String, Object> m = new HashMap<String,Object>();
            m.put("school","Zkool");

        // First, the meta-trait IThing is applied.
        // Supports meta-traiting operations, but does
        // not define any domain-specific behaviour
        IThing<Object> thing = TraitMantle.wrap(o, m);
        assertFalse(thing instanceof IStudent);

        // Now something allows as to infer that our thing
        // was actually a student:
        IStudent s = thing.don(IStudent.class);
        // the "traited" reference we got back has the original object at its core
        assertEquals(o, s.getCore());
        // is effectively an instance of the interface we provided
        assertTrue(s instanceof IStudent);
        // and the type can be queried explicitly
        assertTrue(thing.hasType(IStudent.class));

        // but now, we have type and methods!
        assertEquals(IBreath_Impl.BREATHE,s.breathe());
        assertEquals("Zkool",s.getSchool());

    }




    @Test
    /**
     * This test looks at what happens to the core object when
     * it's actually a real ("legacy") object with fields and methods
     *
     * Semantic traits inheriting from IThing are actually generic,
     * so you can provide the core type at traiting time.
     *
     * The effects are twofold:
     *   - you can call getCore() on any traited reference and
     *     get a typed reference to the core object
     *   - if the core object implemented any interface, those
     *     interfaces are still implemented by the traited reference,
     *     and calls are redirected on the core object
     *     (** note: traits have precedence on methods, so they could override methods )
     *     (** TODO: check what to do with properties )
     *
     */
    public void testCore() {
        Human h = new Human("john",23);

        // now we have typed traits
        IThing<Human> thing = TraitMantle.wrap(h);
        assertFalse(thing instanceof IStudent);

        IStudent<Human> humanStudent = thing.don(IStudent.class);

        // core is typed, so you can invoke methods
        String ans = humanStudent.getCore().humanSkill();
        assertEquals("humanSkill",ans);

        // core had an interface, so you can cast
        // and call methods from that interface directly on the traited reference
        String ans2 = ((IHuman) thing).humanSkill();
        assertEquals("humanSkill",ans2);

        // methods not exposed through an interface can still be called
        // on core explicitly
        String ans3 = humanStudent.getCore().pureHumanSkill();
        assertEquals("pureHumanSkill",ans3);

    }



    @Test
    /**
     * Traits can be stacked, and applied in any sequence,
     * using the IThing interface.
     *
     * After a core object has been wrapped, it can
     * don additional traits.
     *
     * Notice that if you wrapped the object again,
     * you would get a separate traited entity from
     * the perspective of implemented interfaces.
     *
     * However, if you wrap the SAME object, the
     * underlying data structure is the same
     * BUT the dynamic properties are not!
     *
     * (not a strict requirement, the reason is
     * the dynamic property map is copied, not referenced)
     *
     */
    public void testMultiTraiting() {
        Object o = new Object();
        Map<String, Object> m = new HashMap<String, Object>();
            m.put("wage",22.0);

        // start from thing
        IThing<Object> thing = TraitMantle.wrap(o, m);

        // now o is ALSO a student
        IStudent<Object> s = thing.don(IStudent.class);
        assertTrue(s instanceof IThing);
        assertTrue(s instanceof IStudent);
        assertFalse(s instanceof IWorker);

        // now o is ALSO a worker
        IWorker<Object> w = s.don(IWorker.class);
        assertTrue(w instanceof IThing);
        assertTrue(w instanceof IWorker);
        assertTrue(w instanceof IStudent);

        // so one can work with it as if it were
        // a worker
        w.setWage(33.0);
        assertEquals(33.0,w.getWage());
        // or a student, just cast...
        assertEquals(null,((IStudent) w).getSchool());


        // Now o is wrapped again, clean of traits
        IThing<Object> thing2 = TraitMantle.wrap(o,m);
        // even if the core is the same
        assertEquals(thing.getCore(),thing2.getCore());

        // traiting needs to be applied again
        assertFalse(thing2 instanceof IWorker);
        assertFalse(thing2 instanceof IStudent);
        // and dynamic properties changed on "thing"
        // are not propagated to "thing2"
        IWorker<Object> w2 = thing2.don(IWorker.class);
        assertEquals(22.0,w2.getWage());


        // but you can do THIS...
        // after all, thing is a Map...
        IThing<Object> thing3 = TraitMantle.wrap(o,thing);
        assertEquals(33.0,thing3.don(IWorker.class).getWage());

    }



    @Test
    /**
     * For meta-reasoning purposes, types can be accessed explicitly
     * using the IThing interface or any of its sub-interfaces
     *
     * There's also a caveat
     *   IThing thing = wrap(o,m);
     *   IStudent s = thing.don(IStudent);
     *
     * When the wrapper referred by "thing" is created, the trait
     * IThing is added to o.
     * Later, the trait IStudent is also added.
     *
     * Problem is, by the time "thing" is set, a facade proxy is created
     * which  is only aware of the IThing interface that the traited object
     * has been given.
     * When "s" is created, it is a proxy with both interfaces.
     *
     * Both proxies refer to the same wrapper for the core object o,
     * so you can call getType() on both thing and s and get the same
     * correct result.
     * But, proxy references might not be updated, so beware when
     * calling instanceof!
     *
     * So, it is good practice to always use the latest reference
     * returned by a "don" operation
     *
     */
    public void testThingDon() {
        Object o = new Object();
        Map<String, Object> m = new HashMap<String,Object>();

        IThing<Object> thing = TraitMantle.wrap(o, m);
        assertFalse(thing instanceof IStudent);

        IStudent<Object> s = thing.don(IStudent.class);

        IWorker<Object> w = s.don(IWorker.class);

        IStudent<Object> s2 = w.don(IStudent.class);


        assertTrue(thing.hasType(IStudent.class));
        assertTrue(thing.hasType(IWorker.class));
        assertTrue(thing.hasType(IThing.class));

        assertTrue(s.hasType(IStudent.class));
        assertTrue(s.hasType(IWorker.class));
        assertTrue(s.hasType(IThing.class));

        assertTrue(w.hasType(IStudent.class));
        assertTrue(w.hasType(IWorker.class));
        assertTrue(w.hasType(IThing.class));

        assertTrue(s2.hasType(IStudent.class));
        assertTrue(s2.hasType(IWorker.class));
        assertTrue(s2.hasType(IThing.class));


        // this shows the "out-of-date" proxy "problem"
        // solution : use the latest reference
        assertTrue(thing instanceof IThing);
        assertFalse(thing instanceof IWorker);
        assertFalse(thing instanceof IStudent);

        assertTrue(s2 instanceof IThing);
        assertTrue(s2 instanceof IWorker);
        assertTrue(s2 instanceof IStudent);

    }





    @Test
    /**
     * Traits can be removed.
     * Should an object no longer have the requirements to qualify for -
     * or the reasons for exposing - a trait, the trait can be removed
     * (but applied again later, just beware of any state kept in the implementor!)
     *
     * When a trait is shed, the returning interface is IThing.
     * Notice that IThing can't be shed.
     *
     * Also notice that the "shed" feature is available only if you
     * apply a Trait that extends the IThing interface
     *
     */
    public void testThingShed() {
        Object o = new Object();
        Map<String, Object> m = new HashMap<String,Object>();

        IThing<Object> thing = TraitMantle.wrap(o, m);
        assertFalse(thing instanceof IStudent);

        thing = thing.don(IStudent.class);
        thing = thing.don(IWorker.class);

        assertTrue(thing instanceof IStudent);
        assertTrue(thing instanceof IWorker);
        assertTrue(thing instanceof IThing);

        thing = thing.shed(IStudent.class);

        assertFalse(thing.hasType(IStudent.class));
        assertFalse(thing instanceof IStudent);

        thing = thing.don(IStudent.class);

        assertTrue(thing instanceof IStudent);

        IStudent<Object> s2 = (IStudent) thing;
        assertNotNull(s2);


        // even if you apply a specific trait directly,
        // the IThing trait is implicitly applied
        IWorker worker2 = TraitMantle.wrap(o,m,IWorker.class);
        IThing thing2 = worker2.shed(IWorker.class);
        assertTrue(thing2.hasType(IThing.class));

        // by choice, no exception is raised, simply nothing happens
        thing2 = thing2.shed(IThing.class);
        assertTrue(thing2.hasType(IThing.class));

    }


    @Test
    /**
     * Remark: ensure that traited facades can share the same core object
     */
    public void testThingCore() {
        Object o = new Object();
        Object o2 = new Object();


        Map<String, Object> m = new HashMap<String,Object>();
        Map<String, Object> m2 = new HashMap<String,Object>();

        IThing<Object> thing = TraitMantle.wrap(o, m);
        IThing<Object> thing2 = TraitMantle.wrap(o2, m2);

        assertFalse(thing.getCore().equals(thing2.getCore()));

        IStudent<Object> s = thing.don(IStudent.class);
        IWorker<Object> w = s.don(IWorker.class);
        IStudent<Object> s2 = w.don(IStudent.class);

        IWorker<Object> w2 = thing2.don(IWorker.class);

        assertEquals(o,s.getCore());
        assertEquals(o,w.getCore());
        assertEquals(o,s2.getCore());
        assertEquals(o,thing.getCore());

        assertEquals(o2,w2.getCore());
        assertEquals(o2,thing2.getCore());

    }




    @Test
    /**
     * IThing extends the Map interface to ensure that you
     * can add "dynamic" properties, which are not
     * explicitly included in any interface (using getters/setters).
     *
     * The obvious intent is: (i) enhance an object with dynamic
     * properties; (ii) classify/recognize it according to those
     * properties; (iii) wear any trait that apply and allow
     * that object to benefit from the use strong typing -
     * at least until that trait is shed....
     *
     */
    public void testThingAsMap() {

        Object o = new Object();

        Map<String, Object> m = new HashMap<String,Object>();
        m.put("age",18);
        m.put("name","jon");
        m.put("weight",84.23);

        IThing<Object> thing = TraitMantle.wrap(o, m);

        assertEquals("jon",(String) thing.get("name"));

        // notice that unlike static properties, dynamic properties can be removed
        // which is different from making them null!
        Integer i = (Integer) thing.remove("age");
        assertEquals(18, i.intValue());

        assertTrue(thing.containsKey("weight"));
        assertEquals(84.23, (Double) thing.get("weight"), 1e-5);
    }




    @Test
    /**
     * ... notice that while a trait is being used, which
     * relies on dynamic properties, getters and setters
     * from the trait interface will target the dynamic
     * fields in the property map.
     * The map interface will still apply, so both
     * options are available
     *
     * Moreover, the other way around is provided!
     * If a getter/setter from an interface is not supported by any
     * implementor, the property will be created as a dynamic one
     */
    public void testTwinAccess() {

        Human h = new Human("jon",18);
        Map<String,Object> m = new HashMap<String, Object>();
        m.put("school","Skool");

        IStudent<Human> pers = TraitMantle.wrap(h, m, IStudent.class);

        assertEquals("jon",pers.get("name"));
        assertEquals("jon",pers.getName());

        assertEquals("Skool",pers.get("school"));
        assertEquals("Skool",pers.getSchool());

        // property from IPerson, hooked to the "name" field of Human
        pers.setName("adam");
        assertEquals("adam",pers.get("name"));
        assertEquals("adam", pers.getName());
        pers.set("name", "bob");
        assertEquals("bob",pers.get("name"));
        assertEquals("bob",pers.getName());
        pers.put("name","charles");
        assertEquals("charles", pers.get("name"));
        assertEquals("charles",pers.getName());


        // property from IStudent, hooked to a dynamic property
        pers.setSchool("s1");
        assertEquals("s1",pers.get("school"));
        assertEquals("s1",pers.getSchool());
        pers.set("school","s2");
        assertEquals("s2",pers.get("school"));
        assertEquals("s2",pers.getSchool());
        pers.put("school","s3");
        assertEquals("s3",pers.get("school"));
        assertEquals("s3",pers.getSchool());


    }



    @Test
    /**
     * When multiple traits are applied, with multiple implementors,
     * the implementors are kept in a stack.
     * Currently, method implementation are being searched in that
     * stack, starting from the top.
     *
     * To reduce inefficiency, a trait can be reapplied to
     * force the trait implementor to appear on top of the stack
     */
    public void testMultiTraitImpl() {
        Object o = new Object();
        IThing<Object> thing = TraitMantle.wrap(o);

        thing.don(IStudent.class);
        IWorker<Object> w = thing.don(IWorker.class);
        IStudent<Object> s = (IStudent) w;

        System.out.println(w.toil());

        // not so efficient (should don the student's mantle again)
        // but workable as there's no overlapping
        System.out.println(s.breathe());

    }




    @Test
    /**
     * The reason for that choice is: avoid the "diamond problem"
     * when multiple inheritance is present.
     *
     * Here, IPersons, IStudents and IWorkers can "sum" integers
     * with different results
     * (IPersons always return 0, IWorkers give a wrong result by +1,
     * IStudents return the correct sum)
     *
     * As traits ovverride each other, the last trait implementor providing the
     * desired method will actually be executed.
     * (re)-wiring a trait causes its implementor to appear on top of
     * the stack
     */
    public void testTraitOverriding() {
        Human h = new Human("john", 18);
        IThing<Human> thing = TraitMantle.wrap(h);
        System.out.println(thing.getTypes());

        IWorker<Human> fw = thing.don(IWorker.class);
        System.out.println(fw.getTypes());
            fw.sum(3,2);
        assertEquals(6, fw.sum(3, 2));

        IPerson<Human> fp = thing.don(IPerson.class);
        System.out.println(fp.getTypes());
            fp.sum(3,2);
            fw.sum(3,2);
        assertEquals(0,fp.sum(3,2));
        assertEquals(0,fw.sum(3,2));

        IPerson<Human> fs = thing.don(IStudent.class);
        System.out.println(fs.getTypes());
            fs.sum(3,2);
        assertEquals(5,fs.sum(3,2));

        IWorker<Human> full = thing.don(IWorker.class);
        System.out.println(full.getTypes());
            full.sum(3,2);
        assertEquals(6, full.sum(3, 2));


    }



    @Test
    /**
     * full, legacy example mimicking a semantic recognition problem, prior to using a proper trait interface
     * better expressed in the next, rule-based test.
     */
    public void testTraits() {

        Human p1 = new Human("john",18);

        Triple[] tripples = new Triple[] {
                new Triple( p1, "weight", 90.0 ),
                new Triple( p1, "height", 180.0 ),
                new Triple( p1, "wage", 43.72)
                // ...........
        };


        Map<String,Object> props1 = new HashMap<String,Object>();
        for (Triple t : tripples) {
            if ( t.subject.equals( p1 ) ) {
                props1.put(t.property,t.object);
            }
        }

        /*---------------------------------------------------------------------*/


        // Human entity enhanced with dynamic properties
        IThing<Human> thing = TraitMantle.wrap(p1, props1);

        // hard-classified as a person
        IPerson<Human> p1AsPerson = thing.don(IPerson.class);

        // almost-hard-classified as a student
        if ( p1AsPerson.getAge() < 30 ) {
            IStudent<Human> p1AsStudent = p1AsPerson.don(IStudent.class);

            p1AsStudent.setSchool("skool");
            p1AsStudent.setName("johnnie");

            assertEquals("johnnie", p1AsStudent.getName());
            assertEquals("skool", p1AsStudent.getSchool());

        }

        // and a worker as well
        if ( p1AsPerson.getAge() >= 18 ) {
            IWorker<Human> p1AsWorker = p1AsPerson.don(IWorker.class);

            assertEquals(180.0, p1AsWorker.getHeight(), 1e-16);

            IStudent<Human> p1AsStudentAgain = p1AsWorker.don(IStudent.class);
            assertEquals("skool", p1AsStudentAgain.getSchool());
        }



        // do something
        IStudent<Human> p1AsStudentOnceMore = p1AsPerson.don(IStudent.class);
        p1AsStudentOnceMore.setAge(33);


        // then another recognition process decidedes that it's no longer a worker
        if ( p1AsPerson.getAge() > 25 ) {
            IPerson<Human> p = p1AsStudentOnceMore.don(IPerson.class);
            IThing<Human> x = p.shed(IWorker.class);

            assertFalse(p.hasType(IWorker.class));
            assertFalse(x instanceof IWorker);
        }


    }


    @Test
    /**
     * see comments in traitsExample.drl
     */
    public void testTraitsInRules() {



        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(new ClassPathResource("org/drools/semantics/lang/dl/model/traitsExample.drl"), ResourceType.DRL);
            System.out.println(kbuilder.getErrors());
            assertEquals(0, kbuilder.getErrors().size());
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        List list = new ArrayList();
        List toks = new ArrayList();
            ksession.setGlobal("list",list);
            ksession.setGlobal("tokens",toks);
            ksession.insert("trigger");
            ksession.fireAllRules();

        assertEquals(4,list.size());
        assertTrue(list.contains("Math"));
        assertTrue(list.contains("Zchool"));
        assertTrue(list.contains(IStudent_Impl.BREATHE));
        assertTrue(list.contains(214));

        // one for the human
        // one for the thing wrapping the human
        // three updates
        assertEquals(5, toks.size());

    }





}
