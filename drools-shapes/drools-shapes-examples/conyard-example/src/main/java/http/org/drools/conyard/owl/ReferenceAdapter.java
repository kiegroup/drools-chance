package http.org.drools.conyard.owl;


import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public class ReferenceAdapter extends XmlAdapter<UIdAble,Thing> {


    private Map<String,UIdAble> cache = new HashMap<String, UIdAble>();

    public ReferenceAdapter() {
        cache = new HashMap<String, UIdAble>();

    }

    @Override
    public Thing unmarshal(UIdAble v) throws Exception {
        if ( cache.containsKey( v.getUniversalId() ) ) {
            return (Thing) ( cache.get( v.getUniversalId() ) );
        } else {
            UIdAble reborn = v;
            String baseType = this.getClass().getPackage().getName() + "." + v.getActualType();
            String actualClass = baseType + "Impl";
            if ( ! v.getClass().getName().equals( actualClass ) ) {

                Class k = Class.forName( actualClass );
                if ( k != null ) {
                    String desiredClass = baseType + "$$Shadow";
                    Class shadowInterface = Class.forName(desiredClass );
                    if ( shadowInterface != null ) {
                        Constructor con = k.getConstructor( shadowInterface );
                        reborn = (UIdAble) con.newInstance( v );
                    }
                }
            }

            cache.put( reborn.getUniversalId(), reborn );
            return (Thing) reborn;
        }
    }

    @Override
    public UIdAble marshal(Thing v) throws Exception {

        if ( v instanceof UIdAble) {
            UIdAble x = (UIdAble) v;
            
            x.setActualType(x.getClass().getSimpleName().substring(0, x.getClass().getSimpleName().lastIndexOf("Impl")));
            
            if ( ! cache.containsKey(x.getUniversalId()) ) {
                cache.put( x.getUniversalId(), x );
                return  x;
            } else {
                Class k = x.getClass();
                UIdAble alter = (UIdAble) k.newInstance();
                alter.setUniversalId( x.getUniversalId() );
                alter.setReference( true );
                return alter;
            }
        }
        return null;
    }





}
