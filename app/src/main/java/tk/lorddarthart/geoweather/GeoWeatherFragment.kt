package tk.lorddarthart.geoweather

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context.LOCATION_SERVICE
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import kotlinx.android.synthetic.main.fragment_geo_weather.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*
import android.location.*
import android.preference.PreferenceManager
import android.support.v4.app.ActivityCompat
import org.apache.commons.lang3.StringUtils
import java.lang.NumberFormatException

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

private const val SAVED_LATITUDE = "tk.lorddarthart.geoweather.latitude"
private const val SAVED_LONGITUDE = "tk.lorddarthart.geoweather.longitude"

class GeoWeatherFragment : Fragment() {
    private lateinit var mSqLiteDatabase: SQLiteDatabase
    private lateinit var geocoder: Geocoder
    private lateinit var mDatabaseHelper: WeatherDatabaseHelper
    private lateinit var httpServiceHelper: HttpServiceHelper
    private var weather = ArrayList<WeatherSample>()
    private lateinit var cursor: Cursor
    private var cursor2: Cursor? = null
    private var dialog: ProgressDialog? = null
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var ed: SharedPreferences.Editor
    private var opening = 0
    private var opening2 = 0
    private lateinit var addresses: List<Address>
    private var cities = LinkedList<City>()
    private lateinit var consLayText: ConstraintLayout
    private lateinit var constraintLayout: ImageView
    private lateinit var fab: FloatingActionButton
    private lateinit var editText: EditText
    private var isOpen = false
    private var param1: String? = null
    private var param2: String? = null
    private var locationListener: LocationListener? = null
    private var locationManager: LocationManager? = null
    private var locationLongitude: Double? = null
    private var locationLatitude: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        container?.removeAllViews()
        val view = inflater.inflate(R.layout.fragment_geo_weather, container, false)
        val geocoder = Geocoder(context)

        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                locationLongitude = location.longitude;
                locationLatitude = location.latitude;
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

            }

            override fun onProviderEnabled(provider: String?) {

            }

            override fun onProviderDisabled(provider: String?) {

            }
        }

        locationManager = context!!.getSystemService(LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                context!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context!!,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            locationLatitude = null
            locationLongitude = null
        } else {
            if (locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null) {
                locationLatitude = (locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)).latitude
                locationLongitude = (locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)).longitude
                locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0F, locationListener)
                locationManager!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0F, locationListener)
            } else {
                if (locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) != null) {
                    locationLatitude =
                        (locationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)).latitude
                    locationLongitude =
                        (locationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)).longitude
                    locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0F, locationListener)
                    locationManager!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0F, locationListener)
                } else {
                    locationLatitude = null
                    locationLongitude = null
                }
            }
        }

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context!!)
        val ed = sharedPreferences.edit()

        mDatabaseHelper = WeatherDatabaseHelper(
            activity?.applicationContext!!,
            WeatherDatabaseHelper.DATABASE_NAME,
            null,
            WeatherDatabaseHelper.DATABASE_VERSION
        )
        mSqLiteDatabase = mDatabaseHelper.writableDatabase
        val query =
            ("SELECT "
                    + WeatherDatabaseHelper.WEATHER_DATE + ", "
                    + WeatherDatabaseHelper.WEATHER_CITY + ", "
                    + WeatherDatabaseHelper.WEATHER_NOW + ", "
                    + WeatherDatabaseHelper.WEATHER_HIGH + ", "
                    + WeatherDatabaseHelper.WEATHER_LOW + ", "
                    + WeatherDatabaseHelper.WEATHER_TEXT + ", "
                    + WeatherDatabaseHelper.WEATHER_DESCRIPTION + ", "
                    + WeatherDatabaseHelper.WEATHER_HUMIDITY + ", "
                    + WeatherDatabaseHelper.WEATHER_PRESSURE + ", "
                    + WeatherDatabaseHelper.WEATHER_SUNRISE + ", "
                    + WeatherDatabaseHelper.WEATHER_SUNSET +
                    " FROM " + WeatherDatabaseHelper.DATABASE_WEATHER)
        cursor = mSqLiteDatabase.rawQuery(query, arrayOfNulls(0))

        if (sharedPreferences.contains(SAVED_LATITUDE) && sharedPreferences.contains(SAVED_LONGITUDE)) { // Если уже вводили геоданные
            view.edit_text_latitude.setText(sharedPreferences.getString(SAVED_LATITUDE, ""))
            view.edit_text_longitude.setText(sharedPreferences.getString(SAVED_LONGITUDE, ""))
        } else {
            if (locationLatitude != null || locationLongitude != null) { // Если предоставили доступ к геоданным, получаем погоду по текущему местоположению
                view.edit_text_latitude.setText(locationLatitude?.toString())
                view.edit_text_longitude.setText(locationLongitude?.toString())
            }
        }

        view.button_enter.setOnClickListener {
            // Нажатие на кнопку "Ввод"
            try {
                var latitude: Double? = null
                var longitude: Double? = null
                try {
                     latitude = view.edit_text_latitude.text.toString().toDouble()
                } catch (e: NumberFormatException) {
                    view.text_input_layout_latitude.error =
                        context!!.resources.getString(R.string.error_numericformat)
                }
                try {
                    longitude = view.edit_text_longitude.text.toString().toDouble()
                } catch (e: NumberFormatException) {
                    view.text_input_layout_longitude.error =
                        context!!.resources.getString(R.string.error_numericformat)
                }
                if (latitude != null && longitude != null) {
                    dialog = ProgressDialog(activity) // Диалоговое окно загрузки данных
                    dialog!!.max = 100
                    dialog!!.setMessage(context!!.resources.getString(R.string.dialog_loading))
                    dialog!!.show()
                    dialog!!.setCancelable(false)
                    addresses = geocoder.getFromLocation(
                        latitude,
                        longitude,
                        1
                    )
                    if (addresses.isNotEmpty()) {
                        val calendar = Calendar.getInstance()
                        HttpServiceHelper().getInstance().getJSONApi().getCurrentForecastForMyPlace(
                            latitude.toString(),
                            longitude.toString(),
                            context!!.resources.getString(R.string.lang)
                        ).enqueue(object : Callback<WeatherSample> {

                            @SuppressLint("CommitPrefEdits")
                            override fun onResponse(call: Call<WeatherSample>, response: Response<WeatherSample>) {
                                val weather = response.body()
                                if (weather != null) {
                                    calendar.timeInMillis = weather.weatherDate!! * 1000
                                    val day = calendar.get(Calendar.DAY_OF_WEEK)
                                    val dayOfWeek = getDayOfWeek(day)
                                    weather.weatherCity = addresses[0].locality
                                    if (weather.weatherCity==null) {
                                        weather.weatherCity = context!!.resources.getString(R.string.error_city_not_found)
                                    }
                                    WeatherDatabaseHelper.addWeather(
                                        mSqLiteDatabase, weather.weatherDate,
                                        weather.weatherFact!!.weatherNow, weather.weatherCity,
                                        weather.weatherForecastsItem!![0].weatherDayParts!!.weatherDayTime!!.weatherHigh,
                                        weather.weatherForecastsItem!![0].weatherDayParts!!.weatherDayTime!!.weatherLow,
                                        weather.weatherFact!!.weatherDescription,
                                        dayOfWeek,
                                        weather.weatherFact!!.weatherHumidity,
                                        weather.weatherFact!!.weatherPressure,
                                        weather.weatherForecastsItem!![0].weatherSunrise,
                                        weather.weatherForecastsItem!![0].weatherSunset
                                    )
                                    getCurrentForecast()
                                    val args: Bundle? = Bundle()
                                    args!!.putLong("weatherDate", weather.weatherDate!!)
                                    args.putString("weatherText", dayOfWeek)
                                    args.putString("weatherCity", weather.weatherCity!!)
                                    args.putDouble("weatherNow", weather.weatherFact!!.weatherNow!!)
                                    args.putDouble("weatherHumidity", weather.weatherFact!!.weatherHumidity!!)
                                    args.putDouble("weatherPressure", weather.weatherFact!!.weatherPressure!!)
                                    val frag = GeoWeatherMoreFragment()
                                    frag.arguments = args
                                    fragmentManager!!.beginTransaction().replace(R.id.geo_coordinates_fragment, frag)
                                        .commit()
                                    ed.putString(SAVED_LATITUDE, latitude.toString())
                                    ed.putString(SAVED_LONGITUDE, longitude.toString())
                                    ed.apply()
                                } else {
                                    getCurrentForecast()
                                    Snackbar.make(
                                        activity!!.findViewById(R.id.main_constraint_layout),
                                        context!!.resources.getString(R.string.no_forecast),
                                        Snackbar.LENGTH_LONG
                                    ).show()
                                }
                                dialog?.dismiss()
                            }

                            override fun onFailure(call: Call<WeatherSample>, t: Throwable) {
                                getCurrentForecast()
                                Snackbar.make(
                                    activity!!.findViewById(R.id.main_constraint_layout),
                                    context!!.resources.getString(R.string.server_error),
                                    Snackbar.LENGTH_LONG
                                ).show()
                                dialog?.dismiss()
                            }
                        })
                    } else {
                        dialog?.dismiss()
                        Snackbar.make(activity!!.findViewById(R.id.main_constraint_layout), R.string.error_city_not_found, Snackbar.LENGTH_SHORT).show()
                    }
                } else {
                    if (view.text_view_geocoordinates_latitude.text.toString().isEmpty()) {
                        view.text_input_layout_latitude.error =
                            context!!.resources.getString(R.string.error_emptylatitude)
                    } else if (latitude == null) {
                        view.text_input_layout_latitude.error =
                            context!!.resources.getString(R.string.error_numericformat)
                    }
                    if (view.text_view_geocoordinates_longitude.text.toString().isEmpty()) {
                        view.text_input_layout_longitude.error =
                            context!!.resources.getString(R.string.error_emptylongitude)
                    } else if (longitude == null) {
                        view.text_input_layout_longitude.error =
                            context!!.resources.getString(R.string.error_numericformat)
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                Snackbar.make(activity!!.findViewById(R.id.main_constraint_layout), R.string.ioerror, Snackbar.LENGTH_SHORT)
                    .show()
                dialog?.dismiss()
            }
        }

        if (view.edit_text_latitude.text.toString() != "" && view.edit_text_longitude.text.toString() != "") {
            // Если текстовые поля не пустые, производим программное нажатие
            view.button_enter.performClick()
        }

        return view
    }

    fun getCurrentForecast() {
        // Получаем объект погоды для локаций из БД на текущий день
        cursor.moveToFirst()
        cursor.moveToPrevious()
        weather.clear()
        while (cursor.moveToNext()) {
            val weathers = WeatherSample()
            weathers.weatherCity = cursor.getString(cursor.getColumnIndex(WeatherDatabaseHelper.WEATHER_CITY))
            weathers.weatherDate = cursor.getLong(cursor.getColumnIndex(WeatherDatabaseHelper.WEATHER_DATE))
            weathers.weatherFact = WeatherFact(
                cursor.getDouble(cursor.getColumnIndex(WeatherDatabaseHelper.WEATHER_NOW)),
                cursor.getString(cursor.getColumnIndex(WeatherDatabaseHelper.WEATHER_DESCRIPTION)),
                cursor.getDouble(cursor.getColumnIndex(WeatherDatabaseHelper.WEATHER_HUMIDITY)),
                cursor.getDouble(cursor.getColumnIndex(WeatherDatabaseHelper.WEATHER_PRESSURE))
            )
            weathers.weatherForecastsItem = mutableListOf()
            weathers.weatherForecastsItem!!.add(
                WeatherForecastsItem(
                    WeatherForecastsItemParts(
                        WeatherForecastsItemPartsDay(
                            cursor.getDouble(cursor.getColumnIndex(WeatherDatabaseHelper.WEATHER_HIGH)),
                            cursor.getDouble(cursor.getColumnIndex(WeatherDatabaseHelper.WEATHER_LOW))
                        )
                    ),
                    cursor.getString(cursor.getColumnIndex(WeatherDatabaseHelper.WEATHER_SUNRISE)),
                    cursor.getString(cursor.getColumnIndex(WeatherDatabaseHelper.WEATHER_SUNSET))
                )
            )
            weather.add(weathers)
        }
    }

    private fun getDayOfWeek(day: Int): String? {
        // Получаем конкретное наименование дня недели
        when (day) {
            Calendar.MONDAY -> {
                return context!!.resources.getString(R.string.week_monday)
            }

            Calendar.TUESDAY -> {
                return context!!.resources.getString(R.string.week_tuesday)
            }

            Calendar.WEDNESDAY -> {
                return context!!.resources.getString(R.string.week_wednesday)
            }

            Calendar.THURSDAY -> {
                return context!!.resources.getString(R.string.week_thursday)
            }

            Calendar.FRIDAY -> {
                return context!!.resources.getString(R.string.week_friday)
            }

            Calendar.SATURDAY -> {
                return context!!.resources.getString(R.string.week_saturday)
            }

            Calendar.SUNDAY -> {
                return context!!.resources.getString(R.string.week_sunday)
            }
        }
        return null
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            GeoWeatherFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
