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

import java.io.IOException;

import org.apach3.http.HttpRequest;
import org.apach3.http.annotation.NotThreadSafe;
import org.apach3.http.message.LineFormatter;
import org.apach3.http.nio.NHttpMessageWriter;
import org.apach3.http.nio.reactor.SessionOutputBuffer;
import org.apach3.http.params.HttpParams;
import org.apach3.http.util.CharArrayBuffer;

/**
 * Default {@link NHttpMessageWriter} implementation for {@link HttpRequest}s.
 *
 * @since 4.1
 */
@NotThreadSafe
public class DefaultHttpRequestWriter extends AbstractMessageWriter<HttpRequest> {

    public DefaultHttpRequestWriter(final SessionOutputBuffer buffer,
                             final LineFormatter formatter,
                             final HttpParams params) {
        super(buffer, formatter, params);
    }

    @Override
    protected void writeHeadLine(final HttpRequest message) throws IOException {
        CharArrayBuffer buffer = lineFormatter.formatRequestLine(
                this.lineBuf, message.getRequestLine());
        this.sessionBuffer.writeLine(buffer);
    }

}
