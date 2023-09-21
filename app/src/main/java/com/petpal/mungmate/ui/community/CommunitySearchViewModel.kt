package com.petpal.mungmate.ui.community

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.petpal.mungmate.model.Post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CommunitySearchViewModel (application: Application) : AndroidViewModel(application) {
    private val repository: CommunitySearchRepository
    val allSearchHistory: LiveData<List<SearchesEntity>>
    private val _searchQuery = MutableLiveData<String>()
    val searchQuery: LiveData<String>
        get() = _searchQuery

    init {
        // Repository, LiveData 초기화
        val searchesDao = SearchesDatabase.getDataBase(application).SearchesDao()
        repository = CommunitySearchRepository(searchesDao)
        allSearchHistory = repository.allSearchHistory
    }

    // 검색 기록 함수
    fun insert(searchesEntity: SearchesEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(searchesEntity)
    }

    // 모든 데이터 삭제 함수
    fun deleteAllData() = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteAllData()
    }

    // 아이템 한개 삭제 함수
    fun delete(searchesEntity: SearchesEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(searchesEntity)
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

}