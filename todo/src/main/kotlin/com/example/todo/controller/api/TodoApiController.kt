package com.example.todo.controller.api

import com.example.todo.model.http.TodoDto
import com.example.todo.service.TodoService
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.RequestEntity
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/todo")
class TodoApiController(
        val todoService: TodoService
) {

    // R
    @GetMapping(path = [""])
    fun read(@RequestParam(required = false) index:Int?): ResponseEntity<Any?> {

        return index?.let {

            todoService.read(it)

        }?.let {

            return ResponseEntity.ok(it)
        }
        ?: kotlin.run {

            return ResponseEntity
                    .status(HttpStatus.MOVED_PERMANENTLY)
                    .header(HttpHeaders.LOCATION, "/api/todo/all")
                    .build()
        }
    }

    // R
    @GetMapping(path = ["/all"])
    fun readAll(): MutableList<TodoDto> {
        return todoService.readAll()
    }

    // C
    @PostMapping(path = [""])
    fun create(@Valid @RequestBody todoDto: TodoDto): TodoDto? {
        return todoService.create(todoDto)
    }

    // U todo create = 201, update = 200 되게 변경해야함
    @PutMapping(path = [""])
    fun update(@Valid @RequestBody todoDto: TodoDto): TodoDto? {
        return todoService.update(todoDto)
    }


    // D
    @DeleteMapping(path = ["/{index}"])
    fun delete(@PathVariable(name = "index") _index:Int): ResponseEntity<Any> {

        if(!todoService.delete(_index)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
        return ResponseEntity.ok().build()
    }
}