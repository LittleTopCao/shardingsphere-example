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

package org.apache.shardingsphere.example.sharding.raw.jdbc.config;

import org.apache.shardingsphere.example.core.api.DataSourceUtil;
import org.apache.shardingsphere.example.config.ExampleConfiguration;
import org.apache.shardingsphere.api.config.sharding.KeyGeneratorConfiguration;
import org.apache.shardingsphere.api.config.sharding.ShardingRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.TableRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.InlineShardingStrategyConfiguration;
import org.apache.shardingsphere.shardingjdbc.api.ShardingDataSourceFactory;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;

/**
 * 精确 的 数据库 分片 类型
 *
 * 使用 行表达式配置的 分片策略，根据 user_id 路由 数据库
 *
 */
public final class ShardingDatabasesConfigurationPrecise implements ExampleConfiguration {
    
    @Override
    public DataSource getDataSource() throws SQLException {

        //创建数据源
        Map<String, DataSource> dataSourceMap = createDataSourceMap();

        //新建分片规则
        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();

        //设置 Order 表规则
        shardingRuleConfig.getTableRuleConfigs().add(getOrderTableRuleConfiguration());

        //设置 OrderItem 表规则
        shardingRuleConfig.getTableRuleConfigs().add(getOrderItemTableRuleConfiguration());

        //设置广播表 t_address
        shardingRuleConfig.getBroadcastTables().add("t_address");

        //设置 默认 数据库 分片 策略
        shardingRuleConfig.setDefaultDatabaseShardingStrategyConfig(new InlineShardingStrategyConfiguration("user_id", "demo_ds_${user_id % 2}"));

        //新建一个 sharding 数据源
        DataSource dataSource = ShardingDataSourceFactory.createDataSource(dataSourceMap, shardingRuleConfig, new Properties());

        return dataSource;
    }
    
    private static TableRuleConfiguration getOrderTableRuleConfiguration() {
        TableRuleConfiguration result = new TableRuleConfiguration("t_order");
        result.setKeyGeneratorConfig(new KeyGeneratorConfiguration("SNOWFLAKE", "order_id", getProperties()));
        return result;
    }
    
    private static TableRuleConfiguration getOrderItemTableRuleConfiguration() {
        TableRuleConfiguration result = new TableRuleConfiguration("t_order_item");
        result.setKeyGeneratorConfig(new KeyGeneratorConfiguration("SNOWFLAKE", "order_item_id", getProperties()));
        return result;
    }
    
    private static Map<String, DataSource> createDataSourceMap() {
        Map<String, DataSource> result = new HashMap<>();
        result.put("demo_ds_0", DataSourceUtil.createDataSource("demo_ds_0"));
        result.put("demo_ds_1", DataSourceUtil.createDataSource("demo_ds_1"));
        return result;
    }
    
    private static Properties getProperties() {
        Properties result = new Properties();
        result.setProperty("worker.id", "123");
        return result;
    }
}
