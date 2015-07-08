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

package org.drools.expectations;

import static org.kie.api.runtime.EnvironmentName.GLOBALS;
import static org.kie.api.runtime.EnvironmentName.CALENDARS;
import it.unibo.deis.lia.org.drools.expectations.ECEHelper;
import it.unibo.deis.lia.org.drools.expectations.model.Expectation;
import it.unibo.deis.lia.org.drools.expectations.model.ExpectationContext;
import org.drools.compiler.builder.impl.ECE;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import it.unibo.deis.lia.org.drools.expectations.DRLExpectationHelper;
import org.drools.core.ClockType;
import org.drools.core.WorkingMemory;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.common.DroolsObjectInputStream;
import org.drools.core.common.DroolsObjectOutputStream;
import org.drools.core.common.EventFactHandle;
import org.drools.core.marshalling.impl.ClassObjectMarshallingStrategyAcceptor;
import org.drools.core.marshalling.impl.IdentityPlaceholderResolverStrategy;
import org.drools.core.spi.Activation;
import org.drools.core.spi.ConsequenceExceptionHandler;
import org.drools.core.time.SessionPseudoClock;
import org.drools.core.util.DroolsStreamUtils;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.type.FactType;
import org.kie.api.marshalling.KieMarshallers;
import org.kie.api.marshalling.Marshaller;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.marshalling.ObjectMarshallingStrategyAcceptor;
import org.kie.api.runtime.*;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.time.SessionClock;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.conf.RuleEngineOption;
import org.kie.internal.definition.KnowledgePackage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.PriorityQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ExpTestBase {

    protected SessionClock clock;
    private KieSessionConfiguration sessionConfig;
    private Environment sessionEnvironment;
    private KieBase kieBase;

    public class TestConsequenceHandler implements ConsequenceExceptionHandler {

        @Override
        public void handleException(Activation activation, WorkingMemory workingMemory, Exception exception) {
            System.err.println("Consequence error in activation: "+activation.getRule());
            if (activation.getSubRule() != null) {
                System.err.println("  sub-rule name: "+activation.getSubRule().getChildren());
            }
            System.err.println("Consequence: "+activation.getConsequence().getName());
            exception.printStackTrace();
        }
    }

    public KieSession buildKnowledgeSession( byte[] source ) {
        KieSession kieSession = new ECEHelper().addECEContent(new String(source)).newECESession(false);
        kieBase = kieSession.getKieBase();
        sessionConfig = kieSession.getSessionConfiguration();
        sessionEnvironment = kieSession.getEnvironment();
        clock = kieSession.getSessionClock();
        assertEquals( 0, kieSession.getObjects().size() );
        return kieSession;
    }

    private Marshaller createMarshaller(KieBase kBase) {
        KieMarshallers marshallers = KieServices.Factory.get().getMarshallers();
        ObjectMarshallingStrategyAcceptor acceptor = marshallers.newClassFilterAcceptor(new String[]{"*.*"});
        ObjectMarshallingStrategy strategy = marshallers.newSerializeMarshallingStrategy(acceptor);
        return marshallers.newMarshaller(kBase,new ObjectMarshallingStrategy[] {strategy});
    }

    public ByteArrayOutputStream marshallSession(KieSession session) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        KieBase kBase = session.getKieBase();
        try {
            DroolsObjectOutputStream stream = new DroolsObjectOutputStream(baos);
            stream.writeObject(kBase);
            stream.writeObject(session.getGlobals());
            stream.writeObject(session.getCalendars());
            createMarshaller(kBase).marshall(stream, session);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return baos;
    }

    public KieSession unmarshallSession(ByteArrayInputStream bais) {
        KieSession kSession = null;
        try {
            DroolsObjectInputStream stream = new DroolsObjectInputStream(bais);
            KieBase base = (KieBase)stream.readObject();
            Globals globals = (Globals)stream.readObject();
            Calendars calendars = (Calendars)stream.readObject();
            sessionEnvironment.set(GLOBALS, globals);
            sessionEnvironment.set(CALENDARS, calendars);
            kSession = createMarshaller(base).unmarshall(stream, sessionConfig, sessionEnvironment);
            clock = kSession.getSessionClock();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return kSession;
    }


    public static String reportWMObjects( KieSession session ) {
        PriorityQueue<String> queue = new PriorityQueue<String>();
        DecimalFormat formatter = new DecimalFormat( "00000000" );
        for (FactHandle fh : session.getFactHandles()) {
            Object o;
            if (fh instanceof EventFactHandle ) {
                EventFactHandle efh = (EventFactHandle) fh;
                String ts = formatter.format( efh.getStartTimestamp() ).toString();
                queue.add( "\t " + ts + "\t" + efh.getId() + " \t >> \t" + efh.getObject().toString() + "\n" );
            } else {
                o = ((DefaultFactHandle) fh).getObject();
                queue.add("\t " + o.toString() + "\n");
            }

        }
        String ans = " ================ WM " + session.getObjects().size() + " ==============\n";
        ans += "Session Clock Current Time: "+session.getSessionClock().getCurrentTime()+"\n";
        while (! queue.isEmpty())
            ans += queue.poll();
        ans += " ================ END WM ===========\n";
        return ans;
    }

    protected void sleep( int time ) {
        ((SessionPseudoClock) clock ).advanceTime( time, TimeUnit.MILLISECONDS );
    }

    protected Object newTextMessage( KieSession kSession, String sendingPhone, String receivingPhone, String message, String emoticon ) {
        try {
            FactType msgType = kSession.getKieBase().getFactType( "org.drools", "TextMsg" );
            Object o = null;

            o = msgType.newInstance();
            msgType.set( o, "sendingPhone", sendingPhone );
            msgType.set( o, "receivingPhone", receivingPhone );
            msgType.set( o, "message", message);
            msgType.set( o, "emoticon", emoticon);
            return o;
        } catch ( InstantiationException e ) {
            fail( e.getMessage() );
        } catch ( IllegalAccessException e ) {
            fail( e.getMessage() );
        }
        return null;
    }

    protected Object newMessage( KieSession kSession, String sender, String receiver, String body, String more ) {
        try {
            FactType msgType = kSession.getKieBase().getFactType( "org.drools", "Msg" );
            Object o = null;

            o = msgType.newInstance();
            msgType.set( o, "sender", sender );
            msgType.set( o, "receiver", receiver );
            msgType.set( o, "body", body );
            msgType.set( o, "more", more );
            return o;
        } catch ( InstantiationException e ) {
            fail( e.getMessage() );
        } catch ( IllegalAccessException e ) {
            fail( e.getMessage() );
        }
        return null;
    }

    protected Object newInterrupt( KieSession kSession, String reason ) {
        try {
            FactType intType = kSession.getKieBase().getFactType("org.drools","Interrupt");
            Object o = null;
            o = intType.newInstance();
            intType.set(o, "reason", reason);
            return o;
        } catch (InstantiationException e) {
            fail(e.getMessage());
        } catch (IllegalAccessException e) {
            fail(e.getMessage());
        }
        return null;
    }


    protected Object newReading(KieSession kSession, String probe, Double value ) {
        try {
            FactType msgType = kSession.getKieBase().getFactType( "org.drools", "Reading" );
            Object o = null;

            o = msgType.newInstance();
            msgType.set( o, "probeId", probe );
            msgType.set( o, "value", value );
            return o;
        } catch ( InstantiationException e ) {
            fail( e.getMessage() );
        } catch ( IllegalAccessException e ) {
            fail( e.getMessage() );
        }
        return null;
    }

    protected int countMeta( String type, KieSession ksession ) {
        Class klass = null;
        try {
            klass = Class.forName( type );
        } catch ( ClassNotFoundException e ) {
            klass = ksession.getKieBase().getFactType( DRLExpectationHelper.EXP_PACKAGE, type ).getFactClass();
        }
        return ksession.getObjects( new ClassObjectFilter( klass ) ).size();
    }

    protected void checkAllExpectationsInactive( KieSession kieSession ) {
        for ( Object obj : kieSession.getObjects( new org.drools.core.ClassObjectFilter( Expectation.class ) ) ) {
            Expectation exp = (Expectation) obj;
            assertTrue( ! exp.isActive() );
        }

    }


}
