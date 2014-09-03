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

package com.consol.citrus.channel;

import com.consol.citrus.endpoint.AbstractEndpointConfiguration;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.messaging.MessageChannel;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.messaging.core.DestinationResolver;

/**
 * @author Christoph Deppisch
 * @since 1.4
 */
public class ChannelEndpointConfiguration extends AbstractEndpointConfiguration {

    /** Destination channel */
    private MessageChannel channel;

    /** Destination channel name */
    private String channelName;

    /** Message channel template */
    private MessagingTemplate messagingTemplate = new MessagingTemplate();

    /** The parent bean factory used for channel name resolving */
    private BeanFactory beanFactory;

    /** Channel resolver instance */
    private DestinationResolver<MessageChannel> channelResolver;

    /**
     * Set the message channel.
     * @param channel the channel to set
     */
    public void setChannel(MessageChannel channel) {
        this.channel = channel;
    }

    /**
     * Sets the messaging template.
     * @param messagingTemplate the messagingTemplate to set
     */
    public void setMessagingTemplate(MessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Sets the bean factory for channel resolver.
     * @see org.springframework.beans.factory.BeanFactoryAware#setBeanFactory(org.springframework.beans.factory.BeanFactory)
     */
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    /**
     * Gets the bean factory.
     * @return
     */
    public BeanFactory getBeanFactory() {
        return beanFactory;
    }

    /**
     * Set the channel resolver.
     * @param channelResolver the channelResolver to set
     */
    public void setChannelResolver(DestinationResolver channelResolver) {
        this.channelResolver = channelResolver;
    }

    /**
     * Sets the destination channel name.
     * @param channelName the channelName to set
     */
    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    /**
     * Gets the channel.
     * @return the channel
     */
    public MessageChannel getChannel() {
        return channel;
    }

    /**
     * Gets the channelName.
     * @return the channelName
     */
    public String getChannelName() {
        return channelName;
    }

    /**
     * Gets the messagingTemplate.
     * @return the messagingTemplate
     */
    public MessagingTemplate getMessagingTemplate() {
        return messagingTemplate;
    }

    /**
     * Gets the channelResolver.
     * @return the channelResolver
     */
    public DestinationResolver<MessageChannel> getChannelResolver() {
        return channelResolver;
    }
}
