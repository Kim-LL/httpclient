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

package org.apache.http.impl.execchain;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.http.HttpClientConnection;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.concurrent.Cancellable;
import org.apache.http.conn.ConnectionReleaseTrigger;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.conn.CPoolProxy;

/**
 * Internal connection holder.
 *
 * @since 4.3
 */
@Contract(threading = ThreadingBehavior.SAFE)
class ConnectionHolder implements ConnectionReleaseTrigger, Cancellable, Closeable {

    private final Log log;

    private final HttpClientConnectionManager manager;
    // 实际上它是 CPoolProxy
//    private final HttpClientConnection managedConn;
    private final CPoolProxy managedConn;
    private final AtomicBoolean released;
    private volatile boolean reusable;
    private volatile Object state;
    private volatile long validDuration;
    private volatile TimeUnit timeUnit;

    private final StringBuffer bufferRemoteAddress;

    public ConnectionHolder(
            final Log log,
            final HttpClientConnectionManager manager,
            final HttpClientConnection managedConn) {
        super();
        this.log = log;
        this.manager = manager;
        this.managedConn = (CPoolProxy) managedConn;
        this.released = new AtomicBoolean(false);
        this.bufferRemoteAddress = new StringBuffer();
    }

    public boolean isReusable() {
        return this.reusable;
    }

    public void markReusable() {
        this.reusable = true;
    }

    public void markNonReusable() {
        this.reusable = false;
    }

    public void setState(final Object state) {
        this.state = state;
    }

    public void setValidFor(final long duration, final TimeUnit timeUnit) {
        synchronized (this.managedConn) {
            this.validDuration = duration;
            this.timeUnit = timeUnit;
        }
    }

    public String getRemoteAddress(){
        if(bufferRemoteAddress.length() == 0){
            InetAddress remoteAddress = managedConn.getRemoteAddress();
            String hostAddress = remoteAddress.getHostAddress();
            bufferRemoteAddress.append(hostAddress);
        }
        return bufferRemoteAddress.toString();
    }

    private void releaseConnection(final boolean reusable) {
        if (this.released.compareAndSet(false, true)) {
            synchronized (this.managedConn) {
                if(bufferRemoteAddress.length() == 0){
                    bufferRemoteAddress.append(managedConn.getRemoteAddress().getHostAddress());
                }

                if (reusable) {
                    this.manager.releaseConnection(this.managedConn,
                            this.state, this.validDuration, this.timeUnit);
                } else {
                    try {
                        this.managedConn.close();
                        log.debug("Connection discarded");
                    } catch (final IOException ex) {
                        if (this.log.isDebugEnabled()) {
                            this.log.debug(ex.getMessage(), ex);
                        }
                    } finally {
                        this.manager.releaseConnection(
                                this.managedConn, null, 0, TimeUnit.MILLISECONDS);
                    }
                }
            }
        }
    }

    @Override
    public void releaseConnection() {
        releaseConnection(this.reusable);
    }

    @Override
    public void abortConnection() {
        if (this.released.compareAndSet(false, true)) {
            synchronized (this.managedConn) {
                if(bufferRemoteAddress.length() == 0){
                    InetAddress remoteAddress = managedConn.getRemoteAddress();
                    if(remoteAddress != null){
                        String hostAddress = remoteAddress.getHostAddress();
                        bufferRemoteAddress.append(hostAddress);
                    }
                }
                try {
                    this.managedConn.shutdown();
                    log.debug("Connection discarded");
                } catch (final IOException ex) {
                    if (this.log.isDebugEnabled()) {
                        this.log.debug(ex.getMessage(), ex);
                    }
                } finally {
                    this.manager.releaseConnection(
                            this.managedConn, null, 0, TimeUnit.MILLISECONDS);
                }
            }
        }
    }

    @Override
    public boolean cancel() {
        final boolean alreadyReleased = this.released.get();
        log.debug("Cancelling request execution");
        abortConnection();
        return !alreadyReleased;
    }

    public boolean isReleased() {
        return this.released.get();
    }

    @Override
    public void close() throws IOException {
        releaseConnection(false);
    }

    public long getConnectElapseMillisecond() {
        return this.managedConn.getConnectElapseMillisecond();
    }

}
