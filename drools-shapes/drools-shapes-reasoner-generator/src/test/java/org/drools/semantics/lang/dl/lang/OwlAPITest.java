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

package org.drools.semantics.lang.dl.lang;

import org.drools.io.impl.ClassPathResource;
import org.junit.*;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class OwlAPITest {

    private static Logger log;

    private boolean visual = false;
    public boolean isVisual() {	return visual; }
    public void setVisual(boolean visual) {	this.visual = visual; }



    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        log = Logger.getAnonymousLogger();
        log.setLevel(Level.INFO);
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {

    }

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }



    @Test
    public void kmr2_ontology() {
        test_ontology("kmr2/kmr2_mini.owl");
    }

    @Test
    public void test_test() {
        test_ontology("DLex01.manchester");
        test_ontology("DLex02.manchester");
        test_ontology("DLex03.manchester");
    }


    @Test
    public void test_ontolog5() {
        test_ontology("DLex5.manchester");
    }

    @Test
    public void test_ontology6() {
        test_ontology("DLex6.manchester");
    }


    public void test_ontology(String ontoName) {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

        try {
            ClassPathResource res = new ClassPathResource( ontoName );
            OWLOntology onto = manager.loadOntologyFromOntologyDocument( res.getInputStream() );
            assertNotNull( onto );
            IRI i;

            for (OWLAxiom ax : onto.getAxioms(AxiomType.DECLARATION)) {
                OWLDeclarationAxiom dx = (OWLDeclarationAxiom) ax;
                OWLEntity ent = dx.getEntity();

                if (ent instanceof OWLClass) {
                    Set<OWLClassExpression> ex = ((OWLClass) ent).getEquivalentClasses(onto);
                    for (OWLClassExpression x : ex) {
                        System.out.println("***********");
                        System.out.println(x);
                        System.out.println(x.isAnonymous());
                        System.out.println(x.isClassExpressionLiteral());
                        System.out.println(x instanceof OWLNaryBooleanClassExpression);

                        System.out.println("*********** \n\n");
                    }
                }
            }
            System.out.println( onto );
        } catch (OWLOntologyCreationException e) {
            fail( e.getMessage() );
        } catch (FileNotFoundException e) {
            fail( e.getMessage() );
        } catch (IOException e) {
            fail( e.getMessage() );
        }

    }






    private void check(String rule, String[] drl) {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        try {
            manager.loadOntologyFromOntologyDocument(new ByteArrayInputStream(drl[0].getBytes()));
        } catch (OWLOntologyCreationException e) {
            fail( e.getMessage() );
        }


    }







}
