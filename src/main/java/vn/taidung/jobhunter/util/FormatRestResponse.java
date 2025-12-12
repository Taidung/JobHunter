package vn.taidung.jobhunter.util;

import org.springframework.core.MethodParameter;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import vn.taidung.jobhunter.domain.response.RestResponse;
import vn.taidung.jobhunter.util.annotition.ApiMessage;

@RestControllerAdvice
public class FormatRestResponse implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response) {

        int status = -1;
        if (response instanceof ServletServerHttpResponse servletResponse) {
            status = servletResponse.getServletResponse().getStatus();
        }

        if (body instanceof String || body instanceof Resource) {
            return body;
        }

        String path = request.getURI().getPath();
        if (path.startsWith("/v3/api-docs") || path.startsWith("/swagger-ui")) {
            return body;
        }

        var restResponse = new RestResponse<Object>();
        restResponse.setStatusCode(status);
        if (status >= 400) {
            return body;
        } else {
            // success
            ApiMessage message = returnType.getMethodAnnotation(ApiMessage.class);
            restResponse.setMessage(message != null ? message.value() : "CALL API SUCCESS");
            restResponse.setData(body);
        }
        return restResponse;
    }

}
