package com.petpal.mungmate.model

// test
data class Inquiry(
    var category: String,
    var subject: String,
    var question: String,
    var answer: String?,
    var questionAuthor: String,
    var createDate: String,
    var state: Boolean
)
