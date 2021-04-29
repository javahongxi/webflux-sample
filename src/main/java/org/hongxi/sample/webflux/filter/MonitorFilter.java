package org.hongxi.sample.webflux.filter;

import lombok.extern.slf4j.Slf4j;
import org.hongxi.sample.webflux.support.WebUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * Created by shenhongxi on 2021/4/22.
 */
@Slf4j
@Order(-3)
@Component
public class MonitorFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        preHandle(exchange);
        return chain.filter(exchange)
                .doOnSuccess(signal -> postHandle(exchange, null))
                .doOnError(cause -> postHandle(exchange, cause));
    }

    private void preHandle(ServerWebExchange exchange) {
        log.info("preHandle");
        exchange.getAttributes().put(WebUtils.START_TIMESTAMP_ATTR, System.currentTimeMillis());
//        throw new RuntimeException("test exception");
    }

    private void postHandle(ServerWebExchange exchange, Throwable throwable) {
        log.info("postHandle");
        Long start = exchange.getAttribute(WebUtils.START_TIMESTAMP_ATTR);
        if (start != null) {
            long cost = System.currentTimeMillis() - start;
            log.info("uri: {}, cost: {}, error: {}",
                    exchange.getRequest().getPath(), cost, throwable != null);
        }
    }
}
