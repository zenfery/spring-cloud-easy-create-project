package cc.zenfery.easycreateproject.example.boot2x.config;

import cc.zenfery.easycreateproject.response.CloudResponseHandler;
import cc.zenfery.easycreateproject.response.handler.CamelBodyCloudResponseHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 
 */
@Configuration
public class ResponseConfiguration {

    @Bean
    public CloudResponseHandler cloudResponseHandler() {
        return new CamelBodyCloudResponseHandler();
    }
}
