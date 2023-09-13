package com.petpal.mungmate.ui.community

import androidx.lifecycle.LiveData

class CommunitySearchRepository(private val searchesDao: SearchesDao) {

    // 모든 검색 기록을 가져온다.
    val allSearchHistory: LiveData<List<SearchesEntity>> = searchesDao.getAllData()

    // 검색 기록 삽입
    fun insert(searchesEntity: SearchesEntity) {
        // 검색 기록을 DB에 삽입
        searchesDao.insert(searchesEntity)
    }

    // 모든 데이터 삭제
    fun deleteAllData() {
        // 모든 검색 기록을 DB에서 삭제
        searchesDao.deleteAllData()
    }
    // 아이템 삭제
    fun delete(searchesEntity: SearchesEntity) {
        searchesDao.itemDelete(searchesEntity)
    }
}