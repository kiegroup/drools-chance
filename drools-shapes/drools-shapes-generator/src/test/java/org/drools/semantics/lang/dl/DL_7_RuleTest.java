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

import org.drools.compiler.lang.DrlDumper;
import org.drools.compiler.lang.api.DescrFactory;
import org.drools.compiler.lang.api.PackageDescrBuilder;
import org.drools.core.io.impl.ByteArrayResource;
import org.drools.semantics.NamedIndividual;
import org.drools.semantics.builder.DLFactory;
import org.drools.semantics.builder.DLFactoryBuilder;
import org.drools.semantics.builder.model.OntoModel;
import org.drools.semantics.builder.reasoner.DLogicTransformer;
import org.drools.semantics.builder.reasoner.TemplateRecognitionRuleBuilder;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.ClassObjectFilter;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


@SuppressWarnings("restriction")
public class DL_7_RuleTest {


    protected DLFactory factory = DLFactoryBuilder.newDLFactoryInstance();

    @Test
    public void testPizzaOntologyRecognition() {
        Resource res = KieServices.Factory.get().getResources().newClassPathResource( "ontologies/pizza.owl" );

        OWLOntology pizza = factory.parseOntology( res );

        OntoModel pizzaModel = factory.buildModel( "pizza",
                res,
                OntoModel.Mode.FLAT,
                DLFactory.defaultAxiomGenerators );

        String drl = new TemplateRecognitionRuleBuilder().createDRL( pizza, pizzaModel );

        System.err.println( "***********************************************************************" );
        System.err.println( drl );
        System.err.println( "***********************************************************************" );
    }



    @Test
    public void testExampleDNFRecognition() {
        Resource res = KieServices.Factory.get().getResources().newClassPathResource( "ontologies/testDNF.owl" );

        OWLOntology onto = factory.parseOntology( res );

        OntoModel ontoModel = factory.buildModel( "test",
                res,
                OntoModel.Mode.NONE,
                DLFactory.defaultAxiomGenerators );

        String drl = new TemplateRecognitionRuleBuilder().createDRL( onto, ontoModel );

        String drl2 = "package t.x \n" +
                "import org.drools.semantics.NamedIndividual;\n" +
                "" +
                "rule Init when \n" +
                "then \n" +
                "   NamedIndividual e = new NamedIndividual();\n" +
                "   insert( e ); \n" +
                "   don( e, W.class ); \n" +
                "end \n" +
                "" +
                "" +
                "rule Final when \n" +
                "  $s : String() \n" +
                "  $x : W() \n" +
                "then \n" +
                "   retract( $s );\n " +
                "   shed( $x, W.class ); \n " +
                "end \n";

        KieSession kSession = createSession( drl, drl2 );
        kSession.fireAllRules();

        for ( Object o : kSession.getObjects() ) {
            System.err.println( o );
        }

        System.err.println( "----------" );
        kSession.insert( "go" );
        kSession.fireAllRules();

        for ( Object o : kSession.getObjects() ) {
            System.err.println( o );
        }

    }

    private KieBase createKieBase( String... drls ) {
        KieServices kieServices = KieServices.Factory.get();
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();

        int j = 0;
        for ( String drl : drls ) {
            kieFileSystem.write(
                    kieServices.getResources().newByteArrayResource( drl.getBytes() )
                            .setSourcePath( "test_" + ( j++ ) + ".drl" )
                            .setResourceType( ResourceType.DRL ) );
        }
        KieBuilder kieBuilder = kieServices.newKieBuilder( kieFileSystem );
        kieBuilder.buildAll();

        if ( kieBuilder.getResults().hasMessages( Message.Level.ERROR ) ) {
            fail( kieBuilder.getResults().getMessages( Message.Level.ERROR ).toString() );
        }

        KieBaseConfiguration rbC = kieServices.newKieBaseConfiguration();
        rbC.setOption( EqualityBehaviorOption.EQUALITY );
        KieBase knowledgeBase = kieServices.newKieContainer( kieBuilder.getKieModule().getReleaseId() ).newKieBase( rbC );
        return knowledgeBase;
    }

    private KieSession createSession( String... drls ) {
        return createKieBase( drls ).newKieSession();
    }


