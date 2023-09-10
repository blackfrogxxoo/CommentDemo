package me.wxc.commentdemo.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.GONE
import androidx.recyclerview.widget.RecyclerView.VISIBLE
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import me.wxc.commentdemo.R
import me.wxc.commentdemo.logic.Reducer
import me.wxc.commentdemo.logic.impl.ExpandReducer
import me.wxc.commentdemo.logic.impl.FoldReducer
import me.wxc.commentdemo.logic.impl.LoadLv1Reducer
import me.wxc.commentdemo.logic.impl.ReplyReducer
import me.wxc.commentdemo.logic.impl.StartLoadLv1Reducer

class CommentAdapter(private val reduceBlock: Reducer.() -> Unit) :
    ListAdapter<CommentItem, VH>(object : DiffUtil.ItemCallback<CommentItem>() {
        override fun areItemsTheSame(oldItem: CommentItem, newItem: CommentItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CommentItem, newItem: CommentItem): Boolean {
            if (oldItem::class.java != newItem::class.java) return false
            return (oldItem as? CommentItem.Level1) == (newItem as? CommentItem.Level1)
                    || (oldItem as? CommentItem.Level2) == (newItem as? CommentItem.Level2)
                    || (oldItem as? CommentItem.Folding) == (newItem as? CommentItem.Folding)
                    || (oldItem as? CommentItem.Loading) == (newItem as? CommentItem.Loading)
        }
    }) {

    init {
        submitList(listOf(CommentItem.Loading(page = 0, CommentItem.Loading.State.IDLE)))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_LEVEL1 -> Level1VH(
                inflater.inflate(R.layout.item_comment_level_1, parent, false),
                reduceBlock
            )

            TYPE_LEVEL2 -> Level2VH(
                inflater.inflate(R.layout.item_comment_level_2, parent, false),
                reduceBlock
            )

            TYPE_LOADING -> LoadingVH(
                inflater.inflate(
                    R.layout.item_comment_loading,
                    parent,
                    false
                ), reduceBlock
            )

            else -> FoldingVH(
                inflater.inflate(R.layout.item_comment_folding, parent, false),
                reduceBlock
            )
        }
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.onBind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is CommentItem.Level1 -> TYPE_LEVEL1
            is CommentItem.Level2 -> TYPE_LEVEL2
            is CommentItem.Loading -> TYPE_LOADING
            else -> TYPE_FOLDING
        }
    }

    companion object {
        private const val TYPE_LEVEL1 = 0
        private const val TYPE_LEVEL2 = 1
        private const val TYPE_FOLDING = 2
        private const val TYPE_LOADING = 3
    }
}

abstract class VH(itemView: View, protected val reduceBlock: Reducer.() -> Unit) :
    ViewHolder(itemView) {
    abstract fun onBind(item: CommentItem)
}

class Level1VH(itemView: View, reduceBlock: Reducer.() -> Unit) : VH(itemView, reduceBlock) {
    private val avatar: TextView = itemView.findViewById(R.id.avatar)
    private val username: TextView = itemView.findViewById(R.id.username)
    private val content: TextView = itemView.findViewById(R.id.content)
    private val reply: TextView = itemView.findViewById(R.id.reply)
    override fun onBind(item: CommentItem) {
        avatar.text = item.userName.subSequence(0, 1)
        username.text = item.userName
        content.text = item.content
        reply.setOnClickListener {
            reduceBlock.invoke(ReplyReducer(item, itemView.context))
        }
    }
}

class Level2VH(itemView: View, reduceBlock: Reducer.() -> Unit) : VH(itemView, reduceBlock) {
    private val avatar: TextView = itemView.findViewById(R.id.avatar)
    private val username: TextView = itemView.findViewById(R.id.username)
    private val content: TextView = itemView.findViewById(R.id.content)
    private val reply: TextView = itemView.findViewById(R.id.reply)
    override fun onBind(item: CommentItem) {
        avatar.text = item.userName.subSequence(0, 1)
        username.text = item.userName
        content.text = item.content
        reply.setOnClickListener {
            reduceBlock.invoke(ReplyReducer(item, itemView.context))
        }

    }
}

class FoldingVH(itemView: View, reduceBlock: Reducer.() -> Unit) : VH(itemView, reduceBlock) {
    private val expand: TextView = itemView.findViewById(R.id.expand)
    private val fold: TextView = itemView.findViewById(R.id.fold)
    override fun onBind(item: CommentItem) {
        val folding = item as CommentItem.Folding
        expand.text = folding.content
        expand.visibility =
            if (folding.state == CommentItem.Folding.State.LOADED_ALL) GONE else VISIBLE
        expand.setOnClickListener {
            reduceBlock.invoke(ExpandReducer(folding))
        }
        fold.visibility = if (folding.page == 1) GONE else VISIBLE
        fold.setOnClickListener {
            reduceBlock.invoke(FoldReducer(folding))
        }
    }
}

class LoadingVH(itemView: View, reduceBlock: Reducer.() -> Unit) : VH(itemView, reduceBlock) {
    private val state: TextView = itemView.findViewById(R.id.state)
    override fun onBind(item: CommentItem) {
        state.text = item.content
        if ((item as CommentItem.Loading).state == CommentItem.Loading.State.IDLE) {
            reduceBlock.invoke(StartLoadLv1Reducer())
            reduceBlock.invoke(LoadLv1Reducer())
        }
    }
}