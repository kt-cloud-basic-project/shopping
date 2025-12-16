package com.kt.security.blacklist;

import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.redisson.api.StreamInfo;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class TokenBlacklistStore {
    private final RedissonClient redissonClient;

    public TokenBlacklistStore(RedissonClient redissonClient){
        this.redissonClient = redissonClient;
    }

    public void blacklistAccessToken(String jti, Duration ttl){
        if(jti == null || jti.isBlank()) return;
        if(ttl == null || ttl.isZero() || ttl.isNegative()) return;

        String key = RedisKey.blacklistedAccessToken(jti);
        RBucket<String> bucket = redissonClient.getBucket(key);
        bucket.set("1",ttl);
    }

    public boolean isBlacklisted(String jti){
        if(jti == null || jti.isBlank()) return false;
        String key = RedisKey.blacklistedAccessToken(jti);
        return redissonClient.getBucket(key).isExists();
    }
}
