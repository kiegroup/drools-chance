package org.drools.semantics.builder.model.hierarchy;


import org.drools.semantics.builder.model.Concept;
import org.drools.semantics.builder.model.OntoModel;
import org.drools.semantics.utils.NameUtils;
import org.semanticweb.owlapi.model.IRI;
import org.w3._2002._07.owl.Thing;

public class VariantModelProcessor implements ModelHierarchyProcessor {

    public void process( OntoModel model ) {

        Concept thing = model.getConcept( Thing.IRI );

        String rootName = NameUtils.capitalize( model.getName() ) + "Root";
        Concept localRoot = new Concept( IRI.create( NameUtils.separatingName( model.getDefaultNamespace() ), rootName ),
                rootName,
                false );
//        localRoot.setChosenSuper( Thing.class.getName() );
        localRoot.setChosenSuperConcept( thing );
        thing.getChosenSubConcepts().add( localRoot );


        for ( Concept con : model.getConcepts() ) {
            for ( String propKey : con.getProperties().keySet() ) {
                if ( ! localRoot.getChosenProperties().containsKey( propKey ) ) {
                    localRoot.getChosenProperties().put( propKey, con.getProperty( propKey ) );
                }
            }
            con.getChosenProperties().clear();
            if ( ! Thing.class.getName().equals( con.getFullyQualifiedName() ) ) {
//                con.setChosenSuper( localRoot.getFullyQualifiedName() );
                con.setChosenSuperConcept( localRoot );
                localRoot.getChosenSubConcepts().add( con );
            } else {
                con.setChosenSuperConcept( con );
//                con.setChosenSuper( con.getFullyQualifiedName() );
            }
        }

        model.addConcept( localRoot );
    }

}
