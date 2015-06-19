package org.drools.shapes;


import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.model.CPluginCustomization;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.Outline;
import org.apache.commons.lang3.StringUtils;
import org.drools.core.metadata.ManyToManyPropertyLiteral;
import org.drools.core.metadata.ManyToOnePropertyLiteral;
import org.drools.core.metadata.MetadataHolder;
import org.drools.core.metadata.OneToManyPropertyLiteral;
import org.drools.core.metadata.OneToOnePropertyLiteral;
import org.drools.core.metadata.ToManyPropertyLiteral;
import org.drools.core.metadata.ToOnePropertyLiteral;
import org.drools.semantics.builder.model.compilers.SemanticXSDModelCompilerImpl;
import org.drools.semantics.utils.NameUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class
        MetaClassPlugin extends MetadataPlugin {

    private static String metaAttribTempl = "metaAttrib";

    public String getOptionName() {
        return "Xmetaclass";
    }

    public String getUsage() {
        return "  -Xmetaclass";
    }



    @Override
    public boolean run(Outline outline, Options opt, ErrorHandler errorHandler) throws SAXException {

        for (ClassOutline co : outline.getClasses() ) {

            CPluginCustomization c = co.target.getCustomizations().find( uri.toString(), "type" );
            if( c == null ) {
                continue;
            }

            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put( "klassName", co.target.shortName );
            map.put( "typeName", c.element.getAttribute( "name" ) );
            map.put( "package", c.element.getAttribute( "package" ) );
            map.put( "supertypeName", c.element.getAttribute( "parent" ) );
            map.put( "supertypePackage", c.element.getAttribute( "parentPackage" ) );
            map.put( "typeIri", c.element.getAttribute( "iri" ) );

            String metaAttrib = SemanticXSDModelCompilerImpl.getTemplatedCode( metaAttribTempl, map );

            co.implClass._implements( MetadataHolder.class );
            co.implClass.direct( metaAttrib );

            c.markAsAcknowledged();

        }

        return true;
    }

}
