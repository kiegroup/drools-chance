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


import org.drools.factmodel.BuildUtils;
import org.drools.semantics.builder.DLUtils;
import org.drools.semantics.builder.model.*;
import org.mvel2.asm.*;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class JarInterfaceModelCompilerImpl extends JavaInterfaceModelCompilerImpl implements Opcodes, JarInterfaceModelCompiler {


    public void setModel(OntoModel model) {
        this.model = (CompiledOntoModel) ModelFactory.newModel( ModelFactory.CompileTarget.JAR, model );
    }

    public void compile( String name, Object context, Map<String, Object> params ) {
        super.compile( name, context, params );

        ((JarModel) getModel()).addCompiledTrait(name+"__Datatype", this.compile( name+"__Datatype", params));
        ((JarModel) getModel()).addCompiledTrait(name+"__Type", this.compile( name+"__Type", params));

        ((JarModel) getModel()).addCompiledTrait(name, this.compile( name, params));
    }


    private JarModelImpl.Holder compile( String trait, Map<String, Object> params ) {

        ClassWriter cw = new ClassWriter(0);
        FieldVisitor fv;
        MethodVisitor mv;
        AnnotationVisitor av0;

        String pack = ((String)params.get("package")).replace(".","/") + "/";

        if ( trait.endsWith( "__Type" ) ) {

            Set<Concept> sup = ((Set<Concept>) params.get("superConcepts"));
            String[] superTypes = new String[ sup.size() ];
            int j = 0;
            for ( Iterator<Concept> iter = sup.iterator(); iter.hasNext(); ) {
                superTypes[j++] = pack + iter.next().getName() + "__Type";
            }

            cw.visit(V1_5, ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE,
                    pack +  params.get("name") + "__Type",
                    null,
                    "java/lang/Object",
                    superTypes);


            cw.visitEnd();

            return new JarModelImpl.Holder( cw.toByteArray() );



        } else if ( trait.endsWith( "__Datatype" ) ) {
            String[] superTypes = new String[ 1 ];
            superTypes[0] = "com/clarkparsia/empire/SupportsRdfId";

            Map<String, PropertyRelation> props = (Map<String, PropertyRelation>) params.get( "properties" );

            cw.visit(V1_5, ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE,
                    pack +  params.get("name") + "__Datatype",
                    null,
                    "java/lang/Object",
                    superTypes);

            for ( String propKey : props.keySet() ) {
                PropertyRelation rel = props.get( propKey );
                String propName = rel.getName();
                propName = propName.substring(0,1).toUpperCase() + propName.substring(1);
                //String target =  pack + props.get( rel ).getName();
                String target = rel.getTarget().getName();
                boolean isBoolean = target.equalsIgnoreCase("xsd:boolean");
                if ( target.startsWith("xsd:") ) {
                    target = DLUtils.map( target, rel.getMaxCard() == null || rel.getMaxCard() != 1 ).replace(".","/");
                } else {
                    target = pack + target;
                }

                String propType = BuildUtils.getTypeDescriptor( target )+"__Type";
                String genericGetType = null;
                String genericSetType = null;
                if ( rel.getMaxCard() == null || rel.getMaxCard() != 1  ) {
                    genericGetType = "()Ljava/util/List<" + propType + ">;";
                    genericSetType = "(Ljava/util/List<" + propType + ">;)V";
                    propType = "Ljava/util/List;";
                    isBoolean = false;
                }


                String getPrefix = isBoolean ? "is" : "get";
                mv = cw.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, getPrefix + propName, "()" + propType , genericGetType, null);
                mv.visitEnd();

                mv = cw.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "set" + propName, "(" + propType + ")V", genericSetType, null);
                mv.visitEnd();

                System.out.println("*** Just compiled a property " + getPrefix + "\t\n" + propName + " \t\n " + propType + "\t\n" + genericGetType + "\t\n" + genericSetType + "\t\n" +(pack +  params.get("name"))+"\t\n");
            }

            cw.visitEnd();

            return new JarModelImpl.Holder( cw.toByteArray() );
        } else {

            String name = (String) params.get( "name" );
            String[] superTypes = new String[ 2 ];
            superTypes[0] = pack + name + "__Type";
            superTypes[1] = pack + name + "__Datatype";

            cw.visit(V1_5, ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE,
                    pack +  params.get("name"),
                    null,
                    "java/lang/Object",
                    superTypes);

            cw.visitEnd();

            return new JarModelImpl.Holder( cw.toByteArray() );

        }


    }


//    Set<Concept> sup = ((Set<Concept>) params.get("superConcepts"));
//            String[] superTypes = new String[ sup.size() + 1];
//            superTypes[0] = "com/clarkparsia/empire/SupportsRdfId";
//            int j = 1;
//            for ( Iterator<Concept> iter = sup.iterator(); iter.hasNext(); ) {
//                superTypes[j++] = pack + iter.next().getName();
//            }
//
//            Map<String, PropertyRelation> props = (Map<String, PropertyRelation>) params.get( "properties" );
//
//            cw.visit(V1_5, ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE,
//                    pack +  params.get("name"),
//                    null,
//                    "java/lang/Object",
//                    superTypes);
//
//                for ( String propKey : props.keySet() ) {
//                    PropertyRelation rel = props.get( propKey );
//                    String propName = rel.getName();
//                        propName = propName.substring(0,1).toUpperCase() + propName.substring(1);
//                    //String target =  pack + props.get( rel ).getName();
//                    String target = rel.getTarget().getName();
//                    boolean isBoolean = target.equalsIgnoreCase("xsd:boolean");
//                        if ( target.startsWith("xsd:") ) {
//                            target = DLUtils.map( target, rel.getMaxCard() == null || rel.getMaxCard() != 1 ).replace(".","/");
//                        } else {
//                            target = pack + target;
//                        }
//
//                    String propType = BuildUtils.getTypeDescriptor( target );
//                    String genericGetType = null;
//                    String genericSetType = null;
//                    if ( rel.getMaxCard() == null || rel.getMaxCard() != 1  ) {
//                        genericGetType = "()Ljava/util/List<" + propType + ">;";
//                        genericSetType = "(Ljava/util/List<" + propType + ">;)V";
//                        propType = "Ljava/util/List;";
//                        isBoolean = false;
//                    }
//
//
//                    String getPrefix = isBoolean ? "is" : "get";
//                    mv = cw.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, getPrefix + propName, "()" + propType , genericGetType, null);
//                    mv.visitEnd();
//
//                    mv = cw.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "set" + propName, "(" + propType + ")V", genericSetType, null);
//                    mv.visitEnd();
//
//                    System.out.println("*** Just compiled a property " + getPrefix + "\t\n" + propName + " \t\n " + propType + "\t\n" + genericGetType + "\t\n" + genericSetType + "\t\n" +(pack +  params.get("name"))+"\t\n");
//                }
//
//            cw.visitEnd();
//
//            return new JarModelImpl.Holder( cw.toByteArray() );








}
