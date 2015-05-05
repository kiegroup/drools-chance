/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE=2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.expectations;

import it.unibo.deis.lia.org.drools.expectations.model.Closure;
import it.unibo.deis.lia.org.drools.expectations.model.Expectation;
import it.unibo.deis.lia.org.drools.expectations.model.Failure;
import it.unibo.deis.lia.org.drools.expectations.model.Fulfill;
import it.unibo.deis.lia.org.drools.expectations.model.Pending;
import it.unibo.deis.lia.org.drools.expectations.model.Success;
import it.unibo.deis.lia.org.drools.expectations.model.Viol;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.rule.Global;
import org.kie.api.event.rule.DebugAgendaEventListener;
import org.kie.api.event.rule.DebugRuleRuntimeEventListener;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.runtime.KieSession;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;


public class ExpectationTest extends ExpTestBase {

    @Test
    public void testFailsOn() {
        String src = "" +
                "package org.drools; " +

                "declare Msg " +
                "   @role(event) " +
                "   sender   :   String @key " +
                "   receiver   :   String @key " +
                "   body      :   String @key " +
                "   more      :   String " +
                "end " +
                " " +
                "declare Interrupt " +
                "   reason   :   String @key " +
                "end " +
                " " +
                "global java.util.List list; " +

                "rule FailsOn_Test_Rule " +
                "when " +
                "   $trigger: Msg( 'John', 'Peter', 'Hello' ; ) " +
                "then " +
                "   expect Msg( 'Peter', 'John', 'Hello back', $more ; this after[0,100ms] $trigger ) " +
                "   failsOn Interrupt( 'ignore' ; ) " +
                "   onFulfill { " +
                "      list.add( 'F1'+$more ); " +
                "      System.out.println( 'Expectation fulfilled' ); " +
                "   } onViolation { " +
                "      System.out.println( 'Expectation violated' ); " +
                "   } " +
                "   list.add( 0 ); " +
                "   System.out.println( 'Triggered expectation: '+$trigger ); " +
                "end";

        KieSession kSession = buildKnowledgeSession( src.getBytes() );
//        kSession.addEventListener(new DebugRuleRuntimeEventListener(System.out));
//        kSession.addEventListener(new DebugAgendaEventListener(System.out));
        List<Object> list = new LinkedList<Object>();
        kSession.setGlobal("list", list);

        System.out.println("====================================================================================");
        kSession.insert(newMessage(kSession, "John", "Peter", "Hello", "X"));
        kSession.fireAllRules();
        assertTrue(list.contains(0));

        /**
         * Causes the failsOn rule (which currently is named with *__Expire suffix) to activate
         **/
        sleep(10);
        kSession.insert(newInterrupt(kSession, "ignore"));
        kSession.fireAllRules();
        sleep(50);

        /**
         * Shouldn't cause anything more to activate (due to Closure occurring earlier),
         * but at this time the onFulfill rule activates
         */
        kSession.insert(newMessage(kSession, "Peter", "John", "Hello back", "Y"));
        kSession.fireAllRules();
        System.out.println( reportWMObjects( kSession ) );
    }

    @Test
    public void testSimpleFulfill() {

        String src = "" +
                "package org.drools; " +
                "global java.util.List list; " +

                "declare Msg " +
                "   @role(event) " +
                "   sender : String     @key " +
                "   receiver : String   @key " +
                "   body : String       @key " +
                "   more : String " +
                "end " +

                "rule Expect_Test_Rule " +
                "when " +
                "   $trigger : Msg( 'John', 'Peter', 'Hello' ; ) " +
                "then " +
                "   expect Msg( 'Peter', 'John', 'Hello back', $more ; this after[0,100ms] $trigger ) " +
                "   onFulfill { " +
                "        \t list.add( 'F1' + $more ); \n" +
                "        \t System.out.println( 'Expectation fulfilled' ); \n" +
                "    } onViolation { " +
                "        \t System.out.println( 'Expectation violated' ); " +
                "    } " +
                "   list.add( 0 ); " +
                "   System.out.println( 'Triggered expectation ' + $trigger ); " +
                "end " +
                "";

        KieSession kSession = buildKnowledgeSession( src.getBytes() );
        List<Object> list = new LinkedList<Object>();
        kSession.setGlobal( "list", list );

        System.out.println( "====================================================================================" );
        kSession.insert( newMessage( kSession, "John", "Peter", "Hello", "X" ) );
        kSession.fireAllRules();
        assertTrue( list.contains( 0 ) );

        System.out.println( "================================= SLEEP =========================================" );
        sleep( 20 );
        System.out.println( "================================= WAAKE =========================================" );

        kSession.insert( newMessage( kSession, "Peter", "John", "Hello back", "Y" ) );
        kSession.fireAllRules();
        System.out.println( "================================= DONE =========================================" );




        assertEquals( Arrays.asList( 0, "F1Y" ), list );
        assertEquals( 1, countMeta( Fulfill.class.getName(), kSession ) );
        assertEquals( 1, countMeta( Pending.class.getName(), kSession ) );
        assertEquals( 0, countMeta( Viol.class.getName(), kSession ) );

        sleep( 110 );

        kSession.fireAllRules();

        assertEquals( Arrays.asList( 0, "F1Y" ), list );
        assertEquals( 1, countMeta( Fulfill.class.getName(), kSession ) );
        assertEquals( 0, countMeta( Pending.class.getName(), kSession ) );
        assertEquals( 0, countMeta( Viol.class.getName(), kSession ) );


    }

    @Test
    public void testSimpleViolation() {

        String src = "" +
                "package org.drools; " +
                "global java.util.List list; " +

                "declare Msg " +
                "   @role(event) " +
                "   sender : String     @key " +
                "   receiver : String   @key " +
                "   body : String       @key " +
                "   more : String " +
                "end " +

                "rule Expect_Test_Rule " +
                "when " +
                "   $trigger : Msg( 'John', 'Peter', 'Hello' ; ) " +
                "then " +
                "   expect Msg( 'Peter', 'John', 'Hello back', $more ; this after[0,100ms] $trigger ) " +
                "   onFulfill { " +
                "        \t System.out.println( 'Expectation fulfilled' ); \n" +
                "    } onViolation { " +
                "        \t list.add( 'V1' ); \n" +
                "        \t System.out.println( 'Expectation violated' ); " +
                "    } " +
                "end " +
                "";

        KieSession kSession = buildKnowledgeSession( src.getBytes() );
        List<Object> list = new LinkedList<Object>();
        kSession.setGlobal( "list", list );

        System.out.println( "====================================================================================" );
        kSession.insert( newMessage( kSession, "John", "Peter", "Hello", "X" ) );
        kSession.fireAllRules();

        System.out.println( "================================= SLEEP =========================================" );
        sleep( 20 );
        System.out.println( "================================= WAAKE =========================================" );

        System.out.println( reportWMObjects( kSession ) );

        assertTrue( list.isEmpty() );
        assertEquals( 0, countMeta( Fulfill.class.getName(), kSession ) );
        assertEquals( 1, countMeta( Expectation.class.getName(), kSession ) );
        assertEquals( 1, countMeta( Pending.class.getName(), kSession ) );
        assertEquals( 0, countMeta( Viol.class.getName(), kSession ) );

        sleep( 230 );

        kSession.fireAllRules();

        assertEquals( Arrays.asList( "V1" ), list );
        assertEquals( 0, countMeta( Fulfill.class.getName(), kSession ) );
        assertEquals( 1, countMeta( Expectation.class.getName(), kSession ) );
        assertEquals( 0, countMeta( Pending.class.getName(), kSession ) );
        assertEquals( 1, countMeta( Viol.class.getName(), kSession ) );

        System.out.println( reportWMObjects( kSession ) );

    }


    @Test
    public void testAlert() {

        String src = "" +
                "package org.drools; " +
                "import org.droo1ls.expectations.*; " +
                "global java.util.List list; " +

                "declare Reading " +
                "   @role(event) " +
                "   probeId: String  @key " +
                "   value : Double   @key " +
                "end " +

                "rule Alert " +
                "when " +
                "   $trigger : Reading( 'XYZ', $value ; ) " +
                "then " +
                "   $e1 : expect one Number( doubleValue > 10.0 ) from $value " +
                "   onFulfill { " +
                "        list.add( 'F1' ); " +
                "        list.add( $value ); " +
                "   } onViolation { } " +
                "   System.out.println( 'Rule triggered' ); " +
                "end " +

                "rule 'Meta' " +
                "when " +
                "   Fulfill( ruleName == 'Alert', label == '$e1', $fact : tuple[0] ) " +
                "then " +
                "   System.out.println( 'All is well and good' + $fact ); " +
                "   list.add( $fact ); " +
                "end " +
                "";

        KieSession kSession = buildKnowledgeSession( src.getBytes() );

        List<Object> list = new LinkedList<Object>();
        kSession.setGlobal( "list", list );

        kSession.insert( newReading( kSession, "XYZ", 14.5 ) );
        kSession.fireAllRules();

        sleep( 110 );

        kSession.fireAllRules();

        System.out.println(list);
        assertEquals( 3, list.size() );
        assertTrue( list.contains( 14.5 ) );
        assertTrue( list.contains( "F1" ) );

        System.err.println( reportWMObjects( kSession ) );

        assertEquals( 1,countMeta( Fulfill.class.getName(), kSession ) );
        assertEquals( 1,countMeta( Expectation.class.getName(), kSession ) );
        assertEquals( 0,countMeta( Pending.class.getName(), kSession ) );
        assertEquals( 0,countMeta( Viol.class.getName(), kSession ) );

    }



