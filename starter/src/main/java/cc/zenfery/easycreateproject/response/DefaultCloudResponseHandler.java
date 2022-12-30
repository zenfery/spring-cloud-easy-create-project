package cc.zenfery.easycreateproject.response;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

// 默认的响应结果处理器
public class DefaultCloudResponseHandler implements CloudResponseHandler {

    private static final String RESPONSE_STATUS_HEADER_NAME = "Status";
    private static final String RESPONSE_ERROR_CODE_HEADER_NAME = "Error-Code";

    @Override
    public Object handler(HttpServletRequest request, HttpServletResponse response,
                          Status status, String errorCode, String msg, Object data) {

        response.setHeader(RESPONSE_STATUS_HEADER_NAME, String.valueOf(status.getStat()));

        if(status == Status.SUCCESS){
            return data;
        }else{
            response.setHeader(RESPONSE_ERROR_CODE_HEADER_NAME, errorCode);

            Map<String, Object> ret = new HashMap<>();
            ret.put("status", status.getStat());
            ret.put("errorCode", errorCode);
            ret.put("msg", msg);
            return ret;
        }
    }
}