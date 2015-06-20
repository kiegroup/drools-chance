
import com.foo.MySubKlass;
import com.foo.MySubKlassImpl;
import com.foo.MySubKlass_;

import org.junit.Test;
import org.test.MyKlass;
import org.test.MyKlassImpl;
import org.test.MyKlass_;
import org.w3._2002._07.owl.ThingImpl;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class MetadataTest {


    @Test
    public void testKlassAndMySubKlassWithImpl() {
        MySubKlass ski = new com.foo.MySubKlassImpl();
        ski.setSubProp( 42 );
        ski.setProp( "hello" );

        MySubKlass_ sk = new MySubKlass_( ski );

        MyKlass_ k = new MyKlass_( null );




        assertEquals( 42, (int) sk.subProp.get( ski ) );
        assertEquals( "hello", sk.prop.get( ski ) );

        sk.modify().prop( "bye" ).subProp( -99 ).call();

        assertEquals( -99, (int) sk.subProp.get( ski ) );
        assertEquals( "bye", sk.prop.get( ski ) );
    }

    @Test
    public void testKlassAndMySubKlassWithHolderImpl() {
        com.foo.MySubKlassImpl ski = new com.foo.MySubKlassImpl();
        ski.setSubProp( 42 );
        ski.setProp( "hello" );

        MySubKlass_ sk = ski.get_();

        assertEquals( 42, (int) sk.subProp.get( ski ) );
        assertEquals( "hello", sk.prop.get( ski ) );

        sk.modify().prop( "bye" ).subProp( -99 ).call();

        assertEquals( -99, (int) sk.subProp.get( ski ) );
        assertEquals( "bye", sk.prop.get( ski ) );
    }


    @Test
    public void testDelayedInstantiation() {
        MySubKlass sk = (MySubKlass) MySubKlass_.newMySubKlass( "123" ).prop( "hello" ).call();

        assertEquals( "hello", sk.getProp() );
        assertEquals( URI.create( "123" ), sk.getId() );
        assertTrue( sk instanceof MySubKlassImpl );

    }

    @Test
    public void testNumProperties() {
        Field[] flds = MySubKlass_.class.getDeclaredFields();
        assertEquals( 1, flds.length );
    }



    @Test
    public void testImplicitID() {
        MySubKlass msk = (MySubKlass) MySubKlass_.newMySubKlass().call();
        assertNotNull( msk.getId() );
    }



}

