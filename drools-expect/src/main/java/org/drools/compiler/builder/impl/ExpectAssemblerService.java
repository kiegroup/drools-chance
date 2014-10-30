package org.drools.compiler.builder.impl;

import it.unibo.deis.lia.org.drools.expectations.ECEVisitor;
import org.antlr.runtime.CommonTokenStream;
import org.drools.compiler.compiler.DroolsParserException;
import org.drools.compiler.compiler.ParserError;
import org.drools.compiler.lang.DRLLexer;
import org.drools.compiler.lang.DRLParser;
import org.drools.compiler.lang.DrlDumper;
import org.drools.compiler.lang.ECEParser;
import org.drools.compiler.lang.api.ECEPackageDescrBuilder;
import org.drools.compiler.lang.descr.PackageDescr;
import it.unibo.deis.lia.org.drools.expectations.DRLExpectationHelper;
import org.drools.core.io.internal.InternalResource;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;
import org.kie.internal.assembler.KieAssemblerService;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.conf.LanguageLevelOption;

import java.io.InputStream;

import static org.drools.compiler.compiler.DRLFactory.buildLexer;
import static org.drools.compiler.compiler.DRLFactory.buildParser;

public class ExpectAssemblerService implements KieAssemblerService {

    @Override
    public ResourceType getResourceType() {
        return ECE.ECE;
    }

    @Override
    public Class getServiceInterface() {
        return KieAssemblerService.class;
    }

    @Override
    public void addResource( KnowledgeBuilder kbuilder, Resource resource, ResourceType type, ResourceConfiguration configuration ) throws Exception {
        InputStream is = resource.getInputStream();
        String encoding = resource instanceof InternalResource ? ((InternalResource) resource).getEncoding() : null;
        DRLLexer lexer = buildLexer( is, encoding, LanguageLevelOption.DRL6 );

        CommonTokenStream stream = new CommonTokenStream( lexer );
        ECEParser parser = new ECEParser( stream );

        ECEPackageDescrBuilder packageDescrBuilder = parser.compile();
        packageDescrBuilder.newImport().target( DRLExpectationHelper.EXP_PACKAGE + ".*" );

        PackageDescr packageDescr = packageDescrBuilder.getDescr();
        new ECEVisitor( packageDescr ).visit( packageDescrBuilder );

        DrlDumper dumpr = new DrlDumper();
        System.err.println( dumpr.dump( packageDescr ) );

        KnowledgeBuilderImpl kbuilderImpl = (KnowledgeBuilderImpl) kbuilder;
        kbuilderImpl.addPackage( packageDescr );

        reportErrors( lexer, parser, resource, kbuilderImpl );

    }

    private void reportErrors( final DRLLexer lexer, final DRLParser parser, Resource resource, KnowledgeBuilderImpl kbuilderImpl ) {
        for ( final DroolsParserException recogErr : lexer.getErrors() ) {
            final ParserError err = new ParserError( resource,
                                                     recogErr.getMessage(),
                                                     recogErr.getLineNumber(),
                                                     recogErr.getColumn() );
            kbuilderImpl.addBuilderResult( err );
        }
        for ( final DroolsParserException recogErr : parser.getErrors() ) {
            final ParserError err = new ParserError( resource,
                                                     recogErr.getMessage(),
                                                     recogErr.getLineNumber(),
                                                     recogErr.getColumn() );
            kbuilderImpl.addBuilderResult( err );
        }
    }

}
