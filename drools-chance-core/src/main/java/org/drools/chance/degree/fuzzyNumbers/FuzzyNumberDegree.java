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

package org.drools.chance.degree.fuzzyNumbers;

import org.drools.chance.degree.IDegree;
import org.drools.chance.degree.interval.IntervalDegree;
import org.drools.chance.degree.simple.SimpleDegree;


/**
 * TODO
 *
 * Models a (simple) degree using a fuzzy number instead of a double
 * *
 */
public class FuzzyNumberDegree implements IDegree {

	public IDegree False() {
		// TODO Auto-generated method stub
		return null;
	}

	public IDegree True() {
		// TODO Auto-generated method stub
		return null;
	}

	public IDegree Unknown() {
		// TODO Auto-generated method stub
		return null;
	}

    public IDegree sum(IDegree sum) {
        //TODO
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


    public IntervalDegree asIntervalDegree() {
		// TODO Auto-generated method stub
		return null;
	}

	public SimpleDegree asSimpleDegree() {
		// TODO Auto-generated method stub
		return null;
	}

	public double getConfidence() {
		// TODO Auto-generated method stub
		return 0;
	}

	public double getValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean toBoolean() {
		// TODO Auto-generated method stub
		return false;
	}

	public int compareTo(IDegree o) {
		// TODO Auto-generated method stub
		return 0;
	}

}
