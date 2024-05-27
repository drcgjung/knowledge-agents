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
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriInfo;
import okhttp3.HttpUrl;
import org.eclipse.tractusx.agents.AgentConfig;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Implement uri sanitizing
 */
public class RewritingUriSanitizer implements UriSanitizer {

    protected AgentConfig config;
    protected static final Pattern PARAMETER_KEY_ALLOW = Pattern.compile("^(?<param>(?!asset$)[^&?=]+)$");
    protected static final Pattern PARAMETER_VALUE_ALLOW = Pattern.compile("^(?<value>[^&]+)$");

    public RewritingUriSanitizer(AgentConfig config) {
        this.config = config;
    }

    @Override
    public String sanitizeAssetId(byte[] assetId) {
        Matcher assetMatcher = config.getAssetReferencePattern().matcher(new String(assetId));
        if (assetMatcher.matches()) {
            return assetMatcher.group("asset");
        }
        return null;
    }

    /**
     * computes the url to target the given data plane
     *
     * @param connectorUrl data plane url
     * @param subUrl       sub-path to use
     * @param headers      containing additional info that we need to wrap into a transfer request
     * @return typed url
     */
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
        for (Map.Entry<String, List<String>> param : uri.getQueryParameters().entrySet()) {
            String key = param.getKey();
            Matcher keyMatcher = PARAMETER_KEY_ALLOW.matcher(key);
            if (keyMatcher.matches()) {
                String recodeKey = HttpUtils.urlEncodeParameter(keyMatcher.group("param"));
                for (String value : param.getValue()) {
                    Matcher valueMatcher = PARAMETER_VALUE_ALLOW.matcher(value);
                    if (valueMatcher.matches()) {
                        String recodeValue = HttpUtils.urlEncodeParameter(valueMatcher.group("value"));
                        httpBuilder = httpBuilder.addQueryParameter(recodeKey, recodeValue);
                    }
                }
            }
        }

        List<MediaType> mediaTypes = headers.getAcceptableMediaTypes();
        if (mediaTypes.isEmpty() || mediaTypes.stream().anyMatch(MediaType.APPLICATION_JSON_TYPE::isCompatible)) {
            httpBuilder = httpBuilder.addQueryParameter("cx_accept", HttpUtils.urlEncodeParameter("application/json"));
        } else {
            String mediaParam = mediaTypes.stream().map(MediaType::toString).collect(Collectors.joining(", "));
            mediaParam = HttpUtils.urlEncodeParameter(mediaParam);
            httpBuilder.addQueryParameter("cx_accept", mediaParam);
        }
        return httpBuilder.build();
    }
}
