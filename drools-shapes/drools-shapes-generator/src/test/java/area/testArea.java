package area;

import junit.framework.Assert;
import org.drools.semantics.builder.model.Concept;
import org.drools.semantics.util.area.AreaTxnImpl;
import org.drools.semantics.util.area.PartialAreaTxnImpl;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: mamad
 * Date: 4/3/13
 * Time: 2:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class testArea {

    @Test
    public void testOverlapping(){
        try {
            AreaTxnImpl area = new AreaTxnImpl("specimen",new String[]{"specimen.owl"});
            area.makeAreaNodes();
            area.makeAreaRoots();
            PartialAreaTxnImpl parea = new PartialAreaTxnImpl(area.getAreas(),area.getEncoder(),area.getEncoderArea());
            Set<Concept> olpc = parea.getOverlappingCodes();

            Set<String> olp = new HashSet<String>();
            olp.add("FecalFluidSample");
            olp.add("BodyFluidSample");
            olp.add("BloodSpecimen");
            olp.add("AcellularBlSpecimen");
            olp.add("SerumSpecimen");
            olp.add("SerumSpecFromBlProd");

            Assert.assertTrue(olp.size() == olpc.size());
            for(Concept cct: olpc){
                Assert.assertTrue(olp.contains(cct.getName()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
