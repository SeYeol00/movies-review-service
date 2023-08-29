package com.review.moviesreviewservice.dto


import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Field

data class AddMovieReviewDto(
    @Id
    val reviewId: String,

    /**
     * 코틀린에서 유효성 검증 어노테이션을 실행하려면
     * 아래와 같이 @field:를 붙여야 된다!!!!!!!!!
     * **/
    @field:NotNull(message = "평가하고자 하는 영화의 아이디는 null이 아니어야 합니다.")
    val movieInfoId:Long,
    val comment:String,

    @field:Min(value = 0L, message="평가 점수는 음수가 아니어야 합니다.")
    val rating: Double
) {

}