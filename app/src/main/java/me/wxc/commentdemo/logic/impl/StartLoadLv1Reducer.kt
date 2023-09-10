package me.wxc.commentdemo.logic.impl

import me.wxc.commentdemo.logic.Reducer
import me.wxc.commentdemo.ui.CommentItem

class StartLoadLv1Reducer : Reducer {
    override val reduce: suspend List<CommentItem>.() -> List<CommentItem> = {
        map {
            if (it is CommentItem.Loading) {
                it.copy(
                    state = CommentItem.Loading.State.LOADING
                )
            } else {
                it
            }
        }
    }
}