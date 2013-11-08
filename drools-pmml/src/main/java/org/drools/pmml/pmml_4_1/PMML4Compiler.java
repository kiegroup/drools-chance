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

package org.drools.pmml.pmml_4_1;

import org.dmg.pmml.pmml_4_1.descr.ClusteringModel;
import org.dmg.pmml.pmml_4_1.descr.NeuralNetwork;
import org.dmg.pmml.pmml_4_1.descr.PMML;
import org.dmg.pmml.pmml_4_1.descr.RegressionModel;
import org.dmg.pmml.pmml_4_1.descr.Scorecard;
import org.dmg.pmml.pmml_4_1.descr.SupportVectorMachineModel;
import org.dmg.pmml.pmml_4_1.descr.TreeModel;
import org.drools.compiler.compiler.PMMLCompiler;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.core.RuleBaseConfiguration;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.builder.KnowledgeBuilderResult;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.mvel2.templates.SimpleTemplateRegistry;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRegistry;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PMML4Compiler implements PMMLCompiler {


    public static final String PMML = "org.dmg.pmml.pmml_4_1.descr";
    public static final String SCHEMA_PATH = "xsd/org/dmg/pmml/pmml_4_1/pmml-4-1.xsd";
    public static final String BASE_PACK = PMML4Compiler.class.getPackage().getName().replace('.','/');


    
    
    public static final String VISITOR_RULES  = BASE_PACK + "/pmml_visitor.drl";
    public static boolean visitorRules = false;
    
    public static final String COMPILER_RULES = BASE_PACK + "/pmml_compiler.drl";
    public static boolean compilerRules = false;
    
    public static final String INFORMER_RULES = BASE_PACK + "/pmml_informer.drl";
    public static boolean informerRules = false;

    

    protected static boolean globalLoaded = false;
    protected static final String[] GLOBAL_TEMPLATES = new String[] {
            "global/pmml_header.drlt",
            "global/pmml_import.drlt",
            "global/modelMark.drlt",
            "global/commonQueries.drlt",

            "global/dataDefinition/common.drlt",
            "global/dataDefinition/rootDataField.drlt",
            "global/dataDefinition/inputBinding.drlt",
            "global/dataDefinition/outputBinding.drlt",
            "global/dataDefinition/ioTypeDeclare.drlt",
            "global/dataDefinition/updateIOField.drlt",
            "global/dataDefinition/inputFromEP.drlt",
            "global/dataDefinition/ioTrait.drlt",

            "global/manipulation/confirm.drlt",
            "global/manipulation/mapMissingValues.drlt",
            "global/manipulation/propagateMissingValues.drlt",

            "global/validation/intervalsOnDomainRestriction.drlt",
            "global/validation/valuesOnDomainRestriction.drlt",
            "global/validation/valuesOnDomainRestrictionMissing.drlt",
            "global/validation/valuesOnDomainRestrictionInvalid.drlt",
    };
            
    protected static boolean transformationLoaded = false;
    protected static final String[] TRANSFORMATION_TEMPLATES = new String[] {
            "transformations/normContinuous/boundedLowerOutliers.drlt",
            "transformations/normContinuous/boundedUpperOutliers.drlt",
            "transformations/normContinuous/normContOutliersAsMissing.drlt",
            "transformations/normContinuous/linearTractNormalization.drlt",
            "transformations/normContinuous/lowerExtrapolateLinearTractNormalization.drlt",
            "transformations/normContinuous/upperExtrapolateLinearTractNormalization.drlt",

            "transformations/aggregate/aggregate.drlt",
            "transformations/aggregate/collect.drlt",

            "transformations/simple/constantField.drlt",
            "transformations/simple/aliasedField.drlt",

            "transformations/normDiscrete/indicatorFieldYes.drlt",
            "transformations/normDiscrete/indicatorFieldNo.drlt",
            "transformations/normDiscrete/predicateField.drlt",

            "transformations/discretize/intervalBinning.drlt",
            "transformations/discretize/outOfBinningDefault.drlt",
            "transformations/discretize/outOfBinningMissing.drlt",

            "transformations/mapping/mapping.drlt",

            "transformations/functions/apply.drlt",
            "transformations/functions/function.drlt"
    };
    
    protected static boolean miningLoaded = false;
    protected static final String[] MINING_TEMPLATES = new String[] {
            "models/common/mining/miningField.drlt",
            "models/common/mining/miningFieldInvalid.drlt",
            "models/common/mining/miningFieldMissing.drlt",
            "models/common/mining/miningFieldOutlierAsMissing.drlt",
            "models/common/mining/miningFieldOutlierAsExtremeLow.drlt",
            "models/common/mining/miningFieldOutlierAsExtremeUpp.drlt",

            "models/common/target/targetReshape.drlt",
            "models/common/target/aliasedOutput.drlt",
            "models/common/target/addOutputFeature.drlt",
            "models/common/target/addRelOutputFeature.drlt",
            "models/common/target/outputQuery.drlt",
            "models/common/target/outputQueryPredicate.drlt"
    };
    
    protected static boolean neuralLoaded = false; 
    protected static final String[] NEURAL_TEMPLATES = new String[] {
            "models/neural/neuralBeans.drlt",
            "models/neural/neuralWireInput.drlt",
            "models/neural/neuralBuildSynapses.drlt",
            "models/neural/neuralBuildNeurons.drlt",
            "models/neural/neuralLinkSynapses.drlt",
            "models/neural/neuralFire.drlt",
            "models/neural/neuralLayerMaxNormalization.drlt",
            "models/neural/neuralLayerSoftMaxNormalization.drlt",
            "models/neural/neuralOutputField.drlt",
            "models/neural/neuralClean.drlt"
    };

    protected static boolean svmLoaded = false;
    protected static final String[] SVM_TEMPLATES = new String[] {
            "models/svm/svmParams.drlt",
            "models/svm/svmDeclare.drlt",
            "models/svm/svmFunctions.drlt",
            "models/svm/svmBuild.drlt",
            "models/svm/svmInitSupportVector.drlt",
            "models/svm/svmInitInputVector.drlt",
            "models/svm/svmKernelEval.drlt",
            "models/svm/svmOutputGeneration.drlt",
            "models/svm/svmOutputVoteDeclare.drlt",
            "models/svm/svmOutputVote1vN.drlt",
            "models/svm/svmOutputVote1v1.drlt",
    };

    protected static boolean simpleRegLoaded = false;
    protected static final String[] SIMPLEREG_TEMPLATES = new String[] {
            "models/regression/regDeclare.drlt",
            "models/regression/regCommon.drlt",
            "models/regression/regParams.drlt",
            "models/regression/regEval.drlt",
            "models/regression/regClaxOutput.drlt",
            "models/regression/regNormalization.drlt",
            "models/regression/regDecumulation.drlt",

    };
            
    protected static boolean clusteringLoaded = false;
    protected static final String[] CLUSTERING_TEMPLATES = new String[] {
            "models/clustering/clusteringDeclare.drlt",
            "models/clustering/clusteringInit.drlt",
            "models/clustering/clusteringEvalDistance.drlt",
            "models/clustering/clusteringEvalSimilarity.drlt",
            "models/clustering/clusteringMatrixCompare.drlt"
    };

    protected static boolean treeLoaded = false;
    protected static final String[] TREE_TEMPLATES = new String[] {
            "models/tree/treeDeclare.drlt",
            "models/tree/treeCommon.drlt",
            "models/tree/treeInputDeclare.drlt",
            "models/tree/treeInit.drlt",
            "models/tree/treeAggregateEval.drlt",
            "models/tree/treeDefaultEval.drlt",
            "models/tree/treeEval.drlt",
            "models/tree/treeIOBinding.drlt",
            "models/tree/treeMissHandleAggregate.drlt",
            "models/tree/treeMissHandleWeighted.drlt",
            "models/tree/treeMissHandleLast.drlt",
            "models/tree/treeMissHandleNull.drlt",
            "models/tree/treeMissHandleNone.drlt"
    };

    protected static boolean scorecardLoaded = false;
    protected static final String[] SCORECARD_TEMPLATES = new String[] {
            "models/scorecard/scorecardInit.drlt",
            "models/scorecard/scorecardParamsInit.drlt",
            "models/scorecard/scorecardDeclare.drlt",
            "models/scorecard/scorecardDataDeclare.drlt",
            "models/scorecard/scorecardPartialScore.drlt",
            "models/scorecard/scorecardScoring.drlt",
            "models/scorecard/scorecardOutputGeneration.drlt",
            "models/scorecard/scorecardOutputRankCode.drlt"
    };



    protected static final String RESOURCE_PATH = BASE_PACK;
    protected static final String TEMPLATE_PATH = "/" + RESOURCE_PATH + "/templates/";



    private static TemplateRegistry registry;
    private static KnowledgeBuilder kBuilder;
    private static KnowledgeBase visitor;

    private static List<KnowledgeBuilderResult> visitorBuildResults = new ArrayList<KnowledgeBuilderResult>();
    private List<KnowledgeBuilderResult> results;

    private PMML4Helper helper;


    public PMML4Compiler() {
        super();
        this.results = new ArrayList<KnowledgeBuilderResult>();
        helper = new PMML4Helper();
            helper.setPack( "org.drools.pmml.pmml_4_1.test" );
    }



    private static void initVisitor( PMML pmml ) {
        RuleBaseConfiguration conf = new RuleBaseConfiguration();
            conf.setEventProcessingMode( EventProcessingOption.STREAM );
            //conf.setConflictResolver(LifoConflictResolver.getInstance());
		visitor = KnowledgeBaseFactory.newKnowledgeBase( conf );

        // TODO before rules can be structured, I need to double-check the incremental rule base assembly
        kBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        if ( visitorRules == false ) {
            kBuilder.add( ResourceFactory.newClassPathResource( VISITOR_RULES ), ResourceType.DRL );
            visitorRules = true;
        }
        if ( compilerRules == false ) {
            kBuilder.add( ResourceFactory.newClassPathResource( COMPILER_RULES ), ResourceType.DRL );
            compilerRules = true;
        }

        if ( kBuilder.hasErrors() ) {
            visitorBuildResults.addAll( kBuilder.getErrors() );
        } else {
            visitor.addKnowledgePackages( kBuilder.getKnowledgePackages() );
        }
    }


    public String generateTheory( PMML pmml ) {
        StringBuilder sb = new StringBuilder();

        try {
            checkBuildingResources( pmml );
        } catch ( IOException e ) {
            this.results.add( new PMMLError( e.getMessage() ) );
            return null;
        }

        StatefulKnowledgeSession visitorSession = visitor.newStatefulKnowledgeSession();

        helper.reset();
        visitorSession.setGlobal( "registry", registry );
            visitorSession.setGlobal( "fld2var", new HashMap() );
            visitorSession.setGlobal( "utils", helper );

        visitorSession.setGlobal( "theory", sb );

        long now = System.currentTimeMillis();
            visitorSession.insert( pmml );
            visitorSession.fireAllRules();
        long delta = System.currentTimeMillis() - now;
//        System.out.println( "PMML compiled in " + delta );

        String modelEvaluatingRules = sb.toString();

        visitorSession.dispose();

        return modelEvaluatingRules;
	}


    
    private static void initRegistry() {
        if ( registry == null ) {
            registry = new SimpleTemplateRegistry();
        }

        if ( ! globalLoaded ) {
            for ( String ntempl : GLOBAL_TEMPLATES ) {
                prepareTemplate( ntempl );
            }
            globalLoaded = true;
        }

        if ( ! transformationLoaded ) {
            for ( String ntempl : TRANSFORMATION_TEMPLATES ) {
                prepareTemplate( ntempl );
            }
            transformationLoaded = true;
        }

        if ( ! miningLoaded ) {
            for ( String ntempl : MINING_TEMPLATES ) {
                prepareTemplate( ntempl );
            }
            miningLoaded = true;
        }
    }

    private static void checkBuildingResources( PMML pmml ) throws IOException {

        if ( registry == null ) {
            initRegistry();
        }
        if ( visitor == null ) {
            initVisitor( pmml );
        }

        for ( Object o : pmml.getAssociationModelsAndBaselineModelsAndClusteringModels() ) {


            if ( ! neuralLoaded && o instanceof NeuralNetwork ) {
                for ( String ntempl : NEURAL_TEMPLATES ) {
                    prepareTemplate( ntempl );
                }
                neuralLoaded = true;
            }

            if ( ! clusteringLoaded && o instanceof ClusteringModel ) {
                for ( String ntempl : CLUSTERING_TEMPLATES ) {
                    prepareTemplate( ntempl );
                }
                clusteringLoaded = true;
            }

            if ( ! svmLoaded && o instanceof SupportVectorMachineModel ) {
                for ( String ntempl : SVM_TEMPLATES ) {
                    prepareTemplate( ntempl );
                }
                svmLoaded = true;
            }

            if ( ! treeLoaded && o instanceof TreeModel ) {
                for ( String ntempl : TREE_TEMPLATES ) {
                    prepareTemplate( ntempl );
                }
                treeLoaded = true;
            }

            if ( ! simpleRegLoaded && o instanceof RegressionModel ) {
                for ( String ntempl : SIMPLEREG_TEMPLATES ) {
                    prepareTemplate( ntempl );
                }
                simpleRegLoaded = true;
            }

            if ( ! scorecardLoaded && o instanceof Scorecard ) {
                for ( String ntempl : SCORECARD_TEMPLATES ) {
                    prepareTemplate( ntempl );
                }
                scorecardLoaded = true;
            }
        }

    }

    private static void resetVisitor( PMML pmml ) throws IOException {
        visitor = null;
        visitorRules = false;
        compilerRules = false;
        informerRules = false;

        clusteringLoaded = false;
        globalLoaded = false;
        miningLoaded = false;
        neuralLoaded = false;
        scorecardLoaded = false;
        simpleRegLoaded = false;
        svmLoaded = false;
        transformationLoaded = false;
        treeLoaded = false;

        try {
            checkBuildingResources( pmml );
        } catch ( IOException ioe ) {
            visitorBuildResults.clear();
            visitorBuildResults.add( new PMMLError( ioe.getMessage() ) );
        }
    }





    private static void prepareTemplate( String ntempl ) {
        try {
            String path = TEMPLATE_PATH + ntempl;
            Resource res = ResourceFactory.newClassPathResource(path, PMML4Compiler.class);
            if ( res != null ) {
                InputStream stream = res.getInputStream();
                if ( stream != null ) {
                    registry.addNamedTemplate( path.substring(path.lastIndexOf('/') + 1),
                                               TemplateCompiler.compileTemplate(stream));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public String compile(String fileName, Map<String,PackageRegistry> registries) {
        InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream( RESOURCE_PATH + "/" + fileName );
        return compile(stream,registries);
    }


    public String compile(InputStream source, Map<String,PackageRegistry> registries) {
        this.results = new ArrayList<KnowledgeBuilderResult>();
        PMML pmml = loadModel( PMML, source );
        if ( registries != null ) {
            if ( registries.containsKey( helper.getPack() ) ) {
                helper.setResolver( registries.get( helper.getPack() ).getTypeResolver() );
            } else {
                helper.setResolver( null );
            }

        }
        if ( getResults().isEmpty() ) {
            return generateTheory( pmml );
        } else {
            return null;
        }
    }

    public List<KnowledgeBuilderResult> getResults() {
        List<KnowledgeBuilderResult> combinedResults = new ArrayList<KnowledgeBuilderResult>( this.results );
        combinedResults.addAll( visitorBuildResults );
        return combinedResults;
    }

    @Override
    public void clearResults() {
        this.results.clear();
    }


    public void dump( String s, OutputStream ostream ) {
		// write to outstream
		Writer writer = null;
		try {
			writer = new OutputStreamWriter( ostream, "UTF-8" );
			writer.write(s);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
                if (writer != null) {
                    writer.flush();
                }
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}






	/**
	 * Imports a PMML source file, returning a Java descriptor
	 * @param model			the PMML package name (classes derived from a specific schema)
	 * @param source		the name of the PMML resource storing the predictive model
	 * @return				the Java Descriptor of the PMML resource
	 */
	public PMML loadModel( String model, InputStream source ) {
		try {
            SchemaFactory sf = SchemaFactory.newInstance( XMLConstants.W3C_XML_SCHEMA_NS_URI );
            Schema schema = null;
            try {
                schema = sf.newSchema( Thread.currentThread().getContextClassLoader().getResource( SCHEMA_PATH ) );
            } catch ( SAXException e ) {
                e.printStackTrace();
                visitorBuildResults.add( new PMMLWarning( ResourceFactory.newInputStreamResource( source ), "Could not validate PMML document :" + e.getMessage() ) );
            }

            JAXBContext jc = JAXBContext.newInstance( model );
			Unmarshaller unmarshaller = jc.createUnmarshaller();
            if ( schema != null ) {
                unmarshaller.setSchema( schema );
            }

			return (PMML) unmarshaller.unmarshal( source );
		} catch ( JAXBException e ) {
			this.results.add( new PMMLError( e.toString() ) );
			return null;
		}

	}

    public static void dumpModel( PMML model, OutputStream target ) {
        try {
            JAXBContext jc = JAXBContext.newInstance( PMML.class.getPackage().getName() );
            Marshaller marshaller = jc.createMarshaller();
            marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );

            marshaller.marshal( model, target );
        } catch ( JAXBException e ) {
            e.printStackTrace();
        }

    }







}
