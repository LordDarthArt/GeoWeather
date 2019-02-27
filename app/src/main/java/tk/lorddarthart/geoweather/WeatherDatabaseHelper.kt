package tk.lorddarthart.geoweather

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

class WeatherDatabaseHelper : SQLiteOpenHelper, BaseColumns {

    constructor(context: Context) : super(context, DATABASE_NAME, null, DATABASE_VERSION) {}

    constructor(context: Context, name: String, factory: SQLiteDatabase.CursorFactory?, version: Int) : super(
        context,
        name,
        factory,
        version
    )

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(DATABASE_CREATE_WEATHER_SCRIPT)
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {

    }

    companion object {
        const val DATABASE_NAME = "tk.lorddarthart.geoweather.db"
        var DATABASE_VERSION = 1

        const val DATABASE_WEATHER = "weather"
        private const val WEATHER_ID = "weather_id"
        const val WEATHER_NOW = "weather_now"
        const val WEATHER_DATE = "weather_date"
        const val WEATHER_CITY = "weather_city"
        const val WEATHER_HIGH = "weather_high"
        const val WEATHER_LOW = "weatherLow"
        const val WEATHER_TEXT = "weatherText"
        const val WEATHER_DESCRIPTION = "weather_description"
        const val WEATHER_HUMIDITY = "weather_humidity"
        const val WEATHER_PRESSURE = "weather_pressure"
        const val WEATHER_SUNRISE = "weather_sunrise"
        const val WEATHER_SUNSET = "weather_sunset"

        const val DATABASE_CREATE_WEATHER_SCRIPT = ("create table "
                + DATABASE_WEATHER
                + " (" + WEATHER_ID + " integer not null primary key autoincrement, "
                + WEATHER_DATE + " long not null, "
                + WEATHER_CITY + " text, "
                + WEATHER_NOW + " double not null, "
                + WEATHER_HIGH + " double not null, "
                + WEATHER_LOW + " double not null, "
                + WEATHER_TEXT + " text not null, "
                + WEATHER_DESCRIPTION + " text not null, "
                + WEATHER_HUMIDITY + " double not null, "
                + WEATHER_PRESSURE + " double not null, "
                + WEATHER_SUNRISE + " text not null, "
                + WEATHER_SUNSET + " text not null, "
                + "UNIQUE(" + WEATHER_CITY + ") ON CONFLICT REPLACE);")

        fun addWeather(
            mSqLiteDatabase: SQLiteDatabase,
            weather_date: Long?,
            weather_now: Double?,
            weather_city: String?,
            weather_high: Double?,
            weather_low: Double?,
            weather_text: String?,
            weather_description: String?,
            weather_humidity: Double?,
            weather_pressure: Double?,
            weather_sunrise: String?,
            weather_sunset: String?
        ) {
            val newValues = ContentValues()
            newValues.put(WeatherDatabaseHelper.WEATHER_DATE, weather_date)
            newValues.put(WeatherDatabaseHelper.WEATHER_NOW, weather_now)
            newValues.put(WeatherDatabaseHelper.WEATHER_CITY, weather_city)
            newValues.put(WeatherDatabaseHelper.WEATHER_HIGH, weather_high)
            newValues.put(WeatherDatabaseHelper.WEATHER_LOW, weather_low)
            newValues.put(WeatherDatabaseHelper.WEATHER_TEXT, weather_text)
            newValues.put(WeatherDatabaseHelper.WEATHER_HUMIDITY, weather_humidity)
            newValues.put(WeatherDatabaseHelper.WEATHER_PRESSURE, weather_pressure)
            newValues.put(WeatherDatabaseHelper.WEATHER_DESCRIPTION, weather_description)
            newValues.put(WeatherDatabaseHelper.WEATHER_SUNRISE, weather_sunrise)
            newValues.put(WeatherDatabaseHelper.WEATHER_SUNSET, weather_sunset)

            mSqLiteDatabase.insertWithOnConflict(
                WeatherDatabaseHelper.DATABASE_WEATHER,
                null,
                newValues,
                SQLiteDatabase.CONFLICT_REPLACE
            )
        }
    }
}
