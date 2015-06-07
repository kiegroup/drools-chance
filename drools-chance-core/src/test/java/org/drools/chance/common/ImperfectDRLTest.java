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

package org.drools.chance.common;

import org.drools.chance.Chance;
import org.drools.chance.kbase.AbstractChanceTest;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;


public class ImperfectDRLTest extends AbstractChanceTest {

    protected KieSession kSession;
    protected List<String> list = new ArrayList<String>();

    @BeforeClass
    public static void init() {
        Chance.initialize();
    }

    @Before
    public void setUp() throws Exception {
        initObjects();
    }


    private void initObjects() throws Exception {

        kSession = getChanceEnabledKieSession( KieServices.Factory.get().getResources().newClassPathResource(
                "org/drools/chance/factmodel/testImperfectRules.drl" ) );

        kSession.setGlobal( "list", list );
        kSession.fireAllRules();

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testRules() {
        assertTrue( list.contains( "OK" ) );
    }

}
