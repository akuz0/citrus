/*
 * Copyright 2006-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.consol.citrus.cucumber.step.runner.http;

import java.util.HashMap;
import java.util.Map;

import com.consol.citrus.Citrus;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusFramework;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import com.consol.citrus.http.actions.HttpClientActionBuilder;
import com.consol.citrus.http.actions.HttpClientRequestActionBuilder;
import com.consol.citrus.http.actions.HttpClientResponseActionBuilder;
import com.consol.citrus.http.actions.HttpServerActionBuilder;
import com.consol.citrus.http.actions.HttpServerRequestActionBuilder;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.http.message.HttpMessage;
import com.consol.citrus.http.server.HttpServer;
import com.consol.citrus.validation.context.ValidationContext;
import com.consol.citrus.validation.json.JsonPathMessageValidationContext;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

import static com.consol.citrus.http.actions.HttpActionBuilder.http;
import static com.consol.citrus.validation.PathExpressionValidationContext.Builder.pathExpression;
import static com.consol.citrus.validation.json.JsonPathMessageValidationContext.Builder.jsonPath;
import static com.consol.citrus.validation.xml.XpathMessageValidationContext.Builder.xpath;

/**
 * @author Christoph Deppisch
 * @since 2.7
 */
public class HttpSteps {

    @CitrusResource
    private TestCaseRunner runner;

    @CitrusFramework
    private Citrus citrus;

    protected HttpClient httpClient;
    protected HttpServer httpServer;

    private HttpMessage request;
    private HttpMessage response;

    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> pathValidations = new HashMap<>();

    private String body;
    private String contentType;

    private String url;

    @Before
    public void before() {
        if (httpClient == null && citrus.getCitrusContext().getReferenceResolver().resolveAll(HttpClient.class).size() == 1L) {
            httpClient = citrus.getCitrusContext().getReferenceResolver().resolve(HttpClient.class);
        }

        if (httpServer == null && citrus.getCitrusContext().getReferenceResolver().resolveAll(HttpServer.class).size() == 1L) {
            httpServer = citrus.getCitrusContext().getReferenceResolver().resolve(HttpServer.class);
        }

        request = new HttpMessage();
        response = new HttpMessage();
        headers = new HashMap<>();
        pathValidations = new HashMap<>();
    }

    @Given("^http-client \"([^\"\\s]+)\"$")
    public void setClient(String id) {
        if (!citrus.getCitrusContext().getReferenceResolver().isResolvable(id)) {
            throw new CitrusRuntimeException("Unable to find http client for id: " + id);
        }

        httpClient = citrus.getCitrusContext().getReferenceResolver().resolve(id, HttpClient.class);
    }

    @Given("^http-server \"([^\"\\s]+)\"$")
    public void setServer(String id) {
        if (!citrus.getCitrusContext().getReferenceResolver().isResolvable(id)) {
            throw new CitrusRuntimeException("Unable to find http server for id: " + id);
        }

        httpServer = citrus.getCitrusContext().getReferenceResolver().resolve(id, HttpServer.class);
    }

    @Given("^URL: ([^\\s]+)$")
    public void setUrl(String url) {
        this.url = url;
    }

    @Given("^Path: ([^\\s]+)$")
    public void setRequestPath(String path) {
        request.path(path);
        request.contextPath(path);
    }

    @Given("^Host: (.+)$")
    public void setHost(String host) {
        this.headers.put("Host", host);
    }

    @Given("^Accept: (.+)$")
    public void setAccept(String contentType) {
        headers.put("Accept", contentType);
    }

    @Given("^Accept-Encoding: (.+)$")
    public void setAcceptEncoding(String encoding) {
        this.headers.put("Accept-Encoding", encoding);
    }

    @Given("^Content-Type: (.+)$")
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Given("^Content-Encoding: (.+)$")
    public void setContentEncoding(String encoding) {
        this.headers.put("Content-Encoding", encoding);
    }

    @Given("^(X-[^\\s]+): (.+)$")
    public void addCustomHeader(String name, String value) {
        headers.put(name, value);
    }

    @Given("^Header ([^\\s]+): (.+)$")
    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    @Given("^validate ([^\\s]+) is (.+)$")
    public void addPathValidation(String name, String value) {
        pathValidations.put(name, value);
    }

    @Given("^(?:Payload):$")
    public void setPayloadMultiline(String payload) {
        setPayload(payload);
    }

