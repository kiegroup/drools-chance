
import com.foo.SubKlass;
import com.foo.SubKlass_;
import org.junit.Test;
import org.test.Klass;
import org.w3._2002._07.owl.ThingImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class MetadataTest {


    @Test
    public void testKlassAndSubKlassWithImpl() {
        SubKlass ski = new com.foo.SubKlassImpl();
        ski.setSubProp( 42 );
        ski.setProp( "hello" );

        SubKlass_ sk = new SubKlass_( ski );

        assertEquals( 42, (int) sk.subProp.get( ski ) );
        assertEquals( "hello", sk.prop.get( ski ) );

        sk.modify().prop( "bye" ).subProp( -99 ).call();

        assertEquals( -99, (int) sk.subProp.get( ski ) );
        assertEquals( "bye", sk.prop.get( ski ) );
    }

    @Test
    public void testKlassAndSubKlassWithHolderImpl() {
        com.foo.SubKlassImpl ski = new com.foo.SubKlassImpl();
        ski.setSubProp( 42 );
        ski.setProp( "hello" );

        SubKlass_ sk = ski.get_();

        assertEquals( 42, (int) sk.subProp.get( ski ) );
        assertEquals( "hello", sk.prop.get( ski ) );

        sk.modify().prop( "bye" ).subProp( -99 ).call();

        assertEquals( -99, (int) sk.subProp.get( ski ) );
        assertEquals( "bye", sk.prop.get( ski ) );
    }


    @Test
    public void testKlassAndSubKlassWithInterfaces() {
        SubKlass ski = new Foo();
        ski.setSubProp( 42 );
        ski.setProp( "hello" );

        SubKlass_ sk = new SubKlass_( ski );

        assertEquals( 42, (int) sk.subProp.get( ski ) );
        assertEquals( "hello", sk.prop.get( ski ) );

        sk.modify().subProp( -99 ).prop( "bye" ).call();

        assertEquals( -99, (int) sk.subProp.get( ski ) );
        assertEquals( "bye", sk.prop.get( ski ) );

        System.out.println( ((Foo) ski).map );
        Map tgt = new HashMap();
        tgt.put( "prop", "bye" );
        tgt.put( "subProp", -99 );
        assertEquals( tgt, ((Foo) ski).map );
    }



    public static class Foo extends ThingImpl implements SubKlass {

        public Map<String,Object> map = new HashMap<String,Object>();

        @Override
        public String getProp() {
            return (String) map.get( "prop" );
        }

        @Override
        public void setProp( String value ) {
            map.put( "prop", value );
        }

        @Override
        public void addProp( String x ) {

        }

        @Override
        public void removeProp( Object x ) {

        }

        @Override
        public Boolean getFlag() {
            return (Boolean) map.get( "flag" );
        }

        @Override
        public void setFlag( Boolean value ) {
            map.put( "flag", value );
        }

        @Override
        public void addFlag( Boolean x ) {

        }

        @Override
        public void removeFlag( Object x ) {

        }

        @Override
        public Integer getSubProp() {
            return (Integer) map.get( "subProp" );
        }

        @Override
        public void setSubProp( Integer value ) {
            map.put( "subProp", value );
        }

        @Override
        public void addSubProp( Integer x ) {

        }

        @Override
        public void removeSubProp( Object x ) {

        }
    }


}

