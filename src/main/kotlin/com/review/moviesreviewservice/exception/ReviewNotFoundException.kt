package com.review.moviesreviewservice.exception


class ReviewNotFoundException : RuntimeException {
    override val message: String
    private var ex: Throwable? = null


    // 리턴값이 자신으로 super 지정
    constructor(message: String, ex: Throwable?) : super(message, ex) {
        this.message = message
        this.ex = ex
    }

    constructor(message: String) : super(message) {
        this.message = message
    }
}
