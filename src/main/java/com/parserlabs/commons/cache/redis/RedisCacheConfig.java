package com.parserlabs.commons.cache.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
@ConditionalOnResource(resources = "classpath:redis.cache")
public class RedisCacheConfig {

	@Value("${redis.cache.host}")
	private String redisHost;

	@Value("${redis.cache.port:6379}")
	private int redisPort;

	@Value("${redis.cache.password}")
	private String redisPassword;


	@Bean
	@Lazy
	public JedisConnectionFactory jedisConnectionFactory() {
		RedisStandaloneConfiguration standaloneConfig = new RedisStandaloneConfiguration(redisHost.trim(), redisPort);
				standaloneConfig.setPassword(redisPassword.trim());
		return new JedisConnectionFactory(standaloneConfig);
	}

	@Bean("redisCacheManager")
	@Lazy
	public CacheManager redisCacheManager() {
		return RedisCacheManager.builder(jedisConnectionFactory()).build();
	}

	@Bean
	@Lazy
	public RedisTemplate<String, String> redisTemplate() {
		RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
		try {
			redisTemplate.setConnectionFactory(jedisConnectionFactory());
		} catch (Exception e) {
			log.warn("Unable to create RedisTemplate object due to JedisConnectionFactory error. Message [{}]",
					e.getMessage());
		}
		return redisTemplate;
	}

}
