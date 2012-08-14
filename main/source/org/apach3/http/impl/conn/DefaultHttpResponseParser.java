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

package org.apach3.http.impl.conn;

import java.io.IOException;

import org.apach3.http.annotation.ThreadSafe;

import org.apach3.commons.logging.Log;
import org.apach3.commons.logging.LogFactory;
import org.apach3.http.HttpException;
import org.apach3.http.HttpResponse;
import org.apach3.http.HttpResponseFactory;
import org.apach3.http.NoHttpResponseException;
import org.apach3.http.ProtocolException;
import org.apach3.http.StatusLine;
import org.apach3.http.impl.io.AbstractMessageParser;
import org.apach3.http.io.SessionInputBuffer;
import org.apach3.http.message.LineParser;
import org.apach3.http.message.ParserCursor;
import org.apach3.http.params.HttpParams;
import org.apach3.http.util.CharArrayBuffer;

/**
 * Default HTTP response parser implementation.
 * <p>
 * The following parameters can be used to customize the behavior of this
 * class:
 * <ul>
 *  <li>{@link org.apach3.http.params.CoreConnectionPNames#MAX_HEADER_COUNT}</li>
 *  <li>{@link org.apach3.http.params.CoreConnectionPNames#MAX_LINE_LENGTH}</li>
 * </ul>
 *
 * @since 4.2
 */
@ThreadSafe // no public methods
public class DefaultHttpResponseParser extends AbstractMessageParser<HttpResponse> {

    private final Log log = LogFactory.getLog(getClass());

    private final HttpResponseFactory responseFactory;
    private final CharArrayBuffer lineBuf;

    public DefaultHttpResponseParser(
            final SessionInputBuffer buffer,
            final LineParser parser,
            final HttpResponseFactory responseFactory,
            final HttpParams params) {
        super(buffer, parser, params);
        if (responseFactory == null) {
            throw new IllegalArgumentException
                ("Response factory may not be null");
        }
        this.responseFactory = responseFactory;
        this.lineBuf = new CharArrayBuffer(128);
    }

    @Override
    protected HttpResponse parseHead(
            final SessionInputBuffer sessionBuffer) throws IOException, HttpException {
        //read out the HTTP status string
        int count = 0;
        ParserCursor cursor = null;
        do {
            // clear the buffer
            this.lineBuf.clear();
            int i = sessionBuffer.readLine(this.lineBuf);
            if (i == -1 && count == 0) {
                // The server just dropped connection on us
                throw new NoHttpResponseException("The target server failed to respond");
            }
            cursor = new ParserCursor(0, this.lineBuf.length());
            if (lineParser.hasProtocolVersion(this.lineBuf, cursor)) {
                // Got one
                break;
            } else if (i == -1 || reject(this.lineBuf, count)) {
                // Giving up
                throw new ProtocolException("The server failed to respond with a " +
                        "valid HTTP response");
            }
            if (this.log.isDebugEnabled()) {
                this.log.debug("Garbage in response: " + this.lineBuf.toString());
            }
            count++;
        } while(true);
        //create the status line from the status string
        StatusLine statusline = lineParser.parseStatusLine(this.lineBuf, cursor);
        return this.responseFactory.newHttpResponse(statusline, null);
    }

    protected boolean reject(CharArrayBuffer line, int count) {
        return false;
    }

}
