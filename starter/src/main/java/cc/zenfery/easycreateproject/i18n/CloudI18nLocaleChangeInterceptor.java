package cc.zenfery.easycreateproject.i18n;

import org.springframework.context.i18n.SimpleTimeZoneAwareLocaleContext;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleContextResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;
import java.util.TimeZone;

// 增加对 timezone 的拦截
public class CloudI18nLocaleChangeInterceptor extends LocaleChangeInterceptor {

    public static final String DEFAULT_TIMEZONE_PARAM_NAME = "timezone";

    // 时区请求参数
    private String timezoneParamName = DEFAULT_TIMEZONE_PARAM_NAME;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServletException {

        String newLocale = request.getParameter(getParamName());
        String newTimezone = request.getParameter(getTimezoneParamName());

        if (newLocale != null) {
            LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
            if (localeResolver == null) {
                throw new IllegalStateException(
                        "No LocaleResolver found: not in a DispatcherServlet request?");
            }
            try {

                final Locale locale = parseLocaleValue(newLocale);
                if(newTimezone != null){
                    final TimeZone timeZone = StringUtils.parseTimeZoneString(newTimezone);
                    if( localeResolver instanceof LocaleContextResolver){
                        LocaleContextResolver lcr = (LocaleContextResolver)localeResolver;

                        lcr.setLocaleContext(request, response, new SimpleTimeZoneAwareLocaleContext(locale, timeZone));
                    }
                }else{
                    localeResolver.setLocale(request, response, parseLocaleValue(newLocale));
                }
            }
            catch (IllegalArgumentException ex) {
                if (isIgnoreInvalidLocale()) {
                    logger.debug("Ignoring invalid locale value [" + newLocale + "]: " + ex.getMessage());
                }
                else {
                    throw ex;
                }
            }
        }
        return true;
    }

    public String getTimezoneParamName() {
        return timezoneParamName;
    }

    public void setTimezoneParamName(String timezoneParamName) {
        this.timezoneParamName = timezoneParamName;
    }
}