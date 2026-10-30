package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.AchievementEntity
import com.example.data.model.AnnouncementEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.ChapterEntity
import com.example.data.model.CreativeSubmissionEntity
import com.example.data.model.ExamAttemptEntity
import com.example.data.model.ExamEntity
import com.example.data.model.QuestionEntity
import com.example.data.model.QuestionOptionEntity
import com.example.data.model.StudentAnswerEntity
import com.example.data.model.SubjectEntity
import com.example.data.model.UserEntity

@Database(
    entities = [
        UserEntity::class,
        CategoryEntity::class,
        SubjectEntity::class,
        ChapterEntity::class,
        ExamEntity::class,
        QuestionEntity::class,
        QuestionOptionEntity::class,
        ExamAttemptEntity::class,
        StudentAnswerEntity::class,
        CreativeSubmissionEntity::class,
        AnnouncementEntity::class,
        AchievementEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun educationDao(): EducationDao
    abstract fun examDao(): ExamDao
    abstract fun creativeDao(): CreativeDao
    abstract fun platformDao(): PlatformDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "edurank_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
