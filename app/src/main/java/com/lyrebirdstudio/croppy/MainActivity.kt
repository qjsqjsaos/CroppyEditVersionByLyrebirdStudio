package com.lyrebirdstudio.croppy

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import com.lyrebirdstudio.aspectratiorecyclerviewlib.aspectratio.model.AspectRatio
import com.lyrebirdstudio.croppy.databinding.ActivityMainBinding
import com.lyrebirdstudio.croppylib.Croppy
import com.lyrebirdstudio.croppylib.main.CropRequest
import com.lyrebirdstudio.croppylib.main.CroppyTheme
import com.lyrebirdstudio.croppylib.main.StorageType
import com.lyrebirdstudio.croppylib.util.file.FileCreator
import com.lyrebirdstudio.croppylib.util.file.FileOperationRequest


class MainActivity : AppCompatActivity() {

    /**크롭 설정 경로
     * AspectRatio(범위 지정) - >
     * AspectRatioDataProvider(범위지정 프로바이더)  ->
     * AspectRatioRecyclerView(범위지정 리사이클러뷰) ->
     * fragmentImageCrop(크롭이미지프래그먼트.xml)  ->
     * ImageCropFragment(이미지크롭프래그먼트)
     * */

    var mBinding: ActivityMainBinding? = null
    val binding get() = mBinding!!

    val TAG: String = "로그"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d(TAG, "MainActivity - onCreate() called")

        binding.buttonChoose.setOnClickListener {
            Log.d(TAG, "MainActivity - 버튼클릭 이미지 크롭 실행")
            startCroppy() // 이미지 크롭 실
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "MainActivity - onActivityResult() called")

        if (requestCode == RC_CROP_IMAGE) {
            data?.data?.let {
                Log.v("TEST", it.toString())
                binding.imageViewCropped.setImageURI(it)
                Log.d(TAG, "MainActivity - 수정된 이미지 가져와서 이미지 뷰에 삽입 ")
            }
        }
    }

    private fun startCroppy() {
        val uri = Uri.Builder()
            .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
            .authority(resources.getResourcePackageName(R.drawable.aa)) //권한을 인코딩하고 설정한다.
            .appendPath(resources.getResourceTypeName(R.drawable.aa)) //주어진 세그먼트를 인코딩하고 경로에 추가한다.
            .appendPath(resources.getResourceEntryName(R.drawable.aa))
            .build()

        //외부에 저장하고 URI를 반환합니다.
        val externalCropRequest = CropRequest.Auto(
            sourceUri = uri,
            requestCode = RC_CROP_IMAGE
        )

        //캐시에 저장하고 URI를 반환합니다.
        val cacheCropRequest = CropRequest.Auto(
            sourceUri = uri,
            requestCode = RC_CROP_IMAGE,
            storageType = StorageType.CACHE
        )

        // 지정된 대상 URI에 저장합니다.
        val destinationUri =
            FileCreator
                .createFile(FileOperationRequest.createRandom(), application.applicationContext)
                .toUri()

        val manualCropRequest = CropRequest.Manual(
            sourceUri = uri,
            destinationUri = destinationUri,
            requestCode = RC_CROP_IMAGE
        )



        //실질적으로는 이것을 사용
        //비율종류
        val excludeAspectRatiosCropRequest = CropRequest.Manual(
            sourceUri = uri,
            destinationUri = destinationUri,
            requestCode = RC_CROP_IMAGE,
            //색 설정
            croppyTheme = CroppyTheme(R.color.white),

            //제외했으면 하는 비율 종류 넣기
            excludedAspectRatios = arrayListOf( //비율종류
                AspectRatio.ASPECT_1_2,
                AspectRatio.ASPECT_3_2,
                AspectRatio.ASPECT_3_4,
                AspectRatio.ASPECT_4_3,
                AspectRatio.ASPECT_5_4,
                AspectRatio.ASPECT_A_4,
                AspectRatio.ASPECT_A_5,
                AspectRatio.ASPECT_FACE_COVER,
                AspectRatio.ASPECT_FACE_POST,
                AspectRatio.ASPECT_FREE,
                AspectRatio.ASPECT_INS_1_1,
                AspectRatio.ASPECT_INS_4_5,
                AspectRatio.ASPECT_INS_STORY,
                AspectRatio.ASPECT_PIN_POST,
                AspectRatio.ASPECT_TWIT_HEADER,
                AspectRatio.ASPECT_TWIT_POST,
                AspectRatio.ASPECT_YOU_COVER
            )
        )

        val themeCropRequest = CropRequest.Manual(
            sourceUri = uri,
            destinationUri = destinationUri,
            requestCode = RC_CROP_IMAGE,
            croppyTheme = CroppyTheme(R.color.blue) //색 설정
        )

        Croppy.start(this, excludeAspectRatiosCropRequest)
    }

    companion object {
        private const val RC_CROP_IMAGE = 102

    }
}
