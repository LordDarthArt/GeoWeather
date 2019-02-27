package tk.lorddarthart.geoweather

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class WeatherForecastsItem {
    @SerializedName("parts")
    @Expose
    var weatherDayParts: WeatherForecastsItemParts? = null
    @SerializedName("sunrise")
    @Expose
    var weatherSunrise: String? = null
    @SerializedName("sunset")
    @Expose
    var weatherSunset: String? = null

    constructor(weatherDayParts: WeatherForecastsItemParts?, weatherSunrise: String?, weatherSunset: String?) {
        this.weatherDayParts = weatherDayParts
        this.weatherSunrise = weatherSunrise
        this.weatherSunset = weatherSunset
    }
}