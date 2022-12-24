package a.b.c.base.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Slf4j
abstract public class CacheService<V> {

    protected LoadingCache<String, Optional<V>> cache = CacheBuilder.newBuilder()
            .expireAfterWrite(getCacheTimeoutMinute(), TimeUnit.MINUTES)
            .initialCapacity(getInitialCapacity())
            .maximumSize(getMaximumSize())
            .recordStats()
            .build(new CacheLoader<String, Optional<V>>() {
                       @Override
                       public Optional<V> load(String key) {
                           try {
                               return Optional.ofNullable(loadFunc().apply(key));
                           } catch (Exception e) {
                               log.error("加载缓存数据异常", e);
                               throw e;
                           }
                       }
                   }
            );

    public V get(String key) {
        Optional<V> value = cache.getUnchecked(key);
        if (!value.isPresent()) {
            log.error("数据不存在" + this.getClass().getSimpleName() + "." + key);
        }
        return value.get();
    }

    protected long getMaximumSize() {
        return 1000;
    }

    protected int getInitialCapacity() {
        return 10000;
    }

    protected long getCacheTimeoutMinute() {
        return 36 * 24 * 30;
    }

    abstract protected Function<String, V> loadFunc();
}
