package org.drools.informer;


import org.drools.informer.generator.ISurveyable;
import org.drools.informer.generator.annotations.Questionable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Questionable(label = "Demographic Info Survey" )
public class Person implements ISurveyable {


    @org.drools.informer.generator.annotations.QuestionMark( type = Question.QuestionType.TYPE_TEXT, label = "Please fill name", finalAnswer = true )
    private String name;

    @org.drools.informer.generator.annotations.QuestionMark( type = Question.QuestionType.TYPE_NUMBER, label = "Please fill age", required = false )
    private int age;


    @org.drools.informer.generator.annotations.QuestionMark( type = Question.QuestionType.TYPE_LIST, label = "Please choose all your hobbies", required = true )
    @org.drools.informer.generator.annotations.AllowedAnswers( values = { "Sport=Play Sport", "Reading=Reading Books", "Sleeping=Sleeping All Day" } )
    private List<String> hobbies;


    @org.drools.informer.generator.annotations.QuestionMark( type = Question.QuestionType.TYPE_LIST, label = "Please choose your lucky numbers", required = true )
    @org.drools.informer.generator.annotations.AllowedAnswers( values = { "3", "13", "17", "81" } )
    private List<Integer> luckyNumbers;


    @org.drools.informer.generator.annotations.QuestionMark( type = Question.QuestionType.TYPE_DATE, label = "Birth Date?", dateFormat = "dd/MM/yyyy",
                   whenCondition = "age < 5" )
    private Date birthDate;


    @org.drools.informer.generator.annotations.QuestionMark( type = Question.QuestionType.TYPE_DATE, label = "Timing", dateFormat = "HH:mm:SS" )
    private Date doomsHour;



    private String questionnaireId;

    public String getQuestionnaireId() {
        return questionnaireId;
    }



    public Person(String id, String name, int age) {
        this.questionnaireId = id;
        this.name = name;
        this.age = age;
    }


    @Override
    public String toString() {
        return "Person{" +
                "questionnaireId='" + questionnaireId + '\'' +
                ", age=" + age +
                ", name='" + name + '\'' +
                ", luckyNumbers=" + luckyNumbers +
                ", hobbies=" + hobbies +
                ", birthDate=" + (birthDate != null ? new SimpleDateFormat("dd/MM/yyyy").format(birthDate) : null) +
                ", hour=" + (doomsHour != null ? new SimpleDateFormat("HH:mm:SS").format(doomsHour) : null) +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Person person = (Person) o;

        if (questionnaireId != null ? !questionnaireId.equals(person.questionnaireId) : person.questionnaireId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return questionnaireId != null ? questionnaireId.hashCode() : 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public List<String> getHobbies() {
        return hobbies;
    }

    public void setHobbies(List<String> hobbies) {
        this.hobbies = hobbies;
    }

    public List<Integer> getLuckyNumbers() {
        return luckyNumbers;
    }

    public void setLuckyNumbers(List<Integer> luckyNumbers) {
        this.luckyNumbers = luckyNumbers;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }


    public Date getDoomsHour() {
        return doomsHour;
    }

    public void setDoomsHour(Date doomsHour) {
        this.doomsHour = doomsHour;
    }
}
