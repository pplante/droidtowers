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

package org.apach3.http.client.protocol;

import java.io.IOException;

import org.apach3.commons.logging.Log;
import org.apach3.commons.logging.LogFactory;
import org.apach3.http.annotation.Immutable;

import org.apach3.http.HttpException;
import org.apach3.http.HttpRequest;
import org.apach3.http.HttpRequestInterceptor;
import org.apach3.http.conn.HttpRoutedConnection;
import org.apach3.http.conn.routing.HttpRoute;
import org.apach3.http.protocol.ExecutionContext;
import org.apach3.http.protocol.HTTP;
import org.apach3.http.protocol.HttpContext;

/**
 * This protocol interceptor is responsible for adding <code>Connection</code>
 * or <code>Proxy-Connection</code> headers to the outgoing requests, which
 * is essential for managing persistence of <code>HTTP/1.0</code> connections.
 *
 * @since 4.0
 */
@Immutable
public class RequestClientConnControl implements HttpRequestInterceptor {

    private final Log log = LogFactory.getLog(getClass());

    private static final String PROXY_CONN_DIRECTIVE = "Proxy-Connection";

    public RequestClientConnControl() {
        super();
    }

    public void process(final HttpRequest request, final HttpContext context)
            throws HttpException, IOException {
        if (request == null) {
            throw new IllegalArgumentException("HTTP request may not be null");
        }

        String method = request.getRequestLine().getMethod();
        if (method.equalsIgnoreCase("CONNECT")) {
            request.setHeader(PROXY_CONN_DIRECTIVE, HTTP.CONN_KEEP_ALIVE);
            return;
        }

        // Obtain the client connection (required)
        HttpRoutedConnection conn = (HttpRoutedConnection) context.getAttribute(
                ExecutionContext.HTTP_CONNECTION);
        if (conn == null) {
            this.log.debug("HTTP connection not set in the context");
            return;
        }

        HttpRoute route = conn.getRoute();

        if (route.getHopCount() == 1 || route.isTunnelled()) {
            if (!request.containsHeader(HTTP.CONN_DIRECTIVE)) {
                request.addHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_KEEP_ALIVE);
            }
        }
        if (route.getHopCount() == 2 && !route.isTunnelled()) {
            if (!request.containsHeader(PROXY_CONN_DIRECTIVE)) {
                request.addHeader(PROXY_CONN_DIRECTIVE, HTTP.CONN_KEEP_ALIVE);
            }
        }
    }

}
