package cc.zenfery.easycreateproject.response;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// 响应结果处理器
public interface CloudResponseHandler {

    /**
     * 根据响应信息封装响应
     * @param request
     * @param response
     * @param status
     * @param errorCode
     * @param msg
     * @param data 正确响应对象
     * @return Object Http 响应Body
     */
    Object handler(HttpServletRequest request, HttpServletResponse response
            , Status status, String errorCode, String msg, Object data);
}
