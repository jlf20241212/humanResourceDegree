package com.vs;

import com.vs.ox.common.exception.ErrorCode;
import com.vs.ox.common.exception.IgnoredException;
import com.vs.ox.common.utils.ObjectMapperFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.Ordered;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Slf4j
public class ResponseJsonExceptionResolver extends AbstractHandlerExceptionResolver implements InitializingBean {
    private HttpMessageConverter messageConverter;

    public ResponseJsonExceptionResolver() {
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (messageConverter == null) {
            messageConverter = new MappingJackson2HttpMessageConverter(ObjectMapperFactory.getDefaultObjectMapper());
        }
    }

    public static String getErrorInfoFromException(Exception e) {
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            return "\r\n" + sw.toString() + "\r\n";
        } catch (Exception e2) {
            return "bad getErrorInfoFromException";
        }
    }

    @Override
    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        /**
         * web请求分页单独处理，应对antd框架
         */
        FailData failData = new FailData();
        failData.setMessage(ex.getMessage());
        if (ex instanceof IgnoredException) {
            IgnoredException realEx = (IgnoredException) ex;
            failData.setCode(realEx.getErrorCode());
            failData.setData(realEx.getData());
            log.error("execute {} failed with exception", request.getRequestURL(), ex);
        } else if (ex instanceof ConstraintViolationException) {
            failData.setCode(ErrorCode.WRONG_PARAMETER);
            failData.setMessage(getMessage((ConstraintViolationException) ex));
        } else if (ex instanceof ErrorCode) {
            failData.setCode(((ErrorCode) ex).getErrorCode());
        } else {
            log.error("execute {} failed with exception", request.getRequestURL(), ex);
        }
        try {
            response.setCharacterEncoding("utf-8");
            messageConverter.write(failData, MediaType.APPLICATION_JSON, new ServletServerHttpResponse(response));
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return new ModelAndView();
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    public static String getMessage(ConstraintViolationException e) {
        List<String> msgList = new ArrayList<>();
        for (ConstraintViolation<?> constraintViolation : e.getConstraintViolations()) {
            int i = 0;
            StringBuilder param = new StringBuilder();
            for (Iterator<Path.Node> iterator = constraintViolation.getPropertyPath().iterator(); iterator.hasNext(); i++) {
                Path.Node node = iterator.next();
                if (i == 0) {
                    continue;
                }
                if (!param.toString().isBlank()) {
                    param.append(".");
                }
                param.append(node.getName());
            }
            msgList.add("参数 [ " + param + " ] " + constraintViolation.getMessage());
        }
        return StringUtils.join(msgList.toArray(), ";");
    }

}
