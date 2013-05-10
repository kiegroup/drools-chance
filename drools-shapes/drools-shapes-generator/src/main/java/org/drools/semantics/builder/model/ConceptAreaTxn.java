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

package org.drools.semantics.builder.model;

import org.drools.semantics.util.area.Area;
import org.drools.semantics.util.area.AreaNode;
import org.drools.semantics.util.area.AreaTxnImpl;

import java.util.*;

public class ConceptAreaTxn extends AreaTxnImpl<Concept,PropertyRelation> {

    private OntoModel model;

    public ConceptAreaTxn(OntoModel model) {
        this.model = model;
        buildAreas();
    }

    @Override
    public List<Concept> getInElements() {
        return model.getConcepts();
    }

    @Override
    public Set<PropertyRelation> getInKeys() {
        return model.getProperties();
    }

    @Override
    protected Map<Set<PropertyRelation>, Area<Concept,PropertyRelation>> initNodes() {
        Map<Set<PropertyRelation>, Area<Concept,PropertyRelation>> areaNodes = new HashMap<Set<PropertyRelation>, Area<Concept,PropertyRelation>>();
        for ( Concept cct : getInElements() ) {

            if( ! cct.isAnonymous() ) {
                Set<PropertyRelation> hs = new HashSet<PropertyRelation>();
                for ( PropertyRelation p : cct.getEffectiveBaseProperties() ) {
                    if ( ! p.isRestricted() ) {
                        hs.add( p );
                    }
                }

                if( ! areaNodes.containsKey( hs ) ) {
                    //it is a new area so create an empty entry for that
                    areaNodes.put( hs, new ConceptAreaNode(hs));
                }
                //add the concept to the corresponding area
                areaNodes.get( hs ).addElement( cct, cct.getTypeCode() );
            }
        }
        return areaNodes;
    }

    @Override
    protected void buildAreas() {
        super.buildAreas();

        for ( Area<Concept,PropertyRelation> node : getAreas() ) {
            for ( Concept c : node.getElements() ) {
                c.setAreaCode( node.getAreaCode() );
            }
        }
    }
}
