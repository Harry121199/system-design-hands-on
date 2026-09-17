package com.systemdesign.shardrouter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {
		DataSourceAutoConfiguration.class,
})
public class ShardRouterApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShardRouterApplication.class, args);
	}

}
