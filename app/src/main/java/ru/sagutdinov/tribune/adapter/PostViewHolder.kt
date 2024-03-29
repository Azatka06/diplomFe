package ru.sagutdinov.tribune.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.post_card.view.*
import ru.sagutdinov.tribune.ReactionsActivity
import ru.sagutdinov.tribune.R
import ru.sagutdinov.tribune.UserActivity
import ru.sagutdinov.tribune.postModel.AttachmentModel
import ru.sagutdinov.tribune.postModel.BASE_URL
import ru.sagutdinov.tribune.postModel.Post
import ru.sagutdinov.tribune.postModel.StatusUser

open class PostViewHolder(
    private val adapter: PostAdapter,
    val view: View, var list: MutableList<Post>
) : RecyclerView.ViewHolder(view) {

    companion object {
        const val USERNAME = "USERNAME"
        const val POST_ID = "POST_ID"
    }

    init {
        this.clickButtonListener()
    }

    open fun bind(post: Post) {
        with(view) {
            textViewUserName.text = post.userName
            textViewPostName.text = post.postName
            textViewPost.text = post.postText
            textViewData.text = post.dateOfCreate
            fillCount(textViewNumberUp, post.postUpCount)
            fillCount(textViewNumberDown, post.postDownCount)

            val attachModel = AttachmentModel(post.attachmentImage)
            loadImage(imageViewPic, attachModel.url)

            when {
                post.link == null -> {
                    imageViewLink.visibility = View.GONE
                    imageViewLink.isEnabled = false
                }
                else -> {
                    imageViewLink.visibility = View.VISIBLE
                    imageViewLink.isEnabled = true
                }
            }
            when {
                post.upActionPerforming -> {
                    imageViewUp.setImageResource(R.drawable.ic_baseline_thumb_up_24_white)
                }
                post.pressedPostUp -> {
                    imageViewUp.setImageResource(R.drawable.ic_baseline_thumb_up_24_green)
                }
                else -> {
                    imageViewUp.setImageResource(R.drawable.ic_baseline_thumb_up_24_grey)
                }
            }
            when {
                post.downActionPerforming -> {
                    imageViewDown.setImageResource(R.drawable.ic_baseline_thumb_down_24_white)
                }
                post.pressedPostDown -> {
                    imageViewDown.setImageResource(R.drawable.ic_baseline_thumb_down_24_red)
                }
                else -> {
                    imageViewDown.setImageResource(R.drawable.ic_baseline_thumb_down_24_grey)
                }
            }
            when {
                post.statusUser == StatusUser.NONE -> {
                    textViewUserStatus.visibility = View.GONE
                }
            }
            if (post.statusUser == StatusUser.HATER) {
                textViewUserStatus.setTextColor(resources.getColor(R.color.colorRed))
                textViewUserStatus.text = context.getString(R.string.hater)
            }
            if (post.statusUser == StatusUser.PROMOTER) {
                textViewUserStatus.setTextColor(resources.getColor(R.color.colorGreen))
                textViewUserStatus.text = context.getString(R.string.promoter)
            }
        }
    }

    private fun fillCount(view: TextView, postCount: Int) {
        if (postCount == 0) {
            view.visibility = View.GONE
        } else {
            view.visibility = View.VISIBLE
            view.text = postCount.toString()
        }
    }

    fun loadImage(photoImg: ImageView, imageUrl: String) {
        Glide.with(photoImg.context)
            .load(imageUrl)
            .into(photoImg)
    }

    private fun clickButtonListener() {
        with(view) {
            imageViewUp.setOnClickListener {
                val currentPosition = adapterPosition
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    val item = list[adapterPosition]
                    if (item.upActionPerforming) {
                        Toast.makeText(
                            context,
                            R.string.action_is_performing,
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        adapter.upBtnClickListener?.onUpBtnClick(item, currentPosition)
                    }
                }
            }
            imageViewDown.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    val item = list[adapterPosition]
                    if (item.downActionPerforming) {
                        Toast.makeText(
                            context,
                            R.string.action_is_performing,
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        adapter.downBtnClickListener?.onDownBtnClick(item, adapterPosition)
                    }
                }
            }
            imageViewLink.setOnClickListener {
                val currentPosition = adapterPosition
                if (currentPosition != RecyclerView.NO_POSITION) {
                    val item = list[currentPosition]
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse(item.link)
                    }
                    itemView.context.startActivity(intent)
                }
            }
            imageViewLook.setOnClickListener {
                val currentPosition = adapterPosition
                if (currentPosition != RecyclerView.NO_POSITION) {
                    val idPost = list[adapterPosition].idPost
                    val intent = Intent(context, ReactionsActivity::class.java)
                    intent.putExtra(POST_ID, idPost)
                    itemView.context.startActivity(intent)
                }
            }
            imageViewPhotoAvatar.setOnClickListener {
                context as Activity
                if (context !is UserActivity) {
                    startUserActivity(context)
                }
            }


            textViewUserName.setOnClickListener {
                context as Activity
                if (context !is UserActivity) {
                    startUserActivity(context)
                }
            }
        }
    }

    private fun startUserActivity(context: Context) {
        if (adapterPosition != RecyclerView.NO_POSITION) {
            val usernameOfPost = list[adapterPosition].userName
            val intent = Intent(context, UserActivity::class.java)
            intent.putExtra(USERNAME, usernameOfPost)
            context.startActivity(intent)
        }
    }
}