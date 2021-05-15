package com.parserlabs.commons.cache.redis;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
@Lazy
@ConditionalOnResource(resources = "classpath:redis.cache")
public class RedisCacheService {

	@Value("${redis.cache.validity.time:120}")
	private long cacheValiditiyTimeInMins;

	@Value("${redis.cache.eviction.time:1440}")
	private long cacheEvictionTimeInMins;
	
	@Value("${redis.cache.default.key:REDIS-DEFAULT-KEY}")
	private String defaultRedisKey;

	private HashOperations<String, String, String> hashOperations; // to access Redis cache

	@Autowired
	public RedisCacheService(RedisTemplate<String, String> redisTemplate) {
		try {
			redisTemplate.expire(defaultRedisKey, cacheEvictionTimeInMins, TimeUnit.HOURS);
		} catch (Exception redisExp) {
			log.warn("Exception occured while configuring the Redis Template.", redisExp);
		}
		this.hashOperations = redisTemplate.opsForHash();
	}

	/**
	 * This method will check if the value is present in the cache or not, and the
	 * cached value should be within the 'cacheValiditiyTimeInMins' time.
	 *
	 * by default this will return true if it doesn't get any value
	 *
	 * @param hashKey
	 * @param value
	 * @return
	 */
	public boolean match(String hashKey) {
		boolean doesMatch = false;
		try {
			String cachedValue = hashOperations.get(defaultRedisKey, hashKey);
			if (Objects.nonNull(cachedValue)) {
				doesMatch = true;
			}
		} catch (Exception redisExp) {
			log.warn("Exception occured while fetching the value from the Redis cache.", redisExp);
		}
		return doesMatch;
	}

	/**
	 * This method will put the hash key and value in the cache
	 *
	 * @param hashKey
	 * @param value
	 */
	public boolean put(String hashKey, String value) {
		boolean flag = false;
		try {
			hashOperations.put(defaultRedisKey, hashKey, value);
			log.info("Value strored in redis cache [{}} against {}", defaultRedisKey, hashKey);
			flag = true;
		} catch (Exception redisExp) {
			log.warn("Exception occured while saving the value into the Redis cache.", redisExp);
		}
		return flag;
	}

	/**
	 * This method will remove the key from the cache
	 *
	 * @param hashKey
	 */
	public void remove(String hashKey) {
		try {
			hashOperations.delete(defaultRedisKey, hashKey);
		} catch (Exception redisExp) {
			log.warn("Exception occured while deleting the value into the Redis cache.", redisExp);
		}
	}
}
