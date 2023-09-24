package com.petpal.mungmate.ui.community

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.petpal.mungmate.model.Comment
import com.petpal.mungmate.model.PostImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CommentViewModel : ViewModel() {
    private val _postCommentList = MutableLiveData<MutableList<Comment>>()
    val postCommentList: LiveData<MutableList<Comment>> = _postCommentList

    private val _communityImageList: MutableLiveData<List<PostImage>> = MutableLiveData()

    private val _replyList = MutableLiveData<List<Comment>>()
    val replyList: LiveData<List<Comment>> get() = _replyList
    val communityImageList: LiveData<List<PostImage>>
        get() = _communityImageList
    fun setCommentList(commentList: MutableList<Comment>) {
        _postCommentList.value = commentList
    }

    fun addReply(reply: Comment) {
        val currentList = _replyList.value.orEmpty().toMutableList()
        currentList.add(reply)
        _replyList.value = currentList
    }
    fun deleteComment(commentToDelete: Comment) {
        val currentList = _postCommentList.value ?: mutableListOf()
        currentList.remove(commentToDelete)
        _postCommentList.value = currentList
    }

    fun setCommunityImage(list: List<PostImage>) {
        _communityImageList.value = list
    }
}