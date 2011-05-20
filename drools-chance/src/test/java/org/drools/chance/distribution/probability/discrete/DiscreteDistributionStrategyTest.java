package org.drools.chance.distribution.probability.discrete;

import static org.junit.Assert.assertEquals;

import org.drools.chance.degree.simple.SimpleDegree;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: davide
 * Date: 12/31/10
 * Time: 3:14 AM
 * To change this template use File | Settings | File Templates.
 */
public class DiscreteDistributionStrategyTest {


	private static DiscreteDistribution<String> sortMapDistrCurr = new DiscreteDistribution<String>();
	private static DiscreteDistribution<String> sortMapDistrNew = new DiscreteDistribution<String>();
	private static DiscreteDistributionStrategy<String> DisDisStrategy=new DiscreteDistributionStrategy<String>("simple",String.class);

	@Test
    public void testMerge() throws Exception {
		sortMapDistrCurr.put("one", new SimpleDegree(0.2));
		sortMapDistrCurr.put("two", new SimpleDegree(0.5));
		sortMapDistrCurr.put("three", new SimpleDegree(0.3));

		sortMapDistrNew.put("one", new SimpleDegree(0.3));
		sortMapDistrNew.put("two", new SimpleDegree(0.4));
		sortMapDistrNew.put("three", new SimpleDegree(0.3));

		DiscreteDistribution<String> ret=(DiscreteDistribution<String>) DisDisStrategy.merge(sortMapDistrCurr, sortMapDistrNew);

		//System.out.println(ret.getDegree("one").getValue());
		assertEquals((0.06/0.35),ret.getDegree("one").getValue(),1e-12);
		assertEquals((0.2/0.35),ret.getDegree("two").getValue(),1e-12);
		assertEquals((0.09/0.35),ret.getDegree("three").getValue(),1e-12);

	}

    @Test
    public void testNewDistribution() throws Exception {

    }

    @Test
    public void testToCrispValue() throws Exception {
    	sortMapDistrCurr.put("one", new SimpleDegree(0.2));
		sortMapDistrCurr.put("two", new SimpleDegree(0.5));
		sortMapDistrCurr.put("three", new SimpleDegree(0.3));

		assertEquals("two",DisDisStrategy.toCrispValue(sortMapDistrCurr));
    }

    @Test
    public void testSample() throws Exception {

    }

    @Test
    public void testToDistribution() throws Exception {

    }

    @Test
    public void testParse() throws Exception {

                  DiscreteDistribution<String> dd= (DiscreteDistribution<String>) DisDisStrategy.parse("john/0.3, mark/0.7");
                assertEquals(0.3,dd.getDegree("john").getValue(),0);
                assertEquals(0.7,dd.getDegree("mark").getValue(),0);


        DiscreteDistributionStrategy<Integer> DisDisStrategyInt=new DiscreteDistributionStrategy<Integer>("simple",Integer.class);
         DiscreteDistribution<Integer> ddI= (DiscreteDistribution<Integer>) DisDisStrategyInt.parse("5/0.3, 6/0.7");
         assertEquals(0.3,ddI.getDegree(5).getValue(),0);
         assertEquals(0.7,ddI.getDegree(6).getValue(),0);



    }

    @Test
    public void testNewParametricDistribution() throws Exception {

    }
}
