package org.hongxi.sample.webflux.controller;

import lombok.extern.slf4j.Slf4j;
import org.hongxi.sample.webflux.dao.OrderRepository;
import org.hongxi.sample.webflux.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * Created by shenhongxi on 2021/4/22.
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @PostMapping("/create")
    public Mono<Order> create(@RequestBody Order order) {
        return orderRepository.save(order);
    }

    @GetMapping("/{id}")
    public Mono<Order> findById(@PathVariable String id) {
        log.info("id: {}", id);
        Assert.isTrue(id.length() >= 6, "id length < 6");

        return orderRepository.findById(id);
    }

    @PostMapping("/delete")
    public Mono<Void> deleteById(@RequestParam String id) {
        return orderRepository.deleteById(id);
    }
}
