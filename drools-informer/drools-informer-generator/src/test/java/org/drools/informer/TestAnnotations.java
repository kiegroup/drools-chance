package org.drools.informer;


import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.impl.ClassPathResource;
import org.drools.lang.ParseException;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.fail;

public class TestAnnotations {




    @Test
    public void testGenerateQuestionnaireWithAnnotations() {

        KnowledgeBuilder kb = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kb.add(new ClassPathResource("org/drools/informer/informer-changeset.xml"), ResourceType.CHANGE_SET);
        kb.add(new ClassPathResource("org/drools/informer/informer-generator-changeset.xml"), ResourceType.CHANGE_SET);
        kb.add(new ClassPathResource("org/drools/informer/annotation_tests.drl"), ResourceType.DRL);
            if (kb.hasErrors()) {
                System.err.println(kb.getErrors());
                fail();
            }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
            kbase.addKnowledgePackages(kb.getKnowledgePackages());
        StatefulKnowledgeSession kSession = kbase.newStatefulKnowledgeSession();


        Person p1 = new Person("0001","john",18);
        Person p2 = new Person("0002","alan",35);


        kSession.insert(p1);
        kSession.insert(p2);
        kSession.fireAllRules();

        assertEquals(1, kSession.getQueryResults("questionnaire", p1.getId()).size());
        assertEquals(1, kSession.getQueryResults("questionnaire",p2.getId()).size());

        assertEquals(1, kSession.getQueryResults("question", p1.getId(), "name").size());
        assertEquals(1, kSession.getQueryResults("question", p1.getId(), "age").size());
        assertEquals(1, kSession.getQueryResults("question", p1.getId(), "hobbies").size());
        assertEquals(1, kSession.getQueryResults("question", p1.getId(), "luckyNumbers").size());

        assertEquals(6, kSession.getQueryResults("associations", p1).size());
        assertEquals(6, kSession.getQueryResults("associations", p2).size());


        Answer ans = new Answer("age",p1.getId(),"44");
        kSession.insert(ans);
        kSession.fireAllRules();

        assertEquals(44,p1.getAge());
        assertEquals(35,p2.getAge());

        Answer ans2 = new Answer("name",p1.getId(),"joe");
        kSession.insert(ans2);
        kSession.fireAllRules();

        assertEquals("joe",p1.getName());
        assertEquals("alan",p2.getName());


        Answer ans3 = new Answer("hobbies",p1.getId(),"Reading");
        kSession.insert(ans3);
        kSession.fireAllRules();

        assertEquals(Arrays.asList("Reading"),p1.getHobbies());
        assertNull(p2.getHobbies());


        Answer ans35 = new Answer("hobbies",p1.getId(),"Reading,Swimming,Sleeping");
        kSession.insert(ans35);
        kSession.fireAllRules();

        assertEquals(Arrays.asList("Reading"),p1.getHobbies());
        assertNull(p2.getHobbies());



        Answer ans4 = new Answer("hobbies",p1.getId(),"Reading,Sleeping");
        kSession.insert(ans4);
        kSession.fireAllRules();

        assertEquals(Arrays.asList("Reading", "Sleeping"),p1.getHobbies());
        assertNull(p2.getHobbies());


        Answer ans5 = new Answer("luckyNumbers",p1.getId(),"13");
        kSession.insert(ans5);
        kSession.fireAllRules();

        assertEquals(Arrays.asList("13"),p1.getLuckyNumbers());
        assertNull(p2.getLuckyNumbers());


        Answer ans6 = new Answer("birthDate",p1.getId(),"01/12/1981");
        kSession.insert(ans6);
        kSession.fireAllRules();


        try {
            assertEquals(new SimpleDateFormat("dd/MM/yyyy").parse("01/12/1981"),p1.getBirthDate());
            assertNull(p2.getBirthDate());
        } catch (java.text.ParseException pe) {
            pe.printStackTrace();
            fail();
        }


        Answer ans7 = new Answer("doomsHour",p1.getId(),"01:32:44");
        kSession.insert(ans7);
        kSession.fireAllRules();


        try {
            assertEquals(new SimpleDateFormat("HH:mm:SS").parse("01:32:44"),p1.getDoomsHour());
            assertNull(p2.getDoomsHour());
        } catch (java.text.ParseException pe) {
            pe.printStackTrace();
            fail();
        }


//        System.out.println(p1);
//        System.out.println(p2);

        kSession.retract(kSession.getFactHandle(p1));
        kSession.retract(kSession.getFactHandle(p2));

        kSession.fireAllRules();


        for ( Object o : kSession.getObjects() ) {
            System.err.println(o);
        }

    }

}
