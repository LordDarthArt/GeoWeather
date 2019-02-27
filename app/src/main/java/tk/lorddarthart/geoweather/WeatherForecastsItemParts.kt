package tk.lorddarthart.geoweather

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import retrofit2.http.Url

class WeatherForecastsItemParts {
    constructor(weatherForecastsItemPartsDay: WeatherForecastsItemPartsDay)

    @SerializedName("day")
    @Expose
    var weatherDayTime: WeatherForecastsItemPartsDay? = null
}