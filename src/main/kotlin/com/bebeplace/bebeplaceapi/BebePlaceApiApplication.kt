package com.bebeplace.bebeplaceapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.retry.annotation.EnableRetry

@SpringBootApplication
@EnableRetry
class BebePlaceApiApplication

fun main(args: Array<String>) {
    runApplication<BebePlaceApiApplication>(*args)
}
