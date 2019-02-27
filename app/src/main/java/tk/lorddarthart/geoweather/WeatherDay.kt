package tk.lorddarthart.geoweather

class WeatherDay {
    var weatherDay: String? = null
    var weatherHight: Double? = null
    var weatherLow: Double? = null
    var weatherText: String? = null

    constructor(weatherDay: String?, weatherHight: Double?, weatherLow: Double?, weatherText: String?) {
        this.weatherDay = weatherDay
        this.weatherHight = weatherHight
        this.weatherLow = weatherLow
        this.weatherText = weatherText
    }

    constructor() {

    }
}
