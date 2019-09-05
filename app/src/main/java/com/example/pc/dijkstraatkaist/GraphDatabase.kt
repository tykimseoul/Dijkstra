package com.example.pc.dijkstraatkaist

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context

@Database(entities = [Edge::class], version = 1)
@TypeConverters(Converters::class)
abstract class GraphDatabase : RoomDatabase() {

    abstract fun edgeDAO(): EdgeDAO

    companion object {
        @Volatile
        private var INSTANCE: GraphDatabase? = null

        fun getDatabase(context: Context): GraphDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GraphDatabase::class.java,
                    "word_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }

        fun destroyDatabase() {
            INSTANCE = null
        }
    }
}