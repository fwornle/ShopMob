package com.udacity.asteroidradar.api

import com.tanfra.shopmob.BuildConfig



import okhttp3.Interceptor
import okhttp3.Response

// AUTH: intercept all outgoing requests and append an API key obtained from an ENV variable
// ... see: https://medium.com/@harmittaa/retrofit-2-6-0-with-koin-and-coroutines-4ff45a4792fc
class AuthInterceptor() : Interceptor {

    // fetch API key from build config parameter SMOD_NET_API_KEY, see: build.gradle (:app)
    private val API_KEY = BuildConfig.SMOD_NET_API_KEY

    override fun intercept(chain: Interceptor.Chain): Response {

        var req = chain.request()

        val url = req.url().newBuilder()
            .addQueryParameter("APPID", API_KEY)
            .build()

        req = req.newBuilder()
            .url(url).build()

        return chain.proceed(req)
    }

}

/*

// parse JSON body and extract SmobUser data objects as ArrayList
// ... using ArrayList to be able to spread them to storage function "insertAll"
fun parseSmobUserJsonResult(jsonResult: JSONObject): ArrayList<SmobUser> {

    // fetch received object with SmobUser entries
    val smobUserNet = jsonResult.getJSONObject("near_earth_objects")

    // assemble result array
    val smobUserList = ArrayList<SmobUser>()

    for (smobUserEntry in smobUserNet) {

        val dateAsteroidJsonArray = smobUserNet.getJSONArray(smobUserEntry)

        for (i in 0 until dateAsteroidJsonArray.length()) {
            val asteroidJson = dateAsteroidJsonArray.getJSONObject(i)
            val id = asteroidJson.getString("id")
            val codename = asteroidJson.getString("name")
            val absoluteMagnitude = asteroidJson.getDouble("absolute_magnitude_h")
            val estimatedDiameter = asteroidJson.getJSONObject("estimated_diameter")
                .getJSONObject("kilometers").getDouble("estimated_diameter_max")

            val closeApproachData = asteroidJson
                .getJSONArray("close_approach_data").getJSONObject(0)
            val relativeVelocity = closeApproachData.getJSONObject("relative_velocity")
                .getDouble("kilometers_per_second")
            val distanceFromEarth = closeApproachData.getJSONObject("miss_distance")
                .getDouble("astronomical")
            val isPotentiallyHazardous = asteroidJson
                .getBoolean("is_potentially_hazardous_asteroid")

            val asteroid = SmobUser(id, codename, smobUserEntry, absoluteMagnitude,
                estimatedDiameter, relativeVelocity, distanceFromEarth, isPotentiallyHazardous)
            smobUserList.add(asteroid)
        }
    }

    return smobUserList
}

*/
