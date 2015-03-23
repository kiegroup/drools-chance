package org.drools.shapes;

import org.drools.shapes.model.Condition;
import org.drools.shapes.model.datatypes.CD;
import org.drools.shapes.terms.TestVocabulary;
import org.drools.shapes.terms.evaluator.DenotesEvaluatorDefinition;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.conf.EvaluatorOption;
import org.kie.internal.utils.KieHelper;

import java.util.Date;

public class TestDenotes {

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
    @Ignore
    public void testDenotesInRule() {
        String rule = "package org.drools.shapes.terms; " +
                      "import " + Condition.class.getName() + "; " +
                      "import " + CD.class.getName() + "; " +
                      "import " + TestVocabulary.class.getName() + "; " +

                      "rule RedAlert " +
                      "when " +
                      "     $c : Observation( $pid : pid, value resolvesAs SNOMED.Lung ) " +
                      "then " +
                      "     don( $c, UnusuallyHighBloodPressureObservationOccurrence.class ) ; " +
                      "end ";

        Condition c1 = new Condition( "1",
                                      new Date(),
                                      new CD( "99.1.2.3", "gestational diabetes", TestVocabulary.codeSystemURI, TestVocabulary.codeSystem, TestVocabulary.codeSystemName ),
                                      "JohnDoe" );

        KieSession ks = new KieHelper( EvaluatorOption.get( "denotes", new DenotesEvaluatorDefinition() ) )
                .addContent( rule, ResourceType.DRL )
                .build()
                .newKieSession();

        ks.insert( c1 );
        ks.fireAllRules();

    }

}
