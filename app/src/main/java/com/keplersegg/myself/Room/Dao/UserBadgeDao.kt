package com.keplersegg.myself.Room.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.keplersegg.myself.Room.Entity.UserBadge

@Dao
interface UserBadgeDao {

    @get:Query("SELECT * FROM UserBadge")
    val all: List<UserBadge>

    @Query("SELECT * FROM UserBadge where BadgeId = :badgeId")
    operator fun get(badgeId: Int): UserBadge?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(badge: UserBadge)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    fun update(badge: UserBadge): Int

    @Query("DELETE FROM UserBadge")
    fun deleteAll()
}
