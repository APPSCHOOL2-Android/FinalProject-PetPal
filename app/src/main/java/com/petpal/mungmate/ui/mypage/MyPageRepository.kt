package com.petpal.mungmate.ui.mypage

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MyPageRepository {
    private val db = FirebaseFirestore.getInstance()
    private val simplePetList = MutableLiveData<List<SimplePetUiState>>(emptyList())


    // Firestore에서 반려견 정보를 가져오는 함수
    suspend fun loadPetListFromFirestore(userId: String) {
        try {
            Log.w("pets","repository: loadPetListFromFirestore")
            //컬렉션을 다 받아와서
            val querySnapshot = db.collection("users").document(userId).collection("pets").get().await()
            val result = mutableListOf<SimplePetUiState>()

            //document 하나에 대해 simplepetuistate로 만들어서 list를 차곡차곡 쌓음
            for(documentSnapshot in querySnapshot) {
                Log.d("pets", "${documentSnapshot.id} => ${documentSnapshot.data}")

                val name = documentSnapshot.getString("name")
                val petImageUrl = documentSnapshot.getString("petImageUrl")

                result.add(SimplePetUiState(name!!,petImageUrl!!))
                Log.w("pets","repository: list에 넣기")
            }

            //setvalue는 background thread에서 할 수 없음
            withContext(Dispatchers.Main) {
                //그 후 보내주기
                simplePetList.value = result
                Log.w("pets",simplePetList.value.toString())
            }


        } catch (e: Exception) {
            Log.e("pets", e.message.toString())
        }
    }


    // ViewModel에 반려견 정보 LiveData를 반환하는 함수
    fun getPetListLiveData() = simplePetList

}
