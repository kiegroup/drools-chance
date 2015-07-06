package it.unibo.deis.lia.org.drools.expectations.model;

import it.unibo.deis.lia.org.drools.expectations.model.Expectation;
import org.kie.api.definition.type.PropertyReactive;
import org.kie.api.runtime.rule.Match;

import java.io.Serializable;
import java.util.List;

@PropertyReactive
public class OneShotExpectation extends Expectation implements Serializable {

    private static final long serialVersionUID = 4741942003493008807L;

    public OneShotExpectation( long originId, String label, Match act, long actId, List tuple, long start, long duration, boolean active, String ruleName ) {
        super( originId, label, actId, tuple, start, duration, active, ruleName );
    }

    public OneShotExpectation( long originId, String label, long actId ) {
        super( originId, label, actId );
    }

    public String toString() {
        return "One Shot " + super.toString();
    }

}
