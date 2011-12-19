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

package org.drools.chance.degree.lpad;

import org.drools.chance.degree.IDegree;
import org.drools.chance.degree.interval.IntervalDegree;
import org.drools.chance.degree.simple.SimpleDegree;

import java.util.*;


/**
 * Class that implements the concept of degree using a simple double value.
 * Useful for many semantics (probability, possibility, many-valued truth, confidence, belief, ...)
 */
public class LpadDegree implements IDegree {


    public double getValue() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean toBoolean() {
        return asSimpleDegree().toBoolean();
    }

    public double getConfidence() {
        return asSimpleDegree().getConfidence();
    }

    public IntervalDegree asIntervalDegree() {
        return asSimpleDegree().asIntervalDegree();
    }

    public SimpleDegree asSimpleDegree() {
        return new SimpleDegree(combine());
    }

    public IDegree True() {
        return SimpleDegree.TRUE;
    }

    public IDegree False() {
        return SimpleDegree.FALSE;
    }

    public IDegree Unknown() {
        return SimpleDegree.FALSE;
    }

    public IDegree sum(IDegree sum) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public IDegree mul(IDegree mul) {
        //TODO
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public IDegree div(IDegree div) {
        //TODO
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public IDegree sub(IDegree sub) {
        //TODO
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public IDegree max(IDegree comp) {
        //TODO
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public IDegree min(IDegree comp) {
        //TODO
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public IDegree fromConst(double number) {
        //TODO
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public IDegree fromString(String val) {
        //TODO
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public int compareTo(IDegree iDegree) {
        return asSimpleDegree().compareTo(iDegree);
    }


    public String toString() {
        String ans = "[[ \n";
        for (DegBit bit : parts)
            ans += "\t\t" + bit.toString() + " \n";
        ans += "\n  ]]";
        return ans;
    }


    public LpadDegree() {

    }

    public LpadDegree(int idx, double prob) {
        this.parts.add(new DegBit(idx,prob));
    }


    protected LinkedList<DegBit> parts = new LinkedList<DegBit>();


    public static LpadDegree merge(LpadDegree d1, LpadDegree d2) {
        LpadDegree ans = new LpadDegree();
            ans.parts.addAll(d1.parts);
            ans.parts.addAll(d2.parts);
        return ans;
    }


    protected double combine() {
        System.out.println("\n\n Called combine (from asSimpleDegree) - Noisoring :");
        Iterator<DegBit> iter = parts.iterator();

        List<DegBit> acc = new LinkedList<DegBit>();
            acc.add(iter.next());

        while (iter.hasNext()) {
            List<DegBit> temp = new ArrayList<DegBit>(acc.size());
            DegBit next = iter.next();
            for (int j = 0; j < acc.size(); j++) {
                DegBit bit = acc.get(j);
                temp.add(bit.multiply(next,-1.0));
            }
            acc.add(next);
            acc.addAll(temp);

            System.out.println("**************** Step gives :");
            for (int j = 0; j < acc.size(); j++) {
                System.out.println(acc.get(j));
            }
        }

        double ans = 0.0;
        for (DegBit bit : acc)
            ans += bit.getProb();
        return ans;
    }




    public static LpadDegree modusPonens(LpadDegree prem, LpadDegree impl) {
        DegBit arrow = impl.parts.get(0);

        LpadDegree ans = new LpadDegree();
        Iterator<DegBit> iter = prem.parts.iterator();
        while (iter.hasNext()) {
            DegBit bit = iter.next().clone();
                bit.labs.or(arrow.labs);
                bit.probs.putAll(arrow.probs);
            ans.parts.add(bit);
        }
        return ans;
    }








    public static class DegBit {

        private Map<Integer,Double> probs = new HashMap<Integer,Double>();
        private BitSet labs = new BitSet();
        private double sign = 1.0;

        public DegBit() {

        }

        public DegBit(int idx, double prob) {
            labs.set(idx);
            probs.put(idx,prob);
        }

        public DegBit clone() {
            DegBit ans = new DegBit();
                ans.labs.or(labs);
                ans.probs.putAll(probs);
            return ans;
        }





        public double getProb() {
            if (probs.size() == 0)
                return 0.0;
            double ans = sign;
            for (int j = 0; j < labs.size(); j++) {
                if (this.labs.get(j))
                    ans *= this.probs.get(j);
            }
            return ans;
        }


        public String toString() {
            String ans = "{";
            for (int j = 0; j < labs.size(); j++)
                if (labs.get(j))
                    ans += j + ":" + probs.get(j) +  ", ";
            ans = ans.substring(0,ans.length()-2) +"}";
            ans += " / " + getProb();
            return ans;
        }

        public DegBit multiply(DegBit other, double sign) {
            DegBit ans = new DegBit();
            ans.sign = this.sign*sign;

                ans.labs.or(this.labs);
                ans.labs.or(other.labs);

            for (int j = 0; j < ans.labs.size(); j++) {
                if (this.labs.get(j))
                    ans.probs.put(j,this.probs.get(j));
                else
                    ans.probs.put(j,other.probs.get(j));
            }
            return ans;
        }
    }



}
