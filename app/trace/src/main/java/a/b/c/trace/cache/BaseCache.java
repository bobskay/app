package a.b.c.trace.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


@Slf4j
abstract public class BaseCache<K, V> {
    protected LoadingCache<K, V> cache;

    public BaseCache() {
        cache = CacheBuilder.newBuilder()
                .initialCapacity(1000)
                .expireAfterWrite(365, TimeUnit.DAYS)
                .maximumSize(10000)
                .build(new CacheLoader<K, V>() {
                    public V load(K key) {
                        return loadImpl(key);
                    }
                });
    }

     abstract public V loadImpl(K k);

    public V get(K k)  {
        try {
            return cache.get(k);
        } catch (Exception e) {
           if(e instanceof CacheLoader.InvalidCacheLoadException){
               return null;
           }
           throw new RuntimeException(e);
        }
    }
}
