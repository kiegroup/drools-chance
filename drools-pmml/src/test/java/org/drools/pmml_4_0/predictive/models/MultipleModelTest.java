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


import junit.framework.Assert;
import org.drools.ClassObjectFilter;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.agent.ChangeSetHelperImpl;
import org.drools.agent.KnowledgeAgent;
import org.drools.agent.KnowledgeAgentConfiguration;
import org.drools.agent.KnowledgeAgentFactory;
import org.drools.agent.conf.NewInstanceOption;
import org.drools.agent.conf.UseKnowledgeBaseClassloaderOption;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.definition.type.FactType;
import org.drools.informer.Answer;
import org.drools.io.Resource;
import org.drools.io.ResourceFactory;
import org.drools.io.impl.ChangeSetImpl;
import org.drools.io.impl.ClassPathResource;
import org.drools.pmml_4_0.DroolsAbstractPMMLTest;
import org.drools.pmml_4_0.ModelMarker;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.Variable;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class MultipleModelTest extends DroolsAbstractPMMLTest {


    private static final boolean VERBOSE = true;
    private static final String source1 = "org/drools/pmml_4_0/mock_ptsd.pmml";
    private static final String source2 = "org/drools/pmml_4_0/mock_cold.pmml";
    private static final String source3 = "org/drools/pmml_4_0/mock_breastcancer.pmml";

    private static final String packageName = "org.drools.pmml_4_0.test";


    
    
    @Test
    public void testKnowledgeAgentLoading() throws Exception {

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        KnowledgeAgentConfiguration kaConfig = KnowledgeAgentFactory.newKnowledgeAgentConfiguration();
        kaConfig.setProperty( NewInstanceOption.PROPERTY_NAME, "false" );
        kaConfig.setProperty( UseKnowledgeBaseClassloaderOption.PROPERTY_NAME, "true" );
        KnowledgeAgent kagent = KnowledgeAgentFactory.newKnowledgeAgent( "testPmml", kbase, kaConfig );

        ChangeSetHelperImpl cs;
        ClassPathResource res;

        cs = new ChangeSetHelperImpl();
        res = (ClassPathResource) ResourceFactory.newClassPathResource(source1);
        res.setResourceType( ResourceType.PMML );
        cs.addNewResource( res );
        kagent.applyChangeSet( cs.getChangeSet() );

        System.out.println( " \n\n\n DONE LOADING " + source1 + " \n\n\n " );

        cs = new ChangeSetHelperImpl();
        res = (ClassPathResource) ResourceFactory.newClassPathResource(source2);
        res.setResourceType( ResourceType.PMML );
        cs.addNewResource( res );
        kagent.applyChangeSet( cs.getChangeSet() );

        System.out.println( " \n\n\n DONE LOADING " + source2 + " \n\n\n " );

        cs = new ChangeSetHelperImpl();
        res = (ClassPathResource) ResourceFactory.newClassPathResource(source3);
        res.setResourceType( ResourceType.PMML );
        cs.addNewResource( res );
        kagent.applyChangeSet( cs.getChangeSet() );

        System.out.println( " \n\n\n DONE LOADING " + source3 + " \n\n\n " );

        StatefulKnowledgeSession kSession = kagent.getKnowledgeBase().newStatefulKnowledgeSession();

        kSession.fireAllRules();

        assertNotNull( kSession );
        Collection markers = kSession.getObjects(new ClassObjectFilter(ModelMarker.class));
        assertEquals( 3, markers.size() );

    }


}