    @Given("^(?:Payload): (.+)$")
    public void setPayload(String payload) {
        this.body = payload;
    }

    @When("^(?:http-client )?sends? request$")
    public void sendClientRequestFull(String requestData) {
        sendClientRequest(HttpMessage.fromRequestData(requestData));
    }

    @Then("^(?:http-client )?receives? response$")
    public void receiveClientResponseFull(String responseData) {
        receiveClientResponse(HttpMessage.fromResponseData(responseData));
    }

    @When("^(?:http-server )?receives? request$")
    public void receiveServerRequestFull(String requestData) {
        receiveServerRequest(HttpMessage.fromRequestData(requestData));
    }

    @Then("^(?:http-server )?sends? response$")
    public void sendServerResponseFull(String responseData) {
        sendServerResponse(HttpMessage.fromResponseData(responseData));
    }

    @When("^(?:http-client )?sends? (GET|HEAD|POST|PUT|PATCH|DELETE|OPTIONS|TRACE)$")
    public void sendClientRequest(String method) {
        sendClientRequest(method, null);
    }

    @When("^(?:http-client )?sends? (GET|HEAD|POST|PUT|PATCH|DELETE|OPTIONS|TRACE) ([^\"\\s]+)$")
    public void sendClientRequest(String method, String path) {
        request.method(HttpMethod.valueOf(method));

        if (StringUtils.hasText(path)) {
            request.path(path);
        }

        if (StringUtils.hasText(body)) {
            request.setPayload(body);
            body = null;
        }

        if (StringUtils.hasText(contentType)) {
            request.contentType(contentType);
            contentType = null;
        }

        for (Map.Entry<String, String> headerEntry : headers.entrySet()) {
            request.setHeader(headerEntry.getKey(), headerEntry.getValue());
        }
        headers.clear();

        sendClientRequest(request);
    }

    /**
     * Sends client request.
     * @param request
     */
    protected void sendClientRequest(HttpMessage request) {
        HttpClientActionBuilder.HttpClientSendActionBuilder sendBuilder = http().client(httpClient).send();
        HttpClientRequestActionBuilder requestBuilder;

        if (request.getRequestMethod() == null || request.getRequestMethod().equals(HttpMethod.POST)) {
            requestBuilder = sendBuilder.post().message(request);
        } else if (request.getRequestMethod().equals(HttpMethod.GET)) {
            requestBuilder = sendBuilder.get().message(request);
        } else if (request.getRequestMethod().equals(HttpMethod.PUT)) {
            requestBuilder = sendBuilder.put().message(request);
        } else if (request.getRequestMethod().equals(HttpMethod.DELETE)) {
            requestBuilder = sendBuilder.delete().message(request);
        } else if (request.getRequestMethod().equals(HttpMethod.HEAD)) {
            requestBuilder = sendBuilder.head().message(request);
        } else if (request.getRequestMethod().equals(HttpMethod.TRACE)) {
            requestBuilder = sendBuilder.trace().message(request);
        } else if (request.getRequestMethod().equals(HttpMethod.PATCH)) {
            requestBuilder = sendBuilder.patch().message(request);
        } else if (request.getRequestMethod().equals(HttpMethod.OPTIONS)) {
            requestBuilder = sendBuilder.options().message(request);
        } else {
            requestBuilder = sendBuilder.post().message(request);
        }

        if (StringUtils.hasText(url)) {
            requestBuilder.uri(url);
        }

        runner.run(requestBuilder);
    }

    @Then("^(?:http-client )?receives? status (\\d+)(?: [^\\s]+)?$")
    public void receiveClientResponse(Integer status) {
        response.status(HttpStatus.valueOf(status));

        if (StringUtils.hasText(body)) {
            response.setPayload(body);
            body = null;
        }

        if (StringUtils.hasText(contentType)) {
            response.contentType(contentType);
            contentType = null;
        }

        for (Map.Entry<String, String> headerEntry : headers.entrySet()) {
            response.setHeader(headerEntry.getKey(), headerEntry.getValue());
        }
        headers.clear();
        receiveClientResponse(response);
    }

