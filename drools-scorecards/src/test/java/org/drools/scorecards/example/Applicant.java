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

package org.drools.scorecards.example;


public class Applicant {
    double age;
    String occupation;
    String  residenceState;
    double totalScore;
    boolean validLicense;

    public boolean isValidLicense() {
        return validLicense;
    }

    public void setValidLicense(boolean validLicense) {
        this.validLicense = validLicense;
    }

    public Applicant() {
    }

    public double getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(double totalScore) {
        this.totalScore = totalScore;
    }

    public double getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getResidenceState() {
        return residenceState;
    }

    public void setResidenceState(String residenceState) {
        this.residenceState = residenceState;
    }
}
