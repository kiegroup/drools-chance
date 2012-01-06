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

package org.drools.semantics.builder.model;

import java.util.List;
import java.util.Set;

public interface OntoModel {

    public String getPackage();
        
    public void setPackage( String pack );

    public String getName();
        
    public void setName( String name );
    
    

    public List<Concept> getConcepts();

    public Concept getConcept( String id );

    public void addConcept( Concept con );

    public Concept removeConcept( Concept con );




    public Set<SubConceptOf> getSubConcepts();

    public void addSubConceptOf( SubConceptOf sub );

    public SubConceptOf getSubConceptOf( String sub, String sup );

    public boolean removeSubConceptOf( SubConceptOf sub );



    public Set<PropertyRelation> getProperties();

    public void addProperty( PropertyRelation rel );

    public PropertyRelation removeProperty( PropertyRelation rel );

    public PropertyRelation getProperty( String iri );


    public void sort();
    
    public void flatten();

    public void elevate();

    public boolean isFlat();


}
