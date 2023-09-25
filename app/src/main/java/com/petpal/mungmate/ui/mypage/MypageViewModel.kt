package com.petpal.mungmate.ui.mypage

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.petpal.mungmate.ui.pet.PetUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyPageViewModel : ViewModel() {
    private val repository = MyPageRepository()

    val simplePetList: LiveData<List<PetUiState>> = repository.getPetListLiveData()

    fun loadPetInfo(userId: String) {
        Log.w("pets","viewmodel: loadPetInfo")
        viewModelScope.launch(Dispatchers.IO) {
            repository.loadPetListFromFirestore(userId)
        }
    }
}