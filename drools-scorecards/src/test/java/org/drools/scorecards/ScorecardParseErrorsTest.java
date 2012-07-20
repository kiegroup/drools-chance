package org.drools.scorecards;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

public class ScorecardParseErrorsTest {

    boolean compileResult = true;
    private ScorecardCompiler scorecardCompiler;

    @Before
    public void setUp() throws Exception {
        scorecardCompiler = new ScorecardCompiler();
        compileResult = scorecardCompiler.compileFromExcel(PMMLDocumentTest.class.getResourceAsStream("/scoremodel_errors.xls"));
    }

    @Test
    public void testCompileFailureInd() throws Exception {
        Assert.assertFalse(compileResult);
    }

    @Test
    public void testErrorCount() throws Exception {
        Assert.assertEquals(4, scorecardCompiler.getScorecardParseErrors().size());
        Assert.assertEquals("$C$7", scorecardCompiler.getScorecardParseErrors().get(0).getErrorLocation());
        Assert.assertEquals("Scorecard Package is missing", scorecardCompiler.getScorecardParseErrors().get(0).getErrorMessage());
        for(ScorecardError error : scorecardCompiler.getScorecardParseErrors()){
            System.out.println(error.getErrorLocation()+"->"+error.getErrorMessage());
        }
    }
}
