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

package org.drools.chance.rule.constraint.core.connectives.impl.lukas;

import org.drools.chance.rule.constraint.core.connectives.impl.AbstractConnective;
import org.drools.chance.rule.constraint.core.connectives.impl.LogicConnectives;
import org.drools.chance.degree.Degree;
import org.drools.chance.evaluation.Evaluation;


public class Xor extends AbstractConnective {

    public Xor() { }

    private static Xor instance = new Xor();

    public static Xor getInstance() {
        return instance;
    }

    public LogicConnectives getType() {
        return LogicConnectives.XOR;
    }


    public Degree eval(Degree deg) {
        throw new UnsupportedOperationException("Logic X-Or operator is not unary");
    }

    public Degree eval(Degree left, Degree right) {
        return (left.sub(right)).sum(right.sub(left));
    }

    public Degree eval(Degree... degs) {
        Degree deg = degs[0];
        for (int j = 1; j < degs.length; j++) {
            deg = this.eval(deg,degs[j]);
        }
        return deg;
    }

    public Degree eval(Evaluation... degs) {
        Degree deg = degs[0].getDegree();
        for (int j = 1; j < degs.length; j++) {
            deg = this.eval( deg, degs[j].getDegree() );
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
        return false;
    }
}
