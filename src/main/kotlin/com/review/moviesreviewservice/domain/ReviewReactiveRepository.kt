package com.review.moviesreviewservice.domain

import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface ReviewReactiveRepository:ReactiveMongoRepository<Review,String> {

    fun findReviewsByMovieInfoId(movieId:Long): Flux<Review>

}