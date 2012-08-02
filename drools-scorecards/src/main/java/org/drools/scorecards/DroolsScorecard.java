/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.scorecards;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DroolsScorecard implements Serializable{
    int calculatedScore;
    List<String> reasonCodes = new ArrayList<String>();


    public int getCalculatedScore() {
        return calculatedScore;
    }

    public void addPartialScore(int partialScore) {
        this.calculatedScore += partialScore;
    }

    public void addPartialScore(double partialScore) {
        this.calculatedScore += partialScore;
    }

    public void addReasonCode(String reasonCode){
        reasonCodes.add(reasonCode);
    }

    public List<String> getReasonCodes() {
        return Collections.unmodifiableList(reasonCodes);
    }

    public DroolsScorecard() {
    }
}
