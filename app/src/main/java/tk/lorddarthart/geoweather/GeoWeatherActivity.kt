package tk.lorddarthart.geoweather

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment

class GeoWeatherActivity : AppCompatActivity() {

    private lateinit var mBottomNavigationBar : BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_geo_weather)
    }

    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.geo_coordinates -> { // Пункт 1 (ГеоКоординаты)
                    initializeFragment(GeoWeatherFragment())
                    return@OnNavigationItemSelectedListener true
                }
                R.id.geo_history -> { // Пункт 2 (История)
                    initializeFragment(HistoryFragment())
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }


    private fun initializeFragment(fr: Fragment) {
        // Инициализация кастомного фрагмента
        val fragmentManager = supportFragmentManager
        val fragment = fragmentManager.findFragmentById(R.id.main_activity_fragment)
        if (fragment != null) {
            fragmentManager.beginTransaction().replace(R.id.main_activity_fragment, fragment).commit()
        }
    }
}
