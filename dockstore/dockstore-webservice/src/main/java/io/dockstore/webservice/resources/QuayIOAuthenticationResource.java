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

package io.dockstore.webservice.resources;

import io.dropwizard.views.View;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author dyuen
 */
@Path("/integration.quay.io")
@Api("/integration.quay.io")
@Produces(MediaType.TEXT_HTML)
public class QuayIOAuthenticationResource {
    private final String clientID;
    private final String redirectURI;

    public QuayIOAuthenticationResource(String clientID, String redirectURI) {
        this.clientID = clientID;
        this.redirectURI = redirectURI;
    }

    @GET
    @ApiOperation(value = "Display an authorization link for quay.io", notes = "More notes about this method", response = QuayIOView.class)
    public QuayIOView getView() {
        return new QuayIOView();
    }

    /**
     * @return the clientID
     */
    public String getClientID() {
        return clientID;
    }

    /**
     * @return the redirectURI
     */
    public String getRedirectURI() {
        return redirectURI;
    }

    public class QuayIOView extends View {
        private final QuayIOAuthenticationResource parent;

        public QuayIOView() {
            super("quay.io.auth.view.ftl");
            parent = QuayIOAuthenticationResource.this;
        }

        /**
         * @return the parent
         */
        public QuayIOAuthenticationResource getParent() {
            return parent;
        }
    }

}
