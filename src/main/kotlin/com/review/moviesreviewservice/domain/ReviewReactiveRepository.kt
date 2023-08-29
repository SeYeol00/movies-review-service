package com.review.moviesreviewservice.domain

import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


@Repository
interface ReviewReactiveRepository:ReactiveMongoRepository<Review,String> {

    fun findReviewsByMovieInfoId(movieId:Long): Flux<Review>

    fun findByReviewId(reviewId:String): Mono<Review>

    fun deleteByReviewId(reviewId: String):Mono<Void>

}