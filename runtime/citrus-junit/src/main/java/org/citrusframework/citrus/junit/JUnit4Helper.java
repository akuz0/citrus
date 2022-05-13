/*
 * Copyright 2020 the original author or authors.
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

package org.citrusframework.citrus.junit;

import org.citrusframework.citrus.DefaultTestCase;
import org.citrusframework.citrus.DefaultTestCaseRunner;
import org.citrusframework.citrus.TestCase;
import org.citrusframework.citrus.TestCaseRunner;
import org.citrusframework.citrus.TestResult;
import org.citrusframework.citrus.context.TestContext;
import org.citrusframework.citrus.exceptions.TestCaseFailedException;
import org.springframework.util.ReflectionUtils;

/**
 * @author Christoph Deppisch
 */
public final class JUnit4Helper {

    public static final String BUILDER_ATTRIBUTE = "builder";

    /**
     * Prevent instantiation of utility class
     */
    private JUnit4Helper() {
        // prevent instantiation
    }

    /**
     * Invokes test method based on designer or runner environment.
     * @param target
     * @param frameworkMethod
     * @param runner
     * @param context
     */
    public static void invokeTestMethod(Object target, CitrusFrameworkMethod frameworkMethod, TestCaseRunner runner, TestContext context) {
        final TestCase testCase = runner.getTestCase();
        try {
            Object[] params = JUnit4ParameterHelper.resolveParameter(frameworkMethod, testCase, context);
            runner.start();
            ReflectionUtils.invokeMethod(frameworkMethod.getMethod(), target, params);
        } catch (Exception | AssertionError e) {
            testCase.setTestResult(TestResult.failed(testCase.getName(), testCase.getTestClass().getName(), e));
            throw new TestCaseFailedException(e);
        } finally {
            runner.stop();
        }
    }

    /**
     * Creates new test runner instance for this test method.
     * @param frameworkMethod
     * @param testClass
     * @param context
     * @return
     */
    public static TestCaseRunner createTestRunner(CitrusFrameworkMethod frameworkMethod, Class<?> testClass, TestContext context) {
        TestCaseRunner testCaseRunner = new DefaultTestCaseRunner(new DefaultTestCase(), context);
        testCaseRunner.testClass(testClass);
        testCaseRunner.name(frameworkMethod.getTestName());
        testCaseRunner.packageName(frameworkMethod.getPackageName());

        return testCaseRunner;
    }
}
