package ru.pobopo.gymanager.services.user.service.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.pobopo.gymanager.shared.gson.ExceptionSerializer;
import ru.pobopo.gymanager.shared.objects.UnprotectedPathsValidator;

@Configuration
public class BeansConfig {
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
