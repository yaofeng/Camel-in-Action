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

import javax.sql.DataSource;

import org.apache.camel.CamelContext;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @version $Revision$
 */
public class OrderCreateTable {

    public OrderCreateTable(CamelContext camelContext) {
        DataSource ds = camelContext.getRegistry().lookup("myDataSource", DataSource.class);
        JdbcTemplate jdbc = new JdbcTemplate(ds);

        try {
            jdbc.execute("drop table riders_order");
        } catch (Exception e) {
            // ignore as the table may not already exists
        }
        jdbc.execute("create table riders_order "
            + "( customer_id varchar(10), ref_no varchar(10), part_id varchar(10), amount varchar(10) )");
    }
    
}
