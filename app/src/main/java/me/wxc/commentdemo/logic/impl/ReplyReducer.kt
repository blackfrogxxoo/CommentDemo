package me.wxc.commentdemo.logic.impl

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.wxc.commentdemo.R
import me.wxc.commentdemo.data.FakeApi
import me.wxc.commentdemo.logic.Reducer
import me.wxc.commentdemo.ui.CommentItem
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ReplyReducer(private val commentItem: CommentItem, private val context: Context) : Reducer {
    private val mapper: Entity2ItemMapper by lazy { Entity2ItemMapper() }
    override val reduce: suspend List<CommentItem>.() -> List<CommentItem> = {
        val content = withContext(Dispatchers.Main) {
            suspendCoroutine { continuation ->
                ReplyDialog(context) {
                    continuation.resume(it)
                }.show()
            }
        }
        val parentId = (commentItem as? CommentItem.Level1)?.id
            ?: (commentItem as? CommentItem.Level2)?.parentId ?: 0
        val replyItem = mapper.invoke(FakeApi.addComment(parentId, content))
        val insertIndex = indexOf(commentItem) + 1
        toMutableList().apply {
            add(insertIndex, replyItem)
        }
    }
}

class ReplyDialog(context: Context, private val callback: (String) -> Unit) : Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_reply)
        val editText = findViewById<EditText>(R.id.content)
        findViewById<Button>(R.id.submit).setOnClickListener {
            if (editText.text.toString().isBlank()) {
                Toast.makeText(context, "评论不能为空", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            callback.invoke(editText.text.toString())
            dismiss()
        }
    }
}