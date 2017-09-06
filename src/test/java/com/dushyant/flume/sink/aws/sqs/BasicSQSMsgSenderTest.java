/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dushyant.flume.sink.aws.sqs;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import org.apache.flume.Channel;
import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.junit.Test;

/**
 * Test class for {@link BasicSQSMsgSender}
 *
 * @author dpandya
 */
public class BasicSQSMsgSenderTest {
    @Test
    public void testSend() throws Exception {

        Channel mockChannel = mock(Channel.class);
        Event mockEvent = mock(Event.class);
        when(mockEvent.getBody()).thenReturn("This is a test event message".getBytes());
        when(mockChannel.take()).thenReturn(mockEvent);

        AmazonSQSClient mockSqs = mock(AmazonSQSClient.class);
        AmazonSQSClientFactory clientFactory = mock(AmazonSQSClientFactory.class);
        when(clientFactory.createClient()).thenReturn(mockSqs);

        BasicSQSMsgSender msgSender =
                new BasicSQSMsgSender(clientFactory,"https://some-fake/url", "us-east-1");

        when(mockSqs.sendMessage(any(SendMessageRequest.class))).thenReturn(new SendMessageResult());

        int eventCount = msgSender.send(mockChannel);
        assertEquals(1, eventCount);
    }

    @Test
    public void testSendEmptyChannel() throws Exception {

        Channel mockChannel = mock(Channel.class);
        when(mockChannel.take()).thenReturn(null);

        AmazonSQSClient mockSqs = mock(AmazonSQSClient.class);
        AmazonSQSClientFactory clientFactory = mock(AmazonSQSClientFactory.class);
        when(clientFactory.createClient()).thenReturn(mockSqs);

        BasicSQSMsgSender msgSender =
                new BasicSQSMsgSender(clientFactory,"https://some-fake/url", "us-east-1");

        when(mockSqs.sendMessage(any(SendMessageRequest.class))).thenReturn(new SendMessageResult());

        int eventCount = msgSender.send(mockChannel);
        assertEquals(0, eventCount);
    }

    @Test
    public void testSendEventWithEmptyBody() throws Exception {

        Channel mockChannel = mock(Channel.class);
        Event mockEvent = mock(Event.class);
        when(mockEvent.getBody()).thenReturn("".getBytes());
        when(mockChannel.take()).thenReturn(mockEvent);

        AmazonSQSClient mockSqs = mock(AmazonSQSClient.class);
        AmazonSQSClientFactory clientFactory = mock(AmazonSQSClientFactory.class);
        when(clientFactory.createClient()).thenReturn(mockSqs);

        BasicSQSMsgSender msgSender =
                new BasicSQSMsgSender(clientFactory,"https://some-fake/url", "us-east-1");

        when(mockSqs.sendMessage(any(SendMessageRequest.class))).thenReturn(new SendMessageResult());

        int eventCount = msgSender.send(mockChannel);
        assertEquals(0, eventCount);
    }

    @Test(expected = EventDeliveryException.class)
    public void testSendFailureAmazonServiceException() throws Exception {

        Channel mockChannel = mock(Channel.class);
        Event mockEvent = mock(Event.class);
        when(mockEvent.getBody()).thenReturn("This is a test event message".getBytes());
        when(mockChannel.take()).thenReturn(mockEvent);

        AmazonSQSClient mockSqs = mock(AmazonSQSClient.class);
        AmazonSQSClientFactory clientFactory = mock(AmazonSQSClientFactory.class);
        when(clientFactory.createClient()).thenReturn(mockSqs);

        BasicSQSMsgSender msgSender =
                new BasicSQSMsgSender(clientFactory,"https://some-fake/url", "us-east-1");

        when(mockSqs.sendMessage(any(SendMessageRequest.class)))
            .thenThrow(new AmazonServiceException("Mock AmazonServiceException"));

        msgSender.send(mockChannel);
    }

    @Test(expected = EventDeliveryException.class)
    public void testSendFailureAmazonClientException() throws Exception {

        Channel mockChannel = mock(Channel.class);
        Event mockEvent = mock(Event.class);
        when(mockEvent.getBody()).thenReturn("This is a test event message".getBytes());
        when(mockChannel.take()).thenReturn(mockEvent);

        AmazonSQSClient mockSqs = mock(AmazonSQSClient.class);
        AmazonSQSClientFactory clientFactory = mock(AmazonSQSClientFactory.class);
        when(clientFactory.createClient()).thenReturn(mockSqs);

        BasicSQSMsgSender msgSender =
                new BasicSQSMsgSender(clientFactory,"https://some-fake/url", "us-east-1");

        when(mockSqs.sendMessage(any(SendMessageRequest.class)))
            .thenThrow(new AmazonClientException("Mock AmazonClientException"));

        msgSender.send(mockChannel);
    }
}
