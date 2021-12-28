package com.example.todo.database

import com.example.todo.model.http.TodoDto
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class Todo (
        var index:Int?=null,
        var title:String?=null,
        var description:String?=null,
        var schedule:LocalDateTime?=null,
        var createdAt: LocalDateTime?=null,
        var updatedAt: LocalDateTime?=null
)

// 이 방법 외에, 1) 모델 맵퍼를 사용하는 방법도 있고 2) 코틀린 리플렉션 사용하는 방법도 있음
// 아래 방법이 가장 원시적인(?) 방법
fun Todo.convertTodo(todoDto: TodoDto): Todo{
    return Todo().apply {
        this.index = todoDto.index
        this.title = todoDto.title
        this.description = todoDto.description
        this.schedule = LocalDateTime.parse(todoDto.schedule, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        this.createdAt = todoDto.createdAt
        this.updatedAt = todoDto.updatedAt
    }
}