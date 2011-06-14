package org.drools.chance.constraints.core.evaluators;

import junit.framework.TestCase;
import org.drools.chance.constraints.core.IConstraintCore;
import org.drools.chance.constraints.core.connectives.IConnectiveCore;
import org.drools.chance.constraints.core.connectives.impl.product.And;
import org.drools.chance.constraints.core.connectives.impl.product.Or;
import org.drools.chance.degree.IDegree;
import org.drools.chance.degree.simple.SimpleDegree;
import org.drools.chance.distribution.probability.discrete.DiscreteDistribution;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: davide
 * Date: 1/30/11
 * Time: 4:25 AM
 * To change this template use File | Settings | File Templates.
 */
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
        IDegree deg = SimpleDegree.FALSE;
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

        IDegree ans = wrapper.eval(left,right);
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
