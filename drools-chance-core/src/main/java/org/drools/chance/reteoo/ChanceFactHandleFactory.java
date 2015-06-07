package org.drools.chance.reteoo;


import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.QueryElementFactHandle;
import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.core.reteoo.ReteooFactHandleFactory;
import org.drools.core.rule.TypeDeclaration;
import org.drools.core.spi.FactHandleFactory;
import org.kie.api.runtime.rule.EntryPoint;

import java.util.Date;

public class ChanceFactHandleFactory extends ReteooFactHandleFactory {


    public ChanceFactHandleFactory() {
        super();
    }

    public ChanceFactHandleFactory(int id,
                                   long counter) {
        super( id,
               counter );
    }

    public InternalFactHandle newFactHandle( final int id,
                                             final Object object,
                                             final long recency,
                                             final ObjectTypeConf conf,
                                             final InternalWorkingMemory workingMemory,
                                             final EntryPoint wmEntryPoint) {

        if ( conf != null && conf.isEvent() ) {
            TypeDeclaration type = conf.getTypeDeclaration();
            long timestamp;
            if ( type.getTimestampExtractor() != null ) {
                if ( Date.class.isAssignableFrom( type.getTimestampExtractor().getExtractToClass() ) ) {
                    timestamp = ((Date) type.getTimestampExtractor().getValue( workingMemory,
                                                                               object )).getTime();
                } else {
                    timestamp = type.getTimestampExtractor().getLongValue( workingMemory,
                                                                           object );
                }
            } else {
                timestamp = workingMemory.getTimerService().getCurrentTime();
            }
            long duration = 0;
            if ( type.getDurationExtractor() != null ) {
                duration = type.getDurationExtractor().getLongValue( workingMemory,
                                                                     object );
            }
            return new ChanceEventFactHandle( id,
                                              object,
                                              recency,
                                              timestamp,
                                              duration,
                                              wmEntryPoint,
                                              conf != null && conf.isTrait() );
        } else {
            return new DefaultChanceFactHandle( id,
                                                object,
                                                recency,
                                                wmEntryPoint,
                                                conf != null && conf.isTrait() );
        }
    }


    /* (non-Javadoc)
         * @see org.drools.reteoo.FactHandleFactory#newInstance()
         */
    public FactHandleFactory newInstance() {
        return new ChanceFactHandleFactory();
    }

    public FactHandleFactory newInstance( int id,
                                          long counter ) {
        return new ChanceFactHandleFactory( id,
                                            counter );
    }

    public Class getFactHandleType() {
        return ChanceFactHandle.class;
    }

}
