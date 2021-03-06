package com.guuguo.gank.net

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.guuguo.android.pikacomic.net.https.TrustAllCerts
import com.guuguo.gank.constant.datePattern
import com.guuguo.gank.constant.myGson
import com.guuguo.gank.model.GankDays
import com.guuguo.gank.model.GankNetResult
import com.guuguo.gank.model.Ganks
import com.guuguo.gank.model.entity.GankModel
import io.reactivex.Flowable
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by gaohailong on 2016/5/17.
 */
interface Service {
    @GET("data/{type}/{count}/{page}")
    fun getGanHuo(@Path("type") type: String, @Path("count") count: Int, @Path("page") page: Int): Flowable<Ganks<MutableList<GankModel>>>

    @GET("day/{date}")
    fun getGankOneDay(@Path("date") date: String): Flowable<GankDays>

    @GET("search/query/{query}/category/{category}/count/{count}/page/{page} ")
    fun getGankSearchResult(
        @Path("query") query: String, @Path("category") category: String, @Path(
            "count"
        ) count: Int, @Path("page") page: Int
    ): Flowable<GankNetResult>

    companion object {
        private const val GANHUO_API = "https://gank.io/api/"
        fun create(): Service = create(GANHUO_API.toHttpUrlOrNull()!!)
        fun create(httpUrl: HttpUrl): Service {

            val commonHttpBuilder = OkHttpClient.Builder()
                .sslSocketFactory(TrustAllCerts.createSSLSocketFactory(), TrustAllCerts())
                .hostnameVerifier(TrustAllCerts.TrustAllHostnameVerifier())
                .connectTimeout(7, TimeUnit.SECONDS)
                .readTimeout(7, TimeUnit.SECONDS)
            val httpClient = commonHttpBuilder
                .retryOnConnectionFailure(false)
                .build()

            return Retrofit.Builder()
                .baseUrl(httpUrl)
                .client(httpClient)
                .addConverterFactory(getGsonConverter())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(Service::class.java)
        }

        fun server(gson: Gson = myGson) = MyRetrofit.myRetrofit(gson).create(Service::class.java)

        fun getGsonConverter(): GsonConverterFactory {
            return GsonConverterFactory
                .create(GsonBuilder().setDateFormat(datePattern).create())
        }
    }
}
