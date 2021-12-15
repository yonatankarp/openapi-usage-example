package com.yonatankarp.greeting

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [WebConfig::class])
@AutoConfigureMockMvc
@WebAppConfiguration
class GreetingApiControllerTest(context: WebApplicationContext) {
    private val mockMvc = MockMvcBuilders
        .webAppContextSetup(context).build()

    data class TestCase(val name: String?, val expectedResult: String)

    private fun getTestCase(): List<Arguments> =
        arrayOf(
            TestCase(name = "test", expectedResult = "Hello, test!"),
            TestCase(name = null, expectedResult = "Hello, world!"),
            TestCase(name = "💩", expectedResult = "Hello, 💩!"),
        ).map { Arguments.of(it) }

    @ParameterizedTest
    @MethodSource("getTestCase")
    fun `should return correct greeting`(testCase: TestCase) {
        // Given uri and a request
        val uri = if (testCase.name.isNullOrBlank()) "/v1/greet"
        else "/v1/greet?name=${testCase.name}"

        val request = MockMvcRequestBuilders.get(uri)
            .accept(MediaType.APPLICATION_JSON)

        // When we call the api
        val response =
            mockMvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andReturn()
                .response
                .contentAsString

        // Then we expect to have a response with the correct greeting
        val actualGreeting = ObjectMapper()
            .readTree(response)["greet"]
            .asText()
        assertEquals(testCase.expectedResult, actualGreeting)
    }
}
