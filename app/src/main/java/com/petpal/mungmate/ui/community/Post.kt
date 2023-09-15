package com.petpal.mungmate.ui.community

data class Post(
    val postUid: String?=null,//게시글 작성자 id
    val userImage: String?=null,//유저 프로필 사진
    val userNickName: String?=null,//유저 닉네임
    val userPlace: String?=null,//유저 위치
    val postTitle: String?=null,//게시글 제목
    val postCategory: String?=null,//게시글 카테고리
    val postDateCreated: String?=null,//게시글 작성 날짜
//    val postImages: List<PostImage>,//게시글 이미지 목록
    val postImages:String?=null,
    val postContent: String?=null,//게시글 내용
    val postLike: Long=0,//게시글 좋아요 수
//    val postComment: List<Comment>//게시글 댓글
    val postComment:String?=null
)

data class Comment(
    val commentUid: Long,//댓글 작성자 id
    val commentNickName: String,//댓글 작성자 닉네임
    val commentDateCreated: String,//댓글 작성 날짜
    val commentContent: String,//댓글 내용
    val commentLike: Long,//댓글 좋아요 수
    val parentID: String//부모 ID(최상위인 경우 "root")
)

data class PostImage(
    val mainImage:String,
    val image1:String,
    val image2:String,
    val image3:String,
    val image4:String,
)