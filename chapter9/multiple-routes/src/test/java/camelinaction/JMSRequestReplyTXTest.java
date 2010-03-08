/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package camelinaction;

import org.apache.camel.ExchangePattern;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @version $Revision$
 */
public class JMSRequestReplyTXTest extends CamelSpringTestSupport {

    @Override
    protected AbstractXmlApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext(new String[]{"spring-context.xml"});
    }

    @Override
    protected int getExpectedRouteCount() {
        // use 0 as we use a Java based route builder directly in this unit test
        return 0;
    }

    @Test
    public void testRequestReplyBad() throws Exception {
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                context.setTracing(true);

                from("activemq:queue:a")
                    .transacted()
                    .to("activemq:queue:greet")
                    .to("activemq:queue:b");

                from("activemq:queue:greet")
                    .transform(body().prepend("Hi "));
            }
        });
        template.sendBody("activemq:queue:a", "Camel");

        String reply = consumer.receiveBody("activemq:queue:b", 10000, String.class);
        assertNotNull(reply);

        System.out.println("TODO: We want the reply to be Hi Camel but its not its: " + reply);
        // TODO: This is what we wanted to assert
        // assertEquals("Hi Camel", reply);
    }

    @Test
    public void testRequestReplyOK() throws Exception {
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                context.setTracing(true);

                from("activemq:queue:a")
                    .transacted()
                    .setExchangePattern(ExchangePattern.InOut)
                    .to("activemq:queue:greet")
                    .setExchangePattern(ExchangePattern.InOnly)
                    .to("activemq:queue:b");

                from("activemq:queue:greet")
                    .transacted()
                    .transform(body().prepend("Hi "));
            }
        });
        template.sendBody("activemq:queue:a", "Camel");

        String reply = consumer.receiveBody("activemq:queue:b", 10000, String.class);
        assertEquals("Hi Camel", reply);
    }

}
