package com.review.moviesreviewservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
class MoviesReviewServiceApplication

fun main(args: Array<String>) {
	runApplication<MoviesReviewServiceApplication>(*args)
}
