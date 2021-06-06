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

import org.apache.shardingsphere.example.algorithm.PreciseModuloShardingTableAlgorithm;
import org.apache.shardingsphere.example.core.api.DataSourceUtil;
import org.apache.shardingsphere.example.config.ExampleConfiguration;
import org.apache.shardingsphere.api.config.sharding.KeyGeneratorConfiguration;
import org.apache.shardingsphere.api.config.sharding.ShardingRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.TableRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.StandardShardingStrategyConfiguration;
import org.apache.shardingsphere.shardingjdbc.api.ShardingDataSourceFactory;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 精确的 数据表 分片
 *
 * 一个数据库
 *
 */
public final class ShardingTablesConfigurationPrecise implements ExampleConfiguration {
    
    @Override
    public DataSource getDataSource() throws SQLException {

        //整体规则
        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
        //添加 order 表
        shardingRuleConfig.getTableRuleConfigs().add(getOrderTableRuleConfiguration());
        //添加 order item 表
        shardingRuleConfig.getTableRuleConfigs().add(getOrderItemTableRuleConfiguration());
        //设置绑定表
        shardingRuleConfig.getBindingTableGroups().add("t_order, t_order_item");
        //设置广播表
        shardingRuleConfig.getBroadcastTables().add("t_address");
        //设置默认 表的 分片规则
        shardingRuleConfig.setDefaultTableShardingStrategyConfig(new StandardShardingStrategyConfiguration("order_id", new PreciseModuloShardingTableAlgorithm()));


        return ShardingDataSourceFactory.createDataSource(createDataSourceMap(), shardingRuleConfig, new Properties());
    }

    /**
     * order 表的 规则 配置
     * @return
     */
    private static TableRuleConfiguration getOrderTableRuleConfiguration() {
        TableRuleConfiguration result = new TableRuleConfiguration("t_order", "demo_ds.t_order_${[0, 1]}");
        result.setKeyGeneratorConfig(new KeyGeneratorConfiguration("SNOWFLAKE", "order_id", getProperties()));
        return result;
    }

    /**
     * OrderItem 表的 规则配置
     * @return
     */
    private static TableRuleConfiguration getOrderItemTableRuleConfiguration() {
        TableRuleConfiguration result = new TableRuleConfiguration("t_order_item", "demo_ds.t_order_item_${[0, 1]}");
        result.setKeyGeneratorConfig(new KeyGeneratorConfiguration("SNOWFLAKE", "order_item_id", getProperties()));
        return result;
    }
    
    private static Map<String, DataSource> createDataSourceMap() {
        Map<String, DataSource> result = new HashMap<>();
        result.put("demo_ds", DataSourceUtil.createDataSource("demo_ds"));
        return result;
    }
    
    private static Properties getProperties() {
        Properties result = new Properties();
        result.setProperty("worker.id", "123");
        return result;
    }
}
