/*
 * Copyright 2006-2015 the original author or authors.
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

package org.citrusframework.citrus.dsl.runner;

import org.citrusframework.citrus.TestAction;
import org.citrusframework.citrus.TestActionBuilder;
import org.citrusframework.citrus.TestActionContainerBuilder;
import org.citrusframework.citrus.TestCaseBuilder;
import org.citrusframework.citrus.actions.*;
import org.citrusframework.citrus.container.*;
import org.citrusframework.citrus.context.TestContext;
import org.citrusframework.citrus.dsl.builder.*;
import org.citrusframework.citrus.script.GroovyAction;
import org.citrusframework.citrus.server.Server;

/**
 * Test builder interface defines builder pattern methods for creating a new
 * Citrus test case.
 *
 * @author Christoph Deppisch
 * @since 2.3
 */
public interface TestRunner extends TestCaseBuilder {

    /**
     * Starts the test case execution.
     */
    void start();

    /**
     * Stops test case execution.
     */
    void stop();

    /**
     * Runs test action and returns same action after execution.
     * @param testAction
     * @return
     */
    <A extends TestAction> TestActionBuilder<A> run(A testAction);

    /**
     * Runs test action and returns same action after execution.
     * @param builder
     * @return
     */
    <T extends TestActionBuilder<?>> T run(T builder);

    /**
     * Prepare and add a custom container implementation.
     * @param container
     * @return
     */
    <T extends TestActionContainer, B extends TestActionContainerBuilder<T, B>> TestActionContainerBuilder<T, B> container(T container);

    /**
     * Prepare and add a custom container implementation.
     * @param builder
     * @return
     */
    <T extends TestActionContainerBuilder<? extends TestActionContainer, ?>> T container(T builder);

    /**
     * Apply test apply with all test actions, finally actions and test
     * variables defined in given apply.
     *
     * @param behavior
     */
    ApplyTestBehaviorAction.Builder applyBehavior(TestBehavior behavior);

    /**
     * Action creating a new test variable during a test.
     *
     * @param variableName
     * @param value
     * @return
     */
    CreateVariablesAction.Builder createVariable(String variableName, String value);

    /**
     * Creates a new ANT run action definition
     * for further configuration.
     * @param configurer
     * @return
     */
    AntRunAction.Builder antrun(BuilderSupport<AntRunAction.Builder> configurer);

    /**
     * Creates and executes a new echo action.
     * @param message
     * @return
     */
    EchoAction.Builder echo(String message);

    /**
     * Creates a new executePLSQL action definition
     * for further configuration.
     *
     * @param configurer
     * @return
     */
    ExecutePLSQLAction.Builder plsql(BuilderSupport<ExecutePLSQLAction.Builder> configurer);

    /**
     * Creates a new executeSQL action definition
     * for further configuration.
     *
     * @param configurer
     * @return
     */
    ExecuteSQLAction.Builder sql(BuilderSupport<ExecuteSQLAction.Builder> configurer);

    /**
     * Creates a new executesqlquery action definition
     * for further configuration.
     *
     * @param configurer
     * @return
     */
    ExecuteSQLQueryAction.Builder query(BuilderSupport<ExecuteSQLQueryAction.Builder> configurer);

    /**
     * Creates a new receive timeout action definition
     * for further configuration.
     *
     * @param configurer
     * @return
     */
    ReceiveTimeoutAction.Builder receiveTimeout(BuilderSupport<ReceiveTimeoutAction.Builder> configurer);

    /**
     * Creates a new fail action.
     *
     * @param message
     * @return
     */
    FailAction.Builder fail(String message);

    /**
     * Creates a new input action.
     *
     * @param configurer
     * @return
     */
    InputAction.Builder input(BuilderSupport<InputAction.Builder> configurer);

    /**
     * Creates a new load properties action.
     * @param filePath path to properties file.
     * @return
     */
    LoadPropertiesAction.Builder load(String filePath);

    /**
     * Creates a new purge jms queues action definition
     * for further configuration.
     *
     * @param configurer
     * @return
     */
    PurgeJmsQueuesActionBuilder purgeQueues(BuilderSupport<PurgeJmsQueuesActionBuilder> configurer);

    /**
     * Creates a new purge message channel action definition
     * for further configuration.
     *
     * @param configurer
     * @return
     */
    PurgeMessageChannelActionBuilder purgeChannels(BuilderSupport<PurgeMessageChannelActionBuilder> configurer);

    /**
     * Creates a new purge message endpoint action definition
     * for further configuration.
     *
     * @param configurer
     * @return
     */
    PurgeEndpointAction.Builder purgeEndpoints(BuilderSupport<PurgeEndpointAction.Builder> configurer);

    /**
     * Creates receive message action definition with message endpoint instance.
     *
     * @param configurer
     * @return
     */
    ReceiveMessageActionBuilder<?> receive(BuilderSupport<ReceiveMessageActionBuilder<?>> configurer);

    /**
     * Create send message action definition with message endpoint instance.
     *
     * @param configurer
     * @return
     */
    SendMessageActionBuilder<?> send(BuilderSupport<SendMessageActionBuilder<?>> configurer);

    /**
     * Add sleep action with default delay time.
     * @return
     */
    SleepAction.Builder sleep();

    /**
     * Add sleep action with time in milliseconds.
     *
     * @param milliseconds
     * @return
     */
    SleepAction.Builder sleep(long milliseconds);

    /**
     * Creates a wait action that waits for a condition to be satisfied before continuing.
     * @return
     */
    Wait.Builder waitFor();

    /**
     * Creates a new start server action definition
     * for further configuration.
     *
     * @param servers
     * @return
     */
    StartServerAction.Builder start(Server... servers);

    /**
     * Creates a new start server action definition
     * for further configuration.
     *
     * @param server
     * @return
     */
    StartServerAction.Builder start(Server server);

    /**
     * Creates a new stop server action definition
     * for further configuration.
     *
     * @param servers
     * @return
     */
    StopServerAction.Builder stop(Server... servers);

    /**
     * Creates a new stop server action definition
     * for further configuration.
     *
     * @param server
     * @return
     */
    StopServerAction.Builder stop(Server server);

    /**
     * Creates a new stop time action.
     * @return
     */
    StopTimeAction.Builder stopTime();

    /**
     * Creates a new stop time action.
     *
     * @param id
     * @return
     */
    StopTimeAction.Builder stopTime(String id);

    /**
     * Creates a new stop time action.
     *
     * @param id
     * @param suffix
     * @return
     */
    StopTimeAction.Builder stopTime(String id, String suffix);

    /**
     * Creates a new trace variables action definition
     * that prints variable values to the console/logger.
     *
     * @return
     */
    TraceVariablesAction.Builder traceVariables();

    /**
     * Creates a new trace variables action definition
     * that prints variable values to the console/logger.
     *
     * @param variables
     * @return
     */
    TraceVariablesAction.Builder traceVariables(String... variables);

    /**
     * Creates a new groovy action definition
     * for further configuration.
     *
     * @param configurer
     * @return
     */
    GroovyAction.Builder groovy(BuilderSupport<GroovyAction.Builder> configurer);

    /**
     * Creates a new transform action definition
     * for further configuration.
     *
     * @param configurer
     * @return
     */
    TransformAction.Builder transform(BuilderSupport<TransformAction.Builder> configurer);

    /**
     * Assert exception to happen in nested test action.
     * @return
     */
    Assert.Builder assertException();

    /**
     * Action catches possible exceptions in nested test actions.
     * @return
     */
    Catch.Builder catchException();

    /**
     * Assert SOAP fault during action execution.
     * @return
     */
    AssertSoapFaultBuilder assertSoapFault();

    /**
     * Adds conditional container with nested test actions.
     * @return
     */
    Conditional.Builder conditional();

    /**
     * Adds iterate container with nested test actions.
     * @return
     */
    Iterate.Builder iterate();

    /**
     * Run nested test actions in parallel to each other using multiple threads.
     * @return
     */
    Parallel.Builder parallel();

    /**
     * Adds repeat on error until true container with nested test actions.
     * @return
     */
    RepeatOnErrorUntilTrue.Builder repeatOnError();

    /**
     * Adds repeat until true container with nested test actions.
     * @return
     */
    RepeatUntilTrue.Builder repeat();

    /**
     * Adds sequential container with nested test actions.
     * @return
     */
    Sequence.Builder sequential();

    /**
     * Adds async container with nested test actions.
     * @return
     */
    Async.Builder async();

    /**
     * Repeat nested test actions based on a timer interval.
     * @return
     */
    Timer.Builder timer();

    /**
     * Stops the timer matching the supplied timerId
     * @param timerId
     * @return
     */
    StopTimerAction.Builder stopTimer(String timerId);

    /**
     * Stops all timers within the current test context
     * @return
     */
    StopTimerAction.Builder stopTimers();

    /**
     * Run docker command action.
     * @return
     */
    DockerExecuteActionBuilder docker(BuilderSupport<DockerExecuteActionBuilder> configurer);

    /**
     * Run kubernetes command action.
     * @return
     */
    KubernetesExecuteActionBuilder kubernetes(BuilderSupport<KubernetesExecuteActionBuilder> configurer);

    /**
     * Run selenium command action.
     * @return
     */
    SeleniumActionBuilder selenium(BuilderSupport<SeleniumActionBuilder> configurer);

    /**
     * Run http command action.
     * @return
     */
    HttpActionBuilder http(BuilderSupport<HttpActionBuilder> configurer);

    /**
     * Run soap command action.
     * @return
     */
    SoapActionBuilder soap(BuilderSupport<SoapActionBuilder> configurer);

    /**
     * Run Camel route actions.
     * @return
     */
    CamelRouteActionBuilder camel(BuilderSupport<CamelRouteActionBuilder> configurer);

    /**
     * Run zookeeper command action.
     * @return
     */
    ZooExecuteActionBuilder zookeeper(BuilderSupport<ZooExecuteActionBuilder> configurer);

    /**
     * Adds template container with nested test actions.
     *
     * @param configurer
     * @return
     */
    Template.Builder applyTemplate(BuilderSupport<Template.Builder> configurer);

    /**
     * Adds sequence of test actions to finally block.
     * @return
     */
    FinallySequence.Builder doFinally();

    /**
     * Sets the test context.
     * @param context
     */
    void setTestContext(TestContext context);
}
