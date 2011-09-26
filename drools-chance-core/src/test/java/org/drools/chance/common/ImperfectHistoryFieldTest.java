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

import org.drools.chance.degree.simple.SimpleDegree;
import org.drools.chance.distribution.IDistributionStrategyFactory;
import org.drools.chance.distribution.probability.discrete.DiscreteDistribution;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ImperfectHistoryFieldTest {

    private static DiscreteDistribution<String> ddS1=new DiscreteDistribution<String>();
    private static DiscreteDistribution<String> ddS2=new DiscreteDistribution<String>();
    private static DiscreteDistribution<String> ddS3=new DiscreteDistribution<String>();
    private static ImperfectHistoryField<String> iifS;

    @Before
    public void setUp(){
        ddS1.put("Mark", new SimpleDegree(0.2));
        ddS1.put("John", new SimpleDegree(0.6));
        ddS1.put("Carl", new SimpleDegree(0.2));

        ddS2.put("Mark", new SimpleDegree(0.7));
        ddS2.put("John", new SimpleDegree(0.2));
        ddS2.put("Carl", new SimpleDegree(0.1));

        ddS3.put("Mark", new SimpleDegree(0.2));
        ddS3.put("John", new SimpleDegree(0.3));
        ddS3.put("Carl", new SimpleDegree(0.5));

        IDistributionStrategyFactory factory=null;
        try {
            factory = (IDistributionStrategyFactory) Class.forName("org.drools.chance.distribution.probability.discrete.DiscreteDistributionStrategyFactory").newInstance();
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        StrategyFactory.register(factory.getImp_Kind(), factory.getImp_Model(), factory);

        iifS=new ImperfectHistoryField<String>(StrategyFactory.buildStrategies(factory.getImp_Kind(), factory.getImp_Model(),null,null),2);
    }

    @Test
    public void testSetValue() throws Exception {
        iifS.setValue(ddS1,false);

        assertEquals(1,iifS.getSize());

        iifS.setValue(ddS2,false);
        assertEquals(2,iifS.getSize());

        iifS.setValue(ddS3,false);
        assertEquals(2,iifS.getSize());

    }

    @Test
    public void testGetPast() throws Exception {


        iifS.setValue(ddS1,false);
        iifS.setValue(ddS2,false);

        assertEquals(ddS1,iifS.getPast(-1));

        iifS.setValue(ddS3,false);
        assertEquals(ddS2,iifS.getPast(-1));

    }

    @Test
    public void testGetCurrent() throws Exception {

        iifS.setValue(ddS1,false);
        iifS.setValue(ddS2,false);

        assertEquals(ddS2,iifS.getCurrent());

        iifS.setValue(ddS3,false);
        assertEquals(ddS3,iifS.getCurrent());

    }

    @Test
    public void testGetSize() throws Exception {

    }

    @Test
    public void testGetCrisp() throws Exception {
        iifS.setValue(ddS1,false);
        assertEquals("John",iifS.getCrisp());

        iifS.setValue(ddS2,false);

        assertEquals("Mark",iifS.getCrisp());



    }

    @Test
    public void testGetStrategies() throws Exception {

    }

    @Test
    public void testUpdate() throws Exception {

    }
}
