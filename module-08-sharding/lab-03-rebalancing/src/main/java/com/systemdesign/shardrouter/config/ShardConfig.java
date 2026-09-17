package com.systemdesign.shardrouter.config;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShardConfig {

    @Bean
    public DataSource shardA() {
        return DataSourceBuilder.create()
                .url("jdbc:h2:mem:shard_a;DB_CLOSE_DELAY=-1")
                .driverClassName("org.h2.Driver")
                .username("sa")
                .password("")
                .build();
    }

    @Bean
    public DataSource shardB() {
        return DataSourceBuilder.create()
                .url("jdbc:h2:mem:shard_b;DB_CLOSE_DELAY=-1")
                .driverClassName("org.h2.Driver")
                .username("sa")
                .password("")
                .build();
    }

    @Bean
    public DataSource shardC() {
        return DataSourceBuilder.create()
                .url("jdbc:h2:mem:shard_c;DB_CLOSE_DELAY=-1")
                .driverClassName("org.h2.Driver")
                .username("sa")
                .password("")
                .build();
    }

    @Bean
    public DataSource shardD() {
        return DataSourceBuilder.create()
                .url("jdbc:h2:mem:shard_d;DB_CLOSE_DELAY=-1")
                .driverClassName("org.h2.Driver")
                .username("sa")
                .password("")
                .build();
    }

    @Bean
    public Map<String, DataSource> shardMap(DataSource shardA, DataSource shardB, DataSource shardC, DataSource shardD) {
        Map<String, DataSource> map = new LinkedHashMap<>();
        map.put("shard-a", shardA);
        map.put("shard-b", shardB);
        map.put("shard-c", shardC);
        map.put("shard-d", shardD);
        return map;
    }
}