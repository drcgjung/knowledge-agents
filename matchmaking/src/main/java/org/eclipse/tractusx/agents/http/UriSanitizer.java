// Copyright (c) 2024,2024 Contributors to the Eclipse Foundation
//
// See the NOTICE file(s) distributed with this work for additional
// information regarding copyright ownership.
//
// This program and the accompanying materials are made available under the
// terms of the Apache License, Version 2.0 which is available at
// https://www.apache.org/licenses/LICENSE-2.0.
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
// WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
// License for the specific language governing permissions and limitations
// under the License.
//
// SPDX-License-Identifier: Apache-2.0
package org.eclipse.tractusx.agents.http;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.UriInfo;
import okhttp3.HttpUrl;

/**
 * sanitizes url related information
 */
public interface UriSanitizer {
    /**
     * sanitize an asset id
     *
     * @param assetId incoming asset id
     * @return sanizized asset id
     */
    String sanitizeAssetId(String assetId);

    /**
     * sanitize an outgoing okhttp3 url call from jakarte incoming info
     *
     * @param connectorUrl target connector
     * @param subUrl subpath under connector
     * @param headers to send
     * @param uri to copy params from
     * @return target url for sending
     */
    HttpUrl getUrl(String connectorUrl, String subUrl, HttpHeaders headers, UriInfo uri);
}
