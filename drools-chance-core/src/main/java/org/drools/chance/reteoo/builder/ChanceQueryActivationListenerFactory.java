package org.drools.chance.reteoo.builder;

import org.drools.ActivationListenerFactory;
import org.drools.reteoo.LeftTupleSource;
import org.drools.reteoo.QueryTerminalNode;
import org.drools.reteoo.ReteooComponentFactory;
import org.drools.reteoo.TerminalNode;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.GroupElement;
import org.drools.rule.Rule;

public class ChanceQueryActivationListenerFactory implements ActivationListenerFactory {


    public TerminalNode createActivationListener( int id,
                                                  LeftTupleSource source,
                                                  Rule rule,
                                                  GroupElement subrule,
                                                  int subruleIndex,
                                                  BuildContext context,
                                                  Object... args ) {

        return ReteooComponentFactory.getNodeFactoryService().newQueryTerminalNode( id, source, rule, subrule, subruleIndex, context );

    }

}
