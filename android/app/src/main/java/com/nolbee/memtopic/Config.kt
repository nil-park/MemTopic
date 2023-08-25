package com.nolbee.memtopic

import android.content.Context
import android.util.Base64
import android.util.Log
import androidx.room.*
import com.google.gson.Gson
import com.nolbee.memtopic.security.AndroidAesCipherHelper
import com.nolbee.memtopic.security.AndroidRsaCipherHelper
import com.nolbee.memtopic.security.SecureSharedPreferences
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import java.io.File

@Entity
class Config {
    @PrimaryKey
    var uid: Int = 0

    @ColumnInfo(name = "TextToSpeachApiHost")
    var textToSpApiHost: String = "GCP"

    @ColumnInfo(name = "TextToSpeachApiKey")
    var textToSpeechApiKey: String = ""

    companion object {
        fun fromFile(configFile: File): Config {
            val s = configFile.readText()
            return Gson().fromJson(s, Config::class.java)
        }
    }
}

@Dao
interface ConfigDao {
    @Query("SELECT * FROM config")
    fun getAll(): List<Config>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(config: Config)
}

@Database(entities = [Config::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun configDao(): ConfigDao
}

fun Context.getDb(): AppDatabase {
    val settingsPrefs = applicationContext.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
    val settings = SecureSharedPreferences(settingsPrefs)
    val dbPass = with(settings) {
        var savedDbPass = get("DB_PASSPHRASE", "")
        AndroidRsaCipherHelper.init(applicationContext)
        if (savedDbPass.isEmpty()) {
            val secretKey = AndroidAesCipherHelper.generateRandomKey(256)
            savedDbPass = String(Base64.encode(secretKey, Base64.DEFAULT))
            put("DB_PASSPHRASE",  AndroidRsaCipherHelper.encrypt(savedDbPass))
            secretKey.fill(0, 0, secretKey.size - 1)
        } else {
            savedDbPass = AndroidRsaCipherHelper.decrypt(savedDbPass)
        }
        val dbPassBytes = Base64.decode(savedDbPass, Base64.DEFAULT)
        CharArray(dbPassBytes.size) { i -> dbPassBytes[i].toInt().toChar() }
    }
    val passphrase = SQLiteDatabase.getBytes(dbPass)
    val factory = SupportFactory(passphrase)
    val db = Room.databaseBuilder(
        applicationContext,
        AppDatabase::class.java, "config.db"
    ).openHelperFactory(factory
    ).allowMainThreadQueries().build()
    dbPass.fill('0', 0, dbPass.size - 1)
    return db
}

fun Context.getConfig(): Config {
    try {
        val db = getDb()
        // 설정 데이터 가져 오기
        val configList = db.configDao().getAll()
        // 설정이 데이터가 없으면 기본 설정을 삽입
        return if (configList.isEmpty()) {
            val config = Config()
            db.configDao().insert(config)
            config
        } else {
            configList[0]
        }
    } catch (e: java.lang.Exception) {
        Log.d("Config", "getConfig error: ${e.message}")
        return Config()
    }
}

fun Context.setConfig(config: Config) {
    val db = getDb()
    db.configDao().insert(config)
}
