package me.wxc.commentdemo.logic

import me.wxc.commentdemo.ui.CommentItem

interface Reducer {
    val reduce: suspend List<CommentItem>.() -> List<CommentItem>
}