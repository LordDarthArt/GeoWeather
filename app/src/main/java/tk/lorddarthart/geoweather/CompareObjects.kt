package tk.lorddarthart.geoweather

class CompareObjects {

    companion object : Comparator<WeatherSample> {

        override fun compare(a: WeatherSample, b: WeatherSample): Int = when {
            a.weatherDate!= b.weatherDate -> (b.weatherDate!! - a.weatherDate!!).toInt()
            else -> (a.weatherDate!! - b.weatherDate!!).toInt()
        }
    }
}