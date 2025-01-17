/*
 * Copyright 2021 the original author or authors.
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.consol.citrus;

/**
 * Default implementation of CitrusContext provider. Works with default CitrusContext and creates new instances based on
 * a given strategy. Either creates a new instance for each provider call or always returns a singleton context instance.
 *
 * @author Christoph Deppisch
 */
public class DefaultCitrusContextProvider implements CitrusContextProvider {

    private static CitrusContext context;

    private final CitrusInstanceStrategy strategy;

    public DefaultCitrusContextProvider() {
        this(CitrusInstanceStrategy.SINGLETON);
    }

    public DefaultCitrusContextProvider(CitrusInstanceStrategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public CitrusContext create() {
        if (strategy.equals(CitrusInstanceStrategy.NEW) || context == null) {
            context = CitrusContext.create();
        }

        return context;
    }
}
