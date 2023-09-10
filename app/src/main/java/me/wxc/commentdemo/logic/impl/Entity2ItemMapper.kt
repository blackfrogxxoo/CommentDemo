package me.wxc.commentdemo.logic.impl

import me.wxc.commentdemo.data.CommentLevel1
import me.wxc.commentdemo.data.CommentLevel2
import me.wxc.commentdemo.data.ICommentEntity
import me.wxc.commentdemo.logic.Mapper
import me.wxc.commentdemo.ui.CommentItem
import me.wxc.commentdemo.ui.makeHot

class Entity2ItemMapper : Mapper<ICommentEntity, CommentItem> {
    override fun invoke(entity: ICommentEntity): CommentItem {
        return when (entity) {
            is CommentLevel1 -> {
                CommentItem.Level1(
                    id = entity.id,
                    content = entity.content,
                    userId = entity.userId,
                    userName = entity.userName,
                    level2Count = entity.level2Count,
                )
            }

            is CommentLevel2 -> {
                CommentItem.Level2(
                    id = entity.id,
                    content = if (entity.hot) entity.content.makeHot() else entity.content,
                    userId = entity.userId,
                    userName = entity.userName,
                    parentId = entity.parentId,
                )
            }

            else -> {
                throw IllegalArgumentException("not implemented entity: $entity")
            }
        }
    }
}