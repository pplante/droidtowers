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

package org.apach3.http.nio.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apach3.http.ConnectionReuseStrategy;
import org.apach3.http.HttpEntity;
import org.apach3.http.HttpEntityEnclosingRequest;
import org.apach3.http.HttpException;
import org.apach3.http.HttpRequest;
import org.apach3.http.HttpResponse;
import org.apach3.http.HttpResponseFactory;
import org.apach3.http.annotation.Immutable;
import org.apach3.http.nio.ContentDecoder;
import org.apach3.http.nio.ContentEncoder;
import org.apach3.http.nio.NHttpServerConnection;
import org.apach3.http.nio.NHttpServiceHandler;
import org.apach3.http.nio.entity.BufferingNHttpEntity;
import org.apach3.http.nio.entity.ConsumingNHttpEntity;
import org.apach3.http.nio.util.ByteBufferAllocator;
import org.apach3.http.nio.util.HeapByteBufferAllocator;
import org.apach3.http.params.HttpParams;
import org.apach3.http.protocol.HttpContext;
import org.apach3.http.protocol.HttpExpectationVerifier;
import org.apach3.http.protocol.HttpProcessor;
import org.apach3.http.protocol.HttpRequestHandler;
import org.apach3.http.protocol.HttpRequestHandlerResolver;

/**
 * Service protocol handler implementations that provide compatibility with
 * the blocking I/O by storing the full content of HTTP messages in memory.
 * The {@link HttpRequestHandler#handle(HttpRequest, HttpResponse, HttpContext)}
 * method will fire only when the entire message content has been read into
 * an in-memory buffer. Please note that request processing take place the
 * main I/O thread and therefore individual HTTP request handlers should not
 * block indefinitely.
 * <p>
 * When using this protocol handler {@link HttpEntity}'s content can be
 * generated / consumed using standard {@link InputStream}/{@link OutputStream}
 * classes.
 * <p>
 * IMPORTANT: This protocol handler should be used only when dealing with HTTP
 * messages that are known to be limited in length.
 *
 * @since 4.0
 *
 * @deprecated (4.2) use {@link HttpAsyncService}
 */
@Deprecated
@Immutable // provided injected dependencies are immutable
public class BufferingHttpServiceHandler implements NHttpServiceHandler {

    private final AsyncNHttpServiceHandler asyncHandler;

    private HttpRequestHandlerResolver handlerResolver;

    public BufferingHttpServiceHandler(
            final HttpProcessor httpProcessor,
            final HttpResponseFactory responseFactory,
            final ConnectionReuseStrategy connStrategy,
            final ByteBufferAllocator allocator,
            final HttpParams params) {
        super();
        this.asyncHandler = new AsyncNHttpServiceHandler(
                httpProcessor,
                responseFactory,
                connStrategy,
                allocator,
                params);
        this.asyncHandler.setHandlerResolver(new RequestHandlerResolverAdaptor());
    }

    public BufferingHttpServiceHandler(
            final HttpProcessor httpProcessor,
            final HttpResponseFactory responseFactory,
            final ConnectionReuseStrategy connStrategy,
            final HttpParams params) {
        this(httpProcessor, responseFactory, connStrategy,
                new HeapByteBufferAllocator(), params);
    }

    public void setEventListener(final EventListener eventListener) {
        this.asyncHandler.setEventListener(eventListener);
    }

    public void setExpectationVerifier(final HttpExpectationVerifier expectationVerifier) {
        this.asyncHandler.setExpectationVerifier(expectationVerifier);
    }

    public void setHandlerResolver(final HttpRequestHandlerResolver handlerResolver) {
        this.handlerResolver = handlerResolver;
    }

    public void connected(final NHttpServerConnection conn) {
        this.asyncHandler.connected(conn);
    }

    public void closed(final NHttpServerConnection conn) {
        this.asyncHandler.closed(conn);
    }

    public void requestReceived(final NHttpServerConnection conn) {
        this.asyncHandler.requestReceived(conn);
    }

    public void inputReady(final NHttpServerConnection conn, final ContentDecoder decoder) {
        this.asyncHandler.inputReady(conn, decoder);
    }

    public void responseReady(final NHttpServerConnection conn) {
        this.asyncHandler.responseReady(conn);
    }

    public void outputReady(final NHttpServerConnection conn, final ContentEncoder encoder) {
        this.asyncHandler.outputReady(conn, encoder);
    }

    public void exception(final NHttpServerConnection conn, final HttpException httpex) {
        this.asyncHandler.exception(conn, httpex);
    }

    public void exception(final NHttpServerConnection conn, final IOException ioex) {
        this.asyncHandler.exception(conn, ioex);
    }

    public void timeout(NHttpServerConnection conn) {
        this.asyncHandler.timeout(conn);
    }

    class RequestHandlerResolverAdaptor implements NHttpRequestHandlerResolver {

        public NHttpRequestHandler lookup(final String requestURI) {
            HttpRequestHandler handler = handlerResolver.lookup(requestURI);
            if (handler != null) {
                return new RequestHandlerAdaptor(handler);
            } else {
                return null;
            }
        }

    }

    static class RequestHandlerAdaptor extends SimpleNHttpRequestHandler {

        private final HttpRequestHandler requestHandler;

        public RequestHandlerAdaptor(final HttpRequestHandler requestHandler) {
            super();
            this.requestHandler = requestHandler;
        }

        public ConsumingNHttpEntity entityRequest(
                final HttpEntityEnclosingRequest request,
                final HttpContext context) throws HttpException, IOException {
            return new BufferingNHttpEntity(
                    request.getEntity(),
                    new HeapByteBufferAllocator());
        }

        @Override
        public void handle(
                final HttpRequest request,
                final HttpResponse response,
                final HttpContext context) throws HttpException, IOException {
            this.requestHandler.handle(request, response, context);
        }

    }

}
