package tk.lorddarthart.geoweather

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HttpServiceHelper {
    private lateinit var mInstance: HttpServiceHelper
    private val baseUrl = "https://api.weather.yandex.ru/"
    private var mRetrofit: Retrofit

    init {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
        System.out.println(client)
        mRetrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getInstance(): HttpServiceHelper {
        mInstance = HttpServiceHelper()
        return mInstance
    }

    fun getJSONApi(): JSONPlaceHolderApi {
        return mRetrofit.create(JSONPlaceHolderApi::class.java)
    }
}