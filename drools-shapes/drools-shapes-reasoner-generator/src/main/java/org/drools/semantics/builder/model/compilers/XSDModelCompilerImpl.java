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
import java.util.List;
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

        Element element = new Element( "element", ((XSDModel) getModel()).getNamespace("xsd") );
        element.setAttribute( "name", name );
        element.setAttribute( "type", "tns:"+name );

//        if ( ((List) params.get( "keys" )).size() > 0 ) {
//            element.addContent( buildKeys( params ) );
//        }

        getModel().addTrait(name, element);

        if ( getCurrentMode().equals( Mode.FLAT ) ) {
            getModel().flatten();
            getModel().addTrait(name, buildTypeAsFlat( name, params ) );
        } else {
            getModel().elevate();
            getModel().addTrait(name, buildAsHierarchy( name, params ) );
        }

//
    }

    private Element buildKeys(Map<String, Object> params) {
        XSDModel xmodel = (XSDModel) getModel();
        Element keys = new Element( "key", xmodel.getNamespace( "xsd" ) );
        keys.setAttribute( "name", params.get( "name" ) + "Key" );

        Element selector = new Element( "selector", xmodel.getNamespace( "xsd" ) );
        selector.setAttribute( "xpath", "" + params.get( "name" ) );
        keys.addContent( selector );

        List<String> keyProps = (List<String>) params.get( "keys" );
        for ( String k : keyProps ) {
            Element key = new Element( "field", xmodel.getNamespace( "xsd" ) );
            key.setAttribute( "xpath", "@" + k );
            keys.addContent( key );
        }

        return keys;

    }



    private Element buildProperties( String name, Map<String, PropertyRelation> props, Element root ) {
        XSDModel xmodel = (XSDModel) getModel();
        propCache.put( name, props );

        Element seq = new Element( "sequence", xmodel.getNamespace( "xsd" ) );

        if ( name.equals( "Thing") ) {
            Element key = new Element( "element", xmodel.getNamespace( "xsd" ) );
            key.setAttribute( "name", "universalId" );
            key.setAttribute( "type", "xsd:ID"  );
            seq.addContent( key );
        }

        root.addContent( seq );
        for ( String propKey : props.keySet() ) {
            PropertyRelation rel = props.get( propKey );
            Concept tgt = rel.getTarget();

            if ( rel.isTransient() ) {
                continue;
            }


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
                    prop.setAttribute( "minOccurs", rel.getMinCard() == 0 ? "1" : rel.getMinCard().toString() );
                    prop.setAttribute( "maxOccurs", "unbounded" );
                    prop.setAttribute( "nillable", "false" );
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
//        if ( "Thing".equals(tgt.getName() ) ) return "xsd:anyType";
        return tgt.isPrimitive() ? tgt.getName() : "tns:" + tgt.getName();
//        return tgt.isPrimitive() ? tgt.getName() : "xsd:any";
    }




    private Element buildAsHierarchy(String name, Map<String, Object> params) {
        XSDModel xmodel = (XSDModel) getModel();

        Element type = new Element( "complexType", xmodel.getNamespace( "xsd" ) );
        type.setAttribute( "name", name );

        /* removed this since anonymous classes are need for "skolem" instantiations */
        //        if ( params.containsKey( "abstract" ) ) {
        //            type.setAttribute( "abstract", "true" );
        //        }
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


    private Element buildTypeAsFlat( String name, Map<String, Object> params ) {
        XSDModel xmodel = (XSDModel) getModel();

        Element type = new Element("complexType", xmodel.getNamespace( "xsd" ) );
        type.setAttribute( "name", name );

        Set<Concept> supers = (Set<Concept>) params.get( "superConcepts");
        Concept chosenSuper = null;

        if ( supers.size() == 0 ) {

            Map<String, PropertyRelation> props = new HashMap<String, PropertyRelation>( ( Map<String, PropertyRelation>) params.get( "properties" ) );
            props.putAll( ( Map<String, PropertyRelation>) params.get( "shadowProperties" ) );
            buildProperties( name, props, type );


            System.err.println( "Concept " + name + ", had no supers" );

            return type;
        } else {
            if ( supers.size() == 1) {
                chosenSuper = supers.iterator().next();
            } else {
                int bestScore = 0;
                for ( Concept sup : supers ) {
                    System.err.println( "Scoring " + sup.getName() + " <" + sup.getProperties().size() + " | " + sup.getShadowProperties().size() +">" );
                    int score = sup.getProperties().keySet().size() + sup.getShadowProperties().keySet().size();

                    if ( score > bestScore ) {
                        bestScore = score;
                        chosenSuper = sup;
                    }
                }
            }

            System.err.println( "FOR concept " + name + ", the best super was " + chosenSuper + " among " + supers );
            Concept con = (Concept) params.get( "concept" );

            con.setChosenSuper( chosenSuper != null ? chosenSuper.getName() : null );


            Element complex = new Element( "complexContent", xmodel.getNamespace( "xsd" ) );

            Element ext = new Element("extension", xmodel.getNamespace( "xsd" ) );
            ext.setAttribute("base", "tns:"+chosenSuper.getName() );

            Map<String, PropertyRelation> props = new HashMap<String, PropertyRelation>( ( Map<String, PropertyRelation>) params.get( "properties" ) );

            props.putAll( ( Map<String, PropertyRelation>) params.get( "shadowProperties" ) );

            for ( String propKey : chosenSuper.getProperties().keySet() ) {
                props.remove( propKey );
            }
            for ( String propKey : chosenSuper.getShadowProperties().keySet() ) {
                props.remove( propKey );
            }

            buildProperties( name, props, ext );

            complex.setContent( ext );
            type.setContent( complex );

            return type;
        }


    }





//    private Element buildTypeAsFlat( String name, Map<String, Object> params ) {
//        XSDModel xmodel = (XSDModel) getModel();
//
//        Element type = new Element("complexType", xmodel.getNamespace( "xsd" ) );
//        type.setAttribute( "name", name );
//
//        /* removed this since anonymous classes are need for "skolem" instantiations */
//        //        if ( params.containsKey( "abstract" ) ) {
//        //            type.setAttribute( "abstract", "true" );
//        //        }
//        //        Set<Concept> supers = (Set<Concept>) params.get( "superConcepts" );
//
//        Map<String, PropertyRelation> props = new HashMap<String, PropertyRelation>( ( Map<String, PropertyRelation>) params.get( "properties" ) );
//        props.putAll( ( Map<String, PropertyRelation>) params.get( "shadowProperties" ) );
//
//
//        buildProperties( name, props, type );
//
//        return type;
//    }






}
