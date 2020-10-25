package com.keplersegg.myself.Room.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.keplersegg.myself.Room.Entity.Goal

@Dao
interface GoalDao {

    @get:Query("SELECT * FROM Goal")
    val all: List<Goal>

    @Query("SELECT * FROM Goal where Id = :id")
    operator fun get(id: Int): Goal

    @get:Query("SELECT case when count(0) = 0 then -2 else min(Id)-1 end FROM Goal")
    val minId: Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(goal: Goal)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    fun update(goal: Goal): Int

    @Query("update Goal set Id = :newId where Id = :currentId")
    fun updateId(newId: Int, currentId: Int)

    @Delete()
    fun delete(goal: Goal): Int

    @Query("DELETE FROM Goal")
    fun deleteAll()

    @Query("DELETE FROM Goal where TaskId >= 0")
    fun deleteNonLocal()
}
