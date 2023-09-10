package me.wxc.commentdemo.logic.impl

import me.wxc.commentdemo.data.FakeApi
import me.wxc.commentdemo.logic.Reducer
import me.wxc.commentdemo.ui.CommentItem

data class ExpandReducer(
    val folding: CommentItem.Folding,
) : Reducer {
    private val mapper by lazy { Entity2ItemMapper() }
    override val reduce: suspend List<CommentItem>.() -> List<CommentItem> = {
        val foldingIndex = indexOf(folding)
        val loaded =
            FakeApi.getLevel2Comments(folding.parentId, folding.page, folding.pageSize).getOrNull()
                ?.map(mapper::invoke) ?: emptyList()
        toMutableList().apply {
            addAll(foldingIndex, loaded)
        }.map {
            if (it is CommentItem.Folding && it == folding) {
                val state =
                    if (it.page > 5) CommentItem.Folding.State.LOADED_ALL else CommentItem.Folding.State.IDLE
                it.copy(page = it.page + 1, state = state)
            } else {
                it
            }
        }
    }

}