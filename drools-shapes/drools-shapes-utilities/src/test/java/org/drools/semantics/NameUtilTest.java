package org.drools.semantics;

import org.drools.semantics.utils.NameUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NameUtilTest {

    @Test
    public void testCompactWithTrailingUnderscores() {
        String s = "_foo_bar___";
        String t = NameUtils.compactUpperCase( s );
        System.out.println( t );
        assertEquals( "FooBar3", t );
    }

    @Test
    public void testPluralizeWithTrails() {
        String s = "_foo_bar___";
        String t = NameUtils.pluralize( s );
        System.out.println( t );
        assertEquals( "_foo_bars___", t );

        String u = NameUtils.getter( s, null, null, true );
        assertEquals( "get_Foo_Bars3", u );
    }

}
