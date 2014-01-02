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

import com.clarkparsia.empire.annotation.RdfsClass;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.MismatchedTokenException;
import org.antlr.runtime.RecognitionException;
import org.drools.compiler.rule.builder.dialect.java.parser.JavaLexer;
import org.drools.compiler.rule.builder.dialect.java.parser.JavaParser;
import org.drools.io.Resource;
import org.drools.io.ResourceFactory;
import org.drools.semantics.builder.DLFactory;
import org.drools.semantics.builder.DLFactoryBuilder;
import org.drools.semantics.builder.DLFactoryImpl;
import org.drools.semantics.builder.model.Concept;
import org.drools.semantics.builder.model.DRLModel;
import org.drools.semantics.builder.model.GraphModel;
import org.drools.semantics.builder.model.JarModel;
import org.drools.semantics.builder.model.JavaInterfaceModel;
import org.drools.semantics.builder.model.JavaInterfaceModelImpl;
import org.drools.semantics.builder.model.ModelFactory;
import org.drools.semantics.builder.model.OntoModel;
import org.drools.semantics.builder.model.PropertyRelation;
import org.drools.semantics.builder.model.SemanticXSDModel;
import org.drools.semantics.builder.model.WorkingSetModel;
import org.drools.semantics.builder.model.XSDModel;
import org.drools.semantics.builder.model.compilers.ModelCompiler;
import org.drools.semantics.builder.model.compilers.ModelCompilerFactory;
import org.drools.semantics.builder.model.compilers.XSDModelCompiler;
import org.drools.semantics.util.SemanticWorkingSetConfigData;
import org.drools.core.util.HierarchyEncoderImpl;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.io.ResourceType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;



@SuppressWarnings("restriction")
public class DL_8_ModelTest {



    protected DLFactory factory = DLFactoryBuilder.newDLFactoryInstance();

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();




