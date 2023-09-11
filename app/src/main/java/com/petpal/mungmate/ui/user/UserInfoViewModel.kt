package com.petpal.mungmate.ui.user

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.petpal.mungmate.model.UserBasicInfo

class UserInfoViewModel: ViewModel() {

    var userUid = MutableLiveData<String>()
    var userImage = MutableLiveData<String>()
    val nickname = MutableLiveData<String>()
    val birthday = MutableLiveData<String>()
    val ageVisible = MutableLiveData<Boolean>()
    val gender = MutableLiveData<String>()
    val walkTimeZoneStart = MutableLiveData<String>()
    val walkTimeZoneEnd = MutableLiveData<String>()

    val userBasicInfoData = MutableLiveData<MutableList<UserBasicInfo>>()

    fun setUserBasicInfo(userBasicInfo: UserBasicInfo) {
        UserInfoRepository.addUserBasicInfo(userBasicInfo)
    }

}