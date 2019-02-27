package tk.lorddarthart.geoweather

import android.content.Context
import android.view.View
import kotlinx.android.synthetic.main.fragment_geo_weather_more.view.*

class LabelsSwapping {

    fun hideLabels(view: View) {
        view.textView7.text = ""
        view.textView8.text = ""
        view.textView10.text = ""
    }

    fun showLabels(view: View, context: Context) {
        view.textView7.text = context.resources.getString(R.string.more_today)
        view.textView8.text = context.resources.getString(R.string.more_humidity)
        view.textView10.text = context.resources.getString(R.string.more_pressure)
    }
}