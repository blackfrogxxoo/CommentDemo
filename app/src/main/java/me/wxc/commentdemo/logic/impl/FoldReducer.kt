package me.wxc.commentdemo.logic.impl

import me.wxc.commentdemo.logic.Reducer
import me.wxc.commentdemo.ui.CommentItem

data class FoldReducer(val folding: CommentItem.Folding) : Reducer {
    override val reduce: suspend List<CommentItem>.() -> List<CommentItem> = {
        val parentIndex = indexOfFirst { it.id == folding.parentId }
        val foldingIndex = indexOf(folding)
        (this - subList(parentIndex + 3, foldingIndex).toSet()).map {
            if (it is CommentItem.Folding && it == folding) {
                it.copy(page = 1, state = CommentItem.Folding.State.IDLE)
            } else {
                it
            }
        }
    }
}