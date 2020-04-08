/*
 * Copyright 2006-2014 the original author or authors.
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

package com.consol.citrus.endpoint.direct;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.context.TestContextFactory;
import com.consol.citrus.message.DefaultMessage;
import com.consol.citrus.message.DefaultMessageQueue;
import com.consol.citrus.message.Message;
import com.consol.citrus.message.MessageQueue;
import com.consol.citrus.message.MessageSelector;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author Christoph Deppisch
 */
public class DirectEndpointAdapterTest {

    private MessageQueue queue = new DefaultMessageQueue();
    private DirectEndpointAdapter endpointAdapter;
    private DirectSyncEndpointConfiguration endpointConfiguration;

    private TestContextFactory testContextFactory = TestContextFactory.newInstance();
    private TestContext context;

    @BeforeClass
    public void setup() {
        endpointConfiguration = new DirectSyncEndpointConfiguration();
        endpointConfiguration.setQueue(queue);
        endpointConfiguration.setTimeout(250L);

        endpointAdapter = new DirectEndpointAdapter(endpointConfiguration);
        endpointAdapter.setTestContextFactory(testContextFactory);
    }

    @BeforeMethod
    public void purgeQueue() {
        context = new TestContext();
        queue.purge(new MessageSelector.AllAcceptingMessageSelector());
    }

    @Test
    public void testEndpointAdapter() {
        final Message request = new DefaultMessage("<TestMessage><text>Hi!</text></TestMessage>");

        new SimpleAsyncTaskExecutor().execute(new Runnable() {
            @Override
            public void run() {
                Message receivedMessage = endpointAdapter.getEndpoint().createConsumer().receive(context, endpointConfiguration.getTimeout());
                Assert.assertNotNull(receivedMessage);
                Assert.assertEquals(receivedMessage.getPayload(), request.getPayload());

                endpointAdapter.getEndpoint().createProducer().send(new DefaultMessage("OK"), context);
            }
        });

        Message response = endpointAdapter.handleMessage(request);
        Assert.assertNotNull(response);
        Assert.assertEquals(response.getPayload(String.class), "OK");
    }

    @Test
    public void testNoResponse() {
        Assert.assertNull(endpointAdapter.handleMessage(new DefaultMessage("<TestMessage><text>Hi!</text></TestMessage>")));
    }
}
