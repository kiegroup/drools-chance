package org.drools.chance.reteoo.builder;


import org.drools.WorkingMemory;
import org.drools.base.KnowledgeHelperFactory;
import org.drools.chance.reteoo.ChanceKnowledgeHelper;
import org.drools.chance.reteoo.ChanceSequentialKnowledgeHelper;
import org.drools.spi.KnowledgeHelper;

public class ChanceKnowledgeHelperFactory implements KnowledgeHelperFactory {
    
    public KnowledgeHelper newSequentialKnowledgeHelper( WorkingMemory wm ) {
        return new ChanceSequentialKnowledgeHelper( wm );
    }

    public KnowledgeHelper newStatefulKnowledgeHelper( WorkingMemory wm ) {
        return new ChanceKnowledgeHelper( wm );
    }
    
}
