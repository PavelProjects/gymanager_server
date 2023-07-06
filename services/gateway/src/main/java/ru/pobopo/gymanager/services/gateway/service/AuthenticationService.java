package ru.pobopo.gymanager.services.gateway.service;

import static ru.pobopo.gymanager.shared.constants.HeadersNames.CURRENT_REQUEST_ID;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import reactor.util.annotation.Nullable;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import ru.pobopo.gymanager.services.gateway.exception.AccessDeniedException;
import ru.pobopo.gymanager.services.gateway.util.ParameterStringBuilder;
import ru.pobopo.gymanager.shared.objects.AuthorizedUserInfo;
import ru.pobopo.gymanager.shared.objects.ErrorResponse;

@Slf4j
@Service
public class AuthenticationService {
    public static final String TOKEN_CACHE_EXPIRE_TIME_ENV = "TOKEN_CACHE_EXPIRE_TIME";

    private final String userServicePath;
    private final long cacheExpireTime; //sec
    private final Gson gson;
    private final JedisPool jedisPool;

    @Autowired
    public AuthenticationService(Environment env, Gson gson, JedisPool jedisPool) {
        this.userServicePath = env.getProperty("USER_SERVICE_PATH", "http://192.168.1.230:9090");
        this.gson = gson;
        this.jedisPool = jedisPool;
        this.cacheExpireTime = Long.parseLong(env.getProperty(TOKEN_CACHE_EXPIRE_TIME_ENV, "5"));
    }

    @Nullable
    public AuthorizedUserInfo validateToken(String currentRequestId, String token) throws IOException, AccessDeniedException {
        String userInfoJson = getUserInfoFromRedis(token);
        if (StringUtils.isBlank(userInfoJson)) {
            log.info("CACHE MISS, LOADING USER INFO FROM SERVICE");
            userInfoJson = sendValidateRequest(currentRequestId, token);
            saveUserInfoInRedis(token, userInfoJson);
        } else {
            log.info("CACHE HIT");
        }
        if (StringUtils.isBlank(userInfoJson)) {
            return null;
        }
        return gson.fromJson(userInfoJson, AuthorizedUserInfo.class);
    }

    @Nullable
    private String getUserInfoFromRedis(String token) {
        if (jedisPool == null) {
            return null;
        }
        if (StringUtils.isBlank(token)) {
            return null;
        }
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(token);
        }
    }

    private void saveUserInfoInRedis(String token, String userInfo) {
        if (jedisPool == null) {
            return;
        }
        if (StringUtils.isBlank(token) || StringUtils.isBlank(userInfo)) {
            return;
        }
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set(token, userInfo);
            jedis.expire(token, cacheExpireTime);
        }
    }

    @NotNull
    @NotEmpty
    private String sendValidateRequest(String currentRequestId, String token) throws IOException, AccessDeniedException {
        String params = ParameterStringBuilder.getParamsString(Map.of("token", token));
        // как по мне не очень красиво выглядит, но наверное норм
        URL url = new URL(userServicePath + "/auth/validate?" + params);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty(CURRENT_REQUEST_ID, currentRequestId);
        con.setDoOutput(true);

        int status = con.getResponseCode();
        Reader streamReader;
        if (status > 299) {
            log.warn("Authentication failed with code {}", status);
            streamReader = new InputStreamReader(con.getErrorStream());
        } else {
            streamReader = new InputStreamReader(con.getInputStream());
        }

        StringBuilder content = new StringBuilder();
        try (BufferedReader in = new BufferedReader(streamReader)) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
        }
        con.disconnect();
        if (content.isEmpty()) {
            throw new AccessDeniedException("Empty authentication service response");
        }
        if (status > 299) {
            ErrorResponse errorResponse = new ErrorResponse();
            try {
                errorResponse = gson.fromJson(content.toString(), ErrorResponse.class);
            } catch (Exception exception) {
                log.error(exception.getMessage(), exception);
            }
            throw new AccessDeniedException(errorResponse.getMessage(), errorResponse.getException());
        }
        return content.toString();
    }
}