    @Test
    public void testSimplFulfillAndViolation() {

        String src = "" +
                "package org.drools; " +
                "import org.drools.expectations.*; " +
                "global java.util.List list; " +

                "declare Msg " +
                "   @role(event) " +
                "   sender : String     @key " +
                "   receiver : String   @key " +
                "   body : String       @key " +
                "   more : String       @key " +
                "end " +

                "rule 'Expect' " +
                "when " +
                "   $trigger : Msg( 'John', 'Peter', 'Hello' ; ) " +
                "then " +
                "   expect Msg( 'Peter', 'John', 'Hello back', $more ; this after[0,100ms] $trigger ) " +
                "   onFulfill { " +
                "       list.add( 'F1' + $more ); " +
                "   } onViolation { " +
                "       System.out.println( 'Violation!!!' ); " +
                "       list.add( 'V1' ); " +
                "   } " +
                "end " +
                "";

        KieSession kSession = buildKnowledgeSession(src.getBytes());


        List<Object> list = new LinkedList<Object>();
        kSession.setGlobal( "list", list );

        kSession.insert( newMessage( kSession, "John", "Peter", "Hello", "X" ) );
        kSession.fireAllRules();

        kSession.insert( newMessage( kSession, "Peter", "John", "Hello back", "Y" ) );
        kSession.fireAllRules();
        kSession.insert( newMessage( kSession, "Peter", "John", "Hello back", "Y2" ) );
        kSession.fireAllRules();

        assertTrue( list.contains( "F1Y" ) );
        assertTrue( list.contains( "F1Y2" ) );
        assertFalse( list.contains( "V1" ) );
        assertEquals( 2, countMeta( Fulfill.class.getName(), kSession ) );
        assertEquals( 1, countMeta( Pending.class.getName(), kSession ) );
        assertEquals( 0, countMeta( Viol.class.getName(), kSession ) );

        sleep( 150 );

        System.out.println( reportWMObjects( kSession ) );

        kSession.insert( newMessage( kSession, "Peter", "John", "Hello back", "Y3" ) );
        kSession.fireAllRules();

        System.out.println( reportWMObjects( kSession ) );

        assertFalse( list.contains( "F1Y3" ) );
        assertEquals( 2, countMeta( Fulfill.class.getName(), kSession ) );
        assertEquals( 1, countMeta( Expectation.class.getName(), kSession ) );
        assertEquals( 0, countMeta( Pending.class.getName(), kSession ) );
        assertEquals( 0, countMeta( Viol.class.getName(), kSession ) );

        sleep( 10 );

        kSession.insert( newMessage( kSession, "John", "Peter", "Hello", "X2" ) );

        System.out.println( reportWMObjects( kSession ) );

        kSession.fireAllRules();

        System.out.println( reportWMObjects( kSession ) );

        assertEquals( Arrays.asList( "F1Y", "F1Y2" ), list );
        assertEquals( 2, countMeta( Fulfill.class.getName(), kSession ) );
        assertEquals( 1, countMeta( Pending.class.getName(), kSession ) );
        assertEquals( 0, countMeta( Viol.class.getName(), kSession ) );

        sleep( 110 );

        kSession.addEventListener( new org.kie.api.event.rule.DebugAgendaEventListener() );
        kSession.fireAllRules();

        assertTrue( list.contains( "V1" ) );


        assertEquals( 2, countMeta( Fulfill.class.getName(), kSession ) );
        assertEquals( 0, countMeta( Pending.class.getName(), kSession ) );
        assertEquals( 1, countMeta( Viol.class.getName(), kSession ) );
        assertEquals( 2 ,countMeta( Expectation.class.getName(), kSession ) );

    }

    @Test
    public void testManyFulfills() {

        String src = "" +
                "package org.drools;  " +
                "import org.drools.expectations.*;  " +
                "global java.util.List list; " +

                "declare Msg  " +
                "   @role(event)  " +
                "   sender : String     @key " +
                "   receiver : String   @key " +
                "   body : String       @key " +
                "   more : String   " +
                "end " +

                "rule  'Expect '  " +
                "when " +
                "   $trigger : Msg(  'John',  'Peter',  'Hello' ; )  " +
                "then " +
                "   expect Msg(  'Peter',  'John',  'Hello back', $more ; this after[0,100ms] $trigger )    " +
                "    onFulfill {  " +
                "        list.add( 'F1' + $more );  " +
                "    } onViolation {  " +
                "        list.add( 'V1' ); " +
                "    } " +
                "end ";

        KieSession kSession = buildKnowledgeSession( src.getBytes() );
        List<Object> list = new LinkedList<Object>();
        kSession.setGlobal( "list", list );

        kSession.insert( newMessage( kSession, "John", "Peter", "Hello", "X" ) );
        kSession.fireAllRules();

        kSession.insert( newMessage( kSession, "Peter", "John", "Hello back", "Y" ) );
        kSession.fireAllRules();
        kSession.insert( newMessage( kSession, "Peter", "John", "Hello back", "Y2" ) );
        kSession.fireAllRules();

        sleep( 10 );

        System.out.println( reportWMObjects( kSession ) );

        assertEquals( 2, countMeta( Fulfill.class.getName(), kSession ) );
        assertEquals( 1, countMeta( Pending.class.getName(), kSession ) );
        assertEquals( 0, countMeta( Viol.class.getName(), kSession ) );

        assertEquals( 2, list.size() );

        sleep( 50 );

        kSession.insert( newMessage( kSession, "Peter", "John", "Hello back", "Y4" ) );
        kSession.fireAllRules();

        System.out.println( list );

        assertTrue( list.contains( "F1Y" ) );
        assertTrue( list.contains( "F1Y2" ) );
        assertTrue( list.contains( "F1Y4" ) );
        assertFalse( list.contains( "F1Y5" ) );
        assertEquals( 3, countMeta( Fulfill.class.getName(), kSession ) );
        assertEquals( 1, countMeta( Pending.class.getName(), kSession ) );
        assertEquals( 0, countMeta( Viol.class.getName(), kSession ) );
        assertEquals( 3, list.size() );

        sleep( 60 );

        kSession.insert( newMessage( kSession, "Peter", "John", "Hello back", "Y5" ) );
        kSession.fireAllRules();

        assertFalse( list.contains( "F1Y5" ) );
        assertFalse( list.contains( "V1" ) );
        assertEquals( 3, countMeta( Fulfill.class.getName(), kSession ) );
        assertEquals( 0, countMeta( Pending.class.getName(), kSession ) );
        assertEquals( 0, countMeta( Viol.class.getName(), kSession ) );
        assertEquals( 1, countMeta( Expectation.class.getName(), kSession ) );
        assertEquals( 3, list.size() );


        sleep( 200 );
        kSession.fireAllRules();
    }


    @Test
    public void testOneFulfill() {

        String src = "" +
                "package org.drools;  " +
                "import org.drools.expectations.*;  " +
                "global java.util.List list; " +

                "declare Msg  " +
                "   @role(event)  " +
                "   sender : String     @key " +
                "   receiver : String   @key " +
                "   body : String       @key " +
                "   more : String   " +
                "end " +

                "rule 'Expect'  " +
                "when " +
                "   $trigger : Msg( 'John', 'Peter', 'Hello' ; )  " +
                "then " +
                "   expect one Msg( 'Peter', 'John', 'Hello back', $more ; this after[0,100ms] $trigger )" +
                "    onFulfill {  " +
                "        list.add( 'F1' + $more );  " +
                "    } onViolation {  " +
                "        list.add( 'V1' ); " +
                "    } " +
                "end ";

        KieSession kSession = buildKnowledgeSession(src.getBytes());
        List<Object> list = new LinkedList<Object>();
        kSession.setGlobal( "list", list );

        kSession.insert( newMessage( kSession, "John", "Peter", "Hello", "X" ) );
        kSession.fireAllRules();

        System.out.println(reportWMObjects(kSession));

        assertEquals(0,countMeta( Fulfill.class.getName(), kSession ));
        assertEquals(1,countMeta( Pending.class.getName(), kSession ));
        assertEquals(0,countMeta( Viol.class.getName(), kSession ));


        kSession.insert( newMessage( kSession, "Peter", "John", "Hello back", "Y" ) );
        kSession.fireAllRules();
        kSession.insert( newMessage( kSession, "Peter", "John", "Hello back", "Y2" ) );
        kSession.fireAllRules();

        assertEquals( 1, countMeta( Fulfill.class.getName(), kSession ) );
        assertEquals( 0, countMeta( Pending.class.getName(), kSession ) );
        assertEquals( 0, countMeta( Viol.class.getName(), kSession ) );

        sleep( 50 );

        kSession.insert( newMessage( kSession, "Peter", "John", "Hello back", "Y4" ) );
        kSession.fireAllRules();

        assertEquals( 3,list.size() );
        assertTrue( list.contains( "F1Y" ) );
        assertTrue( list.contains( "F1Y2" ) );
        assertTrue( list.contains( "F1Y4" ) );
        assertFalse( list.contains( "F1Y5" ) );
        assertFalse( list.contains( "V1" ) );
        assertEquals( 1, countMeta( Fulfill.class.getName(), kSession ) );
        assertEquals( 0, countMeta( Pending.class.getName(), kSession ) );
        assertEquals( 0, countMeta( Viol.class.getName(), kSession ) );
        assertEquals( 1, countMeta( Expectation.class.getName(), kSession ) );

        sleep( 150 );

        assertEquals( 3, list.size() );

        kSession.fireAllRules();

    }