    @Test
    public void testOneOfWithNamedIndividuals() {
        String owl = "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)\n" +
                     "Prefix(rdf:=<http://www.w3.org/1999/02/22-rdf-syntax-ns#>)\n" +
                     "Prefix(xml:=<http://www.w3.org/XML/1998/namespace>)\n" +
                     "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n" +
                     "Prefix(rdfs:=<http://www.w3.org/2000/01/rdf-schema#>)\n" +
                     "\n" +
                     "\n" +
                     "Ontology(<http://t/x>\n" +
                     "\n" +
                     "Declaration(Class(<http://t/x#X>))\n" +
                     "Declaration(Class(<http://t/x#Y>))\n" +
                     "EquivalentClasses(<http://t/x#Y> ObjectIntersectionOf(ObjectOneOf(<http://t/x#b> <http://t/x#a>) <http://t/x#X>))\n" +
                     "Declaration(NamedIndividual(<http://t/x#a>))\n" +
                     "Declaration(NamedIndividual(<http://t/x#b>))\n" +
                     ")";

        String drl2 = "package t.x \n" +
                      "import org.drools.semantics.NamedIndividual;\n" +
                      "" +
                      "rule Init when \n" +
                      "then \n" +
                      "   NamedIndividual e = new NamedIndividual( \"http://t/x#a\" );\n" +
                      "   insert( e ); \n" +
                      "   don( e, X.class, true ); \n" +
                      "end \n" +
                      "";

        Resource res = KieServices.Factory.get().getResources().newByteArrayResource( owl.getBytes() );
        OWLOntology onto = factory.parseOntology( res );
        OntoModel ontoModel = factory.buildModel( "test",
                res,
                OntoModel.Mode.NONE,
                DLFactory.defaultAxiomGenerators );

        String drl = new TemplateRecognitionRuleBuilder().createDRL( onto, ontoModel );

        KieSession kSession = createSession( drl, drl2 );

        for ( Object o : kSession.getObjects() ) {
            System.err.println( o );
        }

        for ( Object o : kSession.getObjects( new ClassObjectFilter( NamedIndividual.class ) ) ) {
            NamedIndividual e = (NamedIndividual) o;
            assertTrue( ( (NamedIndividual) o ).hasTrait( "t.x.Y" ) );
        }


    }




    @Test
    public void testDRLDumper() {
        PackageDescrBuilder packBuilder = DescrFactory.newPackage().name( "org.test" )
                .newRule().name( "org.test" )
                .lhs().and().or()
                        .and().pattern().type( "Integer" ).end().pattern().type( "Long" ).end().end()
                        .and().pattern().type( "String" ).end().end()
                .end().end().end()
                .rhs("")
                .end()
        .end();
        String drl = new DrlDumper().dump( packBuilder.getDescr() );
        System.out.println(drl);

        KnowledgeBuilder kb = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kb.add( new ByteArrayResource( drl.getBytes() ), ResourceType.DRL );
        System.out.println( kb.getErrors() );
        assertFalse( kb.hasErrors() );
    }




    @Test
    public void testExampleDNFQuantifiersSome() {

        String owl = "" +
                "<?xml version=\"1.0\"?>\n" +
                "<rdf:RDF xmlns=\"http://t/x#\"\n" +
                "     xml:base=\"http://t/x\"\n" +
                "     xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"\n" +
                "     xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n" +
                "     xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"\n" +
                "     xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n" +
                "    <owl:Ontology rdf:about=\"http://t/x\"/>\n" +
                "    <owl:ObjectProperty rdf:about=\"http://t/x#objProp\"/>\n" +
                "    <owl:Class rdf:about=\"http://t/x#D\"/>\n" +
                "    <owl:Class rdf:about=\"http://t/x#E\">\n" +
                "        <owl:equivalentClass>\n" +
                "            <owl:Class>\n" +
                "                <owl:intersectionOf rdf:parseType=\"Collection\">\n" +
                "                    <rdf:Description rdf:about=\"http://t/x#D\"/>\n" +
                "                    <owl:Restriction>\n" +
                "                        <owl:onProperty rdf:resource=\"http://t/x#objProp\"/>\n" +
                "                        <owl:someValuesFrom rdf:resource=\"http://t/x#F\"/>\n" +
                "                    </owl:Restriction>\n" +
                "                </owl:intersectionOf>\n" +
                "            </owl:Class>\n" +
                "        </owl:equivalentClass>\n" +
                "    </owl:Class>\n" +
                "    <owl:Class rdf:about=\"http://t/x#F\"/>\n" +
                "</rdf:RDF>\n" +
                "\n";

        String drl2 = "package t.x; \n" +
                "import org.drools.semantics.NamedIndividual; \n" +
                "" +
                "declare NamedIndividual end\n" +
                "" +
                "rule Init \n" +
                "when \n" +
                "then \n" +
                "   NamedIndividual e1 = new NamedIndividual( \"X\" ); \n" +
                "   insert( e1 );\n" +
                "   NamedIndividual e2 = new NamedIndividual( \"Y\"); \n" +
                "   insert( e2 );\n" +
                " " +
                "   D d1 = don( e1, D.class, true ); \n" +
                "   F f2 = don( e2, F.class, true ); \n" +
                " " +
                "   modify ( d1 ) { \n" +
                "      getObjProp().add( f2.getCore() );" +
                "   } \n" +
                "   modify ( f2.getCore() ) {} \n " +
                "end \n" +
                "" +
                "" +
                "rule Shed \n" +
                "when \n" +
                "   $s : String( this == \"go\") \n" +
                "   $x : E( $objs : objProp ) \n" +
                "   $y : F( $z : core memberOf $objs ) \n" +
                "then \n" +
                "   retract( $s ); \n" +
                "   System.out.println( \"SUCCESS : E has been recognized \" );\n" +
                "   shed( $y, F.class );" +
                "end \n" +
                "" +
                "" +
                "rule Shed_2 \n" +
                "when \n" +
                "   $s : String( this == \"go2\") \n" +
                "   $x : E( $objs : objProp ) \n" +
                "   $y : F( $z : core memberOf $objs ) \n" +
                "then \n" +
                "   retract( $s ); \n" +
                "   System.out.println( \"SUCCESS : E has been recognized \" );\n" +
                "   modify ( $x ) {\n" +
                "       getObjProp().remove( $z ); \n" +
                "   }\n" +
                "   modify ( $y ) {} " +
                "end \n";


        Resource res = KieServices.Factory.get().getResources().newByteArrayResource( owl.getBytes() );

        OWLOntology onto = factory.parseOntology( res );

        OntoModel ontoModel = factory.buildModel( "test",
                res,
                OntoModel.Mode.NONE,
                DLFactory.defaultAxiomGenerators );

        String drl = new TemplateRecognitionRuleBuilder().createDRL( onto, ontoModel );

        KieBase kieBase = createKieBase( drl, drl2 );
        KieSession kSession = kieBase.newKieSession();

        for ( Object o : kSession.getObjects( new ClassObjectFilter( NamedIndividual.class ) ) ) {
            NamedIndividual e = (NamedIndividual) o;
            if ( e.getId().equals( "X" ) ) {
                assertTrue( e.hasTrait( "t.x.D" ) );
                assertTrue( e.hasTrait( "t.x.E" ) );
                assertFalse( e.hasTrait( "t.x.F" ) );
                assertEquals( 1, ( (List) e._getDynamicProperties().get( "objProp" ) ).size() );
            } else if ( e.getId().equals(  "Y" ) ) {
                assertTrue( e.hasTrait( "t.x.F" ) );
                assertFalse( e.hasTrait( "t.x.D" ) );
                assertFalse( e.hasTrait( "t.x.E" ) );
            } else {
                fail( "Unrecognized entity in WM" );
            }
        }


        kSession.insert( "go" );
        kSession.fireAllRules();

        for ( Object o : kSession.getObjects( new ClassObjectFilter( NamedIndividual.class ) ) ) {
            NamedIndividual e = (NamedIndividual) o;
            if ( e.getId().equals( "X" ) ) {
                assertTrue( e.hasTrait( "t.x.D" ) );
                assertFalse( e.hasTrait( "t.x.E" ) );
                assertFalse( e.hasTrait( "t.x.F" ) );
                assertEquals( 1, ( (List) e._getDynamicProperties().get( "objProp" ) ).size() );
            } else if ( e.getId().equals(  "Y" ) ) {
                assertFalse( e.hasTrait( "t.x.F" ) );
                assertFalse( e.hasTrait( "t.x.D" ) );
                assertFalse( e.hasTrait( "t.x.E" ) );
            } else {
                fail( "Unrecognized entity in WM" );
            }

        }


        kSession = kieBase.newKieSession();
        kSession.fireAllRules();

        kSession.insert( "go2" );
        kSession.fireAllRules();

        for ( Object o : kSession.getObjects( new ClassObjectFilter( NamedIndividual.class ) ) ) {
            NamedIndividual e = (NamedIndividual) o;
            if ( e.getId().equals( "X" ) ) {
                assertTrue( e.hasTrait( "t.x.D" ) );
                assertFalse( e.hasTrait( "t.x.E" ) );
                assertFalse( e.hasTrait( "t.x.F" ) );
                assertEquals( 0, ( (List) e._getDynamicProperties().get( "objProp" ) ).size() );
            } else if ( e.getId().equals(  "Y" ) ) {
                assertTrue( e.hasTrait( "t.x.F" ) );
                assertFalse( e.hasTrait( "t.x.D" ) );
                assertFalse( e.hasTrait( "t.x.E" ) );
            } else {
                fail( "Unrecognized entity in WM" );
            }

        }



    }



    @Test
    public void testExaTemplateRecognitionBuildermpleDNFQuantifiersOnly() {

        String owl = "" +
                     "<?xml version=\"1.0\"?>\n" +
                     "<rdf:RDF xmlns=\"http://t/x#\"\n" +
                     "     xml:base=\"http://t/x\"\n" +
                     "     xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"\n" +
                     "     xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n" +
                     "     xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"\n" +
                     "     xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n" +
                     "    <owl:Ontology rdf:about=\"http://t/x\"/>\n" +
                     "    <owl:ObjectProperty rdf:about=\"http://t/x#objProp\"/>\n" +
                     "    <owl:Class rdf:about=\"http://t/x#D\"/>\n" +
                     "    <owl:Class rdf:about=\"http://t/x#E\">\n" +
                     "        <owl:equivalentClass>\n" +
                     "            <owl:Class>\n" +
                     "                <owl:intersectionOf rdf:parseType=\"Collection\">\n" +
                     "                    <rdf:Description rdf:about=\"http://t/x#D\"/>\n" +
                     "                    <owl:Restriction>\n" +
                     "                        <owl:onProperty rdf:resource=\"http://t/x#objProp\"/>\n" +
                     "                        <owl:allValuesFrom rdf:resource=\"http://t/x#F\"/>\n" +
                     "                    </owl:Restriction>\n" +
                     "                </owl:intersectionOf>\n" +
                     "            </owl:Class>\n" +
                     "        </owl:equivalentClass>\n" +
                     "    </owl:Class>\n" +
                     "    <owl:Class rdf:about=\"http://t/x#F\"/>\n" +
                     "</rdf:RDF>\n" +
                     "\n";

        String drl2 = "package t.x; \n" +
                      "import org.drools.semantics.NamedIndividual; \n" +
                      "import org.w3._2002._07.owl.Thing; \n" +
                      "" +
                      "declare NamedIndividual end\n" +
                      "" +
                      "rule Init \n" +
                      "when \n" +
                      "then \n" +
                      "   NamedIndividual e1 = new NamedIndividual( \"X\" ); \n" +
                      "   insert( e1 );\n" +
                      "   NamedIndividual e2 = new NamedIndividual( \"Y\" ); \n" +
                      "   insert( e2 );\n" +
                      "   NamedIndividual e3 = new NamedIndividual( \"Z\" ); \n" +
                      "   insert( e3 );\n" +
                      " " +
                      "   D d1 = don( e1, D.class, true ); \n" +
                      "   F f2 = don( e2, F.class, true ); \n" +
                      "   F f3 = don( e3, F.class, true ); \n" +
                      " " +
                      "   modify ( d1 ) { \n" +
                      "      getObjProp().add( f2.getCore() )," +
                      "      getObjProp().add( f3.getCore() );" +
                      "   } \n" +
                      "   modify ( f3.getCore() ) {} \n " +
                      "   modify ( f2.getCore() ) {} \n " +
                      "end \n" +
                      "" +
                      "rule Log \n" +
                      "when \n" +
                      "   $x : E() \n" +
                      "then \n" +
                      "   System.out.println( \"RECOGNIZED \" + $x ); \n" +
                      "end" ;

        Resource res = KieServices.Factory.get().getResources().newByteArrayResource( owl.getBytes() );

        OWLOntology onto = factory.parseOntology( res );

        OntoModel ontoModel = factory.buildModel( "test",
                res,
                OntoModel.Mode.NONE,
                DLFactory.defaultAxiomGenerators );

        String drl = new TemplateRecognitionRuleBuilder().createDRL( onto, ontoModel );

        KieSession kSession = createSession( drl, drl2 );
        kSession.fireAllRules();

        for ( Object o : kSession.getObjects( new ClassObjectFilter( NamedIndividual.class ) ) ) {
            NamedIndividual e = (NamedIndividual) o;
            if ( e.getId().equals( "X" ) ) {
                assertTrue( e.hasTrait( "t.x.D" ) );
                assertTrue( e.hasTrait( "t.x.E" ) );
                assertFalse( e.hasTrait( "t.x.F" ) );
                assertEquals( 2, ( (List) e._getDynamicProperties().get( "objProp" ) ).size() );
            } else if ( e.getId().equals(  "Y" ) || e.getId().equals( "Z" ) ) {
                assertTrue( e.hasTrait( "t.x.F" ) );
                assertFalse( e.hasTrait( "t.x.D" ) );
                assertFalse( e.hasTrait( "t.x.E" ) );
            } else {
                fail( "Unrecognized entity in WM" );
            }

        }


    }






