package com.weweibuy.gateway.core.mode.event.exception;

import com.weweibuy.gateway.common.exception.BusinessException;
import com.weweibuy.gateway.common.exception.SystemException;
import com.weweibuy.gateway.common.model.dto.CommonCodeJsonResponse;
import com.weweibuy.gateway.core.mode.event.utils.MediaTypeUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;

/**
 * @author durenhao
 * @date 2020/2/23 19:15
 **/
public class DefaultExceptionMatchHandler implements ExceptionMatchHandler {


    @Override
    public boolean match(ServerWebExchange exchange, Throwable ex) {
        return true;
    }

    @Override
    public Mono<ServerResponse> handler(ServerWebExchange exchange, Throwable ex) {
        if (MediaTypeUtils.acceptsHtml(exchange)) {
            return htmlErrorResponse(ex);
        }
        return toServerResponse(ex);
    }


    private Mono<ServerResponse> htmlErrorResponse(Throwable ex) {
        return ServerResponse.status(HttpStatus.TOO_MANY_REQUESTS)
                .contentType(MediaType.TEXT_PLAIN)
                .syncBody("服务暂时不可用: " + ex.getClass().getSimpleName());
    }


    public Mono<ServerResponse> toServerResponse(Throwable t) {
        if (null == t) {
            return toServerResponse(HttpStatus.INTERNAL_SERVER_ERROR, CommonCodeJsonResponse.unknownException());
        }

        int counter = 0;
        Throwable cause = t;
        while (cause != null && counter++ < 50) {
            if ((cause instanceof BusinessException)) {

                return toServerResponse(HttpStatus.BAD_GATEWAY,
                        CommonCodeJsonResponse.response(((BusinessException) cause).getCodeAndMsg()));
            }
            if ((cause instanceof SystemException)) {
                return toServerResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                        CommonCodeJsonResponse.response(((SystemException) cause).getCodeAndMsg()));
            }
            cause = cause.getCause();
        }
        return toServerResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                CommonCodeJsonResponse.unknownException());
    }

    private Mono<ServerResponse> toServerResponse(HttpStatus httpStatus, Object body) {
        return ServerResponse.status(httpStatus)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(fromObject(body));
    }


}