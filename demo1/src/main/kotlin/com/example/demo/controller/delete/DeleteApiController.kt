package com.example.demo.controller.delete

import org.jetbrains.annotations.NotNull
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.validation.constraints.Min
import javax.validation.constraints.Size

@RestController
@RequestMapping("/api")
@Validated
class DeleteApiController {

    // 2. request param
    @DeleteMapping(path = ["delete-mapping"])
    fun deleteMapping(

            @NotNull
            @Size(min = 2, max = 5)
            @RequestParam(value = "name") _name : String,

            @NotNull
            @Min(value = 20, message = "age는 20보다 커야 함")
            @RequestParam(name = "age") _age : Int

    ): String{
        println(_name)
        println(_age)
        return _name + " " + _age
    }

    // 1. path variable
    @DeleteMapping(path = ["/delete-mapping/name/{name}/age/{age}"])
    fun deleteMappingPath(
            @NotNull
            @Size(min = 2, max = 5, message = "이름 길이 맞춰주세요")
            @PathVariable(name = "name") _name: String,

            @NotNull
            @Min(value = 20, message = "age는 20보다 커야 함")
            @PathVariable(name = "age") _age: Int): String {
        println(_name)
        println(_age)
        return _name + " " + _age

    }

}