package tk.lorddarthart.geoweather

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.os.Build


class GeoWeatherActivity : AppCompatActivity() {

    private lateinit var mBottomNavigationBar: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_geo_weather)
        val fm = supportFragmentManager
        var fr = fm.findFragmentById(R.id.main_activity_fragment)
        if (fr == null) {
            fr = GeoWeatherFragment()
            fm.beginTransaction().replace(R.id.main_activity_fragment, fr).commit()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                requestPermissions(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET
                    ), 10
                )
            }
        }
        mBottomNavigationBar = findViewById(R.id.main_bottom_navigation_view)
        mBottomNavigationBar.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
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
            fragmentManager.beginTransaction().replace(R.id.main_activity_fragment, fr).commit()
        }
    }
}
