package com.petpal.mungmate.ui.community

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.petpal.mungmate.model.Comment

class CommentViewModel : ViewModel() {
    private val _postCommentList = MutableLiveData<MutableList<Comment>>()
    val postCommentList: LiveData<MutableList<Comment>> = _postCommentList

    fun setCommentList(commentList: MutableList<Comment>) {
        _postCommentList.value = commentList
    }

}