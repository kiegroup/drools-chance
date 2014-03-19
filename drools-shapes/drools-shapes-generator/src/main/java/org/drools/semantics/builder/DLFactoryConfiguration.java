package org.drools.semantics.builder;

import org.drools.semantics.builder.model.OntoModel;
import org.drools.semantics.builder.model.inference.ModelInferenceStrategy;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.util.InferredAxiomGenerator;
import org.semanticweb.owlapi.util.InferredClassAssertionAxiomGenerator;
import org.semanticweb.owlapi.util.InferredDataPropertyCharacteristicAxiomGenerator;
import org.semanticweb.owlapi.util.InferredEquivalentClassAxiomGenerator;
import org.semanticweb.owlapi.util.InferredEquivalentDataPropertiesAxiomGenerator;
import org.semanticweb.owlapi.util.InferredEquivalentObjectPropertyAxiomGenerator;
import org.semanticweb.owlapi.util.InferredInverseObjectPropertiesAxiomGenerator;
import org.semanticweb.owlapi.util.InferredObjectPropertyCharacteristicAxiomGenerator;
import org.semanticweb.owlapi.util.InferredPropertyAssertionGenerator;
import org.semanticweb.owlapi.util.InferredSubClassAxiomGenerator;
import org.semanticweb.owlapi.util.InferredSubDataPropertyAxiomGenerator;
import org.semanticweb.owlapi.util.InferredSubObjectPropertyAxiomGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DLFactoryConfiguration {

    public static final List<InferredAxiomGenerator<? extends OWLAxiom>> liteAxiomGenerators = Collections.unmodifiableList(
            new ArrayList<InferredAxiomGenerator<? extends OWLAxiom>>(
                    Arrays.asList(
                            new InferredClassAssertionAxiomGenerator(),
                            new InferredDataPropertyCharacteristicAxiomGenerator(),
                            new InferredEquivalentClassAxiomGenerator(),
//                            new InferredEquivalentDataPropertiesAxiomGenerator(),
                            new InferredEquivalentObjectPropertyAxiomGenerator(),
                            new InferredInverseObjectPropertiesAxiomGenerator(),
//                            new InferredObjectPropertyCharacteristicAxiomGenerator(),
//                            new InferredPropertyAssertionGenerator(),
                            new InferredSubClassAxiomGenerator(),
//                            new InferredSubDataPropertyAxiomGenerator(),
                            new InferredSubObjectPropertyAxiomGenerator()
                    ) ) );


    public static final List<InferredAxiomGenerator<? extends OWLAxiom>> defaultAxiomGenerators = Collections.unmodifiableList(
            new ArrayList<InferredAxiomGenerator<? extends OWLAxiom>>(
                    Arrays.asList(
                            new InferredClassAssertionAxiomGenerator(),
                            new InferredDataPropertyCharacteristicAxiomGenerator(),
                            new InferredEquivalentClassAxiomGenerator(),
//                            new InferredEquivalentDataPropertiesAxiomGenerator(),
                            new InferredEquivalentObjectPropertyAxiomGenerator(),
                            new InferredInverseObjectPropertiesAxiomGenerator(),
//                            new InferredObjectPropertyCharacteristicAxiomGenerator(),
                            new InferredPropertyAssertionGenerator(),
                            new InferredSubClassAxiomGenerator(),
                            new InferredSubDataPropertyAxiomGenerator(),
                            new InferredSubObjectPropertyAxiomGenerator()
                    ) ) );

    public static final List<InferredAxiomGenerator<? extends OWLAxiom>> fullAxiomGenerators = Collections.unmodifiableList(
            new ArrayList<InferredAxiomGenerator<? extends OWLAxiom>>(
                    Arrays.asList(
                            new InferredClassAssertionAxiomGenerator(),
                            new InferredDataPropertyCharacteristicAxiomGenerator(),
                            new InferredEquivalentClassAxiomGenerator(),
                            new InferredEquivalentDataPropertiesAxiomGenerator(),
                            new InferredEquivalentObjectPropertyAxiomGenerator(),
                            new InferredInverseObjectPropertiesAxiomGenerator(),
                            new InferredObjectPropertyCharacteristicAxiomGenerator(),
                            new InferredPropertyAssertionGenerator(),
                            new InferredSubClassAxiomGenerator(),
                            new InferredSubDataPropertyAxiomGenerator(),
                            new InferredSubObjectPropertyAxiomGenerator()
                    ) ) );

    public static final List<InferredAxiomGenerator<? extends OWLAxiom>> minimalAxiomGenerators = Collections.unmodifiableList(
            new ArrayList<InferredAxiomGenerator<? extends OWLAxiom>>(
                    Arrays.asList(
//                            new InferredClassAssertionAxiomGenerator(),
//                            new InferredDataPropertyCharacteristicAxiomGenerator(),
//                            new InferredEquivalentClassAxiomGenerator(),
//                            new InferredEquivalentDataPropertiesAxiomGenerator(),
//                            new InferredEquivalentObjectPropertyAxiomGenerator(),
//                            new InferredInverseObjectPropertiesAxiomGenerator(),
//                            new InferredObjectPropertyCharacteristicAxiomGenerator(),
//                            new InferredPropertyAssertionGenerator(),
                            new InferredSubClassAxiomGenerator()
//                            new InferredSubDataPropertyAxiomGenerator(),
//                            new InferredSubObjectPropertyAxiomGenerator()
                    ) ) );



    public static final List<ModelInferenceStrategy.InferenceTask> defaultInferenceTasks = Collections.unmodifiableList(
            new ArrayList<ModelInferenceStrategy.InferenceTask>(
                    Arrays.asList(
                            ModelInferenceStrategy.InferenceTask.COMMON,
//                        ModelInferenceStrategy.InferenceTask.TABLEAU,
//                        ModelInferenceStrategy.InferenceTask.CLASS_LATTICE_BUILD_AND_PRUNE,
//                        ModelInferenceStrategy.InferenceTask.PROPERTY_MATCH
                            ModelInferenceStrategy.InferenceTask.CLASS_LATTICE_PRUNE
                    ) ) );


    private OntoModel.Mode mode;

    private List<InferredAxiomGenerator<? extends OWLAxiom>> axiomGens = defaultAxiomGenerators;

    private List<ModelInferenceStrategy.InferenceTask> tasks = defaultInferenceTasks;

    private boolean disableFullReasoner = false;


    public OntoModel.Mode getMode() {
        return mode;
    }

    public void setMode( OntoModel.Mode mode ) {
        this.mode = mode;
    }

    public List<InferredAxiomGenerator<? extends OWLAxiom>> getAxiomGens() {
        return axiomGens;
    }

    public void setAxiomGens( List<InferredAxiomGenerator<? extends OWLAxiom>> axiomGens ) {
        this.axiomGens = axiomGens;
    }

    public List<ModelInferenceStrategy.InferenceTask> getTasks() {
        return tasks;
    }

    public void setTasks( List<ModelInferenceStrategy.InferenceTask> tasks ) {
        this.tasks = tasks;
    }

    public boolean isDisableFullReasoner() {
        return disableFullReasoner;
    }

    public void setDisableFullReasoner( boolean disableFullReasoner ) {
        this.disableFullReasoner = disableFullReasoner;
    }


    public static DLFactoryConfiguration newConfiguration( OntoModel.Mode mode ) {
        DLFactoryConfiguration conf = new DLFactoryConfiguration();
        conf.setMode( mode );
        return conf;
    }


    public static DLFactoryConfiguration newConfiguration( OntoModel.Mode mode, List<InferredAxiomGenerator<? extends OWLAxiom>> axiomGenerators ) {
        DLFactoryConfiguration conf = new DLFactoryConfiguration();
        conf.setMode( mode );
        conf.setAxiomGens( axiomGenerators );
        return conf;

    }
}
