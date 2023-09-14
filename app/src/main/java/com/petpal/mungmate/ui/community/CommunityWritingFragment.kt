package com.petpal.mungmate.ui.community


import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.petpal.mungmate.MainActivity
import com.petpal.mungmate.ProductRegistrationAdapter
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.FragmentCommunityWritingBinding
import com.petpal.mungmate.model.Image


class CommunityWritingFragment : Fragment() {

    lateinit var communityWritingBinding: FragmentCommunityWritingBinding
    lateinit var mainActivity: MainActivity

    // 이미지 최대 5개
    private var mainImageList = mutableListOf<Pair<Image, Bitmap>>()

    // 갤러리 실행
    lateinit var mainGalleryLauncher: ActivityResultLauncher<Intent>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        communityWritingBinding = FragmentCommunityWritingBinding.inflate(inflater)
        bottomNavigationViewGone()
        mainGalleryLauncher = mainImageGallerySetting()
        mainActivity = activity as MainActivity

        communityWritingBinding.run {
            toolbar()

            communityWritingImageButton.setOnClickListener {
                val galleryIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
                        type = "image/*"
                        putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*"))
                    }
                mainGalleryLauncher.launch(galleryIntent)
            }

            communityWritingPostImageHorizontalRecyclerView.run {
                adapter = ProductRegistrationAdapter(mainImageList, communityWritingImageButton)
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            }

        }

        return communityWritingBinding.root
    }


    private fun bottomNavigationViewGone() {
        val bottomNavigationView =
            requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigationView.visibility = View.GONE
    }

    private fun FragmentCommunityWritingBinding.toolbar() {
        communityWritingToolbar.run {
            inflateMenu(R.menu.community_writing_menu)
            setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
            setNavigationOnClickListener {
                it.findNavController()
                    .popBackStack()
            }
            setOnMenuItemClickListener {

                when (it?.itemId) {
                    R.id.item_complete -> {
                        Snackbar.make(requireView(), "완료", Snackbar.LENGTH_SHORT).show()
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
                        communityWritingBinding.communityWritingImageButton.text =
                            "${mainImageList.size}/5"
                        communityWritingBinding.communityWritingPostImageHorizontalRecyclerView.adapter?.notifyDataSetChanged()
                    }
                }
            }
        return galleryLauncher
    }

}
