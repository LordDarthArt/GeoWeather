package tk.lorddarthart.geoweather

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class GeoWeatherMoreFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        container?.removeAllViews()
        val view = inflater.inflate(R.layout.fragment_geo_weather_more, container, false)
        if (arguments == null) {
            LabelsSwapping().hideLabels(view)
        } else {
            val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm")
            val txtDay = view.findViewById<TextView>(R.id.txtDay)
            val txtMonthYear = view.findViewById<TextView>(R.id.txtMonthYear)
            val txtText = view.findViewById<TextView>(R.id.txtText)
            val txtTemp = view.findViewById<TextView>(R.id.txtTemp)
            val txtTitle = view.findViewById<TextView>(R.id.txtTitle)
            val txtHumidity = view.findViewById<TextView>(R.id.txtHumidity)
            val txtPressure = view.findViewById<TextView>(R.id.txtPressure)

            val sdfing = SimpleDateFormat("dd-MM-yyyy HH:mm")
            val date = Date((arguments!!.get("weatherDate")!!.toString()+"000").toLong())
            val dateText = sdfing.format(date)

            try {
                txtDay.text = SimpleDateFormat("dd").format(date)
                txtMonthYear.text =
                    SimpleDateFormat("MMMM, yyyy").format(date)
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            txtText.text = arguments!!.get("weatherText") as String
            if (arguments!!.get("weatherNow") as Double > 0.0) {
                txtTemp.text = "+" + (arguments!!.get("weatherNow") as Double).toString()
            } else {
                txtTemp.text = (arguments!!.get("weatherNow") as Double).toString()
            }
            txtTitle.text = arguments!!.get("weatherCity") as String
            txtHumidity.text = (arguments!!.get("weatherHumidity") as Double).toString() + "%"
            txtPressure.text = (arguments!!.get("weatherPressure") as Double).toString() + " " + context!!.resources.getString(R.string.pressure_value)
        }

        return view
    }
}