package com.quye.utils;

public class RedisConstants {
    public static final String LOGIN_CODE_KEY = "quye:login:code:";
    public static final Long LOGIN_CODE_TTL = 2L;
    public static final String LOGIN_USER_KEY = "quye:login:token:";
    public static final Long LOGIN_USER_TTL = 36000L;

    public static final Long CACHE_NULL_TTL = 2L;

    public static final Long CACHE_PLACE_TTL = 30L;
    public static final String CACHE_PLACE_KEY = "quye:place:";

    public static final String LOCK_PLACE_KEY = "quye:lock:place:";
    public static final Long LOCK_PLACE_TTL = 10L;

    public static final String FLASH_PASS_STOCK_KEY = "quye:flash:stock:";
    public static final String NOTE_LIKED_KEY = "quye:note:liked:";
    public static final String FOLLOW_FEED_KEY = "quye:feed:";
    public static final String PLACE_GEO_KEY = "quye:place:geo:";
    public static final String USER_SIGN_KEY = "quye:sign:";
}
