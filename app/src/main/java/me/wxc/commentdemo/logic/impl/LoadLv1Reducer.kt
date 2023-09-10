package me.wxc.commentdemo.logic.impl

import me.wxc.commentdemo.data.FakeApi
import me.wxc.commentdemo.logic.Reducer
import me.wxc.commentdemo.ui.CommentItem

class LoadLv1Reducer : Reducer {
    private val mapper by lazy { Entity2ItemMapper() }
    override val reduce: suspend List<CommentItem>.() -> List<CommentItem> = {
        val loading = get(size - 1) as CommentItem.Loading
        val loaded =
            FakeApi.getComments(loading.page + 1, 5).getOrNull()?.map(mapper::invoke) ?: emptyList()

        val grouped = loaded.groupBy {
            (it as? CommentItem.Level1)?.id ?: (it as? CommentItem.Level2)?.parentId
            ?: throw IllegalArgumentException("invalid comment item")
        }.flatMap {
            it.value + CommentItem.Folding(
                parentId = it.key,
            )
        }

        toMutableList().apply {
            removeAt(size - 1)
            this += grouped
            this += loading.copy(
                state = CommentItem.Loading.State.IDLE,
                page = loading.page + 1
            )
        }.toList()
    }
}