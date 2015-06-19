
package org.drools.semantics.builder.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MetaclassModelImpl extends AbstractJavaModelImpl implements MetaclassModel {

    protected String getFileName( String key ) {
        return super.getFileName( key ) + "_";
    }

    @Override
    public boolean streamFactory( String code, String path ) {
        File f =  new File( path + slash + "MetaFactory.java" );

        if ( ! f.getParentFile().exists() ) {
            f.getParentFile().mkdirs();
        }

        try {
            FileOutputStream fos = new FileOutputStream( f );
            fos.write( code.getBytes() );
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
