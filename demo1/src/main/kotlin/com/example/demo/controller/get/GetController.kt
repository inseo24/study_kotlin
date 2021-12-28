package com.example.demo.controller.get

import com.example.demo.model.http.UserRequest
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class GetController {

    @GetMapping("/hello")
    fun hello(): String {
        return "hello Kotlin!"
    }

    @RequestMapping(method = [RequestMethod.GET], path = ["reqeust-mapping"])
    fun requestMapping(): String {
        return "request-mapping"
    }

    @GetMapping("/get-mapping/path-variable/{name}/{age}")
    fun pathVariable(@PathVariable name: String, @PathVariable age:Int): String {
        println("${name} , ${age}")
        return name + " " + age
    }


    @GetMapping("/get-mapping/path-variable2/{name}/{age}")
    fun pathVariable2(@PathVariable(value = "name") _name: String, @PathVariable(name = "age") age:Int): String {
        println("${_name} , ${age}")
        return _name + " " + age
    }

    // 쿼리 파라미터
    // ?key=value
    // http://localhost:8080/api/get-mapping/quest-param?name=seoin&age=26
    @GetMapping("/get-mapping/quest-param")
    fun queryParam(
            @RequestParam name : String,
            @RequestParam(value = "age") age: Int
    ): String{
        println("${name}, ${age}")
        return name + " " + age
    }

    // 객체로 맵핑, 파라미터가 많을 경우
    // ex) name, age, address, email
    // restcontroller로 선언하면 오브젝트 타입을 리턴시  JSON 형태로 리턴됨
    @GetMapping("/get-mapping/quest-param/object")
    fun queryParamObject(userRequest: UserRequest): UserRequest {
        println(userRequest)
        return userRequest
    }

    @GetMapping("/get-mapping/query-param/map")
    fun queryParamMap(@RequestParam map: Map<String, Any>): Map<String, Any> {
        println(map)
        val phoneNumber = map.get("phone-number")
        println(phoneNumber)
        return map
    }

}