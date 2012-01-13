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

package org.drools.chance.constraints.core.evaluators;

import junit.framework.TestCase;
import org.drools.chance.constraints.core.IConstraintCore;
import org.drools.chance.constraints.core.connectives.IConnectiveCore;
import org.drools.chance.degree.Degree;
import org.drools.chance.degree.simple.SimpleDegree;
import org.drools.chance.distribution.probability.discrete.DiscreteDistribution;
import org.junit.Before;
import org.junit.Test;

public class DiscreteEvaluatorCoreWrapperTest  extends TestCase {


    @Before
    public void setUp() throws Exception {

    }




    @Test
    public void testEvalUnary() throws Exception {

    }

    /**
     * Person(  age1
	             == @Imperfect( kind="eq_impl", degree=="simple", tnorm="product", snorm="lukas" )
	            age2 )

     * @throws Exception
     */
    @Test
    public void testEvalBinary() throws Exception {
        Degree deg = SimpleDegree.FALSE;
        IConstraintCore core = new EqualityEvaluatorCore(deg);
        IConnectiveCore and = org.drools.chance.constraints.core.connectives.impl.product.And.getInstance();
        IConnectiveCore or = org.drools.chance.constraints.core.connectives.impl.lukas.Or.getInstance();

        DiscreteDistribution<String> left=new DiscreteDistribution<String>();
        DiscreteDistribution<String> right=new DiscreteDistribution<String>();

        left.put("Mark", new SimpleDegree(0.2));
        left.put("John", new SimpleDegree(0.6));
        left.put("Carl", new SimpleDegree(0.2));

        right.put("Mark", new SimpleDegree(0.7));
        right.put("John", new SimpleDegree(0.2));
        right.put("Carl", new SimpleDegree(0.1));

        DiscreteEvaluatorCoreWrapper wrapper = new DiscreteEvaluatorCoreWrapper(core, and, or, deg);

        Degree ans = wrapper.eval(left,right);
        assertTrue(ans instanceof SimpleDegree);
        assertEquals(new SimpleDegree(0.2*0.7 + 0.6*0.2 + 0.2*0.1),ans);

    }

    @Test
    public void testEvalNary() throws Exception {

    }

    @Test
    public void testIsUnary() throws Exception {

    }

    @Test
    public void testIsBinary() throws Exception {

    }

    @Test
    public void testIsNary() throws Exception {

    }
}
