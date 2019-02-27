package tk.lorddarthart.geoweather

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class WeatherSample {
    @SerializedName("now")
    @Expose
    var weatherDate: Long? = null
    var weatherCity: String? = null
    @SerializedName("forecasts")
    @Expose
    var weatherForecastsItem: MutableList<WeatherForecastsItem>? = null
    @SerializedName("fact")
    @Expose
    var weatherFact: WeatherFact? = null

    constructor() {

    }

    constructor(
        weatherDate: Long?,
        weatherCity: String?,
        weatherForecastsItem: MutableList<WeatherForecastsItem>?,
        weatherFact: WeatherFact?
    ) {
        this.weatherDate = weatherDate
        this.weatherCity = weatherCity
        this.weatherForecastsItem = weatherForecastsItem
        this.weatherFact = weatherFact
    }
}
