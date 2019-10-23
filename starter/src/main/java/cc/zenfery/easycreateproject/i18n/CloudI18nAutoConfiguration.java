package cc.zenfery.easycreateproject.i18n;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleContextResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import java.util.Locale;

@Configuration
@ConditionalOnProperty(name = "easycreateproject.i18n.enabled", havingValue = "true", matchIfMissing = false)
@ConfigurationProperties(prefix = "easycreateproject.i18n")
@Getter
@Setter
public class CloudI18nAutoConfiguration {

    // 默认国际化时，locale cookie name
    public static final String DEFAULT_I18N_COOKIENAME = "lang";

    private Boolean enabled = false;
    // locale cookie name
    private String cookieName = DEFAULT_I18N_COOKIENAME;

    // locale 解析器
    @Bean("localeResolver")
    public LocaleContextResolver localeContextResolver(){
        CookieLocaleResolver cookieLocaleResolver = new CookieLocaleResolver();
        cookieLocaleResolver.setDefaultLocale(Locale.SIMPLIFIED_CHINESE);
        cookieLocaleResolver.setCookieName(cookieName);
        return cookieLocaleResolver;
    }


    @Configuration
    public static class WebMvcI18nConfig implements WebMvcConfigurer {

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            CloudI18nLocaleChangeInterceptor cloudI18nLocaleChangeInterceptor = new CloudI18nLocaleChangeInterceptor();
            registry.addInterceptor(cloudI18nLocaleChangeInterceptor).addPathPatterns("/**");
        }
    }
}