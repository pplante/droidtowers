package org.apach3.http.contrib.logging;
/*
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

import org.apach3.http.HttpResponseFactory;
import org.apach3.http.annotation.Immutable;
import org.apach3.http.impl.DefaultHttpResponseFactory;
import org.apach3.http.impl.nio.DefaultNHttpClientConnection;
import org.apach3.http.impl.nio.DefaultNHttpClientConnectionFactory;
import org.apach3.http.nio.reactor.IOSession;
import org.apach3.http.nio.util.ByteBufferAllocator;
import org.apach3.http.nio.util.HeapByteBufferAllocator;
import org.apach3.http.params.HttpParams;

@Immutable
public class LoggingNHttpClientConnectionFactory extends DefaultNHttpClientConnectionFactory {

    public LoggingNHttpClientConnectionFactory(
            final HttpResponseFactory responseFactory,
            final ByteBufferAllocator allocator,
            final HttpParams params) {
        super(responseFactory, allocator, params);
    }

    public LoggingNHttpClientConnectionFactory(final HttpParams params) {
        this(new DefaultHttpResponseFactory(), new HeapByteBufferAllocator(), params);
    }

    @Override
    protected DefaultNHttpClientConnection createConnection(
            final IOSession session,
            final HttpResponseFactory responseFactory,
            final ByteBufferAllocator allocator,
            final HttpParams params) {
        return new LoggingNHttpClientConnection(session, responseFactory, allocator, params);
    }

}