    @Test 
    public void testViolation() {

        String src = "" +
                "package org.drools;  " +
                "import org.drools.expectations.*;  " +
                "global java.util.List list; " +
                " " +
                "declare Msg  " +
                "   @role(event)  " +
                "   sender : String     @key " +
                "   receiver : String   @key " +
                "   body : String       @key " +
                "   more : String   " +
                "end " +
                " " +
                "rule 'Expect'  " +
                "when " +
                "   $trigger : Msg( 'John', 'Peter', 'Hello' ; )  " +
                "then " +
                "   expect Msg( 'Peter', 'John', 'Hello back', $more ; this after[0,200ms] $trigger ) onFulfill   " +
                "    {  " +
                "        list.add('F1'+$more);  " +
                "    } onViolation {  " +
                "        list.add('V1'); " +
                "    } " +
                "end ";

        KieSession kSession = buildKnowledgeSession(src.getBytes());
        List<Object> list = new LinkedList<Object>();
        kSession.setGlobal( "list", list );

        kSession.insert( newMessage( kSession, "John", "Peter", "Hello", "X" ) );
        kSession.fireAllRules();


        assertEquals( 0,countMeta( Fulfill.class.getName(), kSession ) );
        assertEquals( 1,countMeta( Pending.class.getName(), kSession ) );
        assertEquals( 0,countMeta( Viol.class.getName(), kSession ) );


        sleep( 250 );

        kSession.fireAllRules();

        assertEquals( 0, countMeta( Fulfill.class.getName(), kSession ) );
        assertEquals( 0, countMeta( Pending.class.getName(), kSession ) );
        assertEquals( 1, countMeta( Viol.class.getName(), kSession ) );
        assertEquals( 1, countMeta( Expectation.class.getName(), kSession ) );

    }


    @Test 
    public void testManyToOneFulfills() {

        String src = "" +
                "package org.drools;  " +
                "import org.drools.expectations.*;  " +
                "global java.util.List list; " +
                " " +
                "declare Msg  " +
                "   @role(event)  " +
                "   sender : String     @key " +
                "   receiver : String   @key " +
                "   body : String       @key " +
                "   more : String       @key  " +
                "end " +
                " " +
                "rule 'Expect'  " +
                "when " +
                "   $trigger : Msg( 'John', 'Peter', 'Hello', $moreIn ; )  " +
                "then " +
                "   expect $msg : Msg( 'Peter', 'John', 'Hello back', $moreOut ; this after[0,100ms] $trigger )   " +
                "    onFulfill {  " +
                "        list.add( 'F1' + $moreIn + ' : ' + $moreOut );  " +
                "    } onViolation {  " +
                "        list.add( 'V1' ); " +
                "    } " +
                "end ";

        KieSession kSession = buildKnowledgeSession(src.getBytes());
        List<Object> list = new LinkedList<Object>();
        kSession.setGlobal("list",list);

        kSession.insert(newMessage(kSession,"John","Peter","Hello","X1"));
        kSession.fireAllRules();

        assertEquals(0,countMeta( Fulfill.class.getName(), kSession ));
        assertEquals(1,countMeta( Pending.class.getName(), kSession ));
        assertEquals(0,countMeta( Viol.class.getName(), kSession ));

        sleep(10);

        kSession.insert(newMessage(kSession,"John","Peter","Hello","X2"));
        kSession.fireAllRules();


        assertEquals(0,countMeta( Fulfill.class.getName(), kSession ));
        assertEquals(2,countMeta( Pending.class.getName(), kSession ));
        assertEquals(0,countMeta( Viol.class.getName(), kSession ));

        sleep(10);

        kSession.insert(newMessage(kSession,"Peter","John","Hello back","Y"));
        kSession.fireAllRules();

        System.out.println( reportWMObjects( kSession ) );

        assertEquals( 2, countMeta( Fulfill.class.getName(), kSession ) );
        assertEquals(2,countMeta( Pending.class.getName(), kSession ));
        assertEquals(0,countMeta( Viol.class.getName(), kSession ));

        sleep(200);
        kSession.fireAllRules();

        assertEquals(2,countMeta( Fulfill.class.getName(), kSession ));
        assertEquals(0,countMeta( Pending.class.getName(), kSession ));
        assertEquals(0,countMeta( Viol.class.getName(), kSession ));
        assertEquals(2,countMeta( Expectation.class.getName(), kSession ));

        assertTrue(list.contains("F1X2 : Y"));
        assertTrue(list.contains("F1X1 : Y"));

    }






    @Test
    public void testOneToManyFulfills() {

        String src = "" +
                "package org.drools;  " +
                "import org.drools.expectations.*;  " +
                "global java.util.List list; " +

                "declare Msg  " +
                "   @role(event)  " +
                "   sender : String     @key " +
                "   receiver : String   @key " +
                "   body : String       @key " +
                "   more : String       @key  " +
                "end " +

                "rule 'Expect'  " +
                "when " +
                "   $trigger : Msg( 'John', 'Peter', 'Hello', 'X1' ; )  " +
                "then " +
                "   expect Msg( 'Peter', 'John', 'Hello back', $moreOut ; this after[0,100ms] $trigger ) " +
                "    onFulfill {  " +
                "        list.add( 'F1' + $moreOut );  " +
                "    } onViolation {  " +
                "        list.add( 'V1' ); " +
                "    } " +
                "end  " +

                "rule 'Expect2'  " +
                "when " +
                "   $trigger : Msg( 'John', 'Peter', 'Hello', 'X2' ; )  " +
                "then " +
                "   expect Msg( 'Peter', 'John', 'Hello back', $moreOut ; this after[0,100ms] $trigger ) " +
                "    onFulfill {  " +
                "        list.add( 'F2' + $moreOut );  " +
                "    } onViolation {  " +
                "        list.add( 'V2' ); " +
                "    } " +
                "end ";

        KieSession kSession = buildKnowledgeSession(src.getBytes());
        List<Object> list = new LinkedList<Object>();
        kSession.setGlobal("list",list);

        kSession.insert(newMessage(kSession,"John","Peter","Hello","X1"));
        kSession.insert(newMessage(kSession,"John","Peter","Hello","X2"));
        kSession.fireAllRules();

        sleep(10);

        assertEquals(0,countMeta( Fulfill.class.getName(), kSession ));
        assertEquals(2,countMeta( Pending.class.getName(), kSession ));
        assertEquals(0,countMeta( Viol.class.getName(), kSession ));


        kSession.insert(newMessage(kSession,"Peter","John","Hello back","Y"));
        kSession.fireAllRules();


        assertEquals(2,countMeta( Fulfill.class.getName(), kSession ));
        assertEquals(2,countMeta( Pending.class.getName(), kSession ));
        assertEquals(0,countMeta( Viol.class.getName(), kSession ));

        sleep(200);
        kSession.fireAllRules();


        assertEquals(2,countMeta( Fulfill.class.getName(), kSession ));
        assertEquals(0,countMeta( Pending.class.getName(), kSession ));
        assertEquals(0,countMeta( Viol.class.getName(), kSession ));
        assertEquals(2,countMeta( Expectation.class.getName(), kSession ));


        assertTrue(list.contains("F2Y"));
        assertTrue(list.contains("F1Y"));

    }



    @Test
    public void testManyToManyFulfills() {

        String src = "" +
                "package org.drools;  " +
                "import org.drools.expectations.*;  " +
                "global java.util.List list; " +
                " " +
                "declare Msg  " +
                "   @role(event)  " +
                "   sender : String     @key " +
                "   receiver : String   @key " +
                "   body : String       @key " +
                "   more : String       @key  " +
                "end " +
                " " +
                "rule 'Expect'  " +
                "when " +
                "   $trigger : Msg( 'John', 'Peter', 'Hello', 'X1' ; )  " +
                "then " +
                "   expect $msg : Msg( 'Peter', 'John', 'Hello back', $moreOut ; this after[0,100ms] $trigger ) onFulfill   " +
                "    {  " +
                "        list.add( 'F1' + $moreOut );  " +
                "    } onViolation {  " +
                "        list.add( 'V1' ); " +
                "    } " +
                "end  " +
                " " +
                "rule 'Expect2'  " +
                "when " +
                "   $trigger : Msg( 'John', 'Peter', 'Hello', 'X2' ; )  " +
                "then " +
                "   expect $msg : Msg( 'Peter', 'John', 'Hello back', $moreOut ; this after[0,100ms] $trigger ) onFulfill   " +
                "    {  " +
                "        list.add( 'F2' + $moreOut );  " +
                "    } onViolation {  " +
                "        list.add( 'V2' ); " +
                "    } " +
                "end ";

        KieSession kSession = buildKnowledgeSession(src.getBytes());
        List<Object> list = new LinkedList<Object>();
        kSession.setGlobal("list",list);

        kSession.insert(newMessage(kSession,"John","Peter","Hello","X1"));
        kSession.insert(newMessage(kSession,"John","Peter","Hello","X2"));
        kSession.fireAllRules();

        sleep(10);

        assertEquals(0,countMeta( Fulfill.class.getName(), kSession ));
        assertEquals(2,countMeta( Pending.class.getName(), kSession ));
        assertEquals(0,countMeta( Viol.class.getName(), kSession ));


        kSession.insert(newMessage(kSession,"Peter","John","Hello back","Y1"));
        kSession.fireAllRules();

        sleep(20);

        kSession.insert(newMessage(kSession,"Peter","John","Hello back","Y2"));
        kSession.fireAllRules();

        assertEquals(4,countMeta( Fulfill.class.getName(), kSession ));
        assertEquals(2,countMeta( Pending.class.getName(), kSession ));
        assertEquals(0,countMeta( Viol.class.getName(), kSession ));

        sleep(200);
        kSession.fireAllRules();


        assertEquals(4,countMeta( Fulfill.class.getName(), kSession ));
        assertEquals(0,countMeta( Pending.class.getName(), kSession ));
        assertEquals(0,countMeta( Viol.class.getName(), kSession ));
        assertEquals(2,countMeta( Expectation.class.getName(), kSession ));

        assertTrue(list.contains("F2Y1"));
        assertTrue(list.contains("F1Y1"));
        assertTrue(list.contains("F2Y2"));
        assertTrue(list.contains("F1Y2"));

    }









