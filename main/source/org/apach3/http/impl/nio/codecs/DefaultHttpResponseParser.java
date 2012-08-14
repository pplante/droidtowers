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

package org.apach3.http.impl.nio.codecs;

import org.apach3.http.HttpException;
import org.apach3.http.HttpResponse;
import org.apach3.http.HttpResponseFactory;
import org.apach3.http.StatusLine;
import org.apach3.http.ParseException;
import org.apach3.http.annotation.NotThreadSafe;
import org.apach3.http.message.LineParser;
import org.apach3.http.message.ParserCursor;
import org.apach3.http.nio.NHttpMessageParser;
import org.apach3.http.nio.reactor.SessionInputBuffer;
import org.apach3.http.params.HttpParams;
import org.apach3.http.util.CharArrayBuffer;

/**
 * Default {@link NHttpMessageParser} implementation for {@link HttpResponse}s.
 * <p>
 * The following parameters can be used to customize the behavior of this
 * class:
 * <ul>
 *  <li>{@link org.apach3.http.params.CoreConnectionPNames#MAX_HEADER_COUNT}</li>
 *  <li>{@link org.apach3.http.params.CoreConnectionPNames#MAX_LINE_LENGTH}</li>
 * </ul>
 *
 * @since 4.1
 */
@NotThreadSafe
public class DefaultHttpResponseParser extends AbstractMessageParser<HttpResponse> {

    private final HttpResponseFactory responseFactory;

    public DefaultHttpResponseParser(
            final SessionInputBuffer buffer,
            final LineParser parser,
            final HttpResponseFactory responseFactory,
            final HttpParams params) {
        super(buffer, parser, params);
        if (responseFactory == null) {
            throw new IllegalArgumentException("Response factory may not be null");
        }
        this.responseFactory = responseFactory;
    }

    @Override
    protected HttpResponse createMessage(final CharArrayBuffer buffer)
            throws HttpException, ParseException {
        ParserCursor cursor = new ParserCursor(0, buffer.length());
        StatusLine statusline = lineParser.parseStatusLine(buffer, cursor);
        return this.responseFactory.newHttpResponse(statusline, null);
    }

}
