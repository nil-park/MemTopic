package com.nolbee.memtopic.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class Topic(
    @PrimaryKey
    val name: String,
    val lastModified: Date,
    val lastPlayback: Date,
    val content: String
)
