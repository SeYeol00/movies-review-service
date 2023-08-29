package com.review.moviesreviewservice.unit

import com.review.moviesreviewservice.domain.Review
import com.review.moviesreviewservice.domain.ReviewReactiveRepository
import com.review.moviesreviewservice.dto.AddMovieReviewDto
import com.review.moviesreviewservice.dto.GetMovieReviewDto
import com.review.moviesreviewservice.dto.UpdateMovieReviewDto
import com.review.moviesreviewservice.exceptionhandler.GlobalErrorHandler
import com.review.moviesreviewservice.handler.ReviewHandler
import com.review.moviesreviewservice.router.ReviewRouter
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.kotlin.isA
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


@WebFluxTest
@ContextConfiguration(classes = [ReviewRouter::class,ReviewHandler::class,GlobalErrorHandler::class])
@AutoConfigureWebTestClient
class ReviewUnitTest {

    // 레포지토리 모킹
    @MockBean
    private lateinit var reviewReactiveRepository: ReviewReactiveRepository
//    private val reviewReactiveRepository:ReviewReactiveRepository = mock<ReviewReactiveRepository>()

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


    @Test
    fun addReview_rating_validation(){
        //given
        val addMovieReviewDto: AddMovieReviewDto = AddMovieReviewDto("a", 1L, "좋은 영화입니다.", -9.0)

        // mocking and stub
        whenever(reviewReactiveRepository.save(isA()))
            .thenReturn(Mono.just(
                Review.of(addMovieReviewDto)
            ))

        //when
        webTestClient
            .post()
            .uri(REVIEW_URL)
            .bodyValue(addMovieReviewDto)
            .exchange()
            .expectStatus()
            .isBadRequest
            .expectBody(String::class.java)
            .isEqualTo("평가 점수는 음수가 아니어야 합니다.")
    }

    @Test
    fun getAllReviews(){
        //given
        val reviewList = listOf(
            Review("a", 1L, "좋은 영화입니다.", 9.0),
            Review("b", 1L, "감명 깊었어요", 9.0),
            Review("c", 2L, "평범한 편이에요", 8.0)
        )
        //mocking and stubbing
        whenever(reviewReactiveRepository.findAll())
            .thenReturn(Flux.fromIterable(reviewList))

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
    fun updateReview(){
        //given
        val updateMovieReviewDto: UpdateMovieReviewDto = UpdateMovieReviewDto("별로에요 보지 마세요", 1.0)
        val targetId: String = "a"

        //mocking and stubbing
        whenever(
            reviewReactiveRepository.findByReviewId(isA())
        )
            .thenReturn(
                Mono.just(
                    Review("a", 1L, "좋은 영화입니다.", 9.0)
                )
            )

        whenever(
            reviewReactiveRepository.save(isA())
        )
            .thenReturn(
                Mono.just
                    (Review("a", 1L, "별로에요 보지 마세요", 1.0)
                )
            )

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
                assert(responseBody != null)
                assert(responseBody?.comment == "별로에요 보지 마세요")
                assert(responseBody?.rating == 1.0)
            }
    }


    @Test
    fun deleteReview(){
        //given
        val reviewId:String = "a"

        //mocking and stubbing
        whenever(reviewReactiveRepository.findByReviewId(isA()))
            .thenReturn(Mono.just(
                Review("a", 1L, "좋은 영화입니다.", 9.0)
                )
            )

        whenever(reviewReactiveRepository.deleteByReviewId(isA()))
            .thenReturn(
                Mono.empty()
            )
        //when
        webTestClient
            .delete()
            .uri("$REVIEW_URL/$reviewId")
            .exchange()
            .expectStatus()
            .isNoContent
            .expectBody(GetMovieReviewDto::class.java)
            .consumeWith {
                reviewResult
                -> val responseBody: GetMovieReviewDto? = reviewResult.responseBody
                assertNull(responseBody)
            }
    }

}