package tk.lorddarthart.geoweather

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class WeatherForecastsItemPartsDay {
    @SerializedName("temp_max")
    @Expose
    var weatherHigh: Double? = null
    @SerializedName("temp_min")
    @Expose
    var weatherLow: Double? = null

    constructor(weatherHigh: Double?, weatherLow: Double?) {
        this.weatherHigh = weatherHigh
        this.weatherLow = weatherLow
    }
}