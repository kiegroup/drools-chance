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

import org.drools.semantics.builder.DLUtils;
import org.drools.semantics.builder.model.*;
import org.jdom.Element;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class XSDModelCompilerImpl extends ModelCompilerImpl implements XSDModelCompiler {



    protected Mode mode = Mode.HIERARCHY;

    protected Map<String,Map<String,PropertyRelation>> propCache = new HashMap<String, Map<String, PropertyRelation>>();

    public void setMode( Mode m ) {
        mode = m;
    }

    public Mode getCurrentMode() {
        return mode;
    }


    public void setModel(OntoModel model) {
        this.model = (CompiledOntoModel) ModelFactory.newModel( ModelFactory.CompileTarget.XSD, model );

        ((XSDModel) getModel()).setNamespace( "tns", DLUtils.reverse( model.getPackage() ) );
    }

    public void compile( String name, Object context, Map<String, Object> params ) {
        XSDModel xmodel = (XSDModel) getModel();


        Element element = new Element( "element", xmodel.getNamespace("xsd") );
        element.setAttribute( "name", name );
        element.setAttribute( "type", "tns:"+name );

        getModel().addTrait( name, element );

        if ( getCurrentMode().equals( Mode.FLAT ) ) {
            getModel().flatten();
            getModel().addTrait(name, buildTypeAsFlat( name, params ) );
        } else {
            getModel().elevate();
            getModel().addTrait(name, buildAsHierarchy( name, params ) );
        }
    }

    private Element buildAsHierarchy(String name, Map<String, Object> params) {
        XSDModel xmodel = (XSDModel) getModel();

        Element type = new Element( "complexType", xmodel.getNamespace( "xsd" ) );
        type.setAttribute( "name", name );
        if ( params.containsKey( "abstract" ) ) {
            type.setAttribute( "abstract", "true" );
        }
        Set<Concept> supers = (Set<Concept>) params.get( "superConcepts");
        if ( supers.size() > 1 ) {
            System.err.println( " Cannot build a hierarchy with more than 1 ancestor for " + name + ", found " + supers );
        }

        if ( ! supers.isEmpty() ) {
            Concept sup = supers.iterator().next();
            Element complex = new Element( "complexContent", xmodel.getNamespace( "xsd" ) );

            Element ext = new Element("extension", xmodel.getNamespace( "xsd" ) );
            ext.setAttribute("base", "tns:"+sup.getName() );

            buildProperties( name, (Map<String, PropertyRelation>) params.get( "properties" ), ext );

            complex.setContent( ext );
            type.setContent( complex );
        } else {
            buildProperties( name, (Map<String, PropertyRelation>) params.get( "properties" ), type );
        }

        return type;
    }

    private Element buildProperties( String name, Map<String, PropertyRelation> props, Element root ) {
        XSDModel xmodel = (XSDModel) getModel();
        propCache.put( name, props );

        Element seq = new Element( "sequence", xmodel.getNamespace( "xsd" ) );
        root.addContent( seq );
        for ( String propKey : props.keySet() ) {
            PropertyRelation rel = props.get( propKey );
            Concept tgt = rel.getTarget();

//            if ( rel.isRestricted() ) {
//                continue;
//            }


            if ( tgt.isPrimitive() ) {
                Element prop = new Element( "attribute", xmodel.getNamespace( "xsd" ) );
                prop.setAttribute( "name", rel.getName() );
                prop.setAttribute( "type", map( tgt ) );

                Integer minCard = rel.getMinCard();
                if (minCard == null) {
                    minCard = 0;
                    rel.setMinCard( 0 );
                }
                Integer maxCard = rel.getMaxCard();
                if (maxCard != null && maxCard == 0) {
                    maxCard = null;
                    rel.setMaxCard( null );
                }
                if ( minCard != null && maxCard != null && minCard == 1 && maxCard == 1 ) {
                    prop.setAttribute( "use", "required" );
                    root.addContent( prop );
                } else if ( minCard != null && minCard <= 1 && maxCard != null && maxCard == 1 ) {
                    prop.setAttribute( "use", "optional" );
                    root.addContent( prop );
                } else {
//                        prop.setAttribute( "minOccurs", rel.getMinCard().toString() );
//                        prop.setAttribute( "maxOccurs", rel.getMaxCard() == null ? "unbounded" : rel.getMaxCard().toString() );
                    prop = new Element( "element", xmodel.getNamespace( "xsd" ) );
                    prop.setAttribute( "name", rel.getName() );
                    prop.setAttribute( "type", map( tgt ) );
                    prop.setAttribute( "minOccurs", rel.getMinCard().toString() );
                    prop.setAttribute( "maxOccurs", "unbounded" );
                    seq.addContent( prop );
                }


            } else {
                Element prop = new Element( "element", xmodel.getNamespace( "xsd" ) );
                prop.setAttribute( "name", rel.getName() );
                prop.setAttribute( "type", map( tgt ) );

                Integer minCard = rel.getMinCard();
                if (minCard == null) {
                    minCard = 0;
                    rel.setMinCard( 0 );
                }
                Integer maxCard = rel.getMaxCard();
                if (maxCard != null && maxCard == 0) {
                    maxCard = null;
                    rel.setMaxCard( null );
                }

                prop.setAttribute( "minOccurs", rel.getMinCard().toString() );
                prop.setAttribute( "maxOccurs", rel.getMaxCard() == null ? "unbounded" : rel.getMaxCard().toString() );

                seq.addContent( prop );
            }

        }

        return seq;
    }

    private String map( Concept tgt ) {
        if ( "Thing".equals(tgt.getName() ) ) return "xsd:anyType";
        return tgt.isPrimitive() ? tgt.getName() : "tns:" + tgt.getName();
    }




    private Element buildTypeAsFlat( String name, Map<String, Object> params ) {
        XSDModel xmodel = (XSDModel) getModel();

        Element type = new Element("complexType", xmodel.getNamespace( "xsd" ) );
        type.setAttribute( "name", name );
        if ( params.containsKey( "abstract" ) ) {
            type.setAttribute( "abstract", "true" );
        }
        Set<Concept> supers = (Set<Concept>) params.get( "superConcepts" );
        Map<String, PropertyRelation> props = new HashMap<String, PropertyRelation>( ( Map<String, PropertyRelation>) params.get( "properties" ) );

        if ( ! supers.isEmpty() ) {
            for ( Concept sup : supers ) {
                Map<String, PropertyRelation> inheritedProps = propCache.get( sup.getName() );
                if ( inheritedProps == null ) {
                    throw new RuntimeException( "Error accessing inherited properties : Concept " + name + " built before its ancestor " + sup.getName() + "?" );
                }
                for ( String propKey : inheritedProps.keySet() ) {
                    if ( ! props.containsKey( propKey ) ) {
                        props.put( propKey, inheritedProps.get( propKey ) );
                    }
                }
            }
        }

        buildProperties( name, props, type );

        return type;
    }



}
