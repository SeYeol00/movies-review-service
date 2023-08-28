package com.review.moviesreviewservice.intg

import com.review.moviesreviewservice.domain.Review
import com.review.moviesreviewservice.domain.ReviewReactiveRepository
import com.review.moviesreviewservice.dto.AddMovieReviewDto
import com.review.moviesreviewservice.dto.GetMovieReviewDto
import com.review.moviesreviewservice.dto.UpdateMovieReviewDto
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
class ReviewsIntgTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Autowired
    private lateinit var reviewReactiveRepository: ReviewReactiveRepository

    companion object{
        val REVIEW_URL = "/v1/reviews"
    }

    @BeforeEach
    fun init() {
        val reviewList = listOf(
            Review("a", 1L, "좋은 영화입니다.", 9.0),
            Review("b", 1L, "감명 깊었어요", 9.0),
            Review("c", 2L, "평범한 편이에요", 8.0)
        )
        reviewReactiveRepository.saveAll(reviewList)
            .blockLast()
    }
    @AfterEach
    fun finished() {
        reviewReactiveRepository.deleteAll().block()
    }

    @Test
    fun addReview(){
        //given
        val review: AddMovieReviewDto = AddMovieReviewDto("a", 1L, "좋은 영화입니다.", 9.0)
        //when
        webTestClient
            .post()
            .uri(REVIEW_URL)
            .bodyValue(review)
            .exchange()
            .expectStatus()
            .is2xxSuccessful
            .expectBody(GetMovieReviewDto::class.java)
            .consumeWith {
                savedReviewResult
                -> val responseBody: GetMovieReviewDto? = savedReviewResult.responseBody
                assert(responseBody?.rating == 9.0)
            }

    }

    @Test
    fun getAllReviews(){
        //given
        //when
        webTestClient
            .get()
            .uri(REVIEW_URL)
            .exchange()
            .expectStatus()
            .is2xxSuccessful
            .expectBodyList(GetMovieReviewDto::class.java)
            .hasSize(3)
    }


    @Test
    fun getReviewsByMovieInfoId(){

        //URI Builder
        val toUri: URI = UriComponentsBuilder.fromUriString(REVIEW_URL)
            .queryParam("movieInfoId", 1L)
            .buildAndExpand().toUri()

        //when
        webTestClient
            .get()
            .uri(toUri)
            .exchange()
            .expectStatus()
            .is2xxSuccessful
            .expectBodyList(
                GetMovieReviewDto::class.java
            )
            .hasSize(2)
    }
    @Test
    fun updateReview(){
        //given
        val updateMovieReviewDto: UpdateMovieReviewDto = UpdateMovieReviewDto( "평범한 편이에요", 8.0)
        val targetId:String = "a"
        //when
        webTestClient
            .put()
            .uri("$REVIEW_URL/$targetId")
            .bodyValue(updateMovieReviewDto)
            .exchange()
            .expectStatus()
            .is2xxSuccessful
            .expectBody(GetMovieReviewDto::class.java)
            .consumeWith {
                savedReviewResult
                -> val responseBody: GetMovieReviewDto? = savedReviewResult.responseBody
                assertEquals("평범한 편이에요", responseBody?.comment)
                assertEquals(8.0,responseBody?.rating)
            }
    }


    @Test
    fun deleteReview(){
        //given
        val reviewId:String = "a"
        //when
        webTestClient
            .delete()
            .uri("$REVIEW_URL/$reviewId")
            .exchange()
            .expectStatus()
            .isNoContent
            .expectBody(GetMovieReviewDto::class.java)
            .consumeWith {
                reviewEntityExchangeResult
                -> val responseBody: GetMovieReviewDto? = reviewEntityExchangeResult.responseBody
                assertNull(responseBody)
            }
    }
}