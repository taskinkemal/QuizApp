package com.keplersegg.myself.Room.Entity

import com.keplersegg.myself.Room.Converter.DateConverter

import java.util.Date

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.TypeConverters

import androidx.room.ForeignKey.CASCADE

@Entity(primaryKeys = ["Day", "TaskId"],
        foreignKeys = [ForeignKey(
                onDelete = CASCADE,
                onUpdate = CASCADE,
                entity = Task::class,
                parentColumns = arrayOf("Id"),
                childColumns = arrayOf("TaskId"))],
        indices = [Index("TaskId")])
class Entry {

    var Day: Int = 0
    var TaskId: Int = 0
    var Value: Int = 0
    @TypeConverters(DateConverter::class)
    var ModificationDate: Date = Date(System.currentTimeMillis())

    companion object {

        fun CreateItem(day: Int, taskId: Int): Entry {

            val m = Entry()

            m.Day = day
            m.TaskId = taskId
            m.Value = 0
            m.ModificationDate = Date(System.currentTimeMillis())

            return m
        }
    }
}
