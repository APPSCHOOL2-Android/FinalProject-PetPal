package com.petpal.mungmate.ui.mypage

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.navOptions
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.petpal.mungmate.MainActivity
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.FragmentMyPageBinding
import com.petpal.mungmate.ui.matchhistory.MatchHistoryUiState
import com.petpal.mungmate.ui.matchhistory.PetFilterUiState
import com.petpal.mungmate.ui.user.UserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Collections
import java.util.Date
import java.util.Locale

class MyPageFragment : Fragment() {
    private lateinit var _fragmentMyPageBinding: FragmentMyPageBinding
    private val fragmentMyPageBinding get() = _fragmentMyPageBinding
    private lateinit var mainActivity: MainActivity

    private val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    private lateinit var userViewModel: UserViewModel
    val db = FirebaseFirestore.getInstance()

    val dateList: MutableList<Date> = ArrayList()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _fragmentMyPageBinding = FragmentMyPageBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity
        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
        fragmentMyPageBinding.run {
            buttonManagePet.setOnClickListener {
                mainActivity.navigate(R.id.action_mainFragment_to_managePetFragment)
            }

            buttonWalkHistory.setOnClickListener {
                mainActivity.navigate(R.id.action_mainFragment_to_walk_history)
            }

            buttonGoToMatchHistory.setOnClickListener {
                mainActivity.navigate(R.id.action_mainFragment_to_match_history)
            }

            buttonGoToManageBlock.setOnClickListener {
                mainActivity.navigate(R.id.action_mainFragment_to_manage_block)
            }

            buttonGoToWalkReview.setOnClickListener {
                mainActivity.navigate(R.id.action_mainFragment_to_walkReviewHistoryFragment)
            }

            buttonAnnouncement.setOnClickListener {
                mainActivity.navigate(R.id.action_mainFragment_to_announcement)
            }

            buttonFAQ.setOnClickListener {
                mainActivity.navigate(R.id.action_mainFragment_to_FAQFragment)
            }

            buttonInquire.setOnClickListener {
                mainActivity.navigate(R.id.action_mainFragment_to_inquiryFragment)
            }

            imageRowSimplePet.setOnClickListener {
                mainActivity.navigate(
                    R.id.action_mainFragment_to_addPetFragment,
                    bundleOf("isAdd" to true)
                )
            }

            buttonOrderHistory.setOnClickListener {
                mainActivity.navigate(R.id.action_mainFragment_to_order_history)
            }

            cardViewProfile.setOnClickListener {
                //회원가입 진입 인지, 마이페이지 진입인지 구분용
                mainActivity.navigate(
                    R.id.action_mainFragment_to_userInfoFragment,
                    bundleOf("isRegister" to false)
                )
            }

            buttonLogOut.setOnClickListener {
                //로그아웃
                auth.signOut()
                Snackbar.make(requireView(), "로그아웃 되었습니다.", Snackbar.LENGTH_SHORT).show()
                //로그인 화면으로 이동 -> 백스택 clear

                mainActivity.popBackStack(R.id.mainFragment, true)
                mainActivity.navigate(
                    R.id.userStartFragment
                )
            }
            getFireStoreUserInfo()

            viewLifecycleOwner.lifecycleScope.launch {
                userViewModel.user.collect { userData ->
                    // userData를 사용하여 사용자 정보 표시
                    if (userData != null) {
                        fragmentMyPageBinding.run {
                            //닉네임 표시
                            textViewNickname.text = userData.displayName

                            //프로필 사진 표시
                            Glide.with(requireContext())
                                .load(userData.photoUrl)
                                .into(imageView)


                        }
                    }
                }
            }

            val matchesCollection = db.collection("matches")

            matchesCollection.whereEqualTo("senderId", user?.uid)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val timestamp = document.getTimestamp("timestamp")
                        val walkPlace= document.getString("walkPlace")
                        val receiverId= document.getString("receiverId")
                        val date = timestamp?.toDate()
                        val walkTimestamp =
                            SimpleDateFormat("yyyy년 MM월 dd일 a hh시 mm분", Locale.getDefault()).format(
                                date
                            )

                        db.collection("users").document(receiverId.toString())
                            .get()
                            .addOnSuccessListener { document ->
                                if (document != null) {
                                    // userImage,nickname, birthday, walkHoursStart, walkHoursEnd
                                    val nickname = document.getString("nickname")
                                    textViewMatchPlace.text="${nickname}님과 ${walkPlace}에서"
                                }
                            }

                        Log.d("walkTimestamp", walkTimestamp.toString())
                        textViewMatchPlace.text=walkPlace

                        if (date != null) {
                            dateList.add(date)
                        }
                    }

                    dateList.sort()

                    if (dateList.isNotEmpty()) {
                        val closestDate = dateList[0]
                        val formattedDate =
                            SimpleDateFormat("M.dd", Locale.getDefault()).format(closestDate)
                        val dayOfWeek =
                            SimpleDateFormat("EEEE", Locale.getDefault()).format(closestDate)
                        val time = SimpleDateFormat("a hh:mm", Locale.getDefault()).format(closestDate)
                        textViewDate.text = formattedDate
                        textViewDay.text = dayOfWeek
                        textViewMatchTime.text=time
                    } else {
                        // 리스트가 비어있는 경우 처리
                        textViewDate.text = "예정된 약속이 없음"
                        textViewDay.visibility=View.GONE
                        imageViewMatchProfile.visibility=View.GONE
                        textViewMatchPlace.visibility=View.GONE
                        textViewMatchTime.visibility=View.GONE
                        imageView5.visibility=View.GONE
                    }
                }
                .addOnFailureListener { exception ->

                }
        }
        return fragmentMyPageBinding.root
    }

    private fun getFireStoreUserInfo() {

        if (user != null) {

            db.collection("users").document(user.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        // userImage,nickname, birthday, walkHoursStart, walkHoursEnd
                        val userImage = document.getString("userImage")
                        val nickname = document.getString("nickname")
                        val gender = document.getLong("gender")
                        val birthday = document.getString("birthday")
                        val walkHoursStart = document.getString("walkHoursStart")
                        val walkHoursEnd = document.getString("walkHoursEnd")

                        val storage = FirebaseStorage.getInstance()
                        val storageRef = storage.reference

                        val profileImage: StorageReference = storageRef.child(userImage.toString())
                        profileImage.downloadUrl
                            .addOnSuccessListener { uri ->
                                val imageUrl = uri.toString()
                                Glide
                                    .with(requireContext())
                                    .load(imageUrl)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .fitCenter()
                                    .fallback(R.drawable.main_image)
                                    .into(fragmentMyPageBinding.imageView)
                            }
                            .addOnFailureListener { exception ->

                                Log.e("이미지 실패", "이미지 다운로드 실패 ${userImage.toString()}")
                            }

                        fragmentMyPageBinding.textViewNickname.text = nickname.toString()
                        var whatGender = "남"
                        if (gender!!.toInt() == 1) {
                            whatGender = "여"
                        }
                        var genderAge = "${whatGender}/${calculateAge(birthday.toString())}"
                        fragmentMyPageBinding.textViewGenderAge.text = genderAge

                        walkHoursStart
                        walkHoursEnd

                        if (walkHoursStart!!.isEmpty() || walkHoursEnd!!.isEmpty()) {
                            fragmentMyPageBinding.textViewAvailable.text = "언제든 가능해요"
                        } else {
                            fragmentMyPageBinding.textViewAvailable.text =
                                "$walkHoursStart ~ $walkHoursEnd 가능해요"
                        }
                    }
                }
        }
    }

    private fun calculateAge(birthDate: String): Int {

        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val birthDateObj = dateFormat.parse(birthDate)


        val diff = currentDate.time - birthDateObj.time


        val ageInMillis = diff / (1000L * 60 * 60 * 24 * 365)

        // 연령을 정수로 변환하여 반환
        return ageInMillis.toInt()
    }

    class DateComparator : Comparator<Date> {
        override fun compare(date1: Date, date2: Date): Int {
            // date1과 date2를 비교하여 정렬 순서를 결정
            return date1.compareTo(date2)
        }
    }

}