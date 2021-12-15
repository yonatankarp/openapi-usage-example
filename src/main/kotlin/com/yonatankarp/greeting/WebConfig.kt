package com.yonatankarp.greeting

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.web.bind.annotation.RestController


/** Web configuration.  */
@Configuration
@ComponentScan(
    basePackageClasses = [GreetingApplication::class],
    includeFilters = [ComponentScan.Filter(RestController::class)]
)
class WebConfig
