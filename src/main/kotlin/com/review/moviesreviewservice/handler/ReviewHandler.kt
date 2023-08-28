package com.review.moviesreviewservice.handler

import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

interface ReviewHandler {

    fun addReview(request: ServerRequest): Mono<ServerResponse>
    fun getReviews(request:ServerRequest): Mono<ServerResponse>

    fun updateReview(request: ServerRequest): Mono<ServerResponse>
    fun deleteReview(request: ServerRequest):Mono<ServerResponse>
    fun getReviewsStream(request: ServerRequest):Mono<ServerResponse>
}