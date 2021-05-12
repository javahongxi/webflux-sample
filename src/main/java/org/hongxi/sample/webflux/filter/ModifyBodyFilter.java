package org.hongxi.sample.webflux.filter;

import lombok.extern.slf4j.Slf4j;
import org.hongxi.sample.webflux.support.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Created by shenhongxi on 2021/4/29.
 */
@Slf4j
@Order(-1)
@Component
public class ModifyBodyFilter implements WebFilter {

    @Autowired
    private ServerCodecConfigurer codecConfigurer;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        if (WebUtils.shouldNotFilter(exchange)) {
            return chain.filter(exchange);
        }
        return ParamUtils.from(exchange)
                .map(params -> decorate(exchange, params))
                .flatMap(chain::filter);
    }

    private ServerWebExchange decorate(ServerWebExchange exchange, Map<String, Object> params) {
        if (params.isEmpty()) {
            return exchange;
        }

        ServerHttpResponse serverHttpResponse = new ModifiedServerHttpResponse(exchange, codecConfigurer.getReaders());

        MediaType contentType = exchange.getRequest().getHeaders().getContentType();
        if (contentType != null && contentType.isCompatibleWith(MediaType.APPLICATION_JSON)) {
            Map<String, Object> decrypted = Crypto.decrypt(params);
            exchange.getAttributes().put(WebUtils.REQUEST_PARAMS_ATTR, decrypted);
            byte[] rawBody = JacksonUtils.serialize(decrypted);
            ServerHttpRequest serverHttpRequest = new ModifiedServerHttpRequest(exchange.getRequest(), rawBody);
            return exchange.mutate().request(serverHttpRequest).response(serverHttpResponse).build();
        }

        ServerWebExchange serverWebExchange = new ModifiedServerWebExchange(exchange);
        return serverWebExchange.mutate().response(serverHttpResponse).build();
    }
}
