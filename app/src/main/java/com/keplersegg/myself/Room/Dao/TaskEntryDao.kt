package com.keplersegg.myself.Room.Dao

import com.keplersegg.myself.Room.Entity.TaskEntry

import androidx.room.Dao
import androidx.room.Query

@Dao
interface TaskEntryDao {

    @Query("SELECT e.*, t.*, t.Id as TaskEntryId FROM Task t LEFT OUTER JOIN Entry e ON e.TaskId = t.Id and e.Day = :day where t.Status = 1")
    fun getTasks(day: Int): List<TaskEntry>
}
