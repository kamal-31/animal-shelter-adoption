package com.animalshelter

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AnimalShelterApplication

fun main(args: Array<String>) {
    runApplication<AnimalShelterApplication>(*args)
}