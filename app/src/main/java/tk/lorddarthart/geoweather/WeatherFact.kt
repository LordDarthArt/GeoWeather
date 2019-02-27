package tk.lorddarthart.geoweather

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class WeatherFact {
    @SerializedName("temp")
    @Expose
    var weatherNow: Double? = null
    @SerializedName("condition")
    @Expose
    var weatherDescription: String? = null
    @SerializedName("humidity")
    @Expose
    var weatherHumidity: Double? = null
    @SerializedName("pressure_mm")
    @Expose
    var weatherPressure: Double? = null

    constructor(weatherNow: Double?, weatherDescription: String?, weatherHumidity: Double?, weatherPressure: Double?) {
        this.weatherNow = weatherNow
        this.weatherDescription = weatherDescription
        this.weatherHumidity = weatherHumidity
        this.weatherPressure = weatherPressure
    }
}