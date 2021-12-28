package com.example.demo.controller.page

import com.example.demo.model.http.UserRequest
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class PageController {

    @GetMapping("/main")
    fun main(): String {
        println("init main")
        return "main.html"
    }

    @ResponseBody // @Controller 에서 데이터를 전달하고 싶을 때(JSON...) 사용
    @GetMapping("/test")
    fun response(): UserRequest{
        return UserRequest().apply {
            this.name = "seoin"
        }
    }
}