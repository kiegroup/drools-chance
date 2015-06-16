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

import org.drools.compiler.lang.api.PackageDescrBuilder;
import org.drools.compiler.lang.api.impl.PackageDescrBuilderImpl;
import org.drools.compiler.lang.descr.RuleDescr;
import org.drools.semantics.builder.reasoner.APIRecognitionRuleBuilder;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RecognitionRuleModelImpl extends ModelImpl implements RecognitionRuleModel {

    PackageDescrBuilder packageBuilder;

    private Map<String,Set<RuleDescr>> rules = new HashMap<String, Set<RuleDescr>>();

    public String getDRL() {
        return APIRecognitionRuleBuilder.generateDRL( packageBuilder.getDescr(), false );
    }

    public boolean stream( OutputStream os ) {
        try {
            os.write( getDRL().getBytes() );
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return false;
        }
        return true;
    }

    @Override
    public void addTrait( String name, Object trait ) {
        if ( trait instanceof RuleDescr ) {
            if ( ! rules.containsKey( name ) ) {
                rules.put( name, new HashSet<RuleDescr>() );
            }
            rules.get( name ).add( (RuleDescr) trait );
        } else {
            throw new IllegalStateException( "A " + RuleDescr.class.getName() + " was expected, received " + trait.getClass().getName()  );
        }
    }

    @Override
    public Object getTrait( String name ) {
        return rules.get( name );
    }

    @Override
    public Set<String> getTraitNames() {
        return rules.keySet();
    }

    @Override
    protected String traitsToString() {
        return getDRL();
    }

    public PackageDescrBuilder getPackage() {
        if ( packageBuilder == null ) {
            packageBuilder = PackageDescrBuilderImpl.newPackage().name( getDefaultPackage() );
        }
        return packageBuilder;
    }
}
