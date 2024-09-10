package com.vs;

import com.vs.ox.common.utils.ObjectMapperFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletResponse;
import java.util.Collections;

/**
 * 处理 Controller 里的返回值，从 Object转为Json
 */
public class ResponseJsonMethodReturnValueHandler implements HandlerMethodReturnValueHandler, InitializingBean {
    private HttpMessageConverter messageConverter;

    @Override
    public void afterPropertiesSet() {
        if (messageConverter == null) {
            messageConverter = new MappingJackson2HttpMessageConverter(ObjectMapperFactory.getDefaultObjectMapper());
        }
    }

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return true;
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        mavContainer.setRequestHandled(true);
        Object result = returnValue;
        /**
         * Web分页请求的返回按照antd框架要求的格式，不转为SuccessData
         */
        result = result == null ? new SuccessData(Collections.emptyMap()) : new SuccessData(returnValue);
        ServletServerHttpResponse response = new ServletServerHttpResponse(webRequest.getNativeResponse(HttpServletResponse.class));
        messageConverter.write(result, new MediaType(MediaType.APPLICATION_JSON, Collections.singletonMap("charset", "utf-8")), response);
    }


}
