package org.drools.chance.reteoo;


import org.drools.common.DefaultFactHandle;
import org.drools.common.EventFactHandle;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.ObjectTypeConf;
import org.drools.reteoo.ReteooFactHandleFactory;
import org.drools.rule.TypeDeclaration;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;
import org.drools.spi.FactHandleFactory;

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

    public final InternalFactHandle newFactHandle(final int id,
                                                  final Object object,
                                                  final long recency,
                                                  final ObjectTypeConf conf,
                                                  final InternalWorkingMemory workingMemory,
                                                  final WorkingMemoryEntryPoint wmEntryPoint) {
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
                    wmEntryPoint );
        } else {
            return new DefaultChanceFactHandle( id,
                    object,
                    recency,
                    wmEntryPoint );
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
