/*
 * Copyright 2013 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package area;

import junit.framework.Assert;
import org.drools.semantics.builder.model.Concept;
import org.drools.semantics.util.area.AreaTxnImpl;
import org.drools.semantics.util.area.PartialAreaTxnImpl;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class TestArea {

    @Test
    public void testOverlapping(){
        try {
            AreaTxnImpl area = new AreaTxnImpl( "specimen", new String[]{ "specimen.owl" } );
                area.makeAreaNodes();
                area.makeAreaRoots();
            PartialAreaTxnImpl parea = new PartialAreaTxnImpl( area.getAreas(), area.getEncoder(), area.getEncoderArea() );
            Set<Concept> olpc = parea.getOverlappingCodes();

            List<String> olp = Arrays.asList(
                    "FecalFluidSample",
                    "BodyFluidSample",
                    "BloodSpecimen",
                    "AcellularBlSpecimen",
                    "SerumSpecimen",
                    "SerumSpecFromBlProd" );

            Assert.assertTrue( olp.size() == olpc.size() );
            for( Concept cct: olpc ) {
                Assert.assertTrue( olp.contains( cct.getName() ) );
            }
        } catch ( IOException e ) {
            e.printStackTrace();
        }

    }
}
