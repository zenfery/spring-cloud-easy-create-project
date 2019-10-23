package cc.zenfery.easycreateproject.response;

import cc.zenfery.easycreateproject.i18n.CloudI18nAutoConfiguration;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Locale;

@CommonsLog
@Configuration
@AutoConfigureAfter({MessageSourceAutoConfiguration.class, CloudI18nAutoConfiguration.class})
public class CloudResponseAutoConfiguration {

    private static final String REQUEST_ATTR_STATUS = CloudResponseAutoConfiguration.class.getName() + "." + "_STATUS_";
    private static final String REQUEST_ATTR_ERRORCODE = CloudResponseAutoConfiguration.class.getName() + "." + "_ERRORCODE_";
    private static final String REQUEST_ATTR_MSG = CloudResponseAutoConfiguration.class.getName() + "." + "_MSG_";

    // 默认的响应结果处理器
    private static final CloudResponseHandler defaultCloudResponseHandler = new DefaultCloudResponseHandler();

    @Autowired(required = false)
    private CloudResponseHandler cloudResponseHandler;

    private CloudResponseHandler getCloudResponseHandler(){
        if(cloudResponseHandler != null){
            return cloudResponseHandler;
        }else{
            return defaultCloudResponseHandler;
        }
    }

    // 请求处理过程中，报错问题
    @Configuration
    @ControllerAdvice
    @ConditionalOnProperty(name = "easycreateproject.response.enabled", matchIfMissing = true, havingValue = "true")
    public class ExceptionHandlerControllerAdvice {

        // 如果未开启 i18n，则不会配置此对象
        @Autowired(required = false)
        private CloudI18nAutoConfiguration cloudI18nAutoConfiguration;

        @Autowired(required = false)
        private MessageSource messageSource;

        @ExceptionHandler(Exception.class)
        @ResponseBody
        public Object commonExceptionHandler(HttpServletRequest request,
                                                   HttpServletResponse response, Exception e) {

            e.printStackTrace();
            CloudResponseHandler crh = getCloudResponseHandler();

            String errorCode = "UnkownError";
            Object[] args = null;
            Status status = Status.ERROR;


            String msg = "error";
            if(e instanceof MethodArgumentNotValidException){

                msg = "";
                MethodArgumentNotValidException methodArgumentNotValidException = (MethodArgumentNotValidException)e;
                List<FieldError> fes = methodArgumentNotValidException.getBindingResult().getFieldErrors();
                for(FieldError fe : fes){
                    msg += " ["+fe.getField()+"] "+fe.getDefaultMessage()+" , ";
                }

                args = new String[]{msg};

            }else if( e instanceof ResponseException){
                ResponseException re = (ResponseException)e;
                errorCode = re.getErrorCode();
                args = re.getArgs();

            }else{
                args = new String[]{e.getMessage()};
            }

            request.setAttribute(REQUEST_ATTR_STATUS, status);
            request.setAttribute(REQUEST_ATTR_ERRORCODE, errorCode);
            request.setAttribute(REQUEST_ATTR_MSG, this.getMessage(errorCode, args));

            // 此处不能返回null, 若返回 null, CloudCommonResponseBodyAdvice 将不再执行
            return "error";
        }


        private String getMessage(String errorCode, Object[] args){

            if(isI18nEnabled()){
                Locale localeFromLocaleContextHolder = LocaleContextHolder.getLocale();
                log.debug(" ===> current locale is : "+localeFromLocaleContextHolder);
                return messageSource.getMessage(errorCode, args, errorCode, localeFromLocaleContextHolder);
            }else{
                return errorCode;
            }
        }

        // 是否开启 i18n
        private Boolean isI18nEnabled(){
            return cloudI18nAutoConfiguration != null && cloudI18nAutoConfiguration.getEnabled();
        }



    }


    @ControllerAdvice
    public class CloudCommonResponseBodyAdvice implements ResponseBodyAdvice<Object>{

        @Override
        public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
            log.debug(returnType + " : "+converterType);
            return true;
        }

        @Override
        public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
            ServletServerHttpRequest httpReq = (ServletServerHttpRequest) request;
            ServletServerHttpResponse httpRes = (ServletServerHttpResponse) response;
            HttpServletRequest httpServletRequest = httpReq.getServletRequest();
            HttpServletResponse httpServletResponse = httpRes.getServletResponse();

            String contextPath = httpServletRequest.getContextPath();
            log.debug(contextPath);
            String path = httpServletRequest.getRequestURI().replaceFirst(contextPath, "");
            log.debug(path);
            if(path.startsWith("/actuator")){
                return body;
            }

            Status status = (Status) httpServletRequest.getAttribute(REQUEST_ATTR_STATUS);
            String errorCode = (String) httpServletRequest.getAttribute(REQUEST_ATTR_ERRORCODE);
            String msg = (String) httpServletRequest.getAttribute(REQUEST_ATTR_MSG);

            Object ret = getCloudResponseHandler().handler(httpServletRequest, httpServletResponse
                    , status == null ? Status.SUCCESS : status
                    , errorCode
                    , msg
                    , body);
            return ret;
        }

    }
}
