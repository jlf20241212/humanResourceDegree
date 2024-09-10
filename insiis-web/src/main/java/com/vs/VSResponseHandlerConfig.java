package com.vs;

import com.vs.common.util.rpc.handler.CustomArgumentResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class VSResponseHandlerConfig implements WebMvcConfigurer {

    @Value("${com.toco.response.handler:true}")
    private boolean useHandler;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new CustomArgumentResolver());
    }

    @Override
    public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
        if (!useHandler) {
            return;
        }
        ResponseJsonExceptionResolver responseJsonExceptionResolver = new ResponseJsonExceptionResolver();
        try {
            responseJsonExceptionResolver.afterPropertiesSet();
        } catch (Exception e) {
            e.printStackTrace();
        }
        exceptionResolvers.add(responseJsonExceptionResolver);
    }

    @Override
    public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
        if (!useHandler) {
            return;
        }
        ResponseJsonMethodReturnValueHandler responseJsonMethodReturnValueHandler = new ResponseJsonMethodReturnValueHandler();
        responseJsonMethodReturnValueHandler.afterPropertiesSet();
        returnValueHandlers.add(responseJsonMethodReturnValueHandler);
    }

}
