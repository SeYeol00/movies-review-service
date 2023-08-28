package com.review.moviesreviewservice.exceptionhandler

import com.review.moviesreviewservice.exception.ReviewDataException
import com.review.moviesreviewservice.exception.ReviewNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class GlobalErrorHandler:ErrorWebExceptionHandler {
    private val log = LoggerFactory.getLogger(GlobalErrorHandler::class.java)

    override fun handle(exchange: ServerWebExchange, ex: Throwable): Mono<Void> {
        log.error("익셉션 메세지 : {}", ex.message,ex)
        val dataBufferFactory: DataBufferFactory = exchange.response.bufferFactory()
        val exceptionMessage: DataBuffer = dataBufferFactory.wrap(ex.message!!.toByteArray())

        // 익셉션 상황 지정
        // is => class 체크 연산자
        if (ex is ReviewDataException){
            // 익셉션 처리 코드
            // 리스폰스 status 코드 지정
            exchange.response.setStatusCode(HttpStatus.BAD_REQUEST)
            // 리스폰스 바디 지정
            return exchange.response.writeWith(Mono.just(exceptionMessage))
        }
        if (ex is ReviewNotFoundException){
            exchange.response.setStatusCode(HttpStatus.NOT_FOUND)
            return exchange.response.writeWith(Mono.just(exceptionMessage))
        }
        exchange.response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR)
        return exchange.response.writeWith(Mono.just(exceptionMessage))
    }
}