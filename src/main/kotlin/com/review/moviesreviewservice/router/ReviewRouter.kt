package com.review.moviesreviewservice.router

import com.review.moviesreviewservice.domain.Review
import com.review.moviesreviewservice.domain.ReviewReactiveRepository
import com.review.moviesreviewservice.handler.ReviewHandler
import com.review.moviesreviewservice.handler.ReviewHandlerImplV1
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.mapping.MongoMappingContext
import org.springframework.data.mongodb.repository.support.SimpleReactiveMongoRepository
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.RequestPredicates.*
import org.springframework.web.reactive.function.server.RouterFunctions.*


@Configuration// 스프링 시큐리티와 같이 라우터를 수동 빈 등록하는 방식으로 반응형 라우터 형식 진행
class ReviewRouter(
) {
    /**
     * 기존 스프링 MVC에서 Controller를 생각하면 된다.
     * 함수형 웹플럭스 프로그래밍 예시, ServerResponse 타입의 라우터 펑션으로 함수형 프로그래밍을 작성한다.
     * 빈등록을 통해서 RouterFunction<ServerResponse> 객체를 스프링 컨테이너에 넣어줘야 한다.
     * 이 빈 안에 여러 매서드들이 들어간다.
     * **/


    @Bean
    fun reviewHandlerImplV1(reviewReactiveRepository: ReviewReactiveRepository): ReviewHandler {
        return ReviewHandlerImplV1(reviewReactiveRepository)
    }

    @Bean // @Component로 띄운 핸들러를 인수로 받는다.
    fun reviewRoute(reviewHandler: ReviewHandlerImplV1): RouterFunction<ServerResponse>{

        return router {
            // nest -> RequestMapping
            // Dsl -> 빌더라 생각하면 됨
            ("/v1/reviews").nest {
                // http 메서드와 각 상황에 맞는 핸들러 호출
                POST("",reviewHandler::addReview)
                GET("",reviewHandler::getReviews)
                PUT("/{id}", reviewHandler::updateReview)
                DELETE("/{id}", reviewHandler::deleteReview)
                GET("/stream", reviewHandler::getReviewsStream)
            }
        }
    }
}