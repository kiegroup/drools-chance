package http.www.semanticweb.org.ontologies._2012._1.rule.merged.owl;


import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ReferenceAdapter extends XmlAdapter<UIdAble,Thing> {


    private Map<String,UIdAble> cache = new HashMap<String, UIdAble>();

    public ReferenceAdapter() {
        cache = new HashMap<String, UIdAble>();

    }

    public Collection getObjects() {
        return cache.values();
    }

    @Override
    public Thing unmarshal(UIdAble v) throws Exception {
        if ( cache.containsKey( v.getDyEntryId() ) ) {
            return (Thing) ( cache.get( v.getDyEntryId() ) );
        } else {
            UIdAble reborn = v;
            String baseType = this.getClass().getPackage().getName() + "." + v.getDyEntryType();
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

            cache.put( reborn.getDyEntryId(), reborn );
            return (Thing) reborn;
        }
    }

    @Override
    public UIdAble marshal(Thing v) throws Exception {

        if ( v instanceof UIdAble) {
            UIdAble x = (UIdAble) v;
            
            x.setDyEntryType( x.getClass().getSimpleName().substring(0, x.getClass().getSimpleName().lastIndexOf("Impl")));
            
            if ( ! cache.containsKey(x.getDyEntryId()) ) {
                cache.put( x.getDyEntryId(), x );
                return  x;
            } else {
                Class k = x.getClass();
                UIdAble alter = (UIdAble) k.newInstance();
                alter.setDyEntryId( x.getDyEntryId() );
                alter.setDyReference( true );
                return alter;
            }
        }
        return null;
    }





}