    @Test
    public void testExpectChaining() {

        String src = "" +
                "package org.drools;  " +
                "import org.drools.expectations.*;  " +
                "global java.util.List list; " +
                " " +
                "declare Msg  " +
                "   @role(event)  " +
                "   sender : String     @key " +
                "   receiver : String   @key " +
                "   body : String       @key " +
                "   more : String       @key  " +
                "end " +
                " " +
                "rule 'Expect'  " +
                "when " +
                "   $trigger : Msg( 'John', 'Peter', 'Hello' ; )  " +
                "then " +
                "   expect $msg : Msg( 'Peter', 'John', 'Hello back' ; this after[0,100ms] $trigger ) " +
                "    onFulfill { " +
                "        expect Msg( 'Peter', 'John', 'Ack Hello' ; this after[0,100ms] $msg ) " +
                "                   onFulfill  { list.add('F2'); } " +
                "                   onViolation {}   " +
                "          System.out.println(' Just before an exp...');   " +
                "          list.add('F1' );  " +
                "    } " +
                "    onViolation { list.add('V1'); } " +
                "    list.add('F0' );  " +
                "end ";

        KieSession kSession = buildKnowledgeSession(src.getBytes());
        List<Object> list = new LinkedList<Object>();
        kSession.setGlobal("list",list);

        kSession.insert(newMessage(kSession,"John","Peter","Hello","X"));

        kSession.fireAllRules();

        assertEquals(0,countMeta( Fulfill.class.getName(), kSession ));
        assertEquals(1,countMeta( Pending.class.getName(), kSession ));
        assertEquals(0,countMeta( Viol.class.getName(), kSession ));

        sleep(10);

        kSession.insert(newMessage(kSession,"Peter","John","Hello back","Y"));
        kSession.fireAllRules();

        assertEquals(1,countMeta( Fulfill.class.getName(), kSession ));
        assertEquals(2,countMeta( Pending.class.getName(), kSession ));
        assertEquals(0,countMeta( Viol.class.getName(), kSession ));

        sleep(20);


        kSession.insert(newMessage(kSession,"Peter","John","Ack Hello","Z"));
        kSession.fireAllRules();


        sleep(30);
        kSession.fireAllRules();

        assertEquals(2,countMeta( Fulfill.class.getName(), kSession ));
        assertEquals(2,countMeta( Pending.class.getName(), kSession ));
        assertEquals(0,countMeta( Viol.class.getName(), kSession ));

        sleep( 300 );
        kSession.fireAllRules();

        System.out.println( reportWMObjects( kSession ) );

        assertEquals(2,countMeta( Fulfill.class.getName(), kSession ));
        assertEquals(0,countMeta( Pending.class.getName(), kSession ));
        assertEquals(0,countMeta( Viol.class.getName(), kSession ));
        assertEquals(2,countMeta( Expectation.class.getName(), kSession ));

        assertTrue(list.contains("F0"));
        assertTrue(list.contains("F1"));
        assertTrue(list.contains("F2"));

    }

    @Test 
    public void testExpectChainingViolations() {

        String src = "" +
                "package org.drools;  " +
                "import org.drools.expectations.*;  " +
                "global java.util.List list; " +
                " " +
                "declare Msg  " +
                "   @role(event)  " +
                "   sender : String     @key " +
                "   receiver : String   @key " +
                "   body : String       @key " +
                "   more : String       @key  " +
                "end " +
                " " +
                "rule 'Expect'  " +
                "when " +
                "   $trigger : Msg( 'John', 'Peter', 'Hello' ; )  " +
                "then " +
                "   expect $msg : Msg( 'Peter', 'John', 'Hello back' ; this after[0,500ms] $trigger ) onFulfill   " +
                "    {  " +
                "        expect Msg( 'Peter', 'John', 'Ack Hello' ; this after[0,500ms] $msg ) onFulfill " +
                "                   { list.add('F2'); } " +
                "                   onViolation { list.add('V2'); }   " +
                "        list.add('F1' );  " +
                "    } " +
                "    onViolation " +
                "    {  " +
                "        list.add('V1'); " +
                "    } " +
                "    list.add('F0' );  " +
                "end ";

        KieSession kSession = buildKnowledgeSession(src.getBytes());
        List<Object> list = new LinkedList<Object>();
        kSession.setGlobal("list",list);

        kSession.insert(newMessage(kSession,"John","Peter","Hello","X1"));

        kSession.fireAllRules();

        assertEquals(0,countMeta( Fulfill.class.getName(), kSession ));
        assertEquals(1,countMeta( Pending.class.getName(), kSession ));
        assertEquals(0,countMeta( Viol.class.getName(), kSession ));

        sleep(600);

        kSession.fireAllRules();

        assertEquals(0,countMeta( Fulfill.class.getName(), kSession ));
        assertEquals(0,countMeta( Pending.class.getName(), kSession ));
        assertEquals(1,countMeta( Viol.class.getName(), kSession ));
        assertEquals(1,countMeta( Expectation.class.getName(), kSession ));

        System.out.println(list);

        assertTrue(list.contains("F0"));
        assertTrue(list.contains("V1"));


        list.clear();



        kSession.insert(newMessage(kSession,"John","Peter","Hello","X2"));

        kSession.fireAllRules();
        sleep(150);

        kSession.insert(newMessage(kSession,"Peter","John","Hello back","Y1"));
        kSession.fireAllRules();

        assertEquals(1,countMeta( Fulfill.class.getName(), kSession ));
        assertEquals(2,countMeta( Pending.class.getName(), kSession ));
        assertEquals(0+1,countMeta( Viol.class.getName(), kSession ));

        sleep(1200);

        kSession.fireAllRules();

        assertEquals(1,countMeta( Fulfill.class.getName(), kSession ));
        assertEquals(0,countMeta( Pending.class.getName(), kSession ));
        assertEquals(1+1,countMeta( Viol.class.getName(), kSession ));
        assertEquals(2+1,countMeta( Expectation.class.getName(), kSession ));

        System.out.println(list);

        assertTrue(list.contains("F0"));
        assertTrue(list.contains("F1"));
        assertTrue(list.contains("V2"));


        list.clear();


        sleep(200);



        kSession.insert(newMessage(kSession,"John","Peter","Hello","X3"));

        kSession.fireAllRules();
        sleep(200);

        kSession.insert(newMessage(kSession,"Peter","John","Hello back","Y3"));
        kSession.fireAllRules();
        sleep(350);

        kSession.fireAllRules();

        kSession.insert(newMessage(kSession,"Peter","John","Hello back","Ya"));
        kSession.fireAllRules();
        assertEquals(1+1,countMeta( Fulfill.class.getName(), kSession ));
        assertEquals(1,countMeta( Pending.class.getName(), kSession ));
        assertEquals(0+1+1,countMeta( Viol.class.getName(), kSession ));
        sleep(50);

        kSession.insert(newMessage(kSession,"Peter","John","Ack Hello","Z"));
        kSession.fireAllRules();

        assertEquals(2+1,countMeta( Fulfill.class.getName(), kSession ));
        assertEquals(1,countMeta( Pending.class.getName(), kSession ));
        assertEquals(0+1+1,countMeta( Viol.class.getName(), kSession ));
        sleep(500);

        kSession.fireAllRules();

        assertEquals(2+1,countMeta( Fulfill.class.getName(), kSession ));
        assertEquals(0,countMeta( Pending.class.getName(), kSession ));
        assertEquals(0+1+1,countMeta( Viol.class.getName(), kSession ));
        assertEquals(2+2+1,countMeta( Expectation.class.getName(), kSession ));

    }