        @Test()
    public void testConyardComplexModelGeneration() {

        Resource res = ResourceFactory.newClassPathResource( "ontologies/conyard.ttl" );
        OntoModel results = factory.buildModel( "conyard", res, OntoModel.Mode.FLAT,
                DLFactoryImpl.liteAxiomGenerators );

        checkConceptEncoding( results );

        ModelCompiler compiler = ModelCompilerFactory.newModelCompiler( ModelFactory.CompileTarget.XSDX );
        SemanticXSDModel xsdModel;


        ((XSDModelCompiler) compiler).setTransientPropertiesEnabled( false );
        xsdModel = (SemanticXSDModel) compiler.compile( results );

        xsdModel.streamAll( System.out );


        ((XSDModelCompiler) compiler).setTransientPropertiesEnabled( true );
        xsdModel = (SemanticXSDModel) compiler.compile( results );

        xsdModel.streamAll( System.out );

        ((XSDModelCompiler) compiler).setTransientPropertiesEnabled( false );
        xsdModel = (SemanticXSDModel) compiler.compile( results );

        xsdModel.streamAll( System.out );
//                xsdModel.streamBindings( System.out );


        String pack = "<http://org.drools.conyard.owl#>";
        Concept activity = results.getConcept(pack.replace("#", "#Activity") );
        Concept painting = results.getConcept(pack.replace("#", "#Painting") );
        Concept ironInst = results.getConcept(pack.replace("#", "#IronInstallation") );
        Concept wallRais = results.getConcept(pack.replace("#", "#WallRaising") );
        Concept inspectn = results.getConcept(pack.replace("#", "#Inspection") );

        assertNotNull( painting );
        assertNotNull( ironInst );
        assertNotNull( wallRais );


        Concept fallProt = results.getConcept(pack.replace("#", "#FallProtection") );
        Concept frostProt = results.getConcept(pack.replace("#", "#FrostProtection") );
        Concept fireProt = results.getConcept(pack.replace("#", "#FireProtection") );

        assertNotNull( fallProt );
        assertNotNull( fireProt );
        assertNotNull( frostProt );

        Concept beltX = results.getConcept(pack.replace("#", "#BeltBrandX") );
        Concept glove = results.getConcept(pack.replace("#", "#Gloves") );
        Concept gloveX = results.getConcept(pack.replace("#", "#GlovesBrandX") );

        assertNotNull( beltX );
        assertNotNull( gloveX );
        assertNotNull( glove );


        Concept fire = results.getConcept(pack.replace("#", "#Fire") );
        Concept anotherfire = results.getConcept(pack.replace("#", "#AnotherFire") );
        Concept anotherheat = results.getConcept(pack.replace("#", "#AnotherHeat") );
        Concept testfire = results.getConcept(pack.replace("#", "#TestFire") );

        assertNotNull( fire );
        assertNotNull( anotherfire );
        assertNotNull( anotherheat );
        assertNotNull( testfire );



        assertTrue( beltX.getSuperConcepts().contains( fallProt ) );
        assertTrue( gloveX.getSuperConcepts().contains( fireProt ) );
        assertTrue( gloveX.getSuperConcepts().contains( glove ) );
        assertTrue( glove.getSuperConcepts().contains( frostProt ) );

        assertTrue( testfire.getSuperConcepts().contains( fire ) );
        assertTrue( anotherfire.getSuperConcepts().contains( fire ) );
        assertTrue( anotherheat.getSuperConcepts().contains( anotherfire ) );


//        assertTrue( checkProperty( painting, pack, "involvesPersons", "Person", 1, null, true, true) );
        assertTrue( checkProperty( painting, pack, "involvesLabourers", "Labourer", 1, null, true, false ) );
//        assertTrue( checkProperty( painting, pack, "requiresEquipments", "Equipment", 0, null, true, true ) );
        assertTrue( checkProperty( painting, pack, "requiresPaints", "Paint", 1, null, true, false ) );
        assertTrue( checkProperty( painting, pack, "requiresStair", "Stair", 1, 1, true, false ) );
        assertTrue( checkProperty( painting, pack, "requires", "Equipment", 0, null, false, true ) );
        assertTrue( checkProperty( painting, pack, "involves", "Person", 0, null, false, true ) );


        assertEquals( 15, ironInst.getChosenProperties().size() );
        assertTrue( checkProperty( ironInst, pack, "involvesMasons", "Mason", 1, null, true, false ) );
        assertTrue( checkProperty( ironInst, pack, "requiresWeldingTorchs", "WeldingTorch", 1, null, true, false ) );
        assertTrue( checkProperty( ironInst, pack, "requiresIronBars", "IronBar", 1, null, true, false ) );
        assertTrue( checkProperty( ironInst, pack, "requiresGrinders", "Grinder", 1, null, true, false ) );
        assertTrue( checkProperty( ironInst, pack, "requiresCrane", "Crane", 1, 1, true, false ) );
        assertTrue( checkProperty( ironInst, pack, "involvesSmiths", "Smith", 1, 4, true, false) );
        assertTrue( checkProperty( ironInst, pack, "involvesPersons", "Person", 1, null, true, true ) );
        assertTrue( checkProperty( ironInst, pack, "involvesLabourers", "Labourer", 2, null, true, false ) );
//        assertTrue( checkProperty( ironInst, pack, "requiresEquipments", "Equipment", 0, null, true, true ) );
        assertTrue( checkProperty( ironInst, pack, "hasComment", "xsd:string", 1, 1, false, true ) );
//        assertTrue( checkProperty( ironInst, pack, "hasCommentString", "xsd:string", 1, 1, true, true ) );


        assertTrue( checkProperty( wallRais, pack, "involves", "Person", 0, null, false, true ) );
        assertTrue( checkProperty( wallRais, pack, "involvesMasons", "Mason", 3, null, true, false ) );
        assertTrue( checkProperty( wallRais, pack, "requiresBricks", "Bricks", 1, 1, true, false ) );
        assertFalse( checkProperty( wallRais, pack, "requiresBrickses", "Bricks", 1, null, true, true ) );

        assertTrue( checkProperty( inspectn, pack, "involvesPersons", "Person", 1, null, true, true ) );
        assertTrue( checkProperty( inspectn, pack, "involvesPerson", "Person", 1, 1, true, false ) );
        assertTrue( checkProperty( inspectn, pack, "requiresEquipments", "Equipment", 0, 3, true, false ) );

//
        for ( Concept con : results.getConcepts() ) {
            if ( con.getName().endsWith( "Range" ) || con.getName().endsWith( "Domain" ) ) {
                assertTrue( con.isAbstrakt() );
                assertTrue( con.isAnonymous() );
                assertFalse( con.isPrimitive() );
            }
            if ( con.getName().endsWith( "Filler" ) ) {
                assertFalse( con.isAbstrakt() );
                assertTrue( con.isAnonymous() );
                assertFalse( con.isPrimitive() );
            }
        }

        System.out.println( wallRais.getEffectiveProperties() );
        assertEquals( 4, wallRais.getEffectiveProperties().size() );

        Set<PropertyRelation> wrEffectiveBaseProperties = wallRais.getEffectiveBaseProperties();
        for ( PropertyRelation p : wrEffectiveBaseProperties ) {
            System.out.println( p );
            for( PropertyRelation sub : wallRais.getProperties().values() ) {
                if ( sub.isRestricted() && sub.getBaseProperty().equals( p ) ) {
                    if ( p.getName().equals( "involves" ) ) {
                        assertTrue( sub.getName().equals( "involvesMasons" ) || sub.getName().equals( "involvesPersons" ) );
                    }
                }
            }
        }


    }


