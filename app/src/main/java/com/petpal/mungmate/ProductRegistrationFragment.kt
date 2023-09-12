package com.petpal.mungmate

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

import com.petpal.mungmate.databinding.FragmentProductRegistrationBinding
import com.petpal.mungmate.model.Image
import kotlin.math.roundToInt

class ProductRegistrationFragment : Fragment() {

    lateinit var productRegistrationBinding: FragmentProductRegistrationBinding
    lateinit var mainActivity: MainActivity

    // 대표 이미지 최대 5개, 설명 이미지 1개
    private var mainImageList = mutableListOf<Pair<Image, Bitmap>>()
    private var descriptionImage: Image? = null

    // 메인이미지, 상세이미지용 갤러리 실행 설정
    lateinit var mainGalleryLauncher: ActivityResultLauncher<Intent>
    lateinit var descriptionGalleryLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        productRegistrationBinding = FragmentProductRegistrationBinding.inflate(layoutInflater)

        mainGalleryLauncher = mainImageGallerySetting()
        descriptionGalleryLauncher = descriptionImageGallerySetting()

        mainActivity = activity as MainActivity
        productRegistrationBinding.run {

            recyclerViewMainImage.run {
                adapter = ProductRegistrationAdapter(mainImageList,buttonAddMainImage)
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            }

            // 대표 이미지 추가
            buttonAddMainImage.setOnClickListener {
                val galleryIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
                        type = "image/*"
                        putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*"))
                    }
                mainGalleryLauncher.launch(galleryIntent)
            }

            // 설명 이미지 추가
            buttonAddDescImage.setOnClickListener {
                val galleryIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
                        type = "image/*"
                        putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*"))
                    }
                descriptionGalleryLauncher.launch(galleryIntent)
            }

            // EditText 엔터 누르면 chip으로..
            flexBoxLayoutEditText.setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    val et = v as EditText
                    val name = et.text.toString()

                    // chip 최대 5개 까지만
                    if (flexBoxLayout.childCount <= 5 && name.isNotBlank()) {
                        flexBoxLayout.addChip(name)
                        et.text.clear()
                    }

                    return@setOnEditorActionListener true
                }
                false
            }
        }


        return productRegistrationBinding.root
    }

    // 메인 이미지 갤러리 설정
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
                        productRegistrationBinding.buttonAddMainImage.text =
                            "${mainImageList.size}/5"
                        productRegistrationBinding.recyclerViewMainImage.adapter?.notifyDataSetChanged()
                    }
                }
            }
        return galleryLauncher
    }

    // 설명 이미지 갤러리 설정
    private fun descriptionImageGallerySetting(): ActivityResultLauncher<Intent> {
        val galleryLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

                // 갤러리에서 사진을 선택했고, 이미 등록된 설명 이미지가 없을 경우
                if (descriptionImage == null) {
                    if (it.resultCode == AppCompatActivity.RESULT_OK && it.data?.data != null) {
                        val uri = it.data?.data!!
                        // RecyclerView 표시용 저장
                        var bitmap: Bitmap? = null

                        // 이미지 카드뷰 추가
                        val previewLinearLayout = layoutInflater.inflate(
                            R.layout.item_product_registration_imageview,
                            productRegistrationBinding.linearDescriptionImage,
                            false
                        ) as LinearLayout
                        val previewImageView =
                            previewLinearLayout.findViewById<ImageView>(R.id.imageViewDelete)
                        val previewButton =
                            previewLinearLayout.findViewById<Button>(R.id.buttonDelete)
                        previewButton.setOnClickListener {
                            // 이미지 카드뷰 삭제, 리스트에서 제거
                            productRegistrationBinding.linearDescriptionImage.removeView(
                                previewLinearLayout
                            )
                            descriptionImage = null
                            productRegistrationBinding.buttonAddDescImage.text = "0/1"
                        }
                        productRegistrationBinding.linearDescriptionImage.addView(
                            previewLinearLayout
                        )

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            val source =
                                ImageDecoder.createSource(mainActivity.contentResolver, uri)
                            bitmap = ImageDecoder.decodeBitmap(source)
                            previewImageView.setImageBitmap(bitmap)
                        } else {
                            val cursor =
                                mainActivity.contentResolver.query(uri, null, null, null, null)
                            if (cursor != null) {
                                cursor.moveToNext()

                                val colIdx = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
                                val source = cursor.getString(colIdx)

                                bitmap = BitmapFactory.decodeFile(source)
                                previewImageView.setImageBitmap(bitmap)
                            }
                        }

                        // 이미지 정보 저장 해두기
                        descriptionImage = Image(uri.toString(), "")
                        productRegistrationBinding.buttonAddDescImage.text = "1/1"
                    }
                }
            }

        return galleryLauncher
    }

    private fun FlexboxLayout.addChip(text: String) {
        if (text.isNotBlank()) {
            val chip = LayoutInflater.from(context).inflate(R.layout.view_chip, null) as Chip
            chip.text = text

            val layoutParams = ChipGroup.LayoutParams(
                ChipGroup.LayoutParams.WRAP_CONTENT,
                ChipGroup.LayoutParams.WRAP_CONTENT
            )
            layoutParams.rightMargin = requireContext().dpToPx(4)

            chip.setOnCloseIconClickListener {
                removeView(chip as View)
            }

            addView(chip, childCount - 1, layoutParams)
        }
    }

    // Fragment 내부에서 확장 함수로 dpToPx 정의
    fun Context.dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            resources.displayMetrics
        ).roundToInt()
    }

}