    @Test
    public void testRefineExpectations() {

        String src = "" +
                "package org.drools;  " +
                "import org.drools.expectations.*;  " +
                "global java.util.List list; " +
                " " +
                "declare Msg  " +
                "   @role(event)  " +
                "   sender : String     @key " +
                "   receiver : String   @key " +
                "   body : String       @key " +
                "   more : String       @key  " +
                "end " +
                " " +
                "rule 'Expect'  " +
                "when " +
                "   $trigger : Msg( 'John', 'Peter', 'Hello' ; )  " +
                "then " +
                "   expect $msg : Msg( 'Peter', 'John' ; this after[0,100ms] $trigger ) onFulfill   " +
                "    {  " +
                "       expect one $z : Msg( this == $msg, more == 'Y' ) onFulfill { list.add('F2'); } onViolation { list.add('V2'); }  " +
                "        list.add('F1' );  " +
                "    } " +
                "    onViolation " +
                "    {  " +
                "        list.add('V1'); " +
                "    } " +
                "        list.add('F0' );  " +
                "end ";

        KieSession kSession = buildKnowledgeSession(src.getBytes());
        List<Object> list = new LinkedList<Object>();
        kSession.setGlobal("list",list);


        kSession.insert(newMessage(kSession,"John","Peter","Hello","X"));
        kSession.fireAllRules();

        sleep(50);

        kSession.insert(newMessage(kSession,"Peter","John","Hello","Y"));
        kSession.fireAllRules();


        sleep(200);

        kSession.fireAllRules();

        assertTrue(list.contains("F0"));
        assertTrue(list.contains("F1"));
        assertTrue(list.contains("F2"));
        assertFalse(list.contains("V0"));
        assertFalse(list.contains("V1"));
        assertFalse(list.contains("V2"));

        System.out.println( reportWMObjects( kSession ) );

        assertEquals( 2, countMeta( Fulfill.class.getName(), kSession ) );
        assertEquals(0,countMeta( Pending.class.getName(), kSession ));
        assertEquals(0,countMeta( Viol.class.getName(), kSession ));
        assertEquals(2,countMeta( Expectation.class.getName(), kSession ));

    }










    @Test
    public void testPendingStatus() {

        String src = "" +
                "package org.drools;  " +
                "import org.drools.expectations.*;  " +
                "global java.util.List list; " +
                " " +
                "declare Msg  " +
                "   @role(event)  " +
                "   sender : String     @key " +
                "   receiver : String   @key " +
                "   body : String       @key " +
                "   more : String       @key  " +
                "end " +
                " " +
                "rule 'Expect'  " +
                "when " +
                "   $trigger : Msg( 'John', 'Peter', 'Hello' ; )  " +
                "then " +
                "   expect $msg : Msg( 'Peter', 'John', 'Hello back' ; this after[0,200ms] $trigger ) onFulfill   " +
                "    {  " +
                "        expect Msg( 'Peter', 'John', 'Ack Hello' ; this after[0,200ms] $msg ) onFulfill " +
                "                   { list.add('F2'); } " +
                "                   onViolation { list.add('V2'); }   " +
                "        list.add('F1' );  " +
                "    } " +
                "    onViolation " +
                "    {  " +
                "        list.add('V1'); " +
                "    } " +
                "    list.add('F0' );  " +
                "end ";


        KieSession kSession = buildKnowledgeSession(src.getBytes());
        List<Object> list = new LinkedList<Object>();
        kSession.setGlobal("list",list);


        kSession.insert(newMessage(kSession,"John","Peter","Hello","X"));
        kSession.fireAllRules();

        sleep(50);

        assertEquals(0,countMeta( Fulfill.class.getName(), kSession ));
        assertEquals(1,countMeta( Pending.class.getName(), kSession ));
        assertEquals(0,countMeta( Viol.class.getName(), kSession ));
        assertEquals(1,countMeta( Expectation.class.getName(), kSession ));


        sleep(170);

        kSession.fireAllRules();

        assertEquals(0,countMeta( Fulfill.class.getName(), kSession ));
        assertEquals(0,countMeta( Pending.class.getName(), kSession ));
        assertEquals(1,countMeta( Viol.class.getName(), kSession ));
        assertEquals(1,countMeta( Expectation.class.getName(), kSession ));


        kSession.dispose();

        kSession = buildKnowledgeSession(src.getBytes());
        kSession.setGlobal("list",list);



        kSession.insert(newMessage(kSession,"John","Peter","Hello","X"));
        kSession.fireAllRules();

        sleep(50);

        assertEquals(0,countMeta( Fulfill.class.getName(), kSession ));
        assertEquals(1,countMeta( Pending.class.getName(), kSession ));
        assertEquals(0,countMeta( Viol.class.getName(), kSession ));
        assertEquals(1,countMeta( Expectation.class.getName(), kSession ));

        sleep(100);

        kSession.insert(newMessage(kSession,"Peter","John","Hello back","Y"));
        kSession.fireAllRules();

        assertEquals(1,countMeta( Fulfill.class.getName(), kSession ));
        assertEquals(2,countMeta( Pending.class.getName(), kSession ));
        assertEquals(0,countMeta( Viol.class.getName(), kSession ));
        assertEquals(2,countMeta( Expectation.class.getName(), kSession ));


        sleep(100);

        kSession.fireAllRules();

        assertEquals(1,countMeta( Fulfill.class.getName(), kSession ));
        assertEquals(1,countMeta( Pending.class.getName(), kSession ));
        assertEquals(0,countMeta( Viol.class.getName(), kSession ));
        assertEquals(2,countMeta( Expectation.class.getName(), kSession ));


        sleep(150);

        kSession.fireAllRules();


        assertEquals(1,countMeta( Fulfill.class.getName(), kSession ));
        assertEquals(0,countMeta( Pending.class.getName(), kSession ));
        assertEquals(1,countMeta( Viol.class.getName(), kSession ));
        assertEquals(2,countMeta( Expectation.class.getName(), kSession ));


    }



    @Test 
    public void testExpectInnerOr() {

        String src = "" +
                "package org.drools;  " +
                "import org.drools.expectations.*;  " +
                "global java.util.List list; " +
                " " +
                "declare Msg  " +
                "   @role(event)  " +
                "   sender : String     @key " +
                "   receiver : String   @key " +
                "   body : String       @key " +
                "   more : String       @key  " +
                "end " +
                " " +
                "rule 'Expect'  " +
                "when " +
                "   $trigger : Msg( 'John', 'Peter', 'Hello' ; )  " +
                "then " +
                "   expect $msg : Msg( 'Peter', 'John' ; $b1 : body == 'X' || == 'Y' , this after[0,250ms] $trigger ) " +
                "   onFulfill   " +
                "    {   " +
                "  " +
                "       expect Msg( 'Peter', 'John' , 'Z' ; $extra : more == $b1, this after[0,100ms] $msg ) onFulfill  " +
                "          {list.add('F1'+$extra);} onViolation {list.add('V1'+$b1);}  " +
                "  " +
                "       System.out.println('Full by '+$msg);   " +
                "       list.add('F0');  " +
                "    }  " +
                "end ";

        KieSession kSession = buildKnowledgeSession(src.getBytes());
        List<Object> list = new LinkedList<Object>();
        kSession.setGlobal("list",list);

        kSession.insert(newMessage(kSession,"John","Peter","Hello","A"));

        kSession.fireAllRules();

        assertEquals(0,countMeta( Fulfill.class.getName(), kSession ));
        assertEquals(1,countMeta( Pending.class.getName(), kSession ));
        assertEquals(0,countMeta( Viol.class.getName(), kSession ));
        assertEquals(1,countMeta( Expectation.class.getName(), kSession ));

        sleep(200);

        kSession.insert(newMessage(kSession,"Peter","John","X","a"));
        kSession.fireAllRules();


        kSession.insert(newMessage(kSession,"Peter","John","Y","b"));
        kSession.fireAllRules();

        sleep(50);


        kSession.insert(newMessage(kSession,"Peter","John","Z","Y"));
        kSession.fireAllRules();

        kSession.insert(newMessage(kSession,"Peter","John","Z","c"));
        kSession.fireAllRules();


        sleep(200);
        kSession.fireAllRules();

        assertEquals(3,countMeta( Fulfill.class.getName(), kSession ));
        assertEquals(0,countMeta( Pending.class.getName(), kSession ));
        assertEquals(1,countMeta( Viol.class.getName(), kSession ));
        assertEquals(3,countMeta( Expectation.class.getName(), kSession ));

        System.out.println(list);

        assertTrue(list.contains("F0"));
        assertTrue(list.contains("F1Y"));
        assertTrue(list.contains("V1X"));


    }













    @Test 
    public void testCompensation() {

        String src = "" +
                "package org.drools;  " +
                "import org.drools.expectations.*;  " +
                "global java.util.List list; " +
                " " +
                "declare Msg  " +
                "   @role(event)  " +
                "   sender : String     @key " +
                "   receiver : String   @key " +
                "   body : String       @key " +
                "   more : String         " +
                "end " +
                " " +
                "rule 'Expect'  " +
                "when " +
                "   $trigger : Msg( 'John', 'Peter', 'Hello' ; )  " +
                "then " +
                "   $exp1 : expect $msg : Msg( 'Peter', 'John' , 'Hello back'  ; this after[0,50ms] $trigger ) onFulfill   " +
                "    { list.add(+1); } onViolation {   " +
                "       $xcs1 : expect $excuse1 : Msg( 'Peter', 'John' , 'Sorry'  ; this after[0,200ms] $trigger ) onFulfill  " +
                "       {  " +
                "           $xcs2 : expect $excuse2 : Msg( 'Peter', 'John' , 'Sorry Again'  ; this after[0,100ms] $excuse1 ) onFulfill  " +
                "               {  " +
                "                   repair $exp1;  " +
                "                   System.err.println(drools.getMatch().getObjects());  "+
                "                   list.add(+3);  " +
                "               }  " +
                "               onViolation { list.add(-3); }   " +
                "       list.add(+2);" +
                "       } " +
                "       onViolation {list.add(-2);}  "  +
                "    list.add(-1);" +
                "    } " +
                "   list.add(0);" +
                "end ";

        KieSession kSession = buildKnowledgeSession(src.getBytes());
        List<Object> list = new LinkedList<Object>();
        kSession.setGlobal("list",list);


        kSession.insert(newMessage(kSession,"Peter","John","Sorry","C"));
        kSession.insert(newMessage(kSession,"Peter","John","Sorry Again","C"));

        kSession.insert(newMessage(kSession,"John","Peter","Hello","A"));
        kSession.fireAllRules();

        sleep(100);

        kSession.insert(newMessage(kSession,"Peter","John","Hello back","B"));

        kSession.fireAllRules();


        sleep(10);

        kSession.fireAllRules();

        sleep(400);
        kSession.fireAllRules();

        System.out.println( reportWMObjects( kSession ) );

        assertEquals( 3, countMeta( Fulfill.class.getName(), kSession ) );
        assertEquals( 0, countMeta( Pending.class.getName(), kSession ) );
        assertEquals( 1, countMeta( Viol.class.getName(), kSession ) );
        assertEquals( 3, countMeta( Expectation.class.getName(), kSession ) );
        assertEquals( 1, countMeta( Success.class.getName() ,kSession) );
        assertEquals( 0, countMeta( Failure.class.getName()  ,kSession) );

    }




