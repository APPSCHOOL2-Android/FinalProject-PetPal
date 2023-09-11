package com.petpal.mungmate.ui.community

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SearchesDao {

    // 모두 가져오기
    @Query("SELECT *FROM Searches_table")
    fun getAllData(): LiveData<List<SearchesEntity>>

    // 추가
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(text: SearchesEntity)

    // 삭제
    @Query("DELETE FROM Searches_table")
    fun deleteAllData()
}