package com.example.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "job_alerts_cache")
data class JobAlertEntity(
    @PrimaryKey val id: String,
    val title: String,
    val board: String,
    val category: String,
    val startDate: String,
    val lastDate: String,
    val statusLabel: String,
    val isActive: Boolean,
    val importantInfo: String,
    val notifiedUpcoming: Boolean,
    val notifiedExpiring: Boolean
)

@Dao
interface JobAlertDao {
    @Query("SELECT * FROM job_alerts_cache")
    fun getAllJobs(): Flow<List<JobAlertEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJobs(jobs: List<JobAlertEntity>)

    @Query("DELETE FROM job_alerts_cache")
    suspend fun clearJobs()
}

@Database(entities = [JobAlertEntity::class], version = 1, exportSchema = false)
abstract class PrayagiDatabase : RoomDatabase() {
    abstract fun jobAlertDao(): JobAlertDao

    companion object {
        @Volatile
        private var INSTANCE: PrayagiDatabase? = null

        fun getDatabase(context: Context): PrayagiDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = androidx.room.Room.databaseBuilder(
                    context.applicationContext,
                    PrayagiDatabase::class.java,
                    "prayagi_cache.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
