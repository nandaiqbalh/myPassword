package com.nandaiqbalh.mypassword.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.nandaiqbalh.mypassword.data.local.database.dao.PasswordDao
import com.nandaiqbalh.mypassword.data.local.database.entity.PasswordEntity
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SQLiteDatabase.getBytes
import net.sqlcipher.database.SupportFactory

@Database(entities = [PasswordEntity::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun passwordDao(): PasswordDao

    companion object {
        private const val DB_NAME = "myPassword.db"

        @Volatile
        private var INSTANCE: AppDatabase? = null
        fun getInstance(context: Context): AppDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val passphrase: ByteArray =
                    SQLiteDatabase.getBytes("myPassword-hashed".toCharArray())
                val factory = SupportFactory(passphrase)

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DB_NAME
                )
                    .fallbackToDestructiveMigration()
                    .openHelperFactory(factory)
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}
