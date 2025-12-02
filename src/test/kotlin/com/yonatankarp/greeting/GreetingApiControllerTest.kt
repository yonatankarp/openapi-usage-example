package com.yonatankarp.greeting

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@AutoConfigureMockMvc
class GreetingApiControllerTest(
    context: WebApplicationContext,
) {
    private val mockMvc =
        MockMvcBuilders
            .webAppContextSetup(context)
            .build()

    @ParameterizedTest(name = """should return {1} for name "{0}"""")
    @MethodSource("getTestCase")
    fun `should return correct greeting`(
        name: String?,
        expectedResult: String,
    ) {
        // Given uri and a request
        val uri =
            if (name.isNullOrBlank()) {
                "/greet"
            } else {
                "/greet?name=$name"
            }

        val request =
            MockMvcRequestBuilders
                .get(uri)
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
        val actualGreeting =
            ObjectMapper()
                .readTree(response)["greet"]
                .asText()
        assertEquals(expectedResult, actualGreeting)
    }

    companion object {
        @JvmStatic
        private fun getTestCase() =
            arrayOf(
                Arguments.of("test", "Hello, test!"),
                Arguments.of(null, "Hello, world!"),
                Arguments.of("💩", "Hello, 💩!"),
            )
    }
}
