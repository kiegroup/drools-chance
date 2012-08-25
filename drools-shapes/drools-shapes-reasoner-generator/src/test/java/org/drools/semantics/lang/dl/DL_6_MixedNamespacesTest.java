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

package org.drools.semantics.lang.dl;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.RuleBaseConfiguration;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.io.Resource;
import org.drools.io.ResourceFactory;
import org.drools.reteoo.AlphaNode;
import org.drools.reteoo.ObjectSource;
import org.drools.reteoo.builder.BuildContext;
import org.drools.reteoo.builder.DefaultNodeFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.semantics.builder.DLFactory;
import org.drools.semantics.builder.DLFactoryBuilder;
import org.drools.semantics.builder.model.*;
import org.drools.semantics.builder.model.compilers.ModelCompiler;
import org.drools.semantics.builder.model.compilers.ModelCompilerFactory;
import org.drools.semantics.builder.model.compilers.XSDModelCompiler;
import org.drools.spi.AlphaNodeFieldConstraint;
import org.drools.spi.PropagationContext;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;

import static junit.framework.Assert.assertEquals;


/**
 * This is a sample class to launch a rule.
 */
public class DL_6_MixedNamespacesTest {

    protected DLFactory factory = DLFactoryBuilder.newDLFactoryInstance();
    



    @Test
    public void testMixedExternal() {
        String source1 = "appendix.owl";
        String source2 = "mixed.owl";
        Resource res = ResourceFactory.newClassPathResource(source1);
        Resource res2 = ResourceFactory.newClassPathResource(source2);
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        StatefulKnowledgeSession kSession = kbase.newStatefulKnowledgeSession();

        factory.setInferenceStrategy( DLFactory.INFERENCE_STRATEGY.EXTERNAL );
        OntoModel results = factory.buildModel( "mixedModel", new Resource[] { res, res2 }, kSession );

        System.out.println( results );

        ModelCompiler jcompiler =  ModelCompilerFactory.newModelCompiler( ModelFactory.CompileTarget.JAR );
        jcompiler.setMode( ModelCompiler.Mode.HIERARCHY );
        JarModel jarModel = (JarModel) jcompiler.compile( results );


        ModelCompiler compiler =  ModelCompilerFactory.newModelCompiler(ModelFactory.CompileTarget.XSDX);
        compiler.setMode( ModelCompiler.Mode.HIERARCHY );
        SemanticXSDModel xsdModel = (SemanticXSDModel) compiler.compile( results );

        assertEquals( 4, xsdModel.getNamespaces().size() );
        assertEquals( 5, xsdModel.getConcepts().size() );

    }



}