    @Test 
    public void testImmediateExpectation() {

        String src = "" +
                "package org.drools;  " +
                "import org.drools.expectations.*;  " +
                "global java.util.List list; " +
                " " +
                "declare Msg  " +
                "   @role(event)  " +
                "   sender : String     @key " +
                "   receiver : String   @key " +
                "   body : String       @key " +
                "   more : String       @key  " +
                "end " +
                " " +
                "rule 'Expect'  " +
                "when " +
                "   $trigger : Msg( 'John', 'Peter', 'Hello' ; )  " +
                "then " +
                "   expect $msg : Msg( 'Peter', 'John', 'Hello back' ;  ) onFulfill   " +
                "    {  " +
                "        list.add('F1' );  " +
                "    } " +
                "    onViolation " +
                "    {  " +
                "        list.add('V1'); " +
                "    } " +
                "end ";

        KieSession kSession = buildKnowledgeSession(src.getBytes());
        List<Object> list = new LinkedList<Object>();
        kSession.setGlobal("list",list);


        kSession.insert(newMessage(kSession,"Peter","John","Hello back","Y"));

        kSession.fireAllRules();

        sleep(30);

        kSession.insert(newMessage(kSession,"John","Peter","Hello","X"));

        kSession.fireAllRules();


        sleep(300);
        kSession.fireAllRules();

        assertEquals(1,countMeta( Fulfill.class.getName(), kSession ));
        assertEquals(1,countMeta( Pending.class.getName(), kSession ));
        assertEquals(0,countMeta( Viol.class.getName(), kSession ));
        assertEquals(1,countMeta( Expectation.class.getName(), kSession ));

        assertTrue(list.contains("F1"));

    }




    @Test 
    public void testPositiveClosure() {

        String src = "" +
                "package org.drools;  " +
                "import org.drools.expectations.*;  " +
                "global java.util.List list; " +
                " " +
                "declare Msg  " +
                "   @role(event)  " +
                "   sender : String     @key " +
                "   receiver : String   @key " +
                "   body : String       @key " +
                "   more : String       @key  " +
                "end " +
                " " +
                "rule 'Expect'  " +
                "when " +
                "   $trigger : Msg( 'John', 'Peter', 'Hello' ; )  " +
                "then " +
                "   expect $msg : Msg( 'Peter', 'John' ; this after[0,100ms] $trigger ) onFulfill   " +
                "    {  " +
                "        list.add('F1' );  " +
                "    } " +
                "    onViolation " +
                "    {  " +
                "        list.add('V1'); " +
                "    } " +
                "end ";

        KieSession kSession = buildKnowledgeSession(src.getBytes());
        List<Object> list = new LinkedList<Object>();
        kSession.setGlobal("list",list);


        kSession.insert(newMessage(kSession,"John","Peter","Hello","X"));
        kSession.fireAllRules();

        sleep(50);


        kSession.fireAllRules();

        assertEquals(0,countMeta( Fulfill.class.getName(), kSession ));
        assertEquals(1,countMeta( Pending.class.getName(), kSession ));
        assertEquals(0,countMeta( Viol.class.getName(), kSession ));
        assertEquals(1,countMeta( Expectation.class.getName(), kSession ));



        kSession.insert( new Closure() );
        kSession.fireAllRules();

        System.out.println( reportWMObjects( kSession ) );

        assertEquals( 0, list.size() );
        assertEquals( 0, countMeta( Fulfill.class.getName(), kSession ) );
        assertEquals( 0, countMeta( Pending.class.getName(), kSession ) );
        assertEquals( 1, countMeta( Viol.class.getName(), kSession ) );
        assertEquals( 1, countMeta( Expectation.class.getName(), kSession ) );


    }




    @Test 
    public void testMultiPositiveClosure() {

        String src = "" +
                "package org.drools;  " +
                "import org.drools.expectations.*;  " +
                "global java.util.List list; " +
                " " +
                "declare Msg  " +
                "   @role(event)  " +
                "   sender : String     @key " +
                "   receiver : String   @key " +
                "   body : String       @key " +
                "   more : String       @key  " +
                "end " +
                " " +
                "rule 'Expect'  " +
                "when " +
                "   $trigger : Msg( 'John', 'Peter', 'Hello' ; )  " +
                "then " +
                "   expect $msg : Msg( 'Peter', 'John' ; this after[0,100ms] $trigger ) onFulfill   " +
                "    {  " +
                "        expect $msg2 : Msg( 'Ronald', 'Donald' ; this after[0,100ms] $msg ) onFulfill {} onViolation {}   " +
                "        list.add('F1' );  " +
                "    } " +
                "    onViolation " +
                "    {  " +
                "        list.add('V1'); " +
                "    } " +
                "end " +
                "  " +
                "rule 'Expect2'  " +
                "when " +
                "   $trigger : Msg( 'John', 'Peter', 'Hello' ; )  " +
                "then " +
                "   expect $msg : Msg( 'Mark', 'Adam' ; this after[0,100ms] $trigger ) onFulfill   " +
                "    {  " +
                "        list.add('F1' );  " +
                "    } " +
                "    onViolation " +
                "    {  " +
                "        list.add('V1'); " +
                "    } " +
                "end";

        KieSession kSession = buildKnowledgeSession(src.getBytes());
        List<Object> list = new LinkedList<Object>();
        kSession.setGlobal("list",list);


        kSession.insert(newMessage(kSession,"John","Peter","Hello","X"));
        kSession.fireAllRules();

        sleep(2);

        kSession.insert(newMessage(kSession,"Peter","John","Hello","X"));
        kSession.fireAllRules();


        sleep(30);


        kSession.fireAllRules();

        assertEquals( 1,countMeta( Fulfill.class.getName(), kSession ) );
        assertEquals( 3,countMeta( Pending.class.getName(), kSession ) );
        assertEquals( 0,countMeta( Viol.class.getName(), kSession ) );
        assertEquals( 3,countMeta( Expectation.class.getName(), kSession ) );


        kSession.insert( new Closure() );
        kSession.fireAllRules();

        System.out.println( reportWMObjects( kSession ) );

        assertEquals(1,countMeta( Fulfill.class.getName(), kSession ));
        assertEquals(0,countMeta( Pending.class.getName(), kSession ));
        assertEquals(2,countMeta( Viol.class.getName(), kSession ));
        assertEquals(3,countMeta( Expectation.class.getName(), kSession ));





    }









    @Test 
    public void testSuccess() {

        String src = "" +
                "package org.drools;  " +
                "import org.drools.expectations.*;  " +
                "global java.util.List list; " +
                " " +
                "declare Msg  " +
                "   @role(event)  " +
                "   sender : String     @key " +
                "   receiver : String   @key " +
                "   body : String       @key " +
                "   more : String         " +
                "end " +
                " " +
                "rule 'Expect'  " +
                "when " +
                "   $trigger : Msg( 'John', 'Peter', 'Hello' ; )  " +
                "then " +
                "   $exp11 : expect $message : Msg( 'Peter', 'John' , 'Hello Back'  ; this after[0,500ms] $trigger ) onFulfill {} onViolation {}   " +
                "   or         " +
                "   $exp12 : expect $altMesg : Msg( 'Peter', 'John' , 'Back Hello'  ; this after[0,50ms] $trigger ) onFulfill {} onViolation {}  " +
                "     " +
                "   $exp2  : expect $moreMsg : Msg( 'Peter', 'John' , 'Hows going' ; this after[0,50ms] $trigger ) onFulfill {} onViolation {}  " +
                "end ";

        KieSession kSession = buildKnowledgeSession(src.getBytes());
        List<Object> list = new LinkedList<Object>();
        kSession.setGlobal("list",list);


        kSession.insert(newMessage(kSession,"John","Peter","Hello","A"));
        kSession.fireAllRules();

        sleep( 20 );

        kSession.insert(newMessage(kSession,"Peter","John","Hows going","A"));      // here due to timer bug
        kSession.fireAllRules();

        sleep( 20 );

        kSession.insert(newMessage(kSession,"Peter","John","Hello Back","A"));
        kSession.fireAllRules();

        sleep( 600 );
        kSession.fireAllRules();

        System.out.println( reportWMObjects( kSession ) );

        assertEquals( 2, countMeta( Fulfill.class.getName(), kSession ) );
        assertEquals( 0, countMeta( Pending.class.getName(), kSession ) );
        assertEquals( 1, countMeta( Viol.class.getName(), kSession ) );
        assertEquals( 3, countMeta( Expectation.class.getName(), kSession ) );
        assertEquals( 1, countMeta( Success.class.getName() ,kSession ) );
        assertEquals( 0, countMeta( Failure.class.getName()  ,kSession ) );


    }






