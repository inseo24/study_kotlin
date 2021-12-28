package com.example.demo.controller.exception

import com.example.demo.model.http.UserRequest
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MockMvcResultMatchersDsl
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.util.LinkedMultiValueMap

@WebMvcTest
@AutoConfigureMockMvc
class ExceptionControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun helloTest() {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/exception/hello")
        ).andExpect(
            MockMvcResultMatchers.status().isOk
        ).andExpect(
                MockMvcResultMatchers.content().string("hello")
        ).andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun getTest() {
        val queryParams = LinkedMultiValueMap<String, String>()
        queryParams.add("name", "seoin")
        queryParams.add("age", "20")

        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/exception").queryParams(queryParams)
        ).andExpect(
            MockMvcResultMatchers.status().isOk
        ).andExpect(
            MockMvcResultMatchers.content().string("seoin 20")
        ).andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun getFailTest() {
        val queryParams = LinkedMultiValueMap<String, String>()
        queryParams.add("name", "seoin")
        queryParams.add("age", "8")

        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/exception").queryParams(queryParams)
        ).andExpect(
                MockMvcResultMatchers.status().isBadRequest
        ).andExpect(
                MockMvcResultMatchers.jsonPath("\$.result_code").value("FAIL")
        ).andExpect(
                MockMvcResultMatchers.content().contentType("application/json")
        ).andExpect(
                MockMvcResultMatchers.jsonPath("\$.errors[0].field").value("age")
        ).andExpect(
                MockMvcResultMatchers.jsonPath("\$.errors[0].value").value("8")
        ).andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun postTest() {

        val userRequest = UserRequest().apply {
            this.name = "seoin"
            this.age = 10
            this.phoneNumber = "010-1234-3421"
            this.address = "seoul"
            this.email = "jnh@naver.com"
            this.createdAt = "2021-12-13 12:23:22"
        }

        val json = jacksonObjectMapper().writeValueAsString(userRequest)
        println(json)

        mockMvc.perform(
                post("/api/exception")
                        .content(json)
                        .contentType("application/json")
                        .accept("application/json")
        ).andExpect(
                MockMvcResultMatchers.status().isOk
        ).andExpect(
                MockMvcResultMatchers.jsonPath("\$.name").value("seoin")
        ).andExpect(
                MockMvcResultMatchers.jsonPath("\$.age").value("10")
        ).andExpect(
                MockMvcResultMatchers.jsonPath("\$.email").value("jnh@naver.com")
        ).andExpect(
                MockMvcResultMatchers.jsonPath("\$.phoneNumber").value("010-1234-3421")
        ).andExpect(
                MockMvcResultMatchers.jsonPath("\$.address").value("seoul")
        ).andExpect(
                MockMvcResultMatchers.jsonPath("\$.createdAt").value("2021-12-13 12:23:22")
        ).andDo(
                MockMvcResultHandlers.print()
        )
    }

    @Test
    fun postFailTest() {

        val userRequest = UserRequest().apply {
            this.name = "seoin"
            this.age = -3
            this.phoneNumber = "010-1234-3421"
            this.address = "seoul"
            this.email = "jnh@naver.com"
            this.createdAt = "2021-12-13 12:23:22"
        }

        val json = jacksonObjectMapper().writeValueAsString(userRequest)
        println(json)

        mockMvc.perform(
                post("/api/exception")
                        .content(json)
                        .contentType("application/json")
                        .accept("application/json")
        ).andExpect(
                MockMvcResultMatchers.status().isBadRequest
        ).andDo(
                MockMvcResultHandlers.print()
        )
    }
}