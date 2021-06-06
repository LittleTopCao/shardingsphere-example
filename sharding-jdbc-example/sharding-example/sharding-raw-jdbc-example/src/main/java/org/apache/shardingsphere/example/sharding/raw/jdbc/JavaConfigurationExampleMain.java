/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.example.sharding.raw.jdbc;

import org.apache.shardingsphere.example.core.api.ExampleExecuteTemplate;
import org.apache.shardingsphere.example.core.api.service.ExampleService;
import org.apache.shardingsphere.example.core.jdbc.service.OrderServiceImpl;
import org.apache.shardingsphere.example.sharding.raw.jdbc.factory.DataSourceFactory;
import org.apache.shardingsphere.example.type.ShardingType;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * java 配置例子
 *
 * main方法直接运行
 *
 * 分为 五种 分片类型：数据库分片、表分片、数据库和表分片、读写分离、分片和读写分离
 *
 * Please make sure master-slave data sync on MySQL is running correctly. Otherwise this example will query empty data from slave.
 */
public class JavaConfigurationExampleMain {
    
    private static ShardingType shardingType = ShardingType.SHARDING_DATABASES;
//    private static ShardingType shardingType = ShardingType.SHARDING_TABLES;
//    private static ShardingType shardingType = ShardingType.SHARDING_DATABASES_AND_TABLES;
//    private static ShardingType shardingType = ShardingType.MASTER_SLAVE;
//    private static ShardingType shardingType = ShardingType.SHARDING_MASTER_SLAVE;


    public static void main(final String[] args) throws SQLException {
        //获得配置过后的 sharding 数据源
        DataSource dataSource = DataSourceFactory.newInstance(shardingType);
        //使用模版运行 service
        ExampleExecuteTemplate.run(getExampleService(dataSource));
    }

    /**
     * 获得 service
     * @param dataSource
     * @return
     */
    private static ExampleService getExampleService(final DataSource dataSource) {
        return new OrderServiceImpl(dataSource);
    }
}
