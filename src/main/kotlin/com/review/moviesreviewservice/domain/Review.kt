package com.review.moviesreviewservice.domain

import com.review.moviesreviewservice.dto.AddMovieReviewDto
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document


@Document
data class Review(
    @Id
    val reviewId: String,
    @NotNull(message = "평가하고자 하는 영화의 아이디는 null이 아니어야 합니다.")
    val movieInfoId:Long,
    val comment:String,
    @Min(value = 0L, message="평가 점수는 음수가 아니어야 합니다.")
    val rating: Double
) {

    companion object{
        fun of(addMovieReviewDto: AddMovieReviewDto): Review{
            return Review(
                addMovieReviewDto.reviewId,
                addMovieReviewDto.movieInfoId,
                addMovieReviewDto.comment,
                addMovieReviewDto.rating,
            )
        }
    }
}