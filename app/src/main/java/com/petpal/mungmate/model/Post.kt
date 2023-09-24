package com.petpal.mungmate.model

data class Post(
    var postID: String? = null, //게시글 id
    val authorUid: String? = null,//작성자 id
    val userImage: String? = null,//유저 프로필 사진
    val userNickName: String? = null,//유저 닉네임
    val userPlace: String? = null,//유저 위치
    val postTitle: String? = null,//게시글 제목
    val postCategory: String? = null,//게시글 카테고리
    var postDateCreated: String? = null,//게시글 작성 날짜
    var postImages: List<PostImage>? = null,//게시글 이미지 목록
    val postContent: String? = null,//게시글 내용
    val likedUserIds: List<String>? = null,// 좋아요를 누른 사용자 uid
    val postComment: List<Comment>? = null//게시글 댓글

)

data class Comment(
    val commentId : String? = null,// 댓글 id
    val commentUid: String? = null,//댓글 작성자 id
    val commentNickName: String? = null,//댓글 작성자 닉네임
    val commentUserImage: String? = null,
    val commentDateCreated: String? = null,//댓글 작성 날짜
    val commentContent: String? = null,//댓글 내용
    val commentLike: Long = 0,//댓글 좋아요 수
    val parentID: String? = null,//부모 ID(최상위인 경우 "root")
    val replyList: MutableList<Comment> = mutableListOf() // 대댓글 리스트
)

data class PostImage(
    val image: String? = ""
)