package com.petpal.mungmate.ui.matchhistory

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.petpal.mungmate.databinding.FragmentMatchHistoryBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Locale


class MatchHistoryFragment : Fragment() {

    private lateinit var _fragmentMatchHistoryFragment: FragmentMatchHistoryBinding
    private val fragmentMatchHistoryBinding get() = _fragmentMatchHistoryFragment
    private lateinit var petFilterAdapter: PetFilterAdapter
    private lateinit var matchHistoryAdapter: MatchHistoryAdapter
    private val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val matchHistorList = mutableListOf<MatchHistoryUiState>()
    val getPetFilterList = mutableListOf<PetFilterUiState>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _fragmentMatchHistoryFragment = FragmentMatchHistoryBinding.inflate(layoutInflater)
        petFilterAdapter = PetFilterAdapter(requireContext())
        matchHistoryAdapter = MatchHistoryAdapter(requireContext())

        fragmentMatchHistoryBinding.run {
            recyclerViewMatchHistory.run {
                adapter = matchHistoryAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }


            recyclerViewPetFilter.run {
                adapter = petFilterAdapter
            }

            val db = FirebaseFirestore.getInstance()
            val matchesCollection = db.collection("matches")


            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val documents = matchesCollection.whereEqualTo("senderId", user!!.uid).get().await()

                    val tempList = mutableListOf<MatchHistoryUiState>()
                    val tempList2 = mutableListOf<PetFilterUiState>()

                    for (document in documents) {
                        val receiverId = document.getString("receiverId").toString()
                        val walkTimestamp = document.getTimestamp("walkTimestamp")
                        val walkPlace = document.getString("walkPlace")
                        val date = walkTimestamp?.toDate()
                        val formattedDate = SimpleDateFormat("yyyy년 MM월 dd일 a hh시 mm분", Locale.getDefault()).format(date)


                        val imageUrl = FirebaseStorage.getInstance()
                            .getReference("/userImage/$receiverId")
                            .downloadUrl.await()


                        val mateName = db.collection("users")
                            .document(receiverId)
                            .get()
                            .await()
                            .getString("nickname") ?: ""


                        val petListSnapshot = db.collection("users")
                            .document(receiverId)
                            .collection("pets")
                            .get()
                            .await()

                        val petNameList = mutableListOf<String>()

                        for (petDocument in petListSnapshot) {
                            val petData = petDocument.data
                            val petName = petData["name"].toString()
                            val petImage =petData["petImageUrl"].toString()
                            petNameList.add(petName)
                        }

                        val petNames = petNameList.joinToString(", ")

                        tempList.add(
                            MatchHistoryUiState(
                                imageUrl.toString(),
                                mateName,
                                petNames,
                                formattedDate,
                                walkPlace.toString()
                            )
                        )

                        tempList2.add(
                            PetFilterUiState(
                                imageUrl.toString(),
                                mateName
                            )
                        )
                    }
                    launch(Dispatchers.Main) {
                        matchHistorList.clear()
                        matchHistorList.addAll(tempList)
                        matchHistoryAdapter.submitList(matchHistorList)

                        getPetFilterList.clear()
                        getPetFilterList.addAll(tempList2)
                        petFilterAdapter.submitList(getPetFilterList)

                        if(matchHistorList.isEmpty()){
                            NotDataLConstraintLayout.visibility=View.VISIBLE
                        }
                    }
                } catch (e: Exception) {

                    Log.e("매칭기록", "데이터 가져오기 실패: ${e.message}")
                }
            }

            toolbarManageHistory.run {
                setNavigationOnClickListener {
                    findNavController().popBackStack()
                }
            }
        }
        return fragmentMatchHistoryBinding.root
    }

}