    /**
     * Receives client response.
     * @param response
     */
    protected void receiveClientResponse(HttpMessage response) {
        HttpClientResponseActionBuilder.HttpMessageBuilderSupport responseBuilder = http().client(httpClient).receive()
                .response(response.getStatusCode())
                .message(response);

        for (Map.Entry<String, String> pathValidation : pathValidations.entrySet()) {
            responseBuilder.validate(pathExpression().expression(pathValidation.getKey(), pathValidation.getValue()));
        }
        pathValidations.clear();

        runner.run(responseBuilder);
    }

    @When("^(?:http-server )?receives? (GET|HEAD|POST|PUT|PATCH|DELETE|OPTIONS|TRACE)$")
    public void receiveServerRequest(String method) {
        receiveServerRequest(method, null);
    }

    @When("^(?:http-server )?receives? (GET|HEAD|POST|PUT|PATCH|DELETE|OPTIONS|TRACE) ([^\"\\s]+)$")
    public void receiveServerRequest(String method, String path) {
        request.method(HttpMethod.valueOf(method));

        if (StringUtils.hasText(path)) {
            request.path(path);
        }

        if (StringUtils.hasText(body)) {
            request.setPayload(body);
            body = null;
        }

        if (StringUtils.hasText(contentType)) {
            request.contentType(contentType);
            contentType = null;
        }

        for (Map.Entry<String, String> headerEntry : headers.entrySet()) {
            request.setHeader(headerEntry.getKey(), headerEntry.getValue());
        }
        headers.clear();
        receiveServerRequest(request);
    }

    /**
     * Receives server request.
     * @param request
     */
    protected void receiveServerRequest(HttpMessage request) {
        HttpServerActionBuilder.HttpServerReceiveActionBuilder receiveBuilder = http().server(httpServer).receive();
        HttpServerRequestActionBuilder.HttpMessageBuilderSupport requestBuilder;

        if (request.getRequestMethod() == null || request.getRequestMethod().equals(HttpMethod.POST)) {
            requestBuilder = receiveBuilder.post().message(request);
        } else if (request.getRequestMethod().equals(HttpMethod.GET)) {
            requestBuilder = receiveBuilder.get().message(request);
        } else if (request.getRequestMethod().equals(HttpMethod.PUT)) {
            requestBuilder = receiveBuilder.put().message(request);
        } else if (request.getRequestMethod().equals(HttpMethod.DELETE)) {
            requestBuilder = receiveBuilder.delete().message(request);
        } else if (request.getRequestMethod().equals(HttpMethod.HEAD)) {
            requestBuilder = receiveBuilder.head().message(request);
        } else if (request.getRequestMethod().equals(HttpMethod.TRACE)) {
            requestBuilder = receiveBuilder.trace().message(request);
        } else if (request.getRequestMethod().equals(HttpMethod.PATCH)) {
            requestBuilder = receiveBuilder.patch().message(request);
        } else if (request.getRequestMethod().equals(HttpMethod.OPTIONS)) {
            requestBuilder = receiveBuilder.options().message(request);
        } else {
            requestBuilder = receiveBuilder.post().message(request);
        }

        for (Map.Entry<String, String> pathValidation : pathValidations.entrySet()) {
            ValidationContext validationContext;

            if (JsonPathMessageValidationContext.isJsonPathExpression(pathValidation.getKey())) {
                validationContext = jsonPath()
                        .expression(pathValidation.getKey(), pathValidation.getValue())
                        .build();
            } else {
                validationContext = xpath()
                        .expression(pathValidation.getKey(), pathValidation.getValue())
                        .build();
            }

            requestBuilder.validate(validationContext);
        }
        pathValidations.clear();

        runner.run(requestBuilder);
    }

    @Then("^(?:http-server )?sends? status (\\d+)(?: [^\\s]+)?$")
    public void sendServerResponse(Integer status) {
        response.status(HttpStatus.valueOf(status));

        if (StringUtils.hasText(body)) {
            response.setPayload(body);
            body = null;
        }

        if (StringUtils.hasText(contentType)) {
            response.contentType(contentType);
            contentType = null;
        }

        for (Map.Entry<String, String> headerEntry : headers.entrySet()) {
            response.setHeader(headerEntry.getKey(), headerEntry.getValue());
        }
        headers.clear();
        sendServerResponse(response);
    }

    /**
     * Sends server response.
     * @param response
     */
    protected void sendServerResponse(HttpMessage response) {
        runner.run(http().server(httpServer).send()
                .response(response.getStatusCode())
                .message(response));
    }
}
