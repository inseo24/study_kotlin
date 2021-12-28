package com.example.demo.controller.response

import com.example.demo.model.http.UserRequest
import com.example.demo.model.http.UserResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/response")
class ResponseApiController {

    // 1. get 4xx

    @GetMapping
    fun getMapping(@RequestParam age : Int?): ResponseEntity<String> {

        age?.let {
            if(age < 20){
                return ResponseEntity.status(400).body("age는 20보다 커야함")
            }

            ResponseEntity.ok("OK")
        }?: kotlin.run {
            // age is null
            return ResponseEntity.status(400).body("age값이 누락되었습니다.")
        }

        println(age)
        return ResponseEntity.ok("OK")
    }

    // 2. post 200

    @PostMapping
    fun postMapping(@RequestBody userRequest: UserRequest?): ResponseEntity<Any>{
        return ResponseEntity.status(200).body(userRequest) // object mapper -> object -> json
    }

    // 3. put 201

    @PutMapping
    fun putMapping(@RequestBody userRequest: UserRequest?): ResponseEntity<UserRequest>{
        // 1. 기존 데이터가 없어서 새로 생성
        return ResponseEntity.status(HttpStatus.CREATED).body(userRequest)
    }

    // 4. delete 500

    @DeleteMapping("/{id}")
    fun deleteMapping(@PathVariable id:Int): ResponseEntity<Any>{
        return ResponseEntity.status(500).body(null)
    }

}