    @Test
    public void testExampleDNFDataProperties() {

        String owl = "<?xml version=\"1.0\"?>\n" +
                     "<!DOCTYPE rdf:RDF [\n" +
                     "    <!ENTITY owl \"http://www.w3.org/2002/07/owl#\" >\n" +
                     "    <!ENTITY xsd \"http://www.w3.org/2001/XMLSchema#\" >\n" +
                     "    <!ENTITY rdfs \"http://www.w3.org/2000/01/rdf-schema#\" >\n" +
                     "    <!ENTITY rdf \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" >\n" +
                     "]>\n" +
                     "<rdf:RDF xmlns=\"http://t/x#\"\n" +
                     "     xml:base=\"http://t/x\"\n" +
                     "     xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"\n" +
                     "     xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n" +
                     "     xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"\n" +
                     "     xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n" +
                     "    <owl:Ontology rdf:about=\"http://t/x\"/>\n" +
                     "    <owl:DatatypeProperty rdf:about=\"http://t/x#dataProp\">\n" +
                     "        <rdfs:range rdf:resource=\"&xsd;string\"/>\n" +
                     "    </owl:DatatypeProperty>\n" +
                     "    <owl:Class rdf:about=\"http://t/x#E\">\n" +
                     "        <owl:equivalentClass>\n" +
                     "            <owl:Class>\n" +
                     "                <owl:intersectionOf rdf:parseType=\"Collection\">\n" +
                     "                    <owl:Class>\n" +
                     "                        <owl:unionOf rdf:parseType=\"Collection\">\n" +
                     "                            <owl:Restriction>\n" +
                     "                                <owl:onProperty rdf:resource=\"http://t/x#dataProp\"/>\n" +
                     "                                <owl:hasValue>a</owl:hasValue>\n" +
                     "                            </owl:Restriction>\n" +
                     "                            <owl:Restriction>\n" +
                     "                                <owl:onProperty rdf:resource=\"http://t/x#dataProp\"/>\n" +
                     "                                <owl:hasValue>b</owl:hasValue>\n" +
                     "                            </owl:Restriction>\n" +
                     "                        </owl:unionOf>\n" +
                     "                    </owl:Class>\n" +
                     "                    <owl:Restriction>\n" +
                     "                        <owl:onProperty rdf:resource=\"http://t/x#dataProp\"/>\n" +
                     "                        <owl:someValuesFrom rdf:resource=\"&xsd;string\"/>\n" +
                     "                    </owl:Restriction>\n" +
                     "                    <owl:Restriction>\n" +
                     "                        <owl:onProperty rdf:resource=\"http://t/x#dataProp\"/>\n" +
                     "                        <owl:allValuesFrom rdf:resource=\"&xsd;string\"/>\n" +
                     "                    </owl:Restriction>\n" +
                     "                </owl:intersectionOf>\n" +
                     "            </owl:Class>\n" +
                     "        </owl:equivalentClass>\n" +
                     "    </owl:Class>\n" +
                     "</rdf:RDF>\n";

        String drl2 = "package t.x; \n" +
                      "import org.drools.semantics.NamedIndividual; \n" +
                      "import org.w3._2002._07.owl.Thing; \n" +
                      "" +
                      "declare NamedIndividual end\n" +
                      "" +
                      "rule Init \n" +
                      "when \n" +
                      "then \n" +
                      "   NamedIndividual e1 = new NamedIndividual( \"X\" ); \n" +
                      "   insert( e1 );\n" +
                      "   RootThing t = don( e1, RootThing.class, true ); \n" +
                      "   modify ( t ) { \n" +
                      "      getDataProp().add( \"a\" )," +
                      "      getDataProp().add( \"c\" );" +
                      "   } \n" +
                      "end \n" +
                      "" +
                      "rule Log \n" +
                      "when \n" +
                      "   $x : E() \n" +
                      "then \n" +
                      "   System.out.println( \"RECOGNIZED \" + $x ); \n" +
                      "end" ;

        Resource res = KieServices.Factory.get().getResources().newByteArrayResource( owl.getBytes() );

        OWLOntology onto = factory.parseOntology( res );

        OntoModel ontoModel = factory.buildModel( "test",
                res,
                OntoModel.Mode.NONE,
                DLFactory.defaultAxiomGenerators );

        String drl = new TemplateRecognitionRuleBuilder().createDRL( onto, ontoModel );

        KieSession kSession = createSession( drl, drl2 );
        kSession.fireAllRules();

        for ( Object o : kSession.getObjects( new ClassObjectFilter( NamedIndividual.class ) ) ) {
            assertTrue( ((NamedIndividual) o).hasTrait( "t.x.E" ) );
        }

    }




