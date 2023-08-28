package com.review.moviesreviewservice.unit

import com.review.moviesreviewservice.domain.Review
import com.review.moviesreviewservice.domain.ReviewReactiveRepository
import com.review.moviesreviewservice.dto.AddMovieReviewDto
import com.review.moviesreviewservice.dto.GetMovieReviewDto
import com.review.moviesreviewservice.exceptionhandler.GlobalErrorHandler
import com.review.moviesreviewservice.handler.ReviewHandler
import com.review.moviesreviewservice.router.ReviewRouter
import org.junit.jupiter.api.Test
import org.mockito.kotlin.isA
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono


@WebFluxTest
@ContextConfiguration(classes = [ReviewRouter::class,ReviewHandler::class,GlobalErrorHandler::class])
@AutoConfigureWebTestClient
class ReviewUnitTest {

    @Autowired
    private val reviewReactiveRepository:ReviewReactiveRepository = mock<ReviewReactiveRepository>()

    @Autowired
    private lateinit var webTestClient: WebTestClient

    companion object{
        val REVIEW_URL = "/v1/reviews"
    }

    @Test
    fun addReview(){
        //given
        val addMovieReviewDto: AddMovieReviewDto = AddMovieReviewDto("a", 1L, "좋은 영화입니다.", 9.0)

        // Mocking Stub
        whenever(reviewReactiveRepository.save(isA()))
            .thenReturn(Mono.just(Review("a", 1L, "좋은 영화입니다.", 9.0)))


        // when
        webTestClient
            .post()
            .uri(REVIEW_URL)
            .bodyValue(addMovieReviewDto)
            .exchange()
            .expectStatus()
            .isCreated
            .expectBody(GetMovieReviewDto::class.java)
            .consumeWith {
                savedReviewResult
                -> val responseBody: GetMovieReviewDto? = savedReviewResult.responseBody
                assert(responseBody!=null)
            }
    }



}