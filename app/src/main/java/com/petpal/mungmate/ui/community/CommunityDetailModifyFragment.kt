package com.petpal.mungmate.ui.community

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.petpal.mungmate.MainActivity
import com.petpal.mungmate.ProductRegistrationAdapter
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.FragmentCommunityDetailModifyBinding
import com.petpal.mungmate.model.Image
import com.petpal.mungmate.model.Post
import java.util.Date


class CommunityDetailModifyFragment : Fragment() {

    lateinit var fragmentCommunityDetailModifyBinding: FragmentCommunityDetailModifyBinding
    lateinit var mainActivity: MainActivity

    // 이미지 최대 5개
    private var mainImageList = mutableListOf<Pair<Image, Bitmap>>()

    // 갤러리 실행
    lateinit var mainGalleryLauncher: ActivityResultLauncher<Intent>
    lateinit var postGetId: String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentCommunityDetailModifyBinding =
            FragmentCommunityDetailModifyBinding.inflate(inflater)
        mainGalleryLauncher = mainImageGallerySetting()
        mainActivity = activity as MainActivity

        fragmentCommunityDetailModifyBinding.run {

            toolbar()

            val args: CommunityDetailModifyFragmentArgs by navArgs()
            val postid = args.positionPostId
            postGetId = postid
            Log.d("확인", postid)

            communityModifyImageButton.setOnClickListener {
                val galleryIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
                        type = "image/*"
                        putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*"))
                    }
                mainGalleryLauncher.launch(galleryIntent)
            }

            communityModifyImageRecyclerView.run {
                adapter = ProductRegistrationAdapter(mainImageList, communityModifyImageButton)
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            }

            getFirestore()

        }
        return fragmentCommunityDetailModifyBinding.root
    }

    private fun FragmentCommunityDetailModifyBinding.toolbar() {
        communityModifyToolbar.run {
            inflateMenu(R.menu.community_post_modify)
            setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
            setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            setOnMenuItemClickListener {

                when (it?.itemId) {
                    R.id.item_modify -> {
                        Snackbar.make(requireView(), "완료", Snackbar.LENGTH_SHORT).show()
                        modifyFirestore()
                    }

                }
                false
            }
        }
    }

    private fun mainImageGallerySetting(): ActivityResultLauncher<Intent> {
        val galleryLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                // 메인 이미지 최대 5개까지만 첨부 가능
                if (mainImageList.count() < 5) {
                    if (it.resultCode == AppCompatActivity.RESULT_OK && it.data?.data != null) {
                        val uri = it.data?.data!!
                        var bitmap: Bitmap? = null

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            val source =
                                ImageDecoder.createSource(mainActivity.contentResolver, uri)
                            bitmap = ImageDecoder.decodeBitmap(source)
                        } else {
                            val cursor =
                                mainActivity.contentResolver.query(uri, null, null, null, null)
                            if (cursor != null) {
                                cursor.moveToNext()
                                val colIdx = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
                                val source = cursor.getString(colIdx)
                                bitmap = BitmapFactory.decodeFile(source)
                            }
                        }
                        // 메인 이미지 리스트에 추가
                        val pairImageInfo = Image(uri.toString(), "") to bitmap!!
                        mainImageList.add(pairImageInfo)
                        fragmentCommunityDetailModifyBinding.communityModifyImageButton.text =
                            "${mainImageList.size}/5"
                        fragmentCommunityDetailModifyBinding.communityModifyImageRecyclerView.adapter?.notifyDataSetChanged()
                    }
                }
            }
        return galleryLauncher
    }

    private fun getFirestore() {
        val db = FirebaseFirestore.getInstance()
        val postRef = db.collection("Post")
        postGetId?.let { id ->
            postRef.document(id)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val postTitle = documentSnapshot.getString("postTitle")
                        val postCategory = documentSnapshot.getString("postCategory")
                        val postContent = documentSnapshot.getString("postContent")
                        fragmentCommunityDetailModifyBinding.run {
                            communityPostWritingTitleTextInputEditText.text =
                                Editable.Factory.getInstance().newEditable(postTitle)
//                            communityPostModifyCategory.isHintEnabled = false
//                            communityPostModifyCategory.isHintAnimationEnabled = false
//                            categoryItem.text = Editable.Factory.getInstance().newEditable(postCategory)
                            categoryItem.setText(postCategory)
                            val items = resources.getStringArray(R.array.communityCategoryList)
                            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, items)
                            categoryItem.setAdapter(adapter)


                            communityPostModifyContentTextInputEditText.text =
                                Editable.Factory.getInstance().newEditable(postContent)
                        }
                    }
                }
        }

    }

    fun modifyFirestore() {
        val db = FirebaseFirestore.getInstance()
        val documentRef = db.collection("Post").document(postGetId)
        val dataToUpdate: Map<String, Any> = hashMapOf(
            "postTitle" to fragmentCommunityDetailModifyBinding.communityPostWritingTitleTextInputEditText.text.toString(),
            "postCategory" to fragmentCommunityDetailModifyBinding.categoryItem.text.toString(),
            "postContent" to fragmentCommunityDetailModifyBinding.communityPostModifyContentTextInputEditText.text.toString()
        )
        documentRef.update(dataToUpdate)
            .addOnSuccessListener { documentReference ->

            }
            .addOnFailureListener {

            }
            .addOnCompleteListener {
                val navController = findNavController()
                navController.popBackStack()
            }
    }
}