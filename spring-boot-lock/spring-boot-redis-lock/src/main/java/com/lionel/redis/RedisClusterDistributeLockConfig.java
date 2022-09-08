package com.lionel.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class RedisClusterDistributeLockConfig {

    @Value("${spring.redis.cluster.nodes}")
    String redisNodes;

    @Bean
    //定义分布式锁对象，稍后讲解实现
    public RedisClusterDistributeLock redisDistributeLock(JedisCluster jedisCluster){
        return new RedisClusterDistributeLock(jedisCluster);
    }

    @Bean
    //定义JedisCluster操作bean
    public JedisCluster jedisCluster(){
        return new JedisCluster(pharseHostAnport());
    }

    private Set<HostAndPort> pharseHostAnport(){
        if (StringUtils.isEmpty(redisNodes)){
            throw new RuntimeException("redis nodes can't be null or empty");
        }
        String[] hps = redisNodes.split(",");
        Set<HostAndPort> hostAndPorts = new HashSet<>();
        for (String hp : hps) {
            String[] hap = hp.split(":");
            hostAndPorts.add(new HostAndPort(hap[0], Integer.parseInt(hap[1])));
        }
        return hostAndPorts;
    }

}
