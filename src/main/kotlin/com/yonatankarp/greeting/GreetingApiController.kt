package com.yonatankarp.greeting

import com.yonatankarp.openapi.GreetingApi
import com.yonatankarp.openapi.models.GreetResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class GreetingApiController : GreetingApi {
    override fun greetName(
        @RequestParam(value = "name", required = false) name: String?,
    ): ResponseEntity<GreetResponse> =
        if (name.isNullOrBlank()) {
            ResponseEntity.ok(GreetResponse("Hello, world!"))
        } else {
            ResponseEntity.ok(GreetResponse("Hello, $name!"))
        }
}
