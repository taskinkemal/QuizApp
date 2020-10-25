package com.keplersegg.myself.Room

import android.content.Context

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.keplersegg.myself.Room.Dao.*
import com.keplersegg.myself.Room.Entity.*

@Database(entities = arrayOf(
        Task::class,
        Entry::class,
        TaskEntry::class,
        UserBadge::class,
        Goal::class), version = 14, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
    abstract fun entryDao(): EntryDao
    abstract fun taskEntryDao(): TaskEntryDao
    abstract fun userBadgeDao(): UserBadgeDao
    abstract fun goalDao(): GoalDao

    companion object {

        private var INSTANCE: AppDatabase? = null

        fun getAppDatabase(context: Context): AppDatabase? {

            if (INSTANCE == null) {
                INSTANCE = Room
                        .databaseBuilder(context.applicationContext, AppDatabase::class.java, "myself-database")
                        .fallbackToDestructiveMigration()
                        .build()
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}
