// Copyright (c) 2022,2023 Contributors to the Eclipse Foundation
//
// See the NOTICE file(s) distributed with this work for additional
// information regarding copyright ownership.
//
// This program and the accompanying materials are made available under the
// terms of the Apache License, Version 2.0 which is available at
// https://www.apache.org/licenses/LICENSE-2.0.
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
// WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
// License for the specific language governing permissions and limitations
// under the License.
//
// SPDX-License-Identifier: Apache-2.0
package org.eclipse.tractusx.agents.conforming.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.tractusx.agents.conforming.ConformingAgent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.stream.Collectors;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

@Provider
public class SparqlProvider implements MessageBodyReader, MessageBodyWriter {

    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean isReadable(Class aclass, Type type, Annotation[] annotations, MediaType mediaType) {
        return mediaType.isCompatible(ConformingAgent.sq);
    }

    @Override
    public Object readFrom(Class aclass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap multivaluedMap, InputStream inputStream) throws IOException, WebApplicationException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    @Override
    public boolean isWriteable(Class aclass, Type type, Annotation[] annotations, MediaType mediaType) {
        return mediaType.isCompatible(ConformingAgent.sq);
    }

    @Override
    public void writeTo(Object o, Class aclass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap multivaluedMap, OutputStream outputStream) throws IOException, WebApplicationException {
        new OutputStreamWriter(outputStream).append(String.valueOf(o));
    }
}
