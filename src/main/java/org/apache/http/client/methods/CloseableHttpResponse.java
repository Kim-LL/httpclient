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

package org.apache.http.client.methods;

import java.io.Closeable;

import org.apache.http.HttpResponse;

/**
 * Extended version of the {@link HttpResponse} interface that also extends {@link Closeable}.
 *
 * @since 4.3
 */
public interface CloseableHttpResponse extends HttpResponse, Closeable {

    /**
     * 获取远程服务器 IP 地址
     * @return String: 请求的远程 IP 地址
     */
    String getRemoteAddress();

    /**
     * 获取建立链接消耗的时间
     * @return Long: 返回建立连接时间消耗（单位: 毫秒）
     */
    long getConnectElapseMillisecond();

    /**
     * 获取发送请求后到接收到响应消耗时间
     * @return Long: 返回建立连接后发出请求到得到请求的时间消耗（单位: 毫秒）
     */
    long getResponseElapseMillisecond();
}
