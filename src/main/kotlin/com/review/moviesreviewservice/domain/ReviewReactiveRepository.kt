package com.review.moviesreviewservice.domain

import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux


@Component
interface ReviewReactiveRepository:ReactiveMongoRepository<Review,String> {

    fun findReviewsByMovieInfoId(movieId:Long): Flux<Review>

}