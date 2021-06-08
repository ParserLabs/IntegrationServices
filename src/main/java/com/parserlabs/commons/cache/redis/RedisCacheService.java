package com.parserlabs.commons.cache.redis;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
@Lazy
@ConditionalOnExpression("${redis.cache.enabled:false}")
public class RedisCacheService {

	@Value("${redis.cache.validity.time:120}")
	private long cacheValiditiyTimeInMins;

	@Value("${redis.cache.eviction.time:1440}")
	private long cacheEvictionTimeInMins;

	@Value("${redis.cache.default.key:REDIS-DEFAULT-KEY}")
	private String defaultRedisKey;

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	private HashOperations<String, String, String> hashOperations; // to access Redis cache

	/**
	 * Initializing the Redis Template
	 */
	@PostConstruct
	public void init() {
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

	/**
	 * This method will check if the key is present in the cache or not.
	 * 
	 * @param hashKey
	 * @param value
	 * @return
	 */
	public boolean hasKey(String hashKey) {
		boolean hasKey = false;
		try {
			hasKey = hashOperations.hasKey(defaultRedisKey, hashKey);
		} catch (Exception redisExp) {
			log.warn("Exception occured while checking the key in the Redis cache.", redisExp);
		}
		return hasKey;
	}

	/**
	 * This method will return the cached value for the hash key
	 *
	 * @param hashKey
	 * @param value
	 * @return
	 */
	public String get(String hashKey) {
		String cachedValue = null;
		try {
			cachedValue = hashOperations.get(defaultRedisKey, hashKey);
		} catch (Exception redisExp) {
			log.warn("Exception occured while fetching the value from the Redis cache.", redisExp);
		}
		return cachedValue;
	}
}
