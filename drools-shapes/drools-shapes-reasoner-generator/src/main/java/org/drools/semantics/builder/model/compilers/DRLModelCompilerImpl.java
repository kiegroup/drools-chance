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

import org.drools.semantics.builder.DLTemplateManager;
import org.drools.semantics.builder.model.CompiledOntoModel;
import org.drools.semantics.builder.model.ModelFactory;
import org.drools.semantics.builder.model.OntoModel;
import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateRegistry;
import org.mvel2.templates.TemplateRuntime;

import java.util.Map;

public class DRLModelCompilerImpl extends ModelCompilerImpl implements DRLModelCompiler {

    private String templateName = "trait.drlt";
    private TemplateRegistry registry = DLTemplateManager.getDataModelRegistry(ModelFactory.CompileTarget.DRL);

    public void setModel(OntoModel model) {
        this.model = (CompiledOntoModel) ModelFactory.newModel( ModelFactory.CompileTarget.DRL, model );
    }

    public void compile( String name, Object context, Map<String, Object> params ) {
        CompiledTemplate template = registry.getNamedTemplate(templateName);
        getModel().addTrait(name, TemplateRuntime.execute(template, context, params).toString().trim());
    }


    public void setMode(Mode mode) {
        //TODO
    }
}
