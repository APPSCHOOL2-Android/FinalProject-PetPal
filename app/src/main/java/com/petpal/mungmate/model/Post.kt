package com.petpal.mungmate.model

data class Post(
    val postID: String,
    val authorUid: Long,
    val userImage: String,
    val userNickName: String,
    val userPlace: String,
    val postTitle: String,
    val postCategory: String,
    val postDateCreated: String,
    val postImage: String,
    val postContent: String,
    val postLike: Long,
    //TODO: Comment로 바꾸기
    val postComment: List<String>,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Post

        if (postID != other.postID) return false
        if (authorUid != other.authorUid) return false
        if (userImage != other.userImage) return false
        if (userNickName != other.userNickName) return false
        if (userPlace != other.userPlace) return false
        if (postTitle != other.postTitle) return false
        if (postCategory != other.postCategory) return false
        if (postDateCreated != other.postDateCreated) return false
        if (postImage != other.postImage) return false
        if (postContent != other.postContent) return false
        if (postLike != other.postLike) return false
        if (postComment != other.postComment) return false

        return true
    }

    override fun hashCode(): Int {
        var result = postID.hashCode()
        result = 31 * result + authorUid.hashCode()
        result = 31 * result + userImage.hashCode()
        result = 31 * result + userNickName.hashCode()
        result = 31 * result + userPlace.hashCode()
        result = 31 * result + postTitle.hashCode()
        result = 31 * result + postCategory.hashCode()
        result = 31 * result + postDateCreated.hashCode()
        result = 31 * result + postImage.hashCode()
        result = 31 * result + postContent.hashCode()
        result = 31 * result + postLike.hashCode()
        result = 31 * result + postComment.hashCode()
        return result
    }
}