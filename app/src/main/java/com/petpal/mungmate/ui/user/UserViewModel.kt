package com.petpal.mungmate.ui.user

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.petpal.mungmate.model.UserBasicInfoData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UserViewModel : ViewModel() {
    private val _user = MutableStateFlow<FirebaseUser?>(null)
    val user: StateFlow<FirebaseUser?> get() = _user

    private val _userBasicInfo = MutableStateFlow<UserBasicInfoData?>(null)
    val userBasicInfo: StateFlow<UserBasicInfoData?> get() = _userBasicInfo

    fun setUser(user: FirebaseUser) {
        _user.value = user
    }

    fun setUserBasicInfoData(userInfo: UserBasicInfoData) {
        _userBasicInfo.value = userInfo
    }
}