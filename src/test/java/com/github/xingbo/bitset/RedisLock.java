package com.github.xingbo.bitset;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * desc:
 *
 * @author: bobo
 * createDate: 19-4-2
 */
public class RedisLock {

    private RedisTemplate redisTemplate;

    public String aquirePessimisticLockWithTimeout(String lockName,int acquireTimeout, int lockTimeout) {

        if (StringUtils.isEmpty(lockName) || lockTimeout <= 0) return null;

        final String lockKey = lockName;
        String identifier = UUID.randomUUID().toString();
        Calendar atoCal = Calendar.getInstance();
        atoCal.add(Calendar.SECOND, acquireTimeout);
        Date atoTime = atoCal.getTime();

        // try to acquire the lock
        Object execute = redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.setNX(redisTemplate.getStringSerializer().serialize(lockKey), redisTemplate.getStringSerializer().serialize(identifier));
            }
        });
        if(Boolean.valueOf(execute.toString())){
                redisTemplate.execute(new RedisCallback<Boolean>() {
                    @Override
                    public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                        return connection.expire(redisTemplate.getStringSerializer().serialize(lockKey), lockTimeout);
                    }
                });
                return identifier;
        }else{
            // fail to acquire the lock
            // set expiration of the lock in case ttl is not set yet.
            if (null == redisTemplate.execute(new RedisCallback<Long>() {
                @Override
                public Long doInRedis(RedisConnection connection) throws DataAccessException {
                    return connection.ttl(redisTemplate.getStringSerializer().serialize(lockKey));
                }
            })) { // set expiration of the lock
                redisTemplate.execute(new RedisCallback<Boolean>() {
                    @Override
                    public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                        return connection.expire(redisTemplate.getStringSerializer().serialize(lockKey), lockTimeout);
                    }
                });
            }
            if (acquireTimeout < 0) // no wait
                return null;
            else {
                try {
                    Thread.sleep(100l); // wait 100 milliseconds before retry
                } catch (InterruptedException ex) {
                }
            }
            if (new Date().after(atoTime));
        }
        return "";
    }


    public void releasePessimisticLockWithTimeout(String lockName, String identifier) {
        if (StringUtils.isEmpty(lockName) || StringUtils.isEmpty(identifier)) return;
        final String lockKey = lockName;
        redisTemplate.execute(new RedisCallback<Void>() {
            @Override
            public Void doInRedis(RedisConnection connection) throws DataAccessException {
                byte[] ctn = connection.get(redisTemplate.getStringSerializer().serialize(lockKey));
                if (ctn != null && identifier.equals(redisTemplate.getStringSerializer().deserialize(ctn)))
                    connection.del(redisTemplate.getStringSerializer().serialize(lockKey));
                return null;
            }
        });
    }
}
