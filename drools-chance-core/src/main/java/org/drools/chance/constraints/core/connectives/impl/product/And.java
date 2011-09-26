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

package org.drools.chance.constraints.core.connectives.impl.product;

import org.drools.chance.constraints.core.IConstraintCore;
import org.drools.chance.constraints.core.connectives.impl.AbstractConnective;
import org.drools.chance.constraints.core.connectives.impl.LOGICCONNECTIVES;
import org.drools.chance.constraints.core.connectives.impl.lukas.Not;
import org.drools.chance.degree.IDegree;

public class And extends AbstractConnective {

     private And() { }

    private static And instance = new And();

    public static And getInstance() {
        return instance;
    }


     public LOGICCONNECTIVES getType() {
        return LOGICCONNECTIVES.AND;
    }





    public IDegree eval(IDegree deg) {
        return deg;
    }


    public IDegree eval(IDegree left, IDegree right) {
        return left.mul(right);
    }


    public IDegree eval(IDegree... degs) {
        IDegree deg = degs[0];
        for (int j = 1; j < degs.length; j++) {
            deg = eval(deg, degs[j]);
        }
        return deg;
    }

    public boolean isUnary() {
        return false;
    }

    public boolean isBinary() {
        return true;
    }

    public boolean isNary() {
        return true;
    }
}