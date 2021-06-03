package ru.sagutdinov.tribune

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import io.ktor.utils.io.errors.*
import kotlinx.android.synthetic.main.activity_add_post.*
import kotlinx.android.synthetic.main.post_card.*
import kotlinx.coroutines.launch
import ru.sagutdinov.tribune.postModel.*
import java.io.IOException

class AddPostActivity : AppCompatActivity() {


    companion object {
        const val REQUEST_IMAGE_CAPTURE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)

        imageAdd.setOnClickListener {
            dispatchTakePictureIntent()
        }

        buttonCreatePost.setOnClickListener {
            val titleContent = editTextNamePost.text.toString()
            val textContent = editTextPost.text.toString()
            val linkContent = editTextAddLink.text.toString()
            val attachmentImage = getAttachModel(this@AddPostActivity)!!

            if (textContent.isEmpty()) {
                Toast.makeText(this, R.string.empty, Toast.LENGTH_SHORT).show()
            } else {
                lifecycleScope.launch {
                    try {
                        switchDeterminateBar(true)
                        val attachModelId = getAttachModel(this@AddPostActivity)
                        val response = Repository.createPost(
                            titleContent,
                            textContent,
                            linkContent,
                            attachmentImage
                        )
                        if (response.isSuccessful) {
                            Toast.makeText(
                                this@AddPostActivity,
                                R.string.succes,
                                Toast.LENGTH_SHORT
                            ).show()
                            setResult(Activity.RESULT_OK)
                            startActivityForResult(intent, 1)
                            finish()
                        } else {
                            Toast.makeText(
                                this@AddPostActivity,
                                R.string.create_post_failed,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: IOException) {
                        Toast.makeText(
                            this@AddPostActivity,
                            R.string.connect_to_server_failed,
                            Toast.LENGTH_SHORT
                        ).show()
                    } finally {
                        switchDeterminateBar(false)
                    }

                }
            }
        }
    }

    private fun switchDeterminateBar(isLaunch: Boolean) {
        if (isLaunch) {
            determinateBarCreatePost.isVisible = true
            buttonCreatePost.isEnabled = false
        } else {
            determinateBarCreatePost.isVisible = false
            buttonCreatePost.isEnabled = true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap?
            imageBitmap?.let {
                switchDeterminateBar(true)
                lifecycleScope.launch {
                    try {
                        switchDeterminateBar(true)
                        val imageUploadResult = Repository.upload(it)
                        if (imageUploadResult.isSuccessful) {
                            Toast.makeText(
                                this@AddPostActivity,
                                R.string.succes,
                                Toast.LENGTH_SHORT
                            ).show()
                            imageAdd.isEnabled = false
                            imageAdd.foreground =
                                getDrawable(R.drawable.ic_baseline_add_a_photo_24)
                            imageAdd.background =
                                getDrawable(R.drawable.ic_baseline_add_a_photo_24_grey)
                            val attachmentModel = imageUploadResult.body()!!
                            savedAttachModel(attachmentModel.id, this@AddPostActivity)
                        } else {
                            Toast.makeText(
                                this@AddPostActivity,
                                R.string.fail,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: IOException) {
                        Toast.makeText(
                            this@AddPostActivity,
                            R.string.connect_to_server_failed,
                            Toast.LENGTH_SHORT
                        ).show()
                    } finally {
                        switchDeterminateBar(false)
                    }
                }
            }
        }

    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }
}