    @Test
    public void testFailure() {

        String src = "" +
                "package org.drools;  " +
                "import org.drools.expectations.*;  " +
                "global java.util.List list; " +
                " " +
                "declare Msg  " +
                "   @role(event)  " +
                "   sender : String     @key " +
                "   receiver : String   @key " +
                "   body : String       @key " +
                "   more : String         " +
                "end " +
                " " +
                "rule 'Expect'  " +
                "when " +
                "   $trigger : Msg( 'John', 'Peter', 'Hello' ; )  " +
                "then " +
                "   $exp11 : expect $message : Msg( 'Peter', 'John' , 'Hello Back'  ; this after[0,500ms] $trigger ) onFulfill {} onViolation {}   " +
                "   or         " +
                "   $exp12 : expect $altMesg : Msg( 'Peter', 'John' , 'Back Hello'  ; this after[0,50ms] $trigger ) onFulfill {} onViolation {}  " +
                "     " +
                "   $exp2  : expect $moreMsg : Msg( 'Peter', 'John' , 'Hows going' ;  this after[0,50ms] $trigger ) onFulfill {} onViolation {}  " +
                "end ";

        KieSession kSession = buildKnowledgeSession(src.getBytes());
        List<Object> list = new LinkedList<Object>();
        kSession.setGlobal("list",list);

        sleep(10);

        kSession.insert(newMessage(kSession,"John","Peter","Hello","A"));
        kSession.fireAllRules();

        sleep(20);

        kSession.insert(newMessage(kSession,"Peter","John","Hows going","A"));      // here due to timer bug
        kSession.fireAllRules();

        sleep(600);
        kSession.fireAllRules();


        assertEquals(1,countMeta( Fulfill.class.getName(), kSession ));
        assertEquals(0,countMeta( Pending.class.getName(), kSession ));
        assertEquals(2,countMeta( Viol.class.getName(), kSession ));
        assertEquals(3,countMeta( Expectation.class.getName(), kSession ));
        assertEquals(0,countMeta( Success.class.getName() ,kSession));
        assertEquals(1,countMeta( Failure.class.getName()  ,kSession));


    }













    @Test 
    public void testDeepNestingFailure() {

        String src = "" +
                "package org.drools; " +
                "import org.drools.expectations.*; " +
                "global java.util.List list;" +
                "" +
                "declare Msg " +
                "   @role(event) " +
                "   sender : String     @key" +
                "   receiver : String   @key" +
                "   body : String       @key" +
                "   more : String        " +
                "end " +
                "" +
                "rule 'Expect' " +
                "when " +
                "   $trigger : Msg( 'John', 'Peter', 'Hello' ; ) " +
                "then " +
                "   $exp1 : expect $message : Msg( 'Peter', 'John' , 'Hello Back'  ; this after[0,200ms] $trigger ) onFulfill {} onViolation {}  " +
                "   " +
                "   $exp2 : expect $altMesg : Msg( 'Peter', 'John' , 'Back Hello'  ; this after[0,100ms] $trigger ) onFulfill " +
                "       { " +
                "           $exp21  : expect $moreMsg : Msg( 'Peter', 'John' , 'Hows going' ; this after[0,100ms] $altMesg ) onFulfill {} onViolation {} " +
                "       } " +
                "       onViolation " +
                "       { " +
                "           $exp22  : expect $xMsg : Msg( 'Peter', 'John' , 'Never Ever' ; /* this after[0,50ms] $trigger */ ) onFulfill " +
                "               { " +
                "                   $exp221  : expect $dfMsg : Msg( 'Peter', 'John' , 'Deep 1' ; /* this after[0,50ms] $trigger */ ) onFulfill {} onViolation {}   " +
                "               } " +
                "               onViolation " +
                "               { " +
                "                   $exp222  : expect $dvMsg : Msg( 'Peter', 'John' , 'Deep 2' ; /* this after[0,200ms] $trigger */ ) onFulfill {} onViolation {}   " +
                "               } " +
                "       } " +
                "end ";

        KieSession kSession = buildKnowledgeSession(src.getBytes());
        List<Object> list = new LinkedList<Object>();
        kSession.setGlobal("list",list);

        kSession.insert(newMessage(kSession,"Peter","John","Deep 1","D"));
        kSession.insert(newMessage(kSession,"Peter","John","Never Ever","C"));
        kSession.fireAllRules();


        kSession.insert(newMessage(kSession,"John","Peter","Hello","A"));
        kSession.fireAllRules();

        sleep(20);

        kSession.insert(newMessage(kSession,"Peter","John","Hello Back","B"));
        kSession.fireAllRules();

        sleep(30);





        sleep(500);
        kSession.fireAllRules();

        assertEquals( 3, countMeta( Fulfill.class.getName(), kSession ) );
        assertEquals( 2, countMeta( Pending.class.getName(), kSession ) );
        assertEquals( 1, countMeta( Viol.class.getName(), kSession ) );
        assertEquals( 4, countMeta( Expectation.class.getName(), kSession ) );
        assertEquals( 0, countMeta( Success.class.getName(), kSession ) );
        assertEquals( 1, countMeta( Failure.class.getName(), kSession ) );


    }












    @Test
    public void testNestedSuccess() {

        String src = "" +
                "package org.drools;  " +
                "import org.drools.expectations.*;  " +
                "global java.util.List list; " +
                " " +
                "declare Msg  " +
                "   @role(event)  " +
                "   sender : String     @key " +
                "   receiver : String   @key " +
                "   body : String       @key " +
                "   more : String         " +
                "end " +
                " " +
                "rule 'Expect'  " +
                "when " +
                "   $trigger : Msg( 'John', 'Peter', 'Hello' ; )  " +
                "then " +
                "   $exp1 : expect $message : Msg( 'Peter', 'John' , 'Hello Back'  ; this after[0,500ms] $trigger ) " +
                "   onFulfill " +
                "   {   $exp11 : expect Msg( 'John', 'Peter', 'InCase1111' ; this after[0,150ms] $message ) " +
                "           onFulfill  " +
                "           { $exp111 : expect Msg( 'John', 'Peter', 'InCase00' ; ) onFulfill {} onViolation {} }  " +
                "           onViolation " +
                "           { $exp112 : expect Msg( 'John', 'Peter', 'InCase99' ; ) onFulfill {} onViolation {} }  " +
                "   }  " +
                "   onViolation  " +
                "   {   $exp12 : expect Msg( 'John', 'Peter', 'InCase2222' ; ) onFulfill {} onViolation {} }   " +
                "   or         " +
                "   $exp2 : expect $moreMsg : Msg( 'Peter', 'John' , 'Hows going' ; this after[0,50ms] $trigger ) onFulfill {} onViolation {}  " +
                "end ";

        KieSession kSession = buildKnowledgeSession(src.getBytes());
        List<Object> list = new LinkedList<Object>();
        kSession.setGlobal("list",list);

        kSession.insert(newMessage(kSession,"John","Peter","InCase00","moreXYZ"));


        kSession.insert(newMessage(kSession,"John","Peter","Hello","A"));
        kSession.fireAllRules();

        sleep(50);

        kSession.insert(newMessage(kSession,"Peter","John","Hello Back",".."));
        kSession.fireAllRules();

        sleep(50);

        kSession.insert(newMessage(kSession,"John","Peter","InCase1111","...."));
        kSession.fireAllRules();


        sleep(600);
        kSession.fireAllRules();

        System.out.println( reportWMObjects( kSession ) );

        assertEquals( 3, countMeta( Fulfill.class.getName(), kSession ) );
        assertEquals( 1, countMeta( Pending.class.getName(), kSession ) );
        assertEquals( 1, countMeta( Viol.class.getName(), kSession ) );
        assertEquals( 4, countMeta( Expectation.class.getName(), kSession ) );
        assertEquals( 1, countMeta( Success.class.getName() ,kSession) );
        assertEquals( 0, countMeta( Failure.class.getName()  ,kSession) );


    }








