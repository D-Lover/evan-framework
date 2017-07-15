package org.evanframework.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * Redis工具类
 *
 * @author shen.wei
 * @version %I%, %G%
 * @since 1.0
 */
public class RedisUtil {
    private static final Logger log = LoggerFactory.getLogger(RedisUtil.class);

    private RedisTemplate<Serializable, Serializable> redisTemplate;

    private HashOperations<Serializable, Object, Object> hashOperations;
//    private ValueOperations<Serializable, Serializable> valueOperations;
//    private ListOperations<Serializable, Serializable> listOperations;
//    private SetOperations<Serializable, Serializable> setOperations;

    //    private SysConfig sysConfig;
    private String redisKeyPrefix;

    public RedisUtil(RedisTemplate<Serializable, Serializable> redisTemplate) {
        this.redisTemplate = redisTemplate;
        hashOperations = redisTemplate.opsForHash();
        redisKeyPrefix = "";
//        valueOperations = redisTemplate.opsForValue();
//        listOperations = redisTemplate.opsForList();
//        setOperations = redisTemplate.opsForSet();
//
//        if (sysConfig != null) {
//            String temp = sysConfig.get("redis.key.prefix");
//            if (StringUtils.isNotBlank(temp)) {
//                redisKeyPrefix = temp + "_";
//            } else {
//                redisKeyPrefix = "";
//            }
//        } else {
//            redisKeyPrefix = "";
//        }

        //log.info("Redis client inited,use[org.springframework.data.redis]");
    }

//    public void destroy() {
//        hashOperations = null;
////        valueOperations = null;
////        listOperations = null;
////        setOperations = null;
//    }


    public void put(Serializable objectTypeKey, Serializable objectKey, final Object o, Integer expireSeconds) {
        checkInited();
        if (objectKey != null && o != null) {
            if (log.isDebugEnabled()) {
                log.debug(String.format("Put redis objectTypeKey is [%s],objectKey is [%s], value is [%s]",
                        objectTypeKey, objectKey, o));
            }
            String fullObjectTypeKey = getFullObjectTypeKey(objectTypeKey);
            try {
                if (expireSeconds != null) {
                    redisTemplate.expire(fullObjectTypeKey, expireSeconds, TimeUnit.SECONDS);
                }
                hashOperations.put(fullObjectTypeKey, String.valueOf(objectKey), o);
            } catch (RuntimeException ex) {
                log.error("Put redis error,fullObjectTypeKey is [" + fullObjectTypeKey + "],objectKey is [" + objectKey
                        + "]," + ex.getMessage(), ex);
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("No redis put");
            }
        }
    }

    public <T> T get(Serializable objectTypeKey, Serializable objectKey, Class<T> c) {
        checkInited();
        T returnO = null;
        if (objectKey != null) {

            String fullObjectTypeKey = getFullObjectTypeKey(objectTypeKey);
            Object o = null;
            try {
                o = hashOperations.get(getFullObjectTypeKey(objectTypeKey), String.valueOf(objectKey));
            } catch (RuntimeException ex) {
                log.error("Get redis error,fullObjectTypeKey is [" + fullObjectTypeKey + "],objectKey is [" + objectKey
                        + "]," + ex.getMessage(), ex);
            }

            if (o != null) {
                returnO = (T) o;
                if (log.isDebugEnabled()) {
                    log.debug(String.format("Get Put redis objectTypeKey is	[%s],objectKey is [%s],value is [%s]",
                            objectTypeKey, objectKey, returnO));
                }
            } else {
                if (log.isDebugEnabled()) {
                    log.debug(String.format("Not find redis objectTypeKey is [%s],objectKey is [%s]", objectTypeKey,
                            objectKey));
                }
            }
        }
        return returnO;
    }

    public void remove(Serializable objectTypeKey, Serializable objectKey) {
        checkInited();
        if (objectKey != null) {
            String fullObjectTypeKey = getFullObjectTypeKey(objectTypeKey);
            try {
                hashOperations.delete(fullObjectTypeKey, String.valueOf(objectKey));
            } catch (RuntimeException ex) {
                log.error("Delete redis error,fullObjectTypeKey is [" + fullObjectTypeKey + "],objectKey is ["
                        + objectKey + "]," + ex.getMessage(), ex);
            }
        }
    }

    private void checkInited() {
        if (hashOperations == null) {
            throw new IllegalStateException("RedisUtil is not initialized，please invoke method [init] first! ");
        }
    }

    public boolean has(Serializable objectTypeKey, Serializable objectKey) {
        String fullObjectTypeKey = getFullObjectTypeKey(objectTypeKey);
        return hashOperations.hasKey(fullObjectTypeKey, String.valueOf(objectKey));
    }

    private String getFullObjectTypeKey(Serializable objectTypeKey) {
        return redisKeyPrefix + String.valueOf(objectTypeKey);
    }

//    public void setRedisTemplate(RedisTemplate<Serializable, Serializable> redisTemplate) {
//        this.redisTemplate = redisTemplate;
//    }
}