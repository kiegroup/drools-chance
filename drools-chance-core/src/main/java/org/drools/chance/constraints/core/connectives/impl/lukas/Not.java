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

package org.drools.chance.constraints.core.connectives.impl.lukas;

import org.drools.chance.constraints.core.connectives.impl.AbstractConnective;
import org.drools.chance.constraints.core.connectives.impl.LogicConnectives;
import org.drools.chance.degree.Degree;
import org.drools.chance.evaluation.Evaluation;


public class Not extends AbstractConnective {


    public Not() { }

    private static Not instance = new Not();

    public static Not getInstance() {
        return instance;
    }


    public LogicConnectives getType() {
        return LogicConnectives.NOT;
    }

    public Degree eval(Degree deg) {
        return deg.True().sub(deg);
    }

    public Degree eval(Degree left, Degree right) {
        return eval(left);
    }

    public Degree eval(Degree... degs) {
        return eval(degs[0]);
    }

    public Degree eval(Evaluation... degs) {
        return eval( degs[0].getDegree() );
    }
    
    

    public boolean isUnary() {
        return true;
    }


    public boolean isBinary() {
        return true;
    }


    public boolean isNary() {
        return true;
    }
}
