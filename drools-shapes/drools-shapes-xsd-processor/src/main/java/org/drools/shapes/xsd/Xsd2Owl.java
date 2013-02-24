/*
 * Copyright 2013 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.shapes.xsd;


import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.w3._2001.xmlschema.Schema;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;

public interface Xsd2Owl {

    URL getSchemaURL( String resource );

    Schema parse( URL resourceName );

    OWLOntology transform(Schema schema, URL schemaLocation, boolean verbose, boolean checkConsistency);

    String compactXMLSchema( String resourceName ) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException, TransformerException, URISyntaxException;

    boolean stream(OWLOntology onto, OutputStream stream, OWLOntologyFormat format);
}
