package com.example.weatherforcast.network.di

import com.example.weatherforcast.network.api.WeatherApi
import com.example.weatherforcast.core.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
/*    @Provides
    @Singleton
    fun providesMoshi(): Moshi =
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()*/



/*    @Provides
    @Singleton
    fun providesRetrofitMoshi(moshi: Moshi): Retrofit =
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()*/

    @Provides
    @Singleton
    fun providesRetrofit():Retrofit =
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(Constants.BASE_URL)
            .build()


    @Provides
    @Singleton
    fun providesRetrofitService(retrofit: Retrofit): WeatherApi =
        retrofit.create(WeatherApi::class.java)
}