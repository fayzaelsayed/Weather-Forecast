package com.example.weatherforecast.di

import android.app.Application
import androidx.room.Room
import com.example.weatherforecast.datasource.local.WeatherDatabase
import com.example.weatherforecast.datasource.local.WeatherDatabaseDao
import com.example.weatherforecast.datasource.remote.ApiService
import com.example.weatherforecast.repository.Repository
import com.example.weatherforecast.utils.GlobalHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideRetrofitBuilder(okHttpClient: OkHttpClient): ApiService {
        return Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(ApiService::class.java)
    }

    @Singleton
    @Provides
    fun okHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val client: OkHttpClient.Builder = OkHttpClient.Builder()
            .hostnameVerifier { hostname, session -> true }
            .readTimeout(15, TimeUnit.SECONDS)
            .connectTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)

        client.addInterceptor(loggingInterceptor)

        val okHttpClient = client.build()
        return okHttpClient
    }

    @Singleton
    @Provides
    fun provideRoomDatabaseBuilder(application: Application): WeatherDatabase {
        return Room.databaseBuilder(
            application,
            WeatherDatabase::class.java, "weather_db"
        ).allowMainThreadQueries().build()
    }

    @Singleton
    @Provides
    fun provideUserDaoInstance(weatherDatabase: WeatherDatabase): WeatherDatabaseDao {
        return weatherDatabase.weatherDatabaseDao
    }

    @Singleton
    @Provides
    fun provideSharedPreferencesInstance(application: Application): GlobalHelper{
        return GlobalHelper(application)
    }
    @Singleton
    @Provides
    fun provideRepository(apiService: ApiService, weatherDatabaseDao: WeatherDatabaseDao, globalHelper: GlobalHelper) :Repository{
        return Repository(apiService, weatherDatabaseDao, globalHelper)
    }


}