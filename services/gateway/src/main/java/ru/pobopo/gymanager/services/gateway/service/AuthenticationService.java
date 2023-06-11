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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import ru.pobopo.gymanager.services.gateway.exception.UnauthorizedException;
import ru.pobopo.gymanager.services.gateway.util.ParameterStringBuilder;
import ru.pobopo.gymanager.shared.objects.ErrorResponse;
import ru.pobopo.gymanager.shared.objects.AuthorizedUserInfo;

@Slf4j
@Service
public class AuthenticationService {
    private final String userServicePath;
    private final Gson gson;

    @Autowired
    public AuthenticationService(Environment env, Gson gson) {
        this.userServicePath = env.getProperty("USER_SERVICE_PATH", "http://192.168.1.230:9090");
        this.gson = gson;
    }

    public AuthorizedUserInfo validateToken(String currentRequestId, String token) throws IOException, UnauthorizedException {
        String params = ParameterStringBuilder.getParamsString(Map.of("token", token));
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
            throw new UnauthorizedException("Empty authentication service response");
        }
        if (status > 299) {
            ErrorResponse errorResponse = new ErrorResponse();
            try {
                 errorResponse = gson.fromJson(content.toString(), ErrorResponse.class);
            } catch (Exception exception) {
                log.error(exception.getMessage(), exception);
            }
            throw new UnauthorizedException(errorResponse.getMessage(), errorResponse.getException());
        }

        return gson.fromJson(content.toString(), AuthorizedUserInfo.class);
    }
}
