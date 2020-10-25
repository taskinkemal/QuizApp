package com.keplersegg.myself.Room.Entity

import androidx.room.*
import com.keplersegg.myself.Room.Converter.DateConverter
import java.util.*

@Entity(foreignKeys = [ForeignKey(
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.CASCADE,
                entity = Task::class,
                parentColumns = arrayOf("Id"),
                childColumns = arrayOf("TaskId"))],
        indices = [Index("TaskId")])
class Goal {

    @PrimaryKey
    var Id: Int = 0
    var TaskId: Int = 0
    var MinMax: Int = 0
    var Target: Int = 0
    var StartDay: Int = 0
    var EndDay: Int = 0
    var AchievementStatus: Int = 0
    var CurrentValue: Int = 0
    @TypeConverters(DateConverter::class)
    var ModificationDate: Date = Date(System.currentTimeMillis())

    companion object {

        fun CreateItem(id: Int, taskId: Int, minMax: Int, target: Int,
                       startDay: Int, endDay: Int): Goal {

            val m = Goal()

            m.Id = id
            m.TaskId = taskId
            m.MinMax = minMax
            m.Target = target
            m.StartDay = startDay
            m.EndDay = endDay
            m.AchievementStatus = 0
            m.CurrentValue = 0
            m.ModificationDate = Date(System.currentTimeMillis())

            return m
        }
    }
}