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