    @Test
    public void testMixedDataTypes() {

        String owl = "<?xml version=\"1.0\"?>\n" +
                     "<!DOCTYPE rdf:RDF [\n" +
                     "    <!ENTITY owl \"http://www.w3.org/2002/07/owl#\" >\n" +
                     "    <!ENTITY xsd \"http://www.w3.org/2001/XMLSchema#\" >\n" +
                     "    <!ENTITY rdfs \"http://www.w3.org/2000/01/rdf-schema#\" >\n" +
                     "    <!ENTITY rdf \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" >\n" +
                     "]>\n" +
                     "<rdf:RDF xmlns=\"http://t/x#\"\n" +
                     "     xml:base=\"http://t/x\"\n" +
                     "     xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"\n" +
                     "     xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n" +
                     "     xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"\n" +
                     "     xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n" +
                     "    <owl:Ontology rdf:about=\"http://t/x\"/>\n" +
                     "    <owl:DatatypeProperty rdf:about=\"http://t/x#dataProp\">\n" +
                     "        <rdfs:range rdf:resource=\"&xsd;string\"/>\n" +
                     "    </owl:DatatypeProperty>\n" +
                     "    <owl:Class rdf:about=\"http://t/x#E\">\n" +
                     "        <owl:equivalentClass>\n" +
                     "            <owl:Restriction>\n" +
                     "                <owl:onProperty rdf:resource=\"http://t/x#dataProp\"/>\n" +
                     "                <owl:someValuesFrom>\n" +
                     "                    <rdfs:Datatype>\n" +
                     "                        <owl:unionOf rdf:parseType=\"Collection\">\n" +
                     "                            <rdf:Description rdf:about=\"&xsd;int\"/>\n" +
                     "                            <rdf:Description rdf:about=\"&xsd;string\"/>\n" +
                     "                        </owl:unionOf>\n" +
                     "                    </rdfs:Datatype>\n" +
                     "                </owl:someValuesFrom>\n" +
                     "            </owl:Restriction>\n" +
                     "        </owl:equivalentClass>\n" +
                     "    </owl:Class>\n" +
                     "</rdf:RDF>";

        String drl2 = "package t.x; \n" +
                      "import org.drools.semantics.NamedIndividual; \n" +
                      "import org.w3._2002._07.owl.Thing; \n" +
                      "" +
                      "declare NamedIndividual end\n" +
                      "" +
                      "rule Init \n" +
                      "when \n" +
                      "then \n" +
                      "   NamedIndividual e1 = new NamedIndividual( \"X\" ); \n" +
                      "   insert( e1 );\n" +
                      "   RootThing t = don( e1, RootThing.class, true ); \n" +
                      "   modify ( t ) { \n" +
                      "      getDataProp().add( new Integer(42) )," +
                      "      getDataProp().add( \"Hello\" )," +
                      "      getDataProp().add( 23.0 );" +
                      "   } \n" +
                      "end \n" +
                      "" +
                      "rule Log \n" +
                      "when \n" +
                      "   $x : E() \n" +
                      "then \n" +
                      "   System.out.println( \"RECOGNIZED \" + $x ); \n" +
                      "end" ;

        Resource res = KieServices.Factory.get().getResources().newByteArrayResource( owl.getBytes() );

        OWLOntology onto = factory.parseOntology( res );

        OntoModel ontoModel = factory.buildModel( "test",
                res,
                OntoModel.Mode.NONE,
                DLFactory.defaultAxiomGenerators );

        String drl = new TemplateRecognitionRuleBuilder().createDRL( onto, ontoModel );

        KieSession kSession = createSession( drl, drl2 );
        kSession.fireAllRules();

        for ( Object o : kSession.getObjects( new ClassObjectFilter( NamedIndividual.class ) ) ) {
            assertTrue( ((NamedIndividual) o).hasTrait( "t.x.E" ) );
        }

    }







    @Test
    public void testDNFConversion() {
        Resource res = KieServices.Factory.get().getResources().newClassPathResource( "ontologies/testDNF.owl" );

        OWLOntology onto = factory.parseOntology( res );
        OWLDataFactory fac = onto.getOWLOntologyManager().getOWLDataFactory();

        Map<OWLClassExpression, OWLClassExpression> dnf = new DLogicTransformer( onto ).getDefinitions();

        String ns = "http://t/x#";
        OWLClassExpression a = fac.getOWLClass( IRI.create( ns + "A" ) );
        OWLClassExpression b = fac.getOWLClass( IRI.create( ns + "B" ) );
        OWLClassExpression c = fac.getOWLClass( IRI.create( ns + "C" ) );
        OWLClassExpression d = fac.getOWLClass( IRI.create( ns + "D" ) );
        OWLClassExpression e = fac.getOWLClass( IRI.create( ns + "E" ) );
        OWLClassExpression f = fac.getOWLClass( IRI.create( ns + "F" ) );

        assertNotNull( a );
        assertNotNull( b );
        assertNotNull( c );
        assertNotNull( d );
        assertNotNull( e );
        assertNotNull( f );
        assertTrue( dnf.containsKey( a ) );
        assertTrue( dnf.containsKey( b ) );
        assertTrue( dnf.containsKey( c ) );
        assertTrue( dnf.containsKey( d ) );
        assertTrue( dnf.containsKey( e ) );
        assertTrue( dnf.containsKey( f ) );

        checkPlainNormal( dnf.get( a ), 6, 3 );
        checkPlainNormal( dnf.get( b ), 1, 4 );
        checkPlainNormal( dnf.get( c ), 1, 4 );
        checkPlainNormal( dnf.get( d ), 4, 1 );
        checkNormal(dnf.get(e), 2, 3);
        checkNormal(dnf.get(f), 2, 2);

    }



    private void checkPlainNormal( OWLClassExpression x, int ands, int args ) {
        checkNormal( x, ands, args, true );
    }

    private void checkNormal( OWLClassExpression x, int ands, int args ) {
        checkNormal( x, ands, args, false );
    }

    private void checkNormal( OWLClassExpression x, int ands, int args, boolean allNamed ) {
        assertTrue( x instanceof OWLObjectUnionOf );
        OWLObjectUnionOf xnorm = (OWLObjectUnionOf) x;
        assertEquals( ands, xnorm.getOperands().size() );
        for ( OWLClassExpression expr : xnorm.getOperands() ) {
            assertTrue( expr instanceof OWLObjectIntersectionOf );
            OWLObjectIntersectionOf xinner = (OWLObjectIntersectionOf) expr;
            assertEquals( args, xinner.getOperands().size() );
            if ( allNamed ) {
                for( OWLClassExpression arg : xinner.getOperands() ) {
                    assertFalse( arg.isAnonymous() );
                }
            }
        }
    }




}