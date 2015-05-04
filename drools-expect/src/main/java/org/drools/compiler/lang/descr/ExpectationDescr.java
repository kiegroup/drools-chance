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

package org.drools.compiler.lang.descr;

public class ExpectationDescr extends AnnotatedBaseDescr {

    private AndDescr expectLhs = new AndDescr();
    private ExpectationRuleDescr fulfill;
    private ExpectationRuleDescr violation;
    private ExpectationRuleDescr expired;
    private String label;
    private boolean matchOne;

    public void setFulfill( ExpectationRuleDescr fulfill ) {
        this.fulfill = fulfill;
    }

    public RuleDescr getFulfill() {
        return fulfill;
    }

    public void setViolation( ExpectationRuleDescr violation ) {
        this.violation = violation;
    }

    public RuleDescr getViolation() {
        return violation;
    }

    public AndDescr getExpectLhs() {
        return expectLhs;
    }

    public void setExpired( ExpectationRuleDescr expired ) { this.expired = expired; }

    public RuleDescr getExpired() { return expired; }

    public void setExpectLhs( AndDescr expectLhs ) {
        this.expectLhs = expectLhs;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel( String label ) {
        this.label = label;
    }

    public void setMatchOne( boolean matchOne ) {
        this.matchOne = matchOne;
    }

    public boolean isMatchOne() {
        return matchOne;
    }

    public boolean isEmpty() {
        return this.expectLhs.getDescrs().isEmpty();
    }
}
