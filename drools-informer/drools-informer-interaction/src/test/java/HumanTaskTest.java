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

import org.drools.ClassObjectFilter;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.definition.type.FactType;
import org.drools.informer.Answer;
import org.drools.informer.generator.Surveyable;
import org.drools.io.impl.ClassPathResource;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


public class HumanTaskTest {


    StatefulKnowledgeSession kSession;
    String drl1 = "org/drools/informer/interaction/interaction.drl";
    String drl2 = "org/drools/informer/interaction/humanTask.drl";

    String drl3 = "org/drools/informer/interaction/humanTaskTest.drl";


    @Before
    public void setupSession() {
        KnowledgeBuilder kBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kBuilder.add( new ClassPathResource( "org/drools/informer/informer-changeset.xml" ), ResourceType.CHANGE_SET );

        kBuilder.add( new ClassPathResource( drl1 ), ResourceType.DRL );
        kBuilder.add( new ClassPathResource( drl2 ), ResourceType.DRL );
        kBuilder.add( new ClassPathResource( drl3 ), ResourceType.DRL );

        if ( kBuilder.hasErrors() ) {
            fail( kBuilder.getErrors().toString() );
        }
        KnowledgeBase kBase = KnowledgeBaseFactory.newKnowledgeBase();
        kBase.addKnowledgePackages( kBuilder.getKnowledgePackages() );
        kSession = kBase.newStatefulKnowledgeSession();
        kSession.fireAllRules();
    }

    @Test
    public void testQuestionnaire() {

        kSession.insert( "Activate" );
        kSession.fireAllRules();


        FactType taskClass = kSession.getKnowledgeBase().getFactType("org.drools.informer.interaction", "InteractiveTask");
        Object iTask = kSession.getObjects( new ClassObjectFilter( taskClass.getFactClass() ) ).iterator().next();
        String tsId = (String) taskClass.get( iTask, "surveyableTxFId" );

//        System.out.println( iTask );
//        System.out.println( tsId );

        kSession.insert( new Answer( "transition", tsId, "Start" ) );
        kSession.fireAllRules();


        kSession.insert( new Answer( "transition", tsId, "Complete" ) );
        kSession.fireAllRules();

        kSession.retract( kSession.getFactHandle( iTask ) );
        kSession.fireAllRules();

        for ( Object o : kSession.getObjects() ) {
            System.err.println( "**" +  o );
        }

    }

}
