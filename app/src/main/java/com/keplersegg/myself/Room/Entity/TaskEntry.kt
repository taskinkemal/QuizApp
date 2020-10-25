package com.keplersegg.myself.Room.Entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.RoomWarnings

@Entity
@SuppressWarnings(RoomWarnings.PRIMARY_KEY_FROM_EMBEDDED_IS_DROPPED)
class TaskEntry {

    @Embedded
    var entry: Entry? = null

    @Embedded
    var task: Task? = null

    @PrimaryKey(autoGenerate = true)
    var TaskEntryId: Int = 0
}
