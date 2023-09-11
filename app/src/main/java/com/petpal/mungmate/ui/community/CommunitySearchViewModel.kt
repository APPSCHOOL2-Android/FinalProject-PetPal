package com.petpal.mungmate.ui.community

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CommunitySearchViewModel (application: Application) : AndroidViewModel(application) {
    private val repository: CommunitySearchRepository
    val allSearchHistory: LiveData<List<SearchesEntity>>

    init {
        val searchesDao = SearchesDatabase.getDataBase(application).SearchesDao()
        repository = CommunitySearchRepository(searchesDao)
        allSearchHistory = repository.allSearchHistory
    }

    fun insert(searchesEntity: SearchesEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(searchesEntity)
    }

    fun deleteAllData() = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteAllData()
    }
}