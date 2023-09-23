package com.petpal.mungmate.model

// test
data class Inquiry(
    val category: String,
    val title: String,
    val question: String,
    val answer: String?,
    val questionAuthor: String,
    val dateCreated: String,
    val status: Boolean
)