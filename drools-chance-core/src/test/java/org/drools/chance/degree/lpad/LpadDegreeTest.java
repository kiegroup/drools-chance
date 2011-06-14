package org.drools.chance.degree.lpad;

import junit.framework.TestCase;
import org.drools.chance.degree.interval.IntervalDegree;
import org.drools.chance.degree.simple.SimpleDegree;
import org.junit.Test;


public class LpadDegreeTest extends TestCase {


    public void testCombine() {
        LpadDegree degree = new LpadDegree();


            LpadDegree degreeA = new LpadDegree(0,1.0);

            LpadDegree degreeB = new LpadDegree();
            LpadDegree degreeC = new LpadDegree();
            LpadDegree degreeG = new LpadDegree();


            LpadDegree degree1 = new LpadDegree(1,0.1);
                System.out.println("Degree on arc 1 " + degree1);
            LpadDegree degree2 = new LpadDegree(2,0.2);
                System.out.println("Degree on arc 2 " + degree2);
            LpadDegree degree3 = new LpadDegree(3,0.3);
                System.out.println("Degree on arc 3 " + degree3);
            LpadDegree degree4 = new LpadDegree(4,0.4);
                System.out.println("Degree on arc 4 " + degree4);
            LpadDegree degree5 = new LpadDegree(5,0.5);
                System.out.println("Degree on arc 5 " + degree5);
            LpadDegree degree6 = new LpadDegree(6,0.6);
                System.out.println("Degree on arc 6 " + degree6);

            System.out.println("-------------------------------------------------------------------");

            System.out.println("Degree at node A " + degreeA);


            LpadDegree contrAG = LpadDegree.modusPonens(degreeA,degree1);
            System.out.println("Contribution : A --> G " + contrAG);
                degreeG = LpadDegree.merge(degreeG, contrAG);

            LpadDegree contrAB = LpadDegree.modusPonens(degreeA,degree2);
            System.out.println("Contribution : A --> B " + contrAB);
                degreeB = LpadDegree.merge(degreeB, contrAB);

            LpadDegree contrAC = LpadDegree.modusPonens(degreeA,degree3);
            System.out.println("Contribution : A --> C " + contrAC);
                degreeC = LpadDegree.merge(degreeC, contrAC);
            System.out.println("Degree at node C " + degreeC);

            LpadDegree contrCB = LpadDegree.modusPonens(degreeC,degree4);
            System.out.println("Contribution : C --> B " + contrCB);
                degreeB = LpadDegree.merge(degreeB, contrCB);
            System.out.println("Degree at node B " + degreeB);

            LpadDegree contrBG = LpadDegree.modusPonens(degreeB,degree5);
            System.out.println("Contribution : B --> G " + contrBG);
                degreeG = LpadDegree.merge(degreeG, contrBG);

            LpadDegree contrCG = LpadDegree.modusPonens(degreeC,degree6);
            System.out.println("Contribution : C --> G " + contrCG);
                degreeG = LpadDegree.merge(degreeG, contrCG);
            System.out.println("Degree at node G " + degreeG);


            System.out.println("-------------------------------------------------------------------");

            double a = degreeA.asSimpleDegree().getValue();
            double b = degreeB.asSimpleDegree().getValue();
            double c = degreeC.asSimpleDegree().getValue();
            double g = degreeG.asSimpleDegree().getValue();


            System.out.println("-------------------------------------------------------------------");


            System.out.println("SIMPLE Degree at node A " + a);
            System.out.println("SIMPLE Degree at node B " + b);
            System.out.println("SIMPLE Degree at node C " + c);
            System.out.println("SIMPLE Degree at node G " + g);

            assertEquals(1.0,a);
            assertEquals(0.296,b);
            assertEquals(0.3,c);
            assertEquals(0.35308,g);

    }


}