    @Test
    public void testDisableMetaExpectations() {

        String src = "" +
                "package org.drools;  " +
                "global java.util.List list; " +
                " " +
                "declare Msg  " +
                "   @role(event)  " +
                "   sender : String     @key " +
                "   receiver : String   @key " +
                "   body : String       @key " +
                "   more : String         " +
                "end " +
                " " +
                "rule 'Expect'  " +
                "@Expect( enabled=false )" +
                "when " +
                "   $trigger : Msg( 'John', 'Peter', 'Hello' ; )  " +
                "then " +
                "   $exp1 : expect $message : Msg( 'Peter', 'John' , 'Hello Back'  ; this after[0,500ms] $trigger ) onFulfill " +
                "   {   $exp11 : expect Msg( 'John', 'Peter', 'InCase1111' ; this after[0,150ms] $message ) onFulfill  " +
                "           { $exp111 : expect Msg( 'John', 'Peter', 'InCase00' ; ) onFulfill { list.add(1234); } onViolation {} }  " +
                "           onViolation " +
                "           { $exp112 : expect Msg( 'John', 'Peter', 'InCase99' ; ) onFulfill {} onViolation {} }  " +
                "   }  " +
                "   onViolation  " +
                "   {   $exp12 : expect Msg( 'John', 'Peter', 'InCase2222' ; ) onFulfill {} onViolation {} }   " +
                "   or         " +
                "   $exp2 : expect $moreMsg : Msg( 'Peter', 'John' , 'Hows going' ; /* this after[0,50ms] $trigger */ ) onFulfill {} onViolation {}  " +
                "end ";

        KieSession kSession = buildKnowledgeSession(src.getBytes());
        List<Object> list = new LinkedList<Object>();
        kSession.setGlobal("list",list);


        kSession.insert(newMessage(kSession,"John","Peter","InCase00","moreXYZ"));


        kSession.insert(newMessage(kSession,"John","Peter","Hello","A"));
        kSession.fireAllRules();

        sleep(50);

        kSession.insert(newMessage(kSession,"Peter","John","Hello Back",".."));
        kSession.fireAllRules();

        sleep(50);

        kSession.insert(newMessage(kSession,"John","Peter","InCase1111","...."));
        kSession.fireAllRules();


        sleep(600);
        kSession.fireAllRules();

        assertTrue( list.contains( 1234 ) );

        assertEquals( 0, countMeta( Fulfill.class.getName(), kSession ));
        assertEquals( 0, countMeta( Pending.class.getName(), kSession ));
        assertEquals( 0, countMeta( Viol.class.getName(), kSession ));
        assertEquals( 0, countMeta( Expectation.class.getName(), kSession ));
        assertEquals( 0, countMeta( Success.class.getName() ,kSession));
        assertEquals( 0, countMeta( Failure.class.getName()  ,kSession));


    }









    @Test 
    public void testProhibition() {

        String src = "" +
                "package org.drools;  " +
                "import org.drools.expectations.*;  " +
                "global java.util.List list; " +
                " " +
                "declare Msg  " +
                "   @role(event)  " +
                "   sender : String     @key " +
                "   receiver : String   @key " +
                "   body : String       @key " +
                "   more : String         " +
                "end " +
                " " +
                "rule 'Expect'  " +
                "when " +
                "   $trigger : Msg( 'John', 'Peter', 'Hello' ; )  " +
                "then " +
                "   $exp1 : expect not Msg( 'Peter', 'John' , 'Hello Back'  ; this after[0,100ms] $trigger ) " +
                "    onFulfill " +
                "    {  list.add(+1); " +
                "    }  " +
                "    onViolation  " +
                "    {  list.add(-1); " +
                "    }  " +
                "end ";

        KieSession kSession = buildKnowledgeSession(src.getBytes());
        List<Object> list = new LinkedList<Object>();
        kSession.setGlobal("list",list);


        kSession.insert(newMessage(kSession,"John","Peter","Hello","A"));
        kSession.fireAllRules();

        sleep(150);

        kSession.insert(newMessage(kSession,"Peter","John","Hello Back",".."));
        kSession.fireAllRules();


        sleep(50);
        kSession.fireAllRules();

        assertEquals( 1, countMeta( Fulfill.class.getName(), kSession ) );
        assertEquals( 0, countMeta( Pending.class.getName(), kSession ) );
        assertEquals( 0, countMeta( Viol.class.getName(), kSession ) );
        assertEquals( 1, countMeta( Expectation.class.getName(), kSession ) );
        assertEquals( 1, countMeta( Success.class.getName() ,kSession) );
        assertEquals( 0, countMeta( Failure.class.getName()  ,kSession) );


    }






    @Test 
    public void testOptionals() {

        String src = "" +
                "package org.drools;  " +
                "import org.drools.expectations.*;  " +
                "global java.util.List list; " +
                " " +
                "declare Msg  " +
                "   @role(event)  " +
                "   sender : String     @key " +
                "   receiver : String   @key " +
                "   body : String       @key " +
                "   more : String         " +
                "end " +
                " " +
                "rule 'Expect'  " +
                "when " +
                "   $trigger : Msg( 'John', 'Peter', 'Hello' ; )  " +
                "then " +
                "   $exp1 : expect Msg( 'Peter', 'John' , 'Hello Back'  ; this after[0,100ms] $trigger )  " +
                "end ";

        KieSession kSession = buildKnowledgeSession(src.getBytes());
        List<Object> list = new LinkedList<Object>();
        kSession.setGlobal("list",list);


        kSession.insert(newMessage(kSession,"John","Peter","Hello","A"));
        kSession.fireAllRules();

        sleep(150);

        kSession.insert(newMessage(kSession,"Peter","John","Hello Back",".."));
        kSession.fireAllRules();


        assertEquals(0,countMeta( Fulfill.class.getName(), kSession ));
        assertEquals(0,countMeta( Pending.class.getName(), kSession ));
        assertEquals(1,countMeta( Viol.class.getName(), kSession ));
        assertEquals(1,countMeta( Expectation.class.getName(), kSession ));
        assertEquals(0,countMeta( Success.class.getName() ,kSession));
        assertEquals(1,countMeta( Failure.class.getName()  ,kSession));


    }



    @Test
    public void testMixedTemporal() {

        String src = "" +
                "package org.drools;  " +
                "import org.drools.expectations.*;  " +
                "global java.util.List list; " +
                " " +
                "declare Msg  " +
                "   @role(event)  " +
                "   sender : String     @key " +
                "   receiver : String   @key " +
                "   body : String       @key " +
                "   more : String         " +
                "end " +
                " " +
                "rule 'Expect'  " +
                "when " +
                "   $trigger1 : Msg( 'John', 'Peter', 'Hello' ; )  " +
                "   $trigger2 : Msg( 'John', 'Peter', 'ollaH' ; )" +
                "then " +
                "   $exp1 : expect Msg( 'Peter', 'John' , 'Hola'  ; this after[0,100ms] $trigger1, this before[0,100ms] $trigger2 )  " +
                "end ";

        KieSession kSession = buildKnowledgeSession(src.getBytes());
        List<Object> list = new LinkedList<Object>();
        kSession.setGlobal("list",list);


        kSession.insert(newMessage(kSession,"John","Peter","Hello","A"));
        kSession.fireAllRules();

        sleep(50);

        kSession.insert(newMessage(kSession,"Peter","John","Hola","B"));
        kSession.fireAllRules();

        sleep(50);

        kSession.insert(newMessage(kSession,"John","Peter","ollaH","C"));
        kSession.fireAllRules();

        sleep(290);


        assertEquals(1,countMeta( Fulfill.class.getName(), kSession ));
        assertEquals(1,countMeta( Pending.class.getName(), kSession ));
        assertEquals(0,countMeta( Viol.class.getName(), kSession ));
        assertEquals(1,countMeta( Expectation.class.getName(), kSession ));
        assertEquals(1,countMeta( Success.class.getName() ,kSession));
        assertEquals(0,countMeta( Failure.class.getName() ,kSession));


    }


















    @Test
    public void testExpectationRule() {

        String src = "" +
                "package org.drools;  " +
                "import org.drools.expectations.*;  " +
                "global java.util.List list; " +
                " " +
                "declare Msg  " +
                "   @role(event)  " +
                "   sender : String     @key " +
                "   receiver : String   @key " +
                "   body : String       @key " +
                "   more : String         " +
                "end " +
                " " +
                "rule 'Expect'  " +
                "when " +
                "   $trigger : Msg( 'John', 'Peter', 'Hello' ; )  " +
                "then " +
                "   $exp11 : expect $message : Msg( 'Peter', 'John' , 'Hello Back'  ; this after[0,500ms] $trigger ) onFulfill {} onViolation {}   " +
                "   or         " +
                "   $exp12 : expect $altMesg : Msg( 'Peter', 'John' , 'Back Hello'  ; /* this after[0,50ms] $trigger */ ) onFulfill {} onViolation {}  " +
                "     " +
                "   $exp2  : expect one $moreMsg : Msg( 'Peter', 'John' , 'Hows going' ; /* this after[0,50ms] $trigger */ ) onFulfill {} onViolation {}  " +
                "end ";

        KieSession kSession = buildKnowledgeSession(src.getBytes());
        List<Object> list = new LinkedList<Object>();
        kSession.setGlobal("list",list);


        kSession.insert(newMessage(kSession,"Peter","John","Hows going","A"));      // here due to timer bug
        kSession.fireAllRules();

        sleep(10);

        kSession.insert(newMessage(kSession,"John","Peter","Hello","A"));
        kSession.fireAllRules();

        sleep(20);


        kSession.insert(newMessage(kSession,"Peter","John","Hello Back","A"));
        kSession.fireAllRules();


        sleep(600);
        kSession.fireAllRules();

        System.out.println( reportWMObjects( kSession ) );

        assertEquals( 2, countMeta( Fulfill.class.getName(), kSession ) );
        assertEquals( 1, countMeta( Pending.class.getName(), kSession ) );
        assertEquals( 1, countMeta( Viol.class.getName(), kSession ) );
        assertEquals( 3, countMeta( Expectation.class.getName(), kSession ) );
        assertEquals( 1, countMeta( Success.class.getName(), kSession ) );
        assertEquals( 0, countMeta( Failure.class.getName(), kSession ) );


    }











}
