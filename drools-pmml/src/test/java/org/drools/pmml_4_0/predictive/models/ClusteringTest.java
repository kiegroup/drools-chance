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

package org.drools.pmml_4_0.predictive.models;


import org.drools.definition.type.FactType;
import org.drools.pmml_4_0.DroolsAbstractPMMLTest;
import org.drools.runtime.ClassObjectFilter;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ClusteringTest extends DroolsAbstractPMMLTest {


    private static final boolean VERBOSE = true;
    private static final String source1 = "org/drools/pmml_4_0/test_clustering.xml";
    private static final String packageName = "org.drools.pmml_4_0.test";



    @Test
    public void testCenterBasedClustering() throws Exception {
        setKSession( getModelSession( source1, VERBOSE ) );
        setKbase( getKSession().getKnowledgeBase() );
        StatefulKnowledgeSession kSession = getKSession();

        kSession.fireAllRules();  //init model

        kSession.getWorkingMemoryEntryPoint( "in_Fld0" ).insert( "y" );
        kSession.getWorkingMemoryEntryPoint( "in_Fld1" ).insert( 2.0 );
        kSession.getWorkingMemoryEntryPoint( "in_Fld2" ).insert( -1.0 );

        kSession.fireAllRules();
        
        System.err.println( reportWMObjects( kSession ) );

        FactType mu = kSession.getKnowledgeBase().getFactType( packageName, "DistanceMembership" );
        Collection mus = kSession.getObjects( new ClassObjectFilter( mu.getFactClass()) );
        for ( Object x : mus ) {
            Integer ix = (Integer) mu.get( x, "index" );
            String lab = (String) mu.get( x, "label" );
            Double m = (Double) mu.get( x, "mu" );

            if ( ix == 0 ) {
                assertEquals( "Klust1", lab );
                assertEquals( 41.1, m, 0.001 );
            } else if ( ix == 1 ) {
                assertEquals( "Klust2", lab );
                assertEquals( 14704.428, m, 0.001 );
            }
        }
    }                         




}
