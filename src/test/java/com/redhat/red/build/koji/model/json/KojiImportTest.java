/**
 * Copyright (C) 2015 Red Hat, Inc. (jcasey@redhat.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.redhat.red.build.koji.model.json;

import com.redhat.red.build.koji.model.xmlrpc.KojiXmlRpcBindery;
import org.apache.commons.codec.digest.DigestUtils;
import org.commonjava.maven.atlas.ident.ref.ProjectVersionRef;
import org.commonjava.maven.atlas.ident.ref.SimpleProjectVersionRef;
import org.commonjava.rwx.impl.TrackingXmlRpcListener;
import org.commonjava.rwx.impl.estream.EventStreamParserImpl;
import org.commonjava.rwx.impl.jdom.JDomRenderer;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;

import static com.redhat.red.build.koji.testutil.TestResourceUtils.readTestResourceBytes;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created by jdcasey on 2/17/16.
 */
public class KojiImportTest
        extends AbstractJsonTest
{

    @Test
    public void jsonRoundTrip()
            throws VerificationException, IOException
    {
        KojiImport info = new KojiImport( KojiJsonConstants.DEFAULT_METADATA_VERSION, newBuildDescription(),
                                          Collections.singleton( newBuildRoot() ),
                                          Arrays.asList( newBuildOutput( 1001, "foo-1.jar" ),
                                                         newLogOutput( 1001, "build.log" ) )
                                                .stream()
                                                .collect( Collectors.toSet() ) );

        String json = mapper.writeValueAsString( info );
        System.out.println( json );

        KojiImport out = mapper.readValue( json, KojiImport.class );

        assertThat( out.getBuild(), equalTo( info.getBuild() ) );
    }

    @Test
    public void xmlRpcRender()
            throws Exception
    {
        ProjectVersionRef gav = new SimpleProjectVersionRef( "org.foo", "bar", "1.1" );

        Date start = new Date( System.currentTimeMillis() - 86400 );
        Date end = new Date( System.currentTimeMillis() - 43200 );

        KojiImport.Builder importBuilder = new KojiImport.Builder();

        int brId = 1;
        String pomPath = "org/foo/bar/1.1/bar-1.1.pom";
        byte[] pomBytes = readTestResourceBytes( "import-data/" + pomPath );

        KojiImport importMetadata = importBuilder.withNewBuildDescription( gav )
                                                 .withStartTime( start )
                                                 .withEndTime( end )
                                                 .withBuildSource( "http://builder.foo.com", "1.0" )
                                                 .parent()
                                                 .withNewBuildRoot( brId )
                                                 .withContentGenerator( "test-cg", "1.0" )
                                                 .withContainer( new BuildContainer( StandardBuildType.maven.name(),
                                                                                     StandardArchitecture.noarch.name() ) )
                                                 .withHost( "linux", StandardArchitecture.x86_64 )
                                                 .parent()
                                                 .withNewOutput( 1, new File( pomPath ).getName() )
                                                 .withFileSize( pomBytes.length )
                                                 .withChecksum( StandardChecksum.md5.name(), DigestUtils.md5Hex( pomBytes ) )
                                                 .withOutputType( "pom" )
                                                 .parent()
                                                 .build();

        JDomRenderer jdom = new JDomRenderer( new XMLOutputter( Format.getCompactFormat() ) );
        EventStreamParserImpl eventParser = new EventStreamParserImpl( new TrackingXmlRpcListener( jdom ) );

        new KojiXmlRpcBindery().render( eventParser, importMetadata );

        System.out.printf( "Event tree:\n\n%s\n\nXML:\n\n%s\n\n", eventParser.renderEventTree(), jdom.documentToString() );
    }

}
