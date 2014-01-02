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

package org.drools.semantics.lang.dl;


import org.drools.core.common.DefaultFactHandle;
import org.drools.core.common.EventFactHandle;
import org.drools.core.util.StringUtils;
import org.drools.semantics.builder.DLReasonerBuilder;
import org.drools.semantics.builder.DLReasonerBuilderImpl;
import org.junit.After;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.io.KieResources;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.kie.internal.KnowledgeBaseFactory;
import org.semanticweb.owlapi.model.OWLOntology;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.PriorityQueue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;


/**
 * This is a sample class to launch a rule.
 */
@Deprecated
public class AbstractReasonerTestBase {


    protected DLReasonerBuilder factory = DLReasonerBuilderImpl.getInstance();
            

    protected KieBase tableauKB;
    protected KieSession ksession;
    protected Object mock;



    private String currentSource = null;
    private OWLOntology currentOntology = null;


    public OWLOntology init( String DLfile ) {
        KieServices kieServices = KieServices.Factory.get();
        KieResources kieResources = kieServices.getResources();
        
        
        // get ontology...
        if ( DLfile.equals( currentSource ) ) {
            reinit();
            return currentOntology;
        }


        OWLOntology ontologyDescr = factory.parseOntology( kieResources.newClassPathResource( DLfile ) );
        currentOntology = ontologyDescr;
        currentSource = DLfile;


        // compile tableau from DL...
        Resource visitor =  kieResources.newClassPathResource( "FALC_TableauBuilderVisitor.drl" )
                .setResourceType( ResourceType.DRL );
        Resource common =  kieResources.newClassPathResource( "FALC_CommonVisitor.drl" )
                .setResourceType( ResourceType.DRL );
        String tableau = factory.buildTableauRules( ontologyDescr,
                                                    new Resource[] { common, visitor } );
        System.err.println("<<<" + tableau + ">>>");
        assertFalse(StringUtils.isEmpty(tableau));

        KieBaseConfiguration kconf = kieServices.newKieBaseConfiguration();
            kconf.setOption( EqualityBehaviorOption.EQUALITY );


        KieFileSystem kfs = kieServices.newKieFileSystem();
        // add main rulebase
        kfs.write( kieResources.newByteArrayResource( tableau.getBytes() ).setSourcePath( "tableau.drl" ).setResourceType( ResourceType.DRL ) );
        kfs.write( kieResources.newClassPathResource( "fuzzyDL/Main.drl" ).setResourceType( ResourceType.DRL ) );
        KieBuilder kieBuilder = kieServices.newKieBuilder( kfs );
        kieBuilder.buildAll();

        if ( kieBuilder.getResults().hasMessages( Message.Level.ERROR ) ) {
            fail( kieBuilder.getResults().getMessages( Message.Level.ERROR ).toString() );
        }

        tableauKB = kieServices.newKieContainer( kieBuilder.getKieModule().getReleaseId() ).newKieBase( kconf );
        assertNotNull(tableauKB);

        reinit();

        return ontologyDescr;

    }



    public void reinit() {
        // get session and provide solver
        ksession = tableauKB.newKieSession();
        // just in case...
        ksession.fireAllRules();


//        System.err.println(reportWMObjects(ksession));
        QueryResults results = ksession.getQueryResults( "q_mock", "m01" );
        if (results.iterator().hasNext()) {
            QueryResultsRow ans = results.iterator().next();
            mock = ans.get("$m");

            assertNotNull(mock);
        }


//        System.err.println(" GO WITH TEST GOAL \n\n\n\n\n\n\n");
//        System.out.println(" GO WITH TEST GOAL \n\n\n\n\n\n\n");

    }


    @After
    public void destroy() {
        // it is a good day...
        ksession.dispose();

    }




    protected void testRecognition(String klass, double tgtTau, double tgtPhi) {
        DLGoal goal = new RecognitionGoal(klass,true);
        ksession.insert(goal);
        ksession.fireAllRules();

//        System.err.println("Objects in WM " + ksession.getObjects().size());
//        for (Object o : ksession.getObjects())
//            System.err.println(o);

        QueryResults results2 = ksession.getQueryResults( "q_recognition", mock, klass );
        Iterator<QueryResultsRow> iter2 = results2.iterator();
        Number tau = 0.0;
        Number phi = 1.0;
        while (iter2.hasNext()) {
            QueryResultsRow ans2 = iter2.next();
            tau = Math.max(tau.doubleValue(),((Number) ans2.get("$tau")).doubleValue());
            phi = Math.min(phi.doubleValue(),((Number) ans2.get("$phi")).doubleValue());
            System.err.println(mock + " isA " + klass+ " >> [" + tau + " : " + phi + "]");
        }

        assertEquals("delta_tau",tgtTau,tau.doubleValue(),MinimizationProblem.precision);
        assertEquals("delta_phi",tgtPhi,phi.doubleValue(),MinimizationProblem.precision);
    }



    protected void testSubsumption(String sub, String sup, double tgtTau, double tgtPhi) {
        DLGoal goal = new SubsumptionGoal(sub,sup,true);
        ksession.insert(goal);
        ksession.fireAllRules();

        QueryResults results2 = ksession.getQueryResults( "q_subsumptionAll", sub, sup );
        Iterator<QueryResultsRow> iter2 = results2.iterator();
        Number tau = 0.0;
        Number phi = 1.0;
        while (iter2.hasNext()) {
            QueryResultsRow ans2 = iter2.next();
            tau = Math.max(tau.doubleValue(),((Number) ans2.get("$tau")).doubleValue());
            phi = Math.min(phi.doubleValue(),((Number) ans2.get("$phi")).doubleValue());
            System.err.println(sub + " subConceptOf" + sup + " >> [" + tau + " : " + phi + "]");
        }
//        System.out.println("Objects in WM " + ksession.getObjects().size());
//        for (Object o : ksession.getObjects())
//            System.out.println(o);

        assertEquals( "delta_tau",tgtTau,tau.doubleValue(), MinimizationProblem.precision );
        assertEquals( "delta_phi",tgtPhi,phi.doubleValue(), MinimizationProblem.precision );

    }





    protected static boolean logFile(String fileName, String content) {
        try {
//            System.out.println(new File(".").getAbsolutePath());
            FileOutputStream fw = new FileOutputStream(fileName);
            byte[] drl = content.getBytes();
            fw.write(drl,0,drl.length);
            fw.flush();
            fw.close();
            return true;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return false;
        }
    }




    public String reportWMObjects(KieSession session) {
        PriorityQueue<String> queue = new PriorityQueue<String>();
        for (FactHandle fh : session.getFactHandles()) {
            Object o;
            if (fh instanceof EventFactHandle ) {
                EventFactHandle efh = (EventFactHandle) fh;
                queue.add("\t " + efh.getStartTimestamp() + "\t" + efh.getObject().toString() + "\n");
            } else {
                o = ((DefaultFactHandle) fh).getObject();
                queue.add("\t " + o.toString() + "\n");
            }

        }
        String ans = " ---------------- WM " + session.getObjects().size() + " --------------\n";
        while (! queue.isEmpty())
            ans += queue.poll();
        ans += " ---------------- END WM -----------\n";
        return ans;
    }




}