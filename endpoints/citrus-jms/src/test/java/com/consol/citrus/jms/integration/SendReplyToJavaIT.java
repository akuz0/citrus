/*
 * Copyright 2006-2013 the original author or authors.
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

package com.consol.citrus.jms.integration;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.message.MessageHeaders;
import com.consol.citrus.testng.TestNGCitrusSupport;
import org.testng.annotations.Test;

import static com.consol.citrus.actions.ReceiveMessageAction.Builder.receive;
import static com.consol.citrus.actions.SendMessageAction.Builder.send;
import static com.consol.citrus.container.Parallel.Builder.parallel;
import static com.consol.citrus.container.Sequence.Builder.sequential;
import static com.consol.citrus.variable.MessageHeaderVariableExtractor.Builder.headerValueExtractor;

/**
 * @author Christoph Deppisch
 */
@Test
public class SendReplyToJavaIT extends TestNGCitrusSupport {

    @CitrusTest
    public void jmsSyncQueues() {
        variable("operation", "GetDate");
        variable("conversationId", "123456789");

        run(parallel().actions(
            send("syncGetDateRequestSender")
                .payload("<GetDateRequestMessage>" +
                            "<MessageHeader>" +
                                "<ConversationId>${conversationId}</ConversationId>" +
                                "<Timestamp>citrus:currentDate()</Timestamp>" +
                            "</MessageHeader>" +
                            "<MessageBody>" +
                                "<Format>yyyy-mm-dd</Format>" +
                            "</MessageBody>" +
                        "</GetDateRequestMessage>")
                .header("Operation", "${operation}")
                .header("ConversationId", "${conversationId}")
                .extract(headerValueExtractor()
                            .header(MessageHeaders.ID, "syncRequestCorrelatorId")),

            sequential().actions(
                receive("syncGetDateRequestReceiver")
                    .payload("<GetDateRequestMessage>" +
                            "<MessageHeader>" +
                                "<ConversationId>${conversationId}</ConversationId>" +
                                "<Timestamp>citrus:currentDate()</Timestamp>" +
                            "</MessageHeader>" +
                            "<MessageBody>" +
                                "<Format>yyyy-mm-dd</Format>" +
                            "</MessageBody>" +
                        "</GetDateRequestMessage>")
                    .header("Operation", "${operation}")
                    .header("ConversationId", "${conversationId}")
                    .validator("defaultXmlMessageValidator"),

                send("syncGetDateRequestReceiver")
                    .payload("<GetDateResponseMessage>" +
                                "<MessageHeader>" +
                                "<ConversationId>${conversationId}</ConversationId>" +
                                "<Timestamp>citrus:currentDate()</Timestamp>" +
                            "</MessageHeader>" +
                            "<MessageBody>" +
                                "<Value>citrus:currentDate()</Value>" +
                            "</MessageBody>" +
                        "</GetDateResponseMessage>")
                    .header("Operation", "${operation}")
                    .header("ConversationId", "${conversationId}"),

                receive("syncGetDateRequestSender")
                    .selector("citrus_message_id = '${syncRequestCorrelatorId}'")
                    .payload("<GetDateResponseMessage>" +
                                "<MessageHeader>" +
                                "<ConversationId>${conversationId}</ConversationId>" +
                                "<Timestamp>citrus:currentDate()</Timestamp>" +
                            "</MessageHeader>" +
                            "<MessageBody>" +
                                "<Value>citrus:currentDate()</Value>" +
                            "</MessageBody>" +
                        "</GetDateResponseMessage>")
                    .header("Operation", "${operation}")
                    .header("ConversationId", "${conversationId}")
                    .timeout(5000L)
            )
        ));
    }
}
