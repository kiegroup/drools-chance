package org.drools.semantics.builder.model.hierarchy;


import org.drools.semantics.builder.model.Concept;
import org.drools.semantics.builder.model.OntoModel;
import org.drools.semantics.builder.model.PropertyRelation;
import org.w3._2002._07.owl.Thing;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FlatModelProcessor implements ModelHierarchyProcessor {

    public void process( OntoModel model ) {
        Concept thing = model.getConcept( Thing.IRI );
        for ( Concept con : model.getConcepts() ) {

            con.setChosenProperties(new HashMap(con.getProperties()));

            Map<String, PropertyRelation> baseProps = con.getChosenProperties();
            Set<Concept> superConcepts = con.getSuperConcepts();
            for ( Concept sup : superConcepts ) {
                Map<String,PropertyRelation> inheritedProperties = sup.getChosenProperties();
                for ( String propKey : inheritedProperties.keySet() ) {
                    if ( ! baseProps.containsKey( propKey ) ) {
                        baseProps.put( propKey, inheritedProperties.get( propKey ) );
                    }
                }
            }
            con.setShadowed( true );
//            con.setChosenSuper( Thing.class.getName() );
            con.setChosenSuperConcept( thing );
            thing.getChosenSubConcepts().add( con );
        }
    }

}
