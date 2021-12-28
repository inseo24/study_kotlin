package com.example.demo.controller.put

import com.example.demo.model.http.Result
import com.example.demo.model.http.UserRequest
import com.example.demo.model.http.UserResponse
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api")
class PutApiController {

    @PutMapping("/put-mapping")
    fun putMapping(): String{
        return "put-mapping"
    }

    @RequestMapping(method = [RequestMethod.PUT], path = ["/request-mapping"])
    fun requestMapping(): String{
        return "request-mapping"
    }

    @PutMapping(path = ["/put-mapping/object"])
    fun putMappingObject(@Valid @RequestBody userRequest: UserRequest, bindingResult: BindingResult): ResponseEntity<String>{

        if(bindingResult.hasErrors()){
            // 500 error
            val msg = StringBuilder()

            bindingResult.allErrors.forEach{
                val field = it as FieldError
                val message = it.defaultMessage
                msg.append(field.field+ " : " + message + "\n")
            }
            return ResponseEntity.badRequest().body(msg.toString())
        }
        return ResponseEntity.ok("OK")

//        // 0. Response
//        return UserResponse().apply {
//
//            // 1. result
//            this.result = Result().apply {
//                this.resultCode = "OK"
//                this.resultMessage = "성공"
//            }
//
//        }.apply {
//                // 2. description
//                this.description = "~~~~~~~~~~~~~~"
//        }.apply {
//            // 3. user mutable list
//            val userList = mutableListOf<UserRequest>()
//
//            userList.add(userRequest)
//            userList.add(UserRequest().apply {
//                this.name = "a"
//                this.age = 10
//                this.email = "a@naver.com"
//                this.address = "a address"
//                this.phoneNumber = "010-1234"
//            })
//
//            userList.add(UserRequest().apply {
//                this.name = "b"
//                this.age = 10
//                this.email = "b@naver.com"
//                this.address = "b address"
//                this.phoneNumber = "010-1234"
//            })
//
//            this.userRequest = userList
//        }
    }
}