package com.review.moviesreviewservice.handler

import com.review.moviesreviewservice.domain.Review
import com.review.moviesreviewservice.domain.ReviewReactiveRepository
import com.review.moviesreviewservice.dto.AddMovieReviewDto
import com.review.moviesreviewservice.dto.GetMovieReviewDto
import com.review.moviesreviewservice.dto.UpdateMovieReviewDto
import com.review.moviesreviewservice.exception.ReviewDataException
import com.review.moviesreviewservice.exceptionhandler.GlobalErrorHandler
import jakarta.validation.ConstraintViolation
import jakarta.validation.Validator
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.Sinks
import java.util.*
import java.util.stream.Collectors

@Component
class ReviewHandlerImplV1(
    private val reviewReactiveRepository: ReviewReactiveRepository
):ReviewHandler {

    /*
    * map은 요소를 변환하고 새로운 컬렉션을 생성하는 데 사용되며,
    * flatMap은 요소를 변환하고 중첩된 컬렉션을 평탄화하여 단일 컬렉션을 생성하는 데 사용됩니다.
    */

    /**
     * lateinit은 자동 주입이나 초기화를 의미하지 않습니다.
     * 이는 변수가 액세스되기 전에 초기화된다는 것을 컴파일러에 약속하는 것입니다.
     * 종속성 주입(Spring처럼)을 사용하는 경우 프레임워크는 일반적으로 생성자 뒤,
     * 비즈니스 로직에서 사용하기 전에 'lateinit' 변수를 초기화하여 이를 처리합니다.
     * 이러한 프레임워크를 사용하지 않는 경우 'lateinit' 변수에 액세스하기 전에
     * 초기화되었는지 확인하는 것은 귀하의 책임입니다.
     * **/
    @Autowired
    private lateinit var validator:Validator

    private val log = LoggerFactory.getLogger(GlobalErrorHandler::class.java)

    val reviewSink:Sinks.Many<Review>  = Sinks.many().replay().latest()
    override fun addReview(request: ServerRequest): Mono<ServerResponse> {
        // 서버 request에 접근해야한다. 바디 투 모노
        return request.bodyToMono(AddMovieReviewDto::class.java)
            // 들어온 dto 검증
            // 람다로 바로 함수를 통해 파라미터를 넘길 수 있다.
            .doOnNext(this::validate)
            // 검증이 끝났으면 저장
            .flatMap {
                dto
                -> reviewReactiveRepository.save(Review.of(dto))
            }
            // SSE Sink에 넣기
            .doOnNext {
                review
                -> reviewSink.tryEmitNext(review)
            }
            .flatMap {
                saved
                -> ServerResponse
                    .status(HttpStatus.CREATED)
                    .bodyValue(GetMovieReviewDto.of(saved))
            }
    }
    // dto에 올바른 값이 들어왔는지 검증용 private 함수
    // 인위적인 validator
    private fun validate(addMovieReviewDto: AddMovieReviewDto){
        val constraintViolation: MutableSet<ConstraintViolation<AddMovieReviewDto>> = validator.validate(addMovieReviewDto)
        log.info("필드 검증 위반 사항은 {}", constraintViolation)
        if(constraintViolation.isNotEmpty()){
            // 포착된 validation의 메세지를 ,를 붙여서 콜렉트 해서 반환해 줘
            val errorMessage: String = constraintViolation
                .stream()
                .map(ConstraintViolation<*>::getMessage)
                .sorted()
                .collect(Collectors.joining(", "))

            throw ReviewDataException(errorMessage)
        }
    }

    override fun getReviews(request: ServerRequest): Mono<ServerResponse> {
        val movieInfoId: Optional<String> = request.queryParam("movieInfoId")
        if (movieInfoId.isPresent){
            val reviewFlux: Flux<GetMovieReviewDto> = reviewReactiveRepository
                .findReviewsByMovieInfoId(movieInfoId.get().toLong())
                .map {
                    review
                    -> GetMovieReviewDto.of(review)
                }
            return ServerResponse
                .ok()
                .body(reviewFlux,GetMovieReviewDto::class.java)
        }else{
            val reviewDtoFlux: Flux<GetMovieReviewDto> = reviewReactiveRepository.findAll()
                .map { review
                    ->
                    GetMovieReviewDto.of(review)
                }
            return ServerResponse
                .ok()
                .body(reviewDtoFlux,GetMovieReviewDto::class.java)
        }
    }

    /**
     * ServerResponse는 pathVariable, queryParam 등의 리퀘스트 패킷 요소를 함수로 제어할 수 있다.
     * 그래서 함수형이라는 것이다.
     * 대부분의 속성이나 필드 값을 다 함수로 관리 가능하다.
     * **/
    // 먼저 pathVariable을 가져오자
    // 들여쓰기 한칸 밖으로 빼기 -> shift + tab
    override fun updateReview(request: ServerRequest): Mono<ServerResponse> {
        val reviewId: String = request.pathVariable("id")
        log.info("id = {}", reviewId)

        return reviewReactiveRepository.findById(reviewId)
            .flatMap { existingReview ->
                request.bodyToMono(UpdateMovieReviewDto::class.java)
                    // 반응형 => flatmap
                    .flatMap { updateDto ->
                        existingReview.update(updateDto)
                        reviewReactiveRepository.save(existingReview)
                    }
                    // 비반응형 => map
                    .map { savedReview -> GetMovieReviewDto.of(savedReview) }
                    .flatMap { dto -> ServerResponse.ok().bodyValue(dto) }
            }
            .switchIfEmpty(ServerResponse.notFound().build())
    }


    override fun deleteReview(request: ServerRequest): Mono<ServerResponse> {
        TODO("Not yet implemented")
    }

    override fun getReviewsStream(request: ServerRequest): Mono<ServerResponse> {
        TODO("Not yet implemented")
    }

}