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

package io.dockstore.webservice.core;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Access tokens for this web service and integrated services like quay.io and github.
 * 
 * @author dyuen
 */
@ApiModel(value = "Token", description = "Access tokens for this web service and integrated services like quay.io and github")
@Entity
@Table(name = "token")
@NamedQueries({
        @NamedQuery(name = "io.dockstore.webservice.core.Token.findAll", query = "SELECT t FROM Token t"),
        @NamedQuery(name = "io.dockstore.webservice.core.Token.findByContent", query = "SELECT t FROM Token t WHERE t.content = :content"),
        @NamedQuery(name = "io.dockstore.webservice.core.Token.findBySource", query = "SELECT t FROM Token t WHERE t.tokenSource = :source"),
        @NamedQuery(name = "io.dockstore.webservice.core.Token.findByUserId", query = "SELECT t FROM Token t WHERE t.userId = :userId"),
        @NamedQuery(name = "io.dockstore.webservice.core.Token.findDockstoreByUserId", query = "SELECT t FROM Token t WHERE t.userId = :userId AND t.tokenSource = 'dockstore'"),
        @NamedQuery(name = "io.dockstore.webservice.core.Token.findGithubByUserId", query = "SELECT t FROM Token t WHERE t.userId = :userId AND t.tokenSource = 'github.com'"),
        @NamedQuery(name = "io.dockstore.webservice.core.Token.findQuayByUserId", query = "SELECT t FROM Token t WHERE t.userId = :userId AND t.tokenSource = 'quay.io'"),
        @NamedQuery(name = "io.dockstore.webservice.core.Token.findBitbucketByUserId", query = "SELECT t FROM Token t WHERE t.userId = :userId AND t.tokenSource = 'bitbucket.org'") })
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("Implementation specific ID for the token in this web service")
    private long id;
    @Column(nullable = false)
    @ApiModelProperty("Source website for this token")
    private String tokenSource;
    @Column(nullable = false)
    @ApiModelProperty("Contents of the access token")
    private String content;
    @Column(nullable = false)
    @ApiModelProperty("When an integrated service is not aware of the username, we store it")
    private String username;
    @Column
    @ApiModelProperty("")
    private String refreshToken;

    // TODO: tokens will need to be associated with a particular user
    @Column
    private long userId;

    public Token() {
    }

    public Token(long id, long userId, String tokenSource, String content) {
        this.id = id;
        this.userId = userId;
        this.tokenSource = tokenSource;
        this.content = content;
    }

    @JsonProperty
    public long getId() {
        return id;
    }

    @JsonProperty
    public String getContent() {
        return content;
    }

    @JsonProperty
    public String getUsername() {
        return username;
    }

    /**
     * @return the tokenSource
     */
    @JsonProperty
    public String getTokenSource() {
        return tokenSource;
    }

    /**
     * @return the refreshToken
     */
    public String getRefreshToken() {
        return refreshToken;
    }

    /**
     * @param tokenSource
     *            the tokenSource to set
     */
    public void setTokenSource(String tokenSource) {
        this.tokenSource = tokenSource;
    }

    /**
     * @param content
     *            the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * @param username
     *            the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @param refreshToken
     *            the refreshToken to set
     */
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Token)) {
            return false;
        }

        final Token that = (Token) o;

        return Objects.equals(id, that.id) && Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, content);
    }

    /**
     * @return the userId
     */
    @JsonProperty
    public long getUserId() {
        return userId;
    }

    /**
     * @param userId
     *            the userId to set
     */
    public void setUserId(long userId) {
        this.userId = userId;
    }
}
