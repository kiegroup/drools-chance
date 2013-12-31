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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class XSDModelCompilerImpl extends ModelCompilerImpl implements XSDModelCompiler {

    protected Map<String,Map<String,PropertyRelation>> propCache = new HashMap<String, Map<String, PropertyRelation>>();


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

    public void setModel( OntoModel model ) {
        this.model = (CompiledOntoModel) ModelFactory.newModel( getCompileTarget(), model );

        ((XSDModel) getModel()).setNamespace("tns", model.getDefaultNamespace());
    }


    public void compile( Concept con, Object context, Map<String, Object> params ) {

        String name = con.getName().substring( con.getName().lastIndexOf( "." ) + 1 );

//        if ( "Thing".equals( con.getName() ) && NamespaceUtils.compareNamespaces( "http://www.w3.org/2002/07/owl", con.getNamespace() ) ) {
//            return;
//        }
        if ( Thing.IRI.equals( con.getIri() ) ) {
            return;
        }


        String effectiveName = isUseImplementation() ? name + "Impl" : name;
        String effectiveType = ( (XSDModel) getModel() ).mapNamespaceToPrefix( con.getNamespace() ) + ":" + name;
        String namespace = NamespaceUtils.removeLastSeparator( con.getNamespace() );

        Element element = new Element( "element", ((XSDModel) getModel()).getNamespace("xsd") );
        element.setAttribute( "name", effectiveName );
        element.setAttribute( "type", effectiveType );

        Element el = buildType( con, params, isTransientPropertiesEnabled() );

        Set<String> dependencies = new HashSet<String>();
        dependencies.add( NamespaceUtils.removeLastSeparator( con.getChosenSuperConcept().getNamespace() ) );
        for ( PropertyRelation prop : con.getEffectiveBaseProperties() ) {
            if ( ! prop.getTarget().isPrimitive() ) {
                dependencies.add( NamespaceUtils.removeLastSeparator( prop.getTarget().getNamespace() ) );
            }
        }
        if ( dependencies.contains( namespace ) ) {
            dependencies.remove( namespace );
        }

        XSDModelImpl.XSDTypeDescr descr = new XSDModelImpl.XSDTypeDescr( name,
                namespace,
                effectiveName,
                effectiveType,
                element,
                el,
                dependencies );

        if ( model.getMode() != OntoModel.Mode.FLAT && model.getMode() != OntoModel.Mode.NONE && model.getMode() != OntoModel.Mode.DATABASE ) {
            getModel().addTrait( name, descr );
        } else {
            if ( ! con.isAbstrakt() ) {
                getModel().addTrait( name, descr );
            }
        }





//
//            getModel().addTrait( name, el );

//        if ( ((List) params.get( "keys" )).size() > 0 ) {
//            element.addContent( buildKeys( params ) );
//        }



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
        String iri = con.getIri();

        propCache.put( iri, props );

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


            if ( ! tgt.isPrimitive() ) {
                String relTargetNamespace = NamespaceUtils.removeLastSeparator( tgt.getNamespace() );
                ((XSDModelImpl) getModel()).addImport( ((XSDModelImpl) getModel()).getXSDSchema(), Namespace.getNamespace( relTargetNamespace ) );
            }


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

        return ( (XSDModel) getModel() ).mapNamespaceToPrefix( tgt.getNamespace() ) + ":" + name ;
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
        ext.setAttribute( "base", ( (XSDModel) getModel() ).mapNamespaceToPrefix( sup.getNamespace() ) + ":" + sup.getName() );

        if ( ! con.isResolved() ) {
            buildProperties( con, (Map<String, PropertyRelation>) params.get( "implProperties" ), ext, includeTransient, true );
        }

        complex.setContent( ext );
        type.setContent( complex );

        return type;
    }




    public ModelFactory.CompileTarget getCompileTarget() {
        return ModelFactory.CompileTarget.XSD;
    }


}
