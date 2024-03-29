package ru.sagutdinov.tribune.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.sagutdinov.tribune.R
import ru.sagutdinov.tribune.postModel.Post

class PostAdapter(var list: MutableList<Post>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_ITEM_POST = 0
        private const val ITEM_FOOTER = 1
    }

    var upBtnClickListener: OnUpBtnClickListener? = null
    var downBtnClickListener: OnDownBtnClickListener? = null

    fun newRecentPosts(newData: List<Post>) {
        this.list.clear()
        this.list.addAll(newData)
    }

    interface OnUpBtnClickListener {
        fun onUpBtnClick(item: Post, position: Int)
    }

    interface OnDownBtnClickListener {
        fun onDownBtnClick(item: Post, position: Int)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            RecyclerView.ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.post_card, parent, false)
        return when (viewType) {
            ITEM_FOOTER -> FooterViewHolder(
                this,
                LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.item_load_more, parent, false)
            )
            else -> PostViewHolder(this, view, list)
        }
    }

    override fun getItemCount(): Int {
        return list.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            list.size -> ITEM_FOOTER
            else -> TYPE_ITEM_POST
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position != list.size) {
            val post = list[position]
            with(holder as PostViewHolder) {
                bind(post)
            }
        }
    }
}
