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

import java.util.Objects;

/**
 * a default sanitizer doing no "dangerous" things
 */
public class DefaultUriSanitizer implements UriSanitizer {
    @Override
    public String sanitizeAssetId(String assetId) {
        return null;
    }

    @Override
    public HttpUrl getUrl(String connectorUrl, String subUrl, HttpHeaders headers, UriInfo uri) {
        var url = connectorUrl;

        // EDC public api slash problem
        if (!url.endsWith("/") && !url.contains("#")) {
            url = url + "/";
        }

        if (subUrl != null && !subUrl.isEmpty()) {
            url = url + subUrl;
        }

        HttpUrl.Builder httpBuilder = Objects.requireNonNull(okhttp3.HttpUrl.parse(url)).newBuilder();
        return httpBuilder.build();
    }
}
