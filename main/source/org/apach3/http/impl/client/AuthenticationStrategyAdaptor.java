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

package org.apach3.http.impl.client;

import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;

import org.apach3.commons.logging.Log;
import org.apach3.commons.logging.LogFactory;
import org.apach3.http.Header;
import org.apach3.http.HttpHost;
import org.apach3.http.HttpResponse;
import org.apach3.http.annotation.Immutable;
import org.apach3.http.auth.AuthOption;
import org.apach3.http.auth.AuthScheme;
import org.apach3.http.auth.AuthScope;
import org.apach3.http.auth.AuthenticationException;
import org.apach3.http.auth.Credentials;
import org.apach3.http.auth.MalformedChallengeException;
import org.apach3.http.client.AuthCache;
import org.apach3.http.client.AuthenticationHandler;
import org.apach3.http.client.AuthenticationStrategy;
import org.apach3.http.client.CredentialsProvider;
import org.apach3.http.client.params.AuthPolicy;
import org.apach3.http.client.protocol.ClientContext;
import org.apach3.http.protocol.HttpContext;

/**
 * @deprecated (4.2) do not use
 */
@Immutable
@Deprecated
class AuthenticationStrategyAdaptor implements AuthenticationStrategy {

    private final Log log = LogFactory.getLog(getClass());

    private final AuthenticationHandler handler;

    public AuthenticationStrategyAdaptor(final AuthenticationHandler handler) {
        super();
        this.handler = handler;
    }

    public boolean isAuthenticationRequested(
            final HttpHost authhost,
            final HttpResponse response,
            final HttpContext context) {
        return this.handler.isAuthenticationRequested(response, context);
    }

    public Map<String, Header> getChallenges(
            final HttpHost authhost,
            final HttpResponse response,
            final HttpContext context) throws MalformedChallengeException {
        return this.handler.getChallenges(response, context);
    }

    public Queue<AuthOption> select(
            final Map<String, Header> challenges,
            final HttpHost authhost,
            final HttpResponse response,
            final HttpContext context) throws MalformedChallengeException {
        if (challenges == null) {
            throw new IllegalArgumentException("Map of auth challenges may not be null");
        }
        if (authhost == null) {
            throw new IllegalArgumentException("Host may not be null");
        }
        if (response == null) {
            throw new IllegalArgumentException("HTTP response may not be null");
        }
        if (context == null) {
            throw new IllegalArgumentException("HTTP context may not be null");
        }

        Queue<AuthOption> options = new LinkedList<AuthOption>();
        CredentialsProvider credsProvider = (CredentialsProvider) context.getAttribute(
                ClientContext.CREDS_PROVIDER);
        if (credsProvider == null) {
            this.log.debug("Credentials provider not set in the context");
            return options;
        }

        AuthScheme authScheme;
        try {
            authScheme = this.handler.selectScheme(challenges, response, context);
        } catch (AuthenticationException ex) {
            if (this.log.isWarnEnabled()) {
                this.log.warn(ex.getMessage(), ex);
            }
            return options;
        }
        String id = authScheme.getSchemeName();
        Header challenge = challenges.get(id.toLowerCase(Locale.US));
        authScheme.processChallenge(challenge);

        AuthScope authScope = new AuthScope(
                authhost.getHostName(),
                authhost.getPort(),
                authScheme.getRealm(),
                authScheme.getSchemeName());

        Credentials credentials = credsProvider.getCredentials(authScope);
        if (credentials != null) {
            options.add(new AuthOption(authScheme, credentials));
        }
        return options;
    }

    public void authSucceeded(
            final HttpHost authhost, final AuthScheme authScheme, final HttpContext context) {
        AuthCache authCache = (AuthCache) context.getAttribute(ClientContext.AUTH_CACHE);
        if (isCachable(authScheme)) {
            if (authCache == null) {
                authCache = new BasicAuthCache();
                context.setAttribute(ClientContext.AUTH_CACHE, authCache);
            }
            if (this.log.isDebugEnabled()) {
                this.log.debug("Caching '" + authScheme.getSchemeName() +
                        "' auth scheme for " + authhost);
            }
            authCache.put(authhost, authScheme);
        }
    }

    public void authFailed(
            final HttpHost authhost, final AuthScheme authScheme, final HttpContext context) {
        AuthCache authCache = (AuthCache) context.getAttribute(ClientContext.AUTH_CACHE);
        if (authCache == null) {
            return;
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug("Removing from cache '" + authScheme.getSchemeName() +
                    "' auth scheme for " + authhost);
        }
        authCache.remove(authhost);
    }

    private boolean isCachable(final AuthScheme authScheme) {
        if (authScheme == null || !authScheme.isComplete()) {
            return false;
        }
        String schemeName = authScheme.getSchemeName();
        return schemeName.equalsIgnoreCase(AuthPolicy.BASIC) ||
                schemeName.equalsIgnoreCase(AuthPolicy.DIGEST);
    }

    public AuthenticationHandler getHandler() {
        return this.handler;
    }

}
