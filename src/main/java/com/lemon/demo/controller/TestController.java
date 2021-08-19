package com.lemon.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author lemon
 * @Date 2021/8/19
 */
@RestController
@RequestMapping("/v1")
public class TestController {

    @GetMapping("/test")
    public String test() {
        return "ok";
    }
}