    @Test
    public void testDRLModelGenerationExternal() {
        String source = "ontologies/kmr2" + File.separator + "kmr2_mini.owl";
        Resource res = ResourceFactory.newClassPathResource(source);

        OntoModel results = factory.buildModel( "kmr2mini", res, OntoModel.Mode.HIERARCHY );

        checkConceptEncoding( results );

        ModelCompiler compiler = ModelCompilerFactory.newModelCompiler( ModelFactory.CompileTarget.DRL );
        DRLModel drlModel = (DRLModel) compiler.compile( results );

        String drl = drlModel.getDRL();

        System.err.println( drl );

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write( ks.getResources()
                           .newByteArrayResource( drl.getBytes() )
                           .setSourcePath( "test.drl" )
                           .setResourceType( ResourceType.DRL ) );
        KieBuilder kieBuilder = ks.newKieBuilder( kfs );
        kieBuilder.buildAll();

        if ( kieBuilder.getResults().hasMessages( Message.Level.ERROR ) ) {
            fail( kieBuilder.getResults().getMessages( Message.Level.ERROR ).toString() );
        }


        ModelCompiler jcompiler = ModelCompilerFactory.newModelCompiler( ModelFactory.CompileTarget.JAR );
        JarModel jarModel = (JarModel) jcompiler.compile( results );

        assertTrue( jarModel.save( folder.getRoot().getAbsolutePath() ) );

        try {
            FileOutputStream fos = new FileOutputStream( folder.newFile( "test.jar" ) );
            byte[] content = jarModel.buildJar().toByteArray();

            fos.write( content, 0, content.length );
            fos.flush();
            fos.close();
        } catch ( IOException e ) {
            fail( e.getMessage() );
        }



        System.err.println( results );

    }



    @Test
    @Ignore //visualization test
    public void testGraphModelGeneration() {
//        String source = "org/drools/semantics/lang/dl/kmr2_mini.owl";
        String source = "ontologies/kmr2" + File.separator + "KMR_OntologySample.manchester.owl";
        Resource res = ResourceFactory.newClassPathResource( source );
        OntoModel results = factory.buildModel("ontologies/kmr2", res, OntoModel.Mode.HIERARCHY );

        ModelCompiler compiler = ModelCompilerFactory.newModelCompiler( ModelFactory.CompileTarget.GRAPH );
        GraphModel gModel = (GraphModel) compiler.compile( results );

        gModel.display();

        System.err.println( gModel );


        try {
//            while (true) {
            Thread.sleep( 5000 );
//            }
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }





    @Test
    public void testXSDModelGeneration() {
        String source = "ontologies/kmr2" + File.separator + "kmr2_mini.owl";
        Resource res = ResourceFactory.newClassPathResource( source );

        OntoModel results = factory.buildModel( "kmr2mini", res, OntoModel.Mode.HIERARCHY );


        ModelCompiler compiler = ModelCompilerFactory.newModelCompiler( ModelFactory.CompileTarget.XSD );
        XSDModel xsdModel = (XSDModel) compiler.compile( results );

        xsdModel.streamAll( System.out );

    }

    @Test
    public void testJarModelGeneration() {
        String source = "ontologies/kmr2" + File.separator + "kmr2_mini.owl";
        Resource res = ResourceFactory.newClassPathResource( source );

        OntoModel results = factory.buildModel( "kmr2mini", res, OntoModel.Mode.HIERARCHY );


        ModelCompiler compiler = ModelCompilerFactory.newModelCompiler( ModelFactory.CompileTarget.JAR );
        JarModel jarModel = (JarModel) compiler.compile( results );
        String src = ( (JavaInterfaceModelImpl.InterfaceHolder) jarModel.getTrait( "org.kmr.ontology.AllergyFactType" ) ).getSource();
        System.out.println( src );

        JavaLexer lexer = new JavaLexer( new ANTLRStringStream( src ) );
        JavaParser parser = new JavaParser( new CommonTokenStream( lexer ) );

        try {
            parser.compilationUnit();
        } catch ( MismatchedTokenException e ) {
            fail( e.getMessage() );
        } catch ( RecognitionException e ) {
            fail( e.getMessage() );
        }

        try {
            ClassLoader cl = new ItemClassLoader( jarModel.getDefaultPackage(), jarModel.getCompiledTraits(), Thread.currentThread().getContextClassLoader() );
            Class fact = cl.loadClass( "org.kmr.ontology.ClinicalFactType" );
            Class thin = cl.loadClass( "org.kmr.ontology.RootThing" );
            assertTrue( thin.isAssignableFrom( fact ) );
            assertTrue( fact.getAnnotation( RdfsClass.class ) != null );
        } catch ( ClassNotFoundException e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }


    }

    private class ItemClassLoader extends ClassLoader {

        private String pack;
        private Map<String, JarModel.Holder> itemClasses;

        public ItemClassLoader( String pack, Map<String, JarModel.Holder> itemClasses, ClassLoader parent ) {
            super( parent );
            this.pack = pack;
            this.itemClasses = itemClasses;
        }

        protected Class<?> findClass( String name ) throws ClassNotFoundException {
            JarModel.Holder holder = itemClasses.get( name );
            if ( holder == null ) {
                return super.findClass( name );
            }
            byte[] data = holder.getBytes();
            return defineClass( name, data, 0, data.length );
        }
    }





    @Test
    public void testWorkingSetModelGeneration() {
        String source = "ontologies/kmr2" + File.separator + "kmr2_mini.owl";
        Resource res = ResourceFactory.newClassPathResource( source );

        OntoModel results = factory.buildModel( "kmr2mini", res, OntoModel.Mode.HIERARCHY );


        ModelCompiler compiler = ModelCompilerFactory.newModelCompiler( ModelFactory.CompileTarget.WORKSET );
        WorkingSetModel wsModel = (WorkingSetModel) compiler.compile( results );

        SemanticWorkingSetConfigData ws = wsModel.getWorkingSet();

        System.out.println(ws);

    }


    @Test
    public void testFullKMR2XSDModelGeneration() {
        String source = "ontologies/kmr2" + File.separator + "kmr2_mini.owl";
//        String source = "kmr2" + File.separator + "KMR_Ontology.ttl";
        Resource res = ResourceFactory.newClassPathResource(source);


        OntoModel results = factory.buildModel("ontologies/kmr2", res, OntoModel.Mode.FLAT );


        ModelCompiler jcompiler =  ModelCompilerFactory.newModelCompiler( ModelFactory.CompileTarget.JAR );
        JarModel jarModel = (JarModel) jcompiler.compile( results );

        try {
            FileOutputStream fos = new FileOutputStream( folder.newFile( "kmr2.jar" ) );
            byte[] content = jarModel.buildJar().toByteArray();

            fos.write( content, 0, content.length );
            fos.flush();
            fos.close();
        } catch ( IOException e ) {
            fail( e.getMessage() );
        }


        /**************************************************************************************************************/

        ModelCompiler compiler = ModelCompilerFactory.newModelCompiler( ModelFactory.CompileTarget.XSDX );
        SemanticXSDModel xsdModel = (SemanticXSDModel) compiler.compile( results );

        xsdModel.streamAll( System.out );
//        xsdModel.streamBindings( System.out );



        try {
            FileOutputStream fos = new FileOutputStream( folder.newFile( "kmr2.xsd" ) );
            xsdModel.streamAll( fos );
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            FileOutputStream fos = new FileOutputStream( folder.newFile("bindings.xjb") );
            xsdModel.streamBindings( fos );
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ModelCompiler javaCompiler = ModelCompilerFactory.newModelCompiler( ModelFactory.CompileTarget.JAVA );
        JavaInterfaceModel javaModel = (JavaInterfaceModel) javaCompiler.compile( results );

        javaModel.save( folder.getRoot().getPath() );


    }





    @Test
    @Ignore
    public void testExternalBindingModelGeneration() {

        URI uri = (new File(".")).getAbsoluteFile().toURI();
        System.out.println( uri );

        String source = "wwtp.owl";
        //        String source = "kmr2" + File.separator + "KMR_Ontology.ttl";
        Resource res = ResourceFactory.newClassPathResource( source );

        OntoModel results = factory.buildModel( "wwtp", res, OntoModel.Mode.OPTIMIZED );

        for ( Concept con : results.getConcepts() ) {
            if ( con.getIri().startsWith( "<java://" ) ) {
                assertTrue( con.isResolved() );
            }
        }

        ModelCompiler jcompiler =  ModelCompilerFactory.newModelCompiler( ModelFactory.CompileTarget.JAR );
        JarModel jarModel = (JarModel) jcompiler.compile( results );

        assertNull( jarModel.getCompiledTrait( "javax.measure.quantity.Quantity" ) );
        assertNotNull( jarModel.getCompiledTrait( "Quantity" ) );
        assertNotNull( jarModel.getCompiledTrait( "NO3Probe" ) );


        ModelCompiler xcompiler =  ModelCompilerFactory.newModelCompiler( ModelFactory.CompileTarget.XSDX );
        XSDModel xsdModel = (XSDModel) xcompiler.compile( results );

        xsdModel.streamAll( System.out );

    }



    @Test
    public void testIndividualGeneration() {

        URI uri = (new File(".")).getAbsoluteFile().toURI();
        System.out.println( uri );

        String source = "ontologies/rule_merged.owl";
//        String source = "rules.owl";
        Resource res = ResourceFactory.newClassPathResource( source );

        OntoModel results = factory.buildModel( "testRules", res, OntoModel.Mode.FLAT, DLFactoryImpl.liteAxiomGenerators );

        checkConceptEncoding( results );

        ModelCompiler xcompiler =  ModelCompilerFactory.newModelCompiler( ModelFactory.CompileTarget.XSDX );
        SemanticXSDModel xsdModel = (SemanticXSDModel) xcompiler.compile( results );

        xsdModel.streamIndividualFactory( System.out );

        assertEquals( 22, xsdModel.getIndividuals().size() );

        System.out.println( "xx " );

    }




    @Test
    public void testPropertyConsistency() {

        URI uri = (new File(".")).getAbsoluteFile().toURI();
        System.out.println( uri );

        String source = "ontologies/rule_merged.owl";
//        String source = "rules.owl";
        Resource res = ResourceFactory.newClassPathResource( source );

        OntoModel results = factory.buildModel( "testRules", res, OntoModel.Mode.FLAT, DLFactory.liteAxiomGenerators );

        System.out.println( results );


    }



    private boolean checkProperty( Concept base, String pack, String propName, String target, Integer minCard, Integer maxCard, boolean restricted, boolean inherited ) {

        PropertyRelation rel = base.getChosenProperties().get( pack.replace("#", "#"+propName) );
        if ( rel == null ) {
            return false;
        }
        if ( ! rel.getTarget().getName().equals( target ) ) {
            return false;
        }
        return rel.getMinCard() == minCard
                && rel.getMaxCard() == maxCard
                && rel.getName().equals( propName )
                && rel.isRestricted() == restricted
                && rel.isInheritedFor( base ) == inherited;
    }




    @Test
    public void testPartiallySpecifiedHierarchicalModelGeneration() {

        Resource res = ResourceFactory.newClassPathResource( "ontologies/missingDomRanHier.owl" );
        OntoModel results = factory.buildModel( "partest", res, OntoModel.Mode.HIERARCHY );

        ModelCompiler compiler = ModelCompilerFactory.newModelCompiler( ModelFactory.CompileTarget.XSDX );
        SemanticXSDModel xsdModel;

        ((XSDModelCompiler) compiler).setTransientPropertiesEnabled( false );
        xsdModel = (SemanticXSDModel) compiler.compile( results );

        xsdModel.streamAll( System.out );

        ((XSDModelCompiler) compiler).setTransientPropertiesEnabled( true );
        xsdModel = (SemanticXSDModel) compiler.compile( results );

//        xsdModel.stream( System.out );

        xsdModel.streamBindings( System.out );


    }

    @Test
    public void testNestedClassesByDisjunction() {

        Resource res = ResourceFactory.newClassPathResource( "ontologies/falseSubclass.owl" );
        OntoModel results = factory.buildModel( "partest", res, OntoModel.Mode.OPTIMIZED );

        checkConceptEncoding( results );

        System.out.println( results );
        Concept master = results.getConcept( "<http://test#AMaster>" );
        assertEquals( 3, master.getSubConcepts().size() );
        assertEquals( 2, master.getSuperConcepts().size() );

        System.out.println( master.getSuperConcepts() );

        Concept klass = results.getConcept( "<http://test#Klass>" );
        Concept subKlass = results.getConcept( "<http://test#SubKlass>" );
        Concept moreKlass = results.getConcept( "<http://test#MoreKlass>" );
        Concept yetKlass = results.getConcept( "<http://test#YetAnotherKlass>" );
        Concept againKlass = results.getConcept( "<http://test#AgainKlass>" );

        Concept disj = results.getConcept( "<http://test#YorXorZ>" );
        Concept xoz = results.getConcept( "<http://test#XorZ>" );

        assertTrue( master.getSuperConcepts().contains( disj ) );
        assertTrue( moreKlass.getSuperConcepts().contains( xoz ) );

        Concept w = results.getConcept( "<http://test#W>" );
        Concept z = results.getConcept( "<http://test#Z>" );

        assertTrue( againKlass.getSubConcepts().contains( w ) );
        assertTrue( againKlass.getSubConcepts().contains( z ) );
        assertTrue( w.getSuperConcepts().contains( againKlass ) );
        assertTrue( z.getSuperConcepts().contains( againKlass ) );


        Concept randKlass = results.getConcept( "<http://test#RandomKlass>" );
        Concept caslKlass = results.getConcept( "<http://test#CasualKlass>" );


        Concept anon0 = results.getConcept( "<http://test#Anon0>" );
        Concept anon1 = results.getConcept( "<http://test#Anon1>" );
        Concept anon2 = results.getConcept( "<http://test#Anon2>" );

        assertNotNull( anon0 );
        assertNotNull( anon1 );
        assertNull( anon2 );


    }


    @Test
    public void testHardwareOntology() {
        Resource res = ResourceFactory.newClassPathResource( "ontologies/hardware.owl" );
        OntoModel results = factory.buildModel( "partest", res, OntoModel.Mode.OPTIMIZED );

        checkConceptEncoding( results );

        assertNotNull( results );
    }

    @Test
    public void testPizzaOntology() {
        Resource res = ResourceFactory.newClassPathResource( "ontologies/pizza.owl" );
        OntoModel results = factory.buildModel( "partest", res, OntoModel.Mode.OPTIMIZED );

        checkConceptEncoding( results );

        assertNotNull( results );
    }



    @Test
    public void testDataEnumRange() {
        Resource res = ResourceFactory.newClassPathResource( "ontologies/dataEnumRange.owl" );
        OntoModel results = factory.buildModel( "partest", res, OntoModel.Mode.OPTIMIZED );

        checkConceptEncoding( results );

        Concept x = results.getConcept( "<http://org/drools/test#X>" );

        PropertyRelation orel = results.getProperty( "<http://org/drools/test#individualProp>" );
        PropertyRelation drel = results.getProperty( "<http://org/drools/test#valuedProp>" );

        assertNotNull( x );
        assertNotNull( orel );
        assertNotNull( drel );

        assertTrue( orel.getTarget().getSuperConcepts().contains( x ) );
        assertEquals( "<http://www.w3.org/2001/XMLSchema#string>", drel.getTarget().getIri() );


        ModelCompiler jcompiler =  ModelCompilerFactory.newModelCompiler( ModelFactory.CompileTarget.JAVA );
        JavaInterfaceModel jModel = (JavaInterfaceModel) jcompiler.compile( results );


    }



    @Test
    public void testKMR2Ontology() {
        Resource res = ResourceFactory.newClassPathResource( "ontologies/kmr2/KMR_Ontology2.ttl" );
        OntoModel results = factory.buildModel( "kmr2", res, OntoModel.Mode.FLAT, DLFactory.liteAxiomGenerators );
        assertNotNull(results);

        checkConceptEncoding(results);
    }



    @Test
    public void testAnonymousClassIndividual() {
        Resource res = ResourceFactory.newClassPathResource( "ontologies/anonClassIndividual.owl" );
        OntoModel results = factory.buildModel( "test", res, OntoModel.Mode.FLAT, DLFactory.liteAxiomGenerators );
        assertNotNull(results);

        checkConceptEncoding( results );

        Concept joeRestriction = results.getConcept( "<http://owl.man.ac.uk/2005/07/sssw/people#JoeRestrictedType4>" );
        Concept joeType = results.getConcept( "<http://owl.man.ac.uk/2005/07/sssw/people#JoeType>" );

        assertNotNull( joeRestriction );
        assertNotNull( joeType );

        assertEquals( 0, joeRestriction.getProperties().size() );
        assertEquals( 1, joeType.getProperties().size() );

        PropertyRelation petRestr = joeType.getProperties().get( "<http://owl.man.ac.uk/2005/07/sssw/people#has_petAnimal>" );
        assertNotNull( petRestr );
        assertTrue( petRestr.isRestricted() );

    }

    @Test
    public void testOutOfDomainProperty() {
        Resource res = ResourceFactory.newClassPathResource( "ontologies/outOfDomainProperty.owl" );
        OntoModel results = factory.buildModel( "test", res, OntoModel.Mode.FLAT, DLFactory.liteAxiomGenerators );
        assertNotNull(results);

        checkConceptEncoding( results );

        Concept joeType = results.getConcept( "<http://owl.man.ac.uk/2005/07/sssw/people#JoeType>" );

        assertNotNull( joeType );

        assertEquals( 1, joeType.getProperties().size() );

        PropertyRelation petRestr = joeType.getProperties().get( "<http://owl.man.ac.uk/2005/07/sssw/people#has_petAnimal>" );
        assertNotNull( petRestr );
        assertTrue( petRestr.isRestricted() );
        assertEquals( 1, petRestr.getMaxCard().intValue() );

    }



    @Test
    public void testPeopleOntology() {
        Resource res = ResourceFactory.newClassPathResource( "ontologies/people.owl" );
        OntoModel results = factory.buildModel( "people", res, OntoModel.Mode.OPTIMIZED, DLFactory.liteAxiomGenerators );
        assertNotNull( results );

        checkConceptEncoding( results );
    }


    @Test
    @Ignore
    public void testDatabaseModel() {
        Resource res = ResourceFactory.newClassPathResource( "ontologies/dbModel.owl" );
        OntoModel results = factory.buildModel( "DB", res, OntoModel.Mode.NONE, DLFactory.liteAxiomGenerators );
        assertNotNull(results);

        checkConceptEncoding( results );


    }



    private boolean checkConceptEncoding( OntoModel results ) {
        boolean ans = true;
        for ( Concept con : results.getConcepts() ) {
            for ( Concept sub : results.getConcepts() ) {
                boolean subByCode = HierarchyEncoderImpl.supersetOrEqualset( sub.getTypeCode(), con.getTypeCode() );
                boolean subByRel = ancestor( con, sub );
                if ( subByCode && ! subByRel  ) {
                    ans = false;
                    System.out.println( "FAILED!! SPURIOUS INHERITANCE " + sub + " vs " + con  );
                }
                if ( ! subByCode && subByRel  ) {
                    ans = false;
                    System.out.println( "FAILED!! MISSING INHERITANCE " + sub + " vs " + con );
                }
            }
        }
        assertTrue( ans );
        return ans;
    }

    private boolean ancestor(Concept con, Concept sub) {
        if ( con == sub ) {
            return true;
        }
        if ( sub.getSuperConcepts().contains( con ) ) {
            return true;
        }
        for ( Concept sup : sub.getSuperConcepts() ) {
            if ( ancestor( con, sup ) ) {
                return true;
            }
        }
        return false;
    }

}