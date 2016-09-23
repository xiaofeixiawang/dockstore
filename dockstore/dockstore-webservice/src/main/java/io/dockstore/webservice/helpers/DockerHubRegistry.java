/*
 *    Copyright 2016 OICR
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package io.dockstore.webservice.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.HttpClient;

import io.dockstore.webservice.core.Tool;
import io.dockstore.webservice.core.Tag;

/**
 * A no-op interface intended as a place-holder for where we will implemnt docker hub functionality when they get around to exposing and
 * implementing their API.
 * 
 * @author dyuen
 */
public class DockerHubRegistry implements ImageRegistryInterface {

    private final HttpClient client;

    public DockerHubRegistry(HttpClient client) {
        this.client = client;
    }

    @Override
    public List<Tag> getTags(Tool tool) {
        return new ArrayList<>();
    }

    @Override
    public List<String> getNamespaces() {
        return new ArrayList<>();
    }

    @Override
    public List<Tool> getContainers(List<String> namespaces) {
        return new ArrayList<>();
    }

    @Override
    public Map<String, ArrayList<?>> getBuildMap(List<Tool> allRepos) {
        return new HashMap<>();
    }
}
