package org.drools.shapes;

import org.drools.shapes.model.Condition;
import org.drools.shapes.model.Observation;
import org.drools.shapes.model.datatypes.CD;
import org.drools.shapes.terms.SNOMED;
import org.drools.shapes.terms.TestVocabulary;
import org.drools.shapes.terms.evaluator.DenotesEvaluatorDefinition;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.conf.EvaluatorOption;
import org.kie.internal.utils.KieHelper;

import java.util.Date;

public class DenotesTest {

    @Test
    public void testCDEqualityInRule() {
        String rule = "package org.drools.shapes.terms; " +
                      "import " + Condition.class.getName() + "; " +
                      "import " + CD.class.getName() + "; " +
                      "import " + TestVocabulary.class.getName() + "; " +

                      "rule RedAlert " +
                      "when " +
                      "     $c : Condition( $pid : pid, code == TestVocabulary.EndocrineSystemDisease ) " +
                      "then " +
                      "     System.out.println( 'We see that Patient ' + $pid + ' has a serious problem ' ); " +
                      "end ";

        Condition c1 = new Condition( "1",
                                      new Date(),
                                      new CD( "99.1", "endocrine system disease", TestVocabulary.codeSystemURI, TestVocabulary.codeSystem, TestVocabulary.codeSystemName ),
                                      "JohnDoe" );

        KieSession ks = new KieHelper()
                .addContent( rule, ResourceType.DRL )
                .build()
                .newKieSession();

        ks.insert( c1 );
        ks.fireAllRules();
    }

    @Test
    public void testDenotesInRule() {
        String rule = "package org.drools.shapes.terms; " +
                      "import " + Condition.class.getName() + "; " +
                      "import " + Observation.class.getName() + "; " +
                      "import " + CD.class.getName() + "; " +
                      "import " + SNOMED.class.getName() + "; " +

                      "rule RedAlert " +
                      "dialect 'mvel' " +
                      "when " +
                      "     $c : Observation( $pid : pid, code denotes SNOMED.MedicalProblem, " +
                      "                       $val : value denotes SNOMED.AcuteDisease ) " +
                      "then " +
                      "     System.out.println( 'Patient ' + $pid + ' has an acute disease : ' + $val ); " +
                      "end ";

        // This is not the RIM observation, but a similar thing : Observation( id, effectiveTime, code, value, patientId )
        Observation c1 = new Observation( "obs1",
                                      new Date(),
                                      // we can use "literal" CDs
                                      (CD) SNOMED.MedicalProblem,
                                      // or CDs built on the fly
                                      new CD( "95653008", "acute migraine", SNOMED.codeSystemURI, SNOMED.codeSystem, SNOMED.codeSystemName ),
                                      "JohnDoe" );

        Observation c2 = new Observation( "obs2",
                                      new Date(),
                                      // we can use "literal" CDs
                                      (CD) SNOMED.MedicalProblem,
                                      // or CDs built on the fly
                                      (CD) SNOMED.DiabetesTypeII,
                                      "JohnDoe" );

        KieSession ks = new KieHelper( EvaluatorOption.get( "denotes", new DenotesEvaluatorDefinition() ) )
                .addContent( rule, ResourceType.DRL )
                .build()
                .newKieSession();

        ks.insert( c1 );
        ks.insert( c2 );
        ks.fireAllRules();

    }

}
