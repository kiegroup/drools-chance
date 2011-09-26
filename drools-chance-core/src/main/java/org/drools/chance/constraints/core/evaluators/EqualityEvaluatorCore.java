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

import org.drools.chance.constraints.core.IConstraintCore;
import org.drools.chance.degree.IDegree;


@Deprecated
public class EqualityEvaluatorCore implements IConstraintCore {

    /**
     * Factory instance, for quick generation of degrees of the desired type
     */
    IDegree master;


    public EqualityEvaluatorCore(IDegree sample) {
        master = sample;
    }

    public IDegree eval(Object left, Object right) {
        return left.equals(right) ? master.True() : master.False();
    }

    public IDegree eval(Object obj) {
        throw new UnsupportedOperationException(" == is not unary ");
    }

    public IDegree eval(Object... objs) {
        throw new UnsupportedOperationException(" == is not "+objs.length+"-ary ");
    }





    public boolean isUnary() {
        return false;
    }

    public boolean isBinary() {
        return true;
    }

    public boolean isNary() {
        return false;
    }
}
