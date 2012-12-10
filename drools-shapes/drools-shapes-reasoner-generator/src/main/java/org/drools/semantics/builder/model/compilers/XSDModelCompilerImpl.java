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

import org.drools.core.util.StringUtils;
import org.drools.semantics.utils.NameUtils;
import org.drools.semantics.builder.model.*;
import org.drools.semantics.utils.NamespaceUtils;
import org.jdom.Element;
import org.jdom.Namespace;
import org.w3._2002._07.owl.Thing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class XSDModelCompilerImpl extends ModelCompilerImpl implements XSDModelCompiler {

    protected Map<String,Map<String,PropertyRelation>> propCache = new HashMap<String, Map<String, PropertyRelation>>();

    protected Map<String,String> reverseNamespaces = new HashMap<String,String>();

    private XSDSchemaMode schemaMode = XSDSchemaMode.JAXB;

    private boolean transientPropertiesEnabled;
    private boolean useImplementation;


    public boolean isTransientPropertiesEnabled() {
        return transientPropertiesEnabled;
    }

    public void setTransientPropertiesEnabled(boolean transientPropertiesEnabled) {
        this.transientPropertiesEnabled = transientPropertiesEnabled;
    }

    public boolean isUseImplementation() {
        return useImplementation;
    }

    public void setUseImplementation(boolean useImplementation) {
        this.useImplementation = useImplementation;
    }

    public void setSchemaMode(XSDSchemaMode mode) {
        this.schemaMode = mode;
    }

    public void setModel( OntoModel model ) {
        this.model = (CompiledOntoModel) ModelFactory.newModel( getCompileTarget(), model );


        reverseNamespaces.clear();
        ((XSDModel) getModel()).setNamespace("tns", model.getDefaultNamespace());
    }


    public void compile( Concept con, Object context, Map<String, Object> params ) {

        String name = con.getName().substring(con.getName().lastIndexOf(".") + 1);

//        if ( "Thing".equals( con.getName() ) && NamespaceUtils.compareNamespaces( "http://www.w3.org/2002/07/owl", con.getNamespace() ) ) {
//            return;
//        }
        if ( Thing.IRI.equals( con.getIri() ) ) {
            return;
        }

        Element element = new Element( "element", ((XSDModel) getModel()).getNamespace("xsd") );
            element.setAttribute( "name", isUseImplementation() ? name + "Impl" : name );
            element.setAttribute( "type", mapNamespace( con.getNamespace() ) + name );
        getModel().addTrait( name, element );


        Element el = buildType( con, params, isTransientPropertiesEnabled() );
        getModel().addTrait( name, el );
//        if ( ((List) params.get( "keys" )).size() > 0 ) {
//            element.addContent( buildKeys( params ) );
//        }



//        switch( model.getMode() ) {
//            case FLAT:
//                getModel().addTrait( name, buildTypeAsFlat( con, params, isTransientPropertiesEnabled() ) );
//                break;
//            case HIERARCHY:
//                getModel().addTrait( name, buildTypeAsHierarchy( con, params, isTransientPropertiesEnabled() ) );
//                break;
//            case VARIANT:
//                getModel().addTrait( name, buildTypeAsLevelled( con, params, isTransientPropertiesEnabled() ) );
//                break;
//        }
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



    private Element buildProperties( Concept con, Map<String, PropertyRelation> props, Element root, boolean includeTransient, boolean excludeInherited ) {

        Namespace xsdNs = ( (XSDModel) getModel() ).getNamespace( "xsd" );
        String name = con.getName();

        propCache.put( name, props );

        Element seq = isUseImplementation() ?
                new Element( "sequence", xsdNs )
                : new Element( "sequence", xsdNs );


        if ( isUseImplementation() ) {
            Element prop;

            prop = new Element( "element", xsdNs );
            prop.setAttribute( "name", "dyEntryType" );
            prop.setAttribute( "type", "xsd:string" );
            prop.setAttribute( "minOccurs", "0" );
            prop.setAttribute( "maxOccurs", "1" );
            seq.addContent( prop );
        }

        if ( isUseImplementation() ) {
            Element prop;

            prop = new Element( "element", xsdNs );
            prop.setAttribute( "name", "dyReference" );
            prop.setAttribute( "type", "xsd:boolean" );
            prop.setAttribute( "minOccurs", "1" );
            prop.setAttribute( "maxOccurs", "1" );
            seq.addContent( prop );
        }

        if ( isUseImplementation() ) {
            Element key = new Element( "element", xsdNs );

            key.setAttribute( "name", "dyEntryId" );
            key.setAttribute( "type", "xsd:string"  );
            key.setAttribute( "minOccurs", "1" );
            key.setAttribute( "maxOccurs", "1" );
            seq.addContent( key );
        }


        root.addContent( seq );

        for ( String propKey : props.keySet() ) {
            PropertyRelation rel = props.get( propKey );
            Concept tgt = rel.getTarget();

            if ( ( rel.isTransient() && ! includeTransient ) || ( rel.isInherited() && excludeInherited )) {
                continue;
            }


            Element prop = new Element( "element", xsdNs );
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

        return seq;
    }

    private String map( Concept tgt ) {
        if ( tgt == null ) {
            return "xsd:anyType";
        }

        String name = tgt.getName().substring( tgt.getName().lastIndexOf(".") + 1 );
        String prefix = "tns";

        if ( tgt.isPrimitive() ) {
            return tgt.getName();
        }

        return mapNamespace( tgt.getNamespace() ) + name ;
    }

    private String mapNamespace( String namespace ) {
        namespace = NamespaceUtils.removeLastSeparator( namespace );
        String prefix;
        if ( StringUtils.isEmpty( namespace ) ) {
            prefix = "tns";
        } else if ( NamespaceUtils.compareNamespaces( model.getDefaultNamespace(), namespace ) ) {
            prefix = "tns";
        } else if ( reverseNamespaces.containsKey( namespace ) ) {
            prefix = reverseNamespaces.get( namespace );
        } else if ( NamespaceUtils.isKnownSchema(namespace) ) {
            prefix = NamespaceUtils.getPrefix( namespace );
            reverseNamespaces.put( namespace, prefix );
            ((XSDModel)model).setNamespace( prefix, namespace );
        } else {
            prefix = "ns" + ( reverseNamespaces.size() + 1 );
            reverseNamespaces.put( namespace, prefix );
            ((XSDModel)model).setNamespace( prefix, namespace );
        }
        return prefix + ":";
    }




    private Element buildType( Concept con, Map<String, Object> params, boolean includeTransient ) {
        Namespace xsdNs = ( (XSDModel) getModel() ).getNamespace( "xsd" );
        String name = con.getName();
        //        name = isUseImplementation() ? name + "Impl" : name;

        Element type = new Element( "complexType",xsdNs );
        type.setAttribute( "name", name );

        Concept sup = con.getChosenSuperConcept();

        Element complex = new Element( "complexContent", xsdNs );

        Element ext = new Element( "extension", xsdNs );
        ext.setAttribute( "base", mapNamespace( sup.getNamespace() ) + sup.getName() );

        buildProperties( con, (Map<String, PropertyRelation>) params.get( "implProperties" ), ext, includeTransient, true );

        complex.setContent( ext );
        type.setContent( complex );

        return type;
    }




    private Element buildTypeAsHierarchy( Concept con, Map<String, Object> params, boolean includeTransient ) {
        XSDModel xmodel = (XSDModel) getModel();
        String name = con.getName().substring( con.getName().lastIndexOf(".") + 1 );
//        name = isUseImplementation() ? name + "Impl" : name;

        Element type = new Element( "complexType", xmodel.getNamespace( "xsd" ) );
        type.setAttribute( "name", name );

        /* removed this since anonymous classes are need for "skolem" instantiations */
        //        if ( params.containsKey( "abstract" ) ) {
        //            type.setAttribute( "abstract", "true" );
        //        }
        Set<Concept> supers = (Set<Concept>) params.get( "superConcepts" );
        if ( supers.size() > 1 ) {
            System.err.println( " Cannot build a hierarchy with more than 1 ancestor for " + name + ", found " + supers );
        }

        if ( ! supers.isEmpty() ) {
            Concept sup = supers.iterator().next();
            con.setChosenSuper( sup.getFullyQualifiedName() );
            Element complex = new Element( "complexContent", xmodel.getNamespace( "xsd" ) );

            Element ext = new Element("extension", xmodel.getNamespace( "xsd" ) );
            ext.setAttribute("base", mapNamespace( sup.getNamespace() ) + sup.getName() );

            buildProperties( con, (Map<String, PropertyRelation>) params.get( "properties" ), ext, includeTransient, true );

            complex.setContent( ext );
            type.setContent( complex );
        } else {
            con.setChosenSuper( "org.w3._2002._07.owl.Thing" );
            buildProperties( con, (Map<String, PropertyRelation>) params.get( "properties" ), type, includeTransient, true );
        }

        return type;
    }


    private Element buildTypeAsLevelled( Concept con, Map<String, Object> params, boolean includeTransient ) {
        XSDModel xmodel = (XSDModel) getModel();
        String name = con.getName().substring( con.getName().lastIndexOf(".") + 1 );
//            name = isUseImplementation() ? name + "Impl" : name;


        Element type = new Element("complexType", xmodel.getNamespace( "xsd" ) );
        type.setAttribute( "name", name );

        Set<Concept> supers = (Set<Concept>) params.get( "superConcepts");
        Concept chosenSuper = null;

        if ( supers.size() == 0 ) {

            Map<String, PropertyRelation> props = new HashMap<String, PropertyRelation>( ( Map<String, PropertyRelation>) params.get( "properties" ) );
            props.putAll( ( Map<String, PropertyRelation>) params.get( "shadowProperties" ) );
            buildProperties( con, props, type, includeTransient, false );


            System.err.println( "Concept " + name + ", had no supers" );

            return type;
        } else {
//            if ( con.isResolved() && con.getResolvedAs().equals( Concept.Resolution.CLASS ) ) {
//                chosenSuper = null;
//            } else
            if ( supers.size() == 1) {
                chosenSuper = supers.iterator().next();
            } else {
                int bestScore = 0;
                for ( Concept sup : supers ) {
//                    System.err.println( "Scoring " + sup.getName() + " <" + sup.getProperties().size() + " | " + sup.getShadowProperties().size() +">" );
                    int score = sup.getProperties().keySet().size() + sup.getChosenProperties().keySet().size();

                    if ( score > bestScore ) {
                        bestScore = score;
                        chosenSuper = sup;
                    }
                }
            }

//            System.err.println( "FOR concept " + name + ", the best super was " + chosenSuper + " among " + supers );

            con.setChosenSuper( chosenSuper != null ? chosenSuper.getFullyQualifiedName() : null );


            Element complex = new Element( "complexContent", xmodel.getNamespace( "xsd" ) );

            Element ext = new Element("extension", xmodel.getNamespace( "xsd" ) );
            ext.setAttribute("base", mapNamespace( chosenSuper.getNamespace() ) + chosenSuper.getName() );

            Map<String, PropertyRelation> props = new HashMap<String, PropertyRelation>( ( Map<String, PropertyRelation>) params.get( "properties" ) );

            props.putAll( ( Map<String, PropertyRelation>) params.get( "shadowProperties" ) );

            for ( String propKey : chosenSuper.getProperties().keySet() ) {
                props.remove( propKey );
            }
            for ( String propKey : chosenSuper.getChosenProperties().keySet() ) {
                props.remove( propKey );
            }

            buildProperties( con, props, ext, includeTransient, false );

            complex.setContent( ext );
            type.setContent( complex );

            return type;
        }


    }





    private Element buildTypeAsFlat( Concept con, Map<String, Object> params, boolean includeTransient ) {
        XSDModel xmodel = (XSDModel) getModel();
        String name = con.getName().substring(con.getName().lastIndexOf(".") + 1);
//            name = isUseImplementation() ? name + "Impl" : name;

        Element type = new Element("complexType", xmodel.getNamespace( "xsd" ) );
        type.setAttribute( "name", name );

        /* removed this since anonymous classes are need for "skolem" instantiations */
        //        if ( params.containsKey( "abstract" ) ) {
        //            type.setAttribute( "abstract", "true" );
        //        }
        //        Set<Concept> supers = (Set<Concept>) params.get( "superConcepts" );

        Map<String, PropertyRelation> props = new HashMap<String, PropertyRelation>( ( Map<String, PropertyRelation>) params.get( "properties" ) );
        props.putAll( ( Map<String, PropertyRelation>) params.get( "shadowProperties" ) );


        buildProperties( con, props, type, includeTransient, false );

        return type;
    }


    public ModelFactory.CompileTarget getCompileTarget() {
        return ModelFactory.CompileTarget.XSD;
    }


}
