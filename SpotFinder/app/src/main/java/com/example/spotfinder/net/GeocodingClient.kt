package com.example.spotfinder.net

import java.net.URLEncoder
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

// talk to positionstack api for geocoding
object GeocodingClient {
    private val httpClient = OkHttpClient()
    private const val BASE_URL = "http://api.positionstack.com/v1/forward"

    // call api and return lat lon or null when it fails
    fun geocodeAddress(accessKey: String, address: String): Pair<Double, Double>? {
        if (accessKey.isBlank()) return null
        val encodedQuery = URLEncoder.encode(address.trim(), Charsets.UTF_8.name())
        val url = "$BASE_URL?access_key=${accessKey.trim()}&query=$encodedQuery&limit=1&country=CA"
        val request = Request.Builder().url(url).get().build()
        return try {
            httpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return null
                val body = response.body?.string() ?: return null
                val json = JSONObject(body)
                val data = json.optJSONArray("data") ?: return null
                if (data.length() == 0) return null
                val first = data.getJSONObject(0)
                val lat = first.optDouble("latitude")
                val lon = first.optDouble("longitude")
                if (lat == 0.0 && lon == 0.0 && !first.has("latitude")) return null
                Pair(lat, lon)
            }
        } catch (_: Exception) {
            null
        }
    }
}
