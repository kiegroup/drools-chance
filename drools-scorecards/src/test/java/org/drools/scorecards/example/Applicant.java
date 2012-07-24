package org.drools.scorecards.example;

/**
 * Created with IntelliJ IDEA.
 * User: vinod
 * Date: 13/7/12
 * Time: 7:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class Applicant {
    int age;
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

    public int getAge() {
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
