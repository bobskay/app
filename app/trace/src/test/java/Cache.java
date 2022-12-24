import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class Cache {

    public static void main(String[] args) throws ExecutionException {
        LoadingCache<String, Optional<String>> cache = CacheBuilder.newBuilder()
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .initialCapacity(10)
                .maximumSize(20)
                .recordStats()
                .build(new CacheLoader<String, Optional<String>>() {
                           @Override
                           public Optional<String> load(String key) {
                               try {
                                   return Optional.ofNullable(loadFunc().apply(key));
                               } catch (Exception e) {
                                   throw e;
                               }
                           }
                       }
                );

        for (int i = 0; i < 100; i++) {
            for (int j = 1; j < 2; j++) {
                Optional x = cache.get("a" + j);
                System.out.println(i + ":" + j + ":" + x.get());
                if (i == 20) {
                    cache.get("abc");
                }
            }

        }


    }

    public static Function<String, String> loadFunc() {
        return (k) -> {
            System.out.println("未命中");
            return k + "abc";
        };
    }
}
