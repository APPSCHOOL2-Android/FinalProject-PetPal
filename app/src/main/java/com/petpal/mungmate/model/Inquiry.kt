package com.petpal.mungmate.model

// test
data class Inquiry(
    var category: String,
    var title: String,
    var question: String,
    var answer: String?,
    var questionAuthor: String,
    var dateCreated: String,
    var status: Boolean
)