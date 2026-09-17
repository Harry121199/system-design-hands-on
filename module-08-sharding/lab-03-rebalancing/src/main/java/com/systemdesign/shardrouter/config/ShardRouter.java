package com.systemdesign.shardrouter.config;

import com.systemdesign.hashing.ConsistentHashRing;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Map;

@Component
public class ShardRouter {

    private final ConsistentHashRing ring;
    private final Map<String, DataSource> shardMap;

    public ShardRouter(Map<String, DataSource> shardMap) {
        this.shardMap = shardMap;
        this.ring = new ConsistentHashRing(150);
        for (String shardName : shardMap.keySet()) {
            if(!shardName.equals("shard-d")){ ring.addNode(shardName);}
        }
        System.out.println("Shard router initialized with ring: "
                + shardMap.keySet().stream().filter(s -> !s.equals("shard-d")).toList());
        System.out.println("Shard-d available but not in ring (waiting for rebalance)");
    }

    public DataSource getShardForProject(Long projectId) {
        String shardName = ring.getNode("project-" + projectId);
        return shardMap.get(shardName);
    }

    public String getShardName(Long projectId) {
        return ring.getNode("project-" + projectId);
    }

    public Map<String, DataSource> getAllShards() {
        return shardMap;
    }

    public void addShardToRing(String shardName) {
        ring.addNode(shardName);
        System.out.println("Added " + shardName + " to hash ring");
    }

    public boolean isShardInRing(String shardName) {
        return ring.getNode(shardName + "-test") != null
                && shardMap.containsKey(shardName);
    }

    public DataSource getDataSource(String shardName) {
        return shardMap.get(shardName);
    }
}