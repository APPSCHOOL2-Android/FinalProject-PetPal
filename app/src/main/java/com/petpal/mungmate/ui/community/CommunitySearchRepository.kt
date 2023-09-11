package com.petpal.mungmate.ui.community

import androidx.lifecycle.LiveData

class CommunitySearchRepository(private val searchesDao: SearchesDao) {
    val allSearchHistory: LiveData<List<SearchesEntity>> = searchesDao.getAllData()

    suspend fun insert(searchesEntity: SearchesEntity) {
        searchesDao.insert(searchesEntity)
    }

    suspend fun deleteAllData() {
        searchesDao.deleteAllData()
    }
}