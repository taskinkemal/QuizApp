package com.keplersegg.myself.Room.Dao

import com.keplersegg.myself.Room.Entity.Task

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface TaskDao {

    @get:Query("SELECT * FROM Task")
    val all: List<Task>

    @Query("SELECT * FROM Task where Status = :status")
    fun getAll(status: Int): List<Task>

    @get:Query("SELECT case when count(0) = 0 then -4 else min(Id)-1 end FROM Task")
    val minId: Int

    @Query("SELECT * FROM Task where Id = :id")
    operator fun get(id: Int): Task

    @Query("SELECT count(0) FROM Task where Label = :label and Status = 1")
    fun getCountByLabel(label: String): Int

    @Query("SELECT * FROM Task where Id <> :taskId and Label = :label and Status = 1")
    fun getCountByLabelExcludeId(taskId: Int, label: String): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(task: Task)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    fun update(task: Task): Int

    @Query("update Task set Id = :newId where Id = :currentId")
    fun updateId(newId: Int, currentId: Int)

    @Query("update Task set Status = 0 where Id = :id")
    fun delete(id: Int)

    @Query("DELETE FROM Task")
    fun deleteAll()

    @Query("DELETE FROM Task where Id >= 0")
    fun deleteNonLocal()
}
