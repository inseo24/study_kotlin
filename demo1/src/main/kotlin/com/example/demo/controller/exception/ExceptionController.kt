package com.example.demo.controller.exception

import com.example.demo.model.http.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.validation.ConstraintViolationException
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size
import com.example.demo.model.http.Error
import com.example.demo.model.http.UserRequest
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import java.lang.reflect.Method
import java.time.LocalDateTime
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid

@RestController
@RequestMapping("/api/exception")
@Validated
class ExceptionController {

    @GetMapping("/hello")
    fun hello(): String {
        val list = mutableListOf<String>()
        return "hello"
    }

    @GetMapping("")
    fun get(
            @NotBlank
            @Size(min =2 , max = 6)
            @RequestParam name:String,

            @Min(10)
            @RequestParam age:Int
    ): String {
        println(name)
        println(age)
        return name + " " + age
    }

    @PostMapping("")
    fun post(@Valid @RequestBody userRequest: UserRequest): UserRequest {
        println(userRequest)
        return userRequest
    }

    @ExceptionHandler(value = [MethodArgumentNotValidException::class])
    fun methodArgumentNotValidException(e: MethodArgumentNotValidException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {

        val errors = mutableListOf<Error>()

        e.bindingResult.allErrors.forEach { errorObject ->
            var error = Error().apply {

                this.field = (errorObject as FieldError).field
                this.message = errorObject.defaultMessage
                this.value = errorObject.rejectedValue
            }
            errors.add(error)
        }

        // 2. ErrorResponse
        val errorResponse = ErrorResponse().apply {
            this.resultCode = "FAIL"
            this.httpStatus = HttpStatus.BAD_REQUEST.value().toString()
            this.httpMethod = request.method
            this.message = "요청에 에러가 발생했습니다."
            this.path = request.requestURI.toString()
            this.timestamp = LocalDateTime.now()
            this.errors = errors
        }
        // 3. ReseponseEntity
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)

    }

    @ExceptionHandler(value = [ConstraintViolationException::class])
    fun constraintViolationException(e : ConstraintViolationException, request: HttpServletRequest) : ResponseEntity<ErrorResponse>{
        // 1. 에러 분석
        val errors = mutableListOf<Error>()

        e.constraintViolations.forEach{
            val field = it.propertyPath.last().name
            val message = it.message

            val error = Error().apply {
                this.field = field
                this.message = message
                this.value = it.invalidValue
            }

            errors.add(error)


        }

        // 2. ErrorResponse
        val errorResponse = ErrorResponse().apply {
            this.resultCode = "FAIL"
            this.httpStatus = HttpStatus.BAD_REQUEST.value().toString()
            this.httpMethod = request.method
            this.message = "요청에 에러가 발생했습니다."
            this.path = request.requestURI.toString()
            this.timestamp = LocalDateTime.now()
            this.errors = errors
        }
        // 3. ReseponseEntity
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(value = [IndexOutOfBoundsException::class])
    fun indexOutOfBoundsException(e: IndexOutOfBoundsException): ResponseEntity<String> {
        println("controller exception handler")
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("index error")
    }
}