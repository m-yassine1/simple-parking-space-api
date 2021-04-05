package com.parking.cars.request;

import io.quarkus.cache.CacheInvalidate;
import io.quarkus.cache.CacheKey;
import io.quarkus.cache.CacheResult;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class IpFilterService {

    @CacheResult(cacheName = "ip-cache")
    public LimitingAddress cacheAddress(@CacheKey String address, Integer times) {
        return new LimitingAddress()
                .address(address)
                .times(times);
    }


    @CacheResult(cacheName = "ip-cache")
    public LimitingAddress getAddress(String address) {
        return new LimitingAddress()
                .address(address)
                .times(0);
    }

    @CacheInvalidate(cacheName = "ip-cache")
    public void invalidateCache(String address) {

    }

    public static class LimitingAddress {
        public Integer times;
        public String address;

        public LimitingAddress times(Integer times) {
            this.times = times;
            return this;
        }

        public LimitingAddress address(String address) {
            this.address = address;
            return this;
        }
    }
}
