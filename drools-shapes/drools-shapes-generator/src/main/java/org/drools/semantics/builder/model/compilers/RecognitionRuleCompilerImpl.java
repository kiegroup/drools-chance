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

package org.drools.semantics.builder.model.compilers;

import org.drools.compiler.lang.api.PackageDescrBuilder;
import org.drools.semantics.builder.model.CompiledOntoModel;
import org.drools.semantics.builder.model.Concept;
import org.drools.semantics.builder.model.ModelFactory;
import org.drools.semantics.builder.model.OntoModel;
import org.drools.semantics.builder.model.RecognitionRuleModel;
import org.drools.semantics.builder.model.RecognitionRuleModelImpl;
import org.drools.semantics.builder.reasoner.APIRecognitionRuleBuilder;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.StreamDocumentSource;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.MissingImportHandlingStrategy;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyLoaderConfiguration;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.w3._2002._07.owl.Thing;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

public class RecognitionRuleCompilerImpl extends ModelCompilerImpl implements RecognitionRuleCompiler {

    protected APIRecognitionRuleBuilder builder;
    protected Properties props;
    protected OWLOntology additionals;

    @Override
    protected void setModel( final OntoModel model ) {
        this.model = (CompiledOntoModel) ModelFactory.newModel( ModelFactory.CompileTarget.RL, model );
        this.builder = new APIRecognitionRuleBuilder( model );
        initialize( props );
        try {
            additionals = loadAdditionalDefinitions( props );
        } catch ( OWLOntologyCreationException e ) {
            e.printStackTrace();
        }
    }


    public CompiledOntoModel compile( OntoModel model ) {
        OWLOntology ontology = model.getOntology();

        if ( getModel() == null ) {
            setModel( model );
        }
        PackageDescrBuilder pdBuilder = ( ( RecognitionRuleModelImpl ) getModel() ).getPackage();
        for ( Concept con : getModel().getConcepts() ) {
            if ( con.isPrimitive() || con.isAbstrakt() || con.isAnonymous() || con.isResolved() ) {
                continue;
            }
            OWLClass klass = ontology.getOWLOntologyManager().getOWLDataFactory().getOWLClass( con.getIRI() );
            processDefinitions( con, klass, ontology, pdBuilder );
            if ( additionals != null ) {
                processDefinitions( con, klass, additionals, pdBuilder );
            }
        }
        return getModel();
    }

    private void processDefinitions( Concept con, OWLClass klass, OWLOntology ontology, PackageDescrBuilder pdBuilder ) {
        Set<OWLClassExpression> defs = klass.getEquivalentClasses( ontology.getImportsClosure() );
        HashMap<String,Object> map = new HashMap<String, Object>();
        if ( ! defs.isEmpty() ) {
            for ( OWLClassExpression def : defs ) {
                map.clear();
                map.put( "klass", klass );
                map.put( "defn", def );
                map.put( "ontology", ontology );
                compile( con, pdBuilder, map );
            }
        }
    }


    public void compile( Concept con, Object context, Map<String, Object> params ) {
        getModel().addTrait( con.getIri(), builder.createDRL( (OWLClass) params.get( "klass" ),
                                                              (OWLClassExpression) params.get( "defn" ),
                                                              false,
                                                              (PackageDescrBuilder) context,
                                                              (OWLOntology) params.get( "ontology" ) ) );
    }

    public RecognitionRuleModel getModel() {
        return (RecognitionRuleModel) super.getModel();
    }

    @Override
    public void configure( Properties properties ) {
        this.props = properties;
    }

    private OWLOntology loadAdditionalDefinitions( Properties props ) throws OWLOntologyCreationException {
        OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
        if ( props.containsKey( "definitions" ) ) {
            String list = props.getProperty( "definitions" );
            StringTokenizer tok = new StringTokenizer( list, "[]," );

            OWLOntologyLoaderConfiguration olc = new OWLOntologyLoaderConfiguration();
            olc.setMissingImportHandlingStrategy( MissingImportHandlingStrategy.SILENT );
            if ( props.containsKey( "ignores" ) ) {
                olc = addIgnores( olc, props.getProperty( "ignores" ) );
            }
            OWLOntology ontology = null;
            while ( tok.hasMoreTokens() ) {
                String res = tok.nextToken();
                try {
                    StreamDocumentSource src;
                    if ( new File( res ).exists() ) {
                        src = new StreamDocumentSource( new FileInputStream( res ) );
                    } else {
                        URL url = RecognitionRuleCompilerImpl.class.getResource( res );
                        src = new StreamDocumentSource( url.openStream() );
                    }
                    ontology = ontologyManager.loadOntologyFromOntologyDocument( src, olc );
                } catch ( Exception e ) {
                    e.printStackTrace();
                }
            }
            return ontology;
        }
        return null;
    }

    private OWLOntologyLoaderConfiguration addIgnores( OWLOntologyLoaderConfiguration olc, String ignores ) {
        StringTokenizer tok = new StringTokenizer( ignores, "[]," );

        while ( tok.hasMoreTokens() ) {
            String res = tok.nextToken();
            olc = olc.addIgnoredImport( IRI.create( res ) );
        }
        return olc;
    }


    protected void initialize( Properties properties ) {
        builder.setRedeclare( Boolean.parseBoolean( properties.getProperty( "redeclare", "true" ) ) )
                .setRefract( Boolean.parseBoolean( properties.getProperty( "refract", "false" ) ) )
                .setUseMetaClass( Boolean.parseBoolean( properties.getProperty( "useMetaClass", "false" ) ) )
                .setUsePropertyReactivity( Boolean.parseBoolean( properties.getProperty( "usePropertyReactivity", "true" ) ) )
                .setUseTMS( Boolean.parseBoolean( properties.getProperty( "useTMS", "false" ) ) )
                .setDebug( Boolean.parseBoolean( properties.getProperty( "debug", "false" ) ) )
                .setRootClass( properties.getProperty( "rootClass", Thing.class.getCanonicalName() ) );
    }
}
