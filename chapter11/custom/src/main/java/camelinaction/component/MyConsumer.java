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
package camelinaction.component;

import org.apache.camel.Endpoint;
import org.apache.camel.Processor;
import org.apache.camel.ShutdownRunningTask;
import org.apache.camel.impl.DefaultConsumer;
import org.apache.camel.spi.ShutdownAware;

/**
 * The direct consumer.
 *
 * @version $Revision$
 */
public class MyConsumer extends DefaultConsumer implements ShutdownAware {

    private MyEndpoint endpoint;

    public MyConsumer(Endpoint endpoint, Processor processor) {
        super(endpoint, processor);
        this.endpoint = (MyEndpoint) endpoint;
    }

    @Override
    public void start() throws Exception {
        // only add as consumer if not already registered
        if (!endpoint.getConsumers().contains(this)) {
            if (!endpoint.getConsumers().isEmpty()) {
                throw new IllegalStateException("Endpoint " + endpoint.getEndpointUri() + " only allows 1 active consumer but you attempted to start a 2nd consumer.");
            }
            endpoint.getConsumers().add(this);
        }
        super.start();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        endpoint.getConsumers().remove(this);
    }

    public boolean deferShutdown(ShutdownRunningTask shutdownRunningTask) {
        // deny stopping on shutdown as we want direct consumers to run in case some other queues
        // depend on this consumer to run, so it can complete its exchanges
        return true;
    }

    public int getPendingExchangesSize() {
        // return 0 as we do not have an internal memory queue with a variable size
        // of inflight messages. 
        return 0;
    }
}
