package com.review.moviesreviewservice.dto

import com.review.moviesreviewservice.domain.Review
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import org.springframework.data.annotation.Id

data class GetMovieReviewDto(
    val reviewId: String,
    val movieInfoId:Long,
    val comment:String,
    val rating: Double
) {
    companion object{
        fun of(review: Review): GetMovieReviewDto {
            return GetMovieReviewDto(
                review.reviewId,
                review.movieInfoId,
                review.comment,
                review.rating,
            )
        }
    }
}