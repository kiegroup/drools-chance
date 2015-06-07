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

package org.drools.chance.factmodel;

import org.drools.chance.distribution.fuzzy.linguistic.Linguistic;
import org.drools.core.factmodel.AnnotationDefinition;
import org.drools.core.factmodel.BuildUtils;
import org.drools.core.factmodel.ClassDefinition;
import org.drools.core.factmodel.DefaultEnumClassBuilder;
import org.drools.core.factmodel.EnumClassDefinition;
import org.mvel2.asm.ClassWriter;
import org.mvel2.asm.MethodVisitor;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class ChanceEnumBuilderImpl extends DefaultEnumClassBuilder {


    public byte[] buildClass(ClassDefinition classDef,ClassLoader loader) throws IOException, IntrospectionException, SecurityException, IllegalArgumentException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchFieldException {
        if ( classDef.getAnnotations() != null ) {
            for ( AnnotationDefinition ad : classDef.getAnnotations() ) {
                if ( ad.getName().equals( LinguisticPartition.class.getName() ) ) {
                    classDef.setInterfaces( new String[] { BuildUtils.getInternalType( Linguistic.class.getName() ) } );
                }
            }
        }
        return super.buildClass(classDef, loader);
    }


    protected void buildConstructors(ClassWriter cw, EnumClassDefinition classDef) throws IOException, ClassNotFoundException {
        super.buildConstructors(cw, classDef);

        {
            MethodVisitor mv = cw.visitMethod( ACC_PUBLIC, "parse", "(Ljava/lang/String;)Lorg/drools/chance/distribution/fuzzy/linguistic/Linguistic;", null, null);
            mv.visitCode();
            mv.visitVarInsn( ALOAD, 1 );
            mv.visitMethodInsn( INVOKESTATIC,
                                BuildUtils.getInternalType( classDef.getClassName() ),
                                "valueOf",
                                "(Ljava/lang/String;)" + BuildUtils.getTypeDescriptor( classDef.getClassName() ) );
            mv.visitInsn( ARETURN );
            mv.visitMaxs( 0, 0 );
            mv.visitEnd();
        }
    }
}
