/*
 * Copyright 2013 JBoss Inc
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

import org.drools.io.ResourceFactory;
import org.drools.semantics.builder.DLFactory;
import org.drools.semantics.builder.DLFactoryBuilder;
import org.drools.semantics.builder.model.OntoModel;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class DL_3_PropertyTest {

    protected DLFactory factory = DLFactoryBuilder.newDLFactoryInstance();


    @Test
    public void testPropertyInheritance() {
        OntoModel results = factory.buildModel( "diamond",
                ResourceFactory.newClassPathResource("ontologies/propInherit.manchester.owl"),
                OntoModel.Mode.OPTIMIZED );

        assertEquals( 3, results.getProperties().size() );
        assertEquals( 1, results.getConcept( "<http://jboss.org/drools/semantics/Diamond2#Top>" ).getProperties().size() );
        assertEquals( 1, results.getConcept( "<http://jboss.org/drools/semantics/Diamond2#Left>" ).getProperties().size() );
        assertEquals( 1, results.getConcept( "<http://jboss.org/drools/semantics/Diamond2#Bottom>" ).getProperties().size() );

        assertTrue( results.isHierarchyConsistent() );

    }
}
