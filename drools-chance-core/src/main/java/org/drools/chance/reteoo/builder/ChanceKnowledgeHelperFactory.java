package org.drools.chance.reteoo.builder;

import org.drools.chance.reteoo.ChanceKnowledgeHelper;
import org.drools.chance.reteoo.ChanceSequentialKnowledgeHelper;
import org.drools.core.WorkingMemory;
import org.drools.core.base.KnowledgeHelperFactory;
import org.drools.core.spi.KnowledgeHelper;

public class ChanceKnowledgeHelperFactory implements KnowledgeHelperFactory {
    
    public KnowledgeHelper newSequentialKnowledgeHelper( WorkingMemory wm ) {
        return new ChanceSequentialKnowledgeHelper( wm );
    }

    public KnowledgeHelper newStatefulKnowledgeHelper( WorkingMemory wm ) {
        return new ChanceKnowledgeHelper( wm );
    }
    
}
