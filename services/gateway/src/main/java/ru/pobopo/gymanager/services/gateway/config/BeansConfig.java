package ru.pobopo.gymanager.services.gateway.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import ru.pobopo.gymanager.shared.gson.ExceptionSerializer;
import ru.pobopo.gymanager.shared.objects.UnprotectedPathsValidator;

@Configuration
@Slf4j
public class BeansConfig {
    public static final String REDIS_HOST_ENV = "REDIS_HOST";
    public static final String REDIS_PORT_ENV = "REDIS_PORT";

    @Bean
    public JedisPool jedisPool(Environment environment, JedisPoolConfig jedisPoolConfig) {
        String redisHost = environment.getProperty(REDIS_HOST_ENV);
        String redisPort = environment.getProperty(REDIS_PORT_ENV);
        if (StringUtils.isBlank(redisHost) || StringUtils.isBlank(redisPort)) {
            log.info("No redis config were found in env. JedisPool = null");
            // Не кидать ошибку, тк не критическая часть инфраструктуры
            return null;
        }
        log.info("Redis host:port :: {}:{}", redisHost, redisPort);
        return new JedisPool(jedisPoolConfig, redisHost, Integer.parseInt(redisPort));
    }

    @Bean
    public JedisPoolConfig buildPoolConfig() {
        final JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(128);
        poolConfig.setMaxIdle(128);
        poolConfig.setMinIdle(16);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);
        poolConfig.setMinEvictableIdleTime(Duration.ofSeconds(60));
        poolConfig.setTimeBetweenEvictionRuns(Duration.ofSeconds(30));
        poolConfig.setNumTestsPerEvictionRun(3);
        poolConfig.setBlockWhenExhausted(true);
        return poolConfig;
    }

    @Bean
    public Gson gson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Exception.class, new ExceptionSerializer());
        return gsonBuilder.create();
    }
    @Bean
    public UnprotectedPathsValidator pathsValidator() {
        return new UnprotectedPathsValidator();
    }
}
