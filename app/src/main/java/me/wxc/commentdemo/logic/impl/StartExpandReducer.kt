package me.wxc.commentdemo.logic.impl

import me.wxc.commentdemo.logic.Reducer
import me.wxc.commentdemo.ui.CommentItem

data class StartExpandReducer(
    val folding: CommentItem.Folding,
) : Reducer {
    override val reduce: suspend List<CommentItem>.() -> List<CommentItem> = {
        map {
            if (it is CommentItem.Folding && it == folding) {
                it.copy(
                    state = CommentItem.Folding.State.LOADING
                )
            } else {
                it
            }
        }
    }

}