package com.review.moviesreviewservice.exception


class ReviewDataException(
    override val message: String)
    : RuntimeException(message)
