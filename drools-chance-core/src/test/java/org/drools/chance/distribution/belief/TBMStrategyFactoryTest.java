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

package org.drools.chance.distribution.belief;


import org.drools.chance.common.ChanceStrategyFactory;
import org.drools.chance.degree.DegreeType;
import org.drools.chance.distribution.Distribution;
import org.drools.chance.distribution.belief.discrete.TBM;
import org.drools.chance.distribution.belief.discrete.TBMStrategyFactory;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TBMStrategyFactoryTest {


    @BeforeClass
    public static void init() {
        ChanceStrategyFactory.initDefaults();
    }
    
    @Test
    public void testParse() {
        
        String str = "{A,B}/0.3, {A}/0.2, {B,C,D}/0.1, {C,D}/0.35";
        
        Distribution<String> tbm = new TBMStrategyFactory<String>().buildStrategies( DegreeType.SIMPLE, String.class ).parse( str );
        
        System.out.println( tbm );
        
    }
    
}
