package com.kt.security.blacklist;

public class RedisKey {
    private RedisKey() {}
    public static String blacklistedAccessToken(String jti){
        return "bl:at:" + jti;
    }
}
