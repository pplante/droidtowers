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
import java.util.Queue;

import org.apach3.commons.logging.Log;
import org.apach3.commons.logging.LogFactory;
import org.apach3.http.Header;
import org.apach3.http.HttpException;
import org.apach3.http.HttpRequest;
import org.apach3.http.HttpRequestInterceptor;
import org.apach3.http.auth.AuthOption;
import org.apach3.http.auth.AuthScheme;
import org.apach3.http.auth.AuthState;
import org.apach3.http.auth.AuthenticationException;
import org.apach3.http.auth.ContextAwareAuthScheme;
import org.apach3.http.auth.Credentials;
import org.apach3.http.protocol.HttpContext;

abstract class RequestAuthenticationBase implements HttpRequestInterceptor {

    final Log log = LogFactory.getLog(getClass());

    public RequestAuthenticationBase() {
        super();
    }

    void process(
            final AuthState authState,
            final HttpRequest request,
            final HttpContext context) throws HttpException, IOException {
        AuthScheme authScheme = authState.getAuthScheme();
        Credentials creds = authState.getCredentials();
        switch (authState.getState()) {
        case FAILURE:
            return;
        case SUCCESS:
            ensureAuthScheme(authScheme);
            if (authScheme.isConnectionBased()) {
                return;
            }
            break;
        case CHALLENGED:
            Queue<AuthOption> authOptions = authState.getAuthOptions();
            if (authOptions != null) {
                while (!authOptions.isEmpty()) {
                    AuthOption authOption = authOptions.remove();
                    authScheme = authOption.getAuthScheme();
                    creds = authOption.getCredentials();
                    authState.update(authScheme, creds);
                    if (this.log.isDebugEnabled()) {
                        this.log.debug("Generating response to an authentication challenge using "
                                + authScheme.getSchemeName() + " scheme");
                    }
                    try {
                        Header header = authenticate(authScheme, creds, request, context);
                        request.addHeader(header);
                        break;
                    } catch (AuthenticationException ex) {
                        if (this.log.isWarnEnabled()) {
                            this.log.warn(authScheme + " authentication error: " + ex.getMessage());
                        }
                    }
                }
                return;
            } else {
                ensureAuthScheme(authScheme);
            }
        }
        if (authScheme != null) {
            try {
                Header header = authenticate(authScheme, creds, request, context);
                request.addHeader(header);
            } catch (AuthenticationException ex) {
                if (this.log.isErrorEnabled()) {
                    this.log.error(authScheme + " authentication error: " + ex.getMessage());
                }
            }
        }
    }

    private void ensureAuthScheme(final AuthScheme authScheme) {
        if (authScheme == null) {
            throw new IllegalStateException("Auth scheme is not set");
        }
    }

    @SuppressWarnings("deprecation")
    private Header authenticate(
            final AuthScheme authScheme,
            final Credentials creds,
            final HttpRequest request,
            final HttpContext context) throws AuthenticationException {
        if (authScheme == null) {
            throw new IllegalStateException("Auth state object is null");
        }
        if (authScheme instanceof ContextAwareAuthScheme) {
            return ((ContextAwareAuthScheme) authScheme).authenticate(creds, request, context);
        } else {
            return authScheme.authenticate(creds, request);
        }
    }

}
