package org.drools.semantics.builder.model.hierarchy;


import org.drools.semantics.builder.model.Concept;
import org.drools.semantics.builder.model.OntoModel;
import org.w3._2002._07.owl.Thing;

public class HierarchicalModelProcessor implements ModelHierarchyProcessor {

    public void process( OntoModel model ) {
        Concept thing = model.getConcept( Thing.IRI );
        for ( Concept con : model.getConcepts() ) {
            switch ( con.getSuperConcepts().size() ) {
                case 0 :
//                    con.setChosenSuper( Thing.class.getName() );
                    con.setChosenSuperConcept( thing );
                    thing.getChosenSubConcepts().add( con );
                    break;
                case 1 :
                    Concept sup = con.getSuperConcepts().iterator().next();
//                    con.setChosenSuper( sup.getFullyQualifiedName() );
                    con.setChosenSuperConcept( sup );
                    sup.getChosenSubConcepts().add( con );
                    break;
                default :
                    throw new UnsupportedOperationException( "FATAL : Trying to create a hierarchy, but concept " + con.getIri() + " has more than one parent " + con.getSuperConcepts() );
            }
            con.setChosenProperties( con.getProperties() );
            con.getImplementingCon().getNeededProperties().addAll( con.getImplementingCon().getAvailablePropertiesVirtual().keySet() );
        }
    }

}
