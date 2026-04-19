package com.crazycoderz.dailychecks.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "checklist_items")
data class ChecklistItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val createdAt: Long,
    val lastCheckedAt: Long? = null,
    val lastCheckedDate: String? = null,
    val streakCount: Int = 0,
)

