package me.wxc.commentdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.wxc.commentdemo.logic.impl.FoldReducer
import me.wxc.commentdemo.logic.impl.LoadLv1Reducer
import me.wxc.commentdemo.logic.impl.StartLoadLv1Reducer
import me.wxc.commentdemo.ui.CommentAdapter
import me.wxc.commentdemo.ui.CommentItem

class MainActivity : AppCompatActivity() {
    private lateinit var commentAdapter: CommentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        commentAdapter = CommentAdapter {
            lifecycleScope.launchWhenResumed {
                val newList = withContext(Dispatchers.IO) {
                    reduce.invoke(commentAdapter.currentList)
                }
                val firstSubmit = commentAdapter.itemCount == 1
                commentAdapter.submitList(newList) {
                    if (firstSubmit) {
                        recyclerView.scrollToPosition(0)
                    } else if (this@CommentAdapter is FoldReducer) {
                        val index = commentAdapter.currentList.indexOf(this@CommentAdapter.folding)
                        recyclerView.scrollToPosition(index)
                    }
                }
            }
        }
        recyclerView.adapter = commentAdapter
    }
}