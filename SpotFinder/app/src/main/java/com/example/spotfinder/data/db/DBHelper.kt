package com.example.spotfinder.data.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// simple model for saved place
data class LocationModel(
    val id: Long,
    val address: String,
    val latitude: Double,
    val longitude: Double
)

// handle database work for locations table
class DBHelper private constructor(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    // build table when db is new
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE $TABLE_LOCATIONS(" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_ADDRESS TEXT NOT NULL," +
                "$COLUMN_LAT REAL NOT NULL," +
                "$COLUMN_LON REAL NOT NULL" +
                ")"
        )
    }

    // reset table when version changes
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_LOCATIONS")
        onCreate(db)
    }

    // add a saved place
    fun insertLocation(model: LocationModel): Long {
        val values = ContentValues().apply {
            put(COLUMN_ADDRESS, model.address)
            put(COLUMN_LAT, model.latitude)
            put(COLUMN_LON, model.longitude)
        }
        return writableDatabase.insert(TABLE_LOCATIONS, null, values)
    }

    // update a saved place
    fun updateLocation(model: LocationModel): Int {
        val values = ContentValues().apply {
            put(COLUMN_ADDRESS, model.address)
            put(COLUMN_LAT, model.latitude)
            put(COLUMN_LON, model.longitude)
        }
        return writableDatabase.update(
            TABLE_LOCATIONS,
            values,
            "$COLUMN_ID = ?",
            arrayOf(model.id.toString())
        )
    }

    // remove a saved place
    fun deleteLocationById(id: Long): Int {
        return writableDatabase.delete(TABLE_LOCATIONS, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    // read all places sorted by address
    fun getAllLocations(): List<LocationModel> {
        val list = mutableListOf<LocationModel>()
        val cursor = readableDatabase.query(
            TABLE_LOCATIONS,
            null,
            null,
            null,
            null,
            null,
            "$COLUMN_ADDRESS COLLATE NOCASE ASC"
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(cursorToModel(it))
            }
        }
        return list
    }

    // read newest places for recent list
    fun getRecentLocations(limit: Int = 5): List<LocationModel> {
        val list = mutableListOf<LocationModel>()
        val cursor = readableDatabase.query(
            TABLE_LOCATIONS,
            null,
            null,
            null,
            null,
            null,
            "$COLUMN_ID DESC",
            limit.toString()
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(cursorToModel(it))
            }
        }
        return list
    }

    // find first place that matches search text
    fun getByAddressLike(query: String): LocationModel? {
        val cursor = readableDatabase.query(
            TABLE_LOCATIONS,
            null,
            "$COLUMN_ADDRESS LIKE ?",
            arrayOf("%$query%"),
            null,
            null,
            "$COLUMN_ID DESC",
            "1"
        )
        cursor.use {
            if (it.moveToFirst()) {
                return cursorToModel(it)
            }
        }
        return null
    }

    // convert cursor row to model
    private fun cursorToModel(cursor: android.database.Cursor): LocationModel {
        return LocationModel(
            id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
            address = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADDRESS)),
            latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LAT)),
            longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LON))
        )
    }

    // prefill database from static list if empty
    fun seedIfEmpty() {
        val cursor = readableDatabase.rawQuery("SELECT COUNT(*) FROM $TABLE_LOCATIONS", null)
        val isEmpty = cursor.use { it.moveToFirst() && it.getInt(0) == 0 }
        if (!isEmpty) {
            return
        }
        DEFAULT_LOCATIONS.forEach { insertLocation(it) }
    }

    companion object {
        private const val DATABASE_NAME = "spotfinder.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_LOCATIONS = "locations"
        const val COLUMN_ID = "id"
        const val COLUMN_ADDRESS = "address"
        const val COLUMN_LAT = "latitude"
        const val COLUMN_LON = "longitude"

        private val DEFAULT_LOCATIONS = listOf(
            LocationModel(0, "CN Tower, Toronto", 43.6426, -79.3871),
            LocationModel(0, "Ripley's Aquarium, Toronto", 43.6424, -79.3859),
            LocationModel(0, "Union Station, Toronto", 43.6450, -79.3807),
            LocationModel(0, "St. Lawrence Market, Toronto", 43.6487, -79.3716),
            LocationModel(0, "Royal Ontario Museum, Toronto", 43.6677, -79.3948),
            LocationModel(0, "Art Gallery of Ontario, Toronto", 43.6536, -79.3925),
            LocationModel(0, "Casa Loma, Toronto", 43.6780, -79.4094),
            LocationModel(0, "High Park, Toronto", 43.6465, -79.4637),
            LocationModel(0, "Distillery District, Toronto", 43.6503, -79.3596),
            LocationModel(0, "Toronto City Hall", 43.6525, -79.3837),
            LocationModel(0, "Nathan Phillips Square", 43.6524, -79.3830),
            LocationModel(0, "Eaton Centre, Toronto", 43.6544, -79.3807),
            LocationModel(0, "Yorkdale Shopping Centre", 43.7253, -79.4521),
            LocationModel(0, "Scarborough Town Centre", 43.7756, -79.2578),
            LocationModel(0, "Fairview Mall, Toronto", 43.7787, -79.3445),
            LocationModel(0, "Ontario Science Centre", 43.7169, -79.3389),
            LocationModel(0, "Toronto Zoo", 43.8177, -79.1859),
            LocationModel(0, "Rogers Centre, Toronto", 43.6414, -79.3893),
            LocationModel(0, "BMO Field, Toronto", 43.6332, -79.4180),
            LocationModel(0, "Exhibition Place, Toronto", 43.6340, -79.4145),
            LocationModel(0, "Harbourfront Centre, Toronto", 43.6387, -79.3821),
            LocationModel(0, "Billy Bishop Airport, Toronto", 43.6275, -79.3962),
            LocationModel(0, "Yonge-Dundas Square, Toronto", 43.6561, -79.3802),
            LocationModel(0, "Allan Gardens, Toronto", 43.6613, -79.3743),
            LocationModel(0, "Riverdale Park, Toronto", 43.6668, -79.3520),
            LocationModel(0, "Evergreen Brick Works, Toronto", 43.6840, -79.3647),
            LocationModel(0, "Sunnybrook Park, Toronto", 43.7285, -79.3631),
            LocationModel(0, "Edwards Gardens, Toronto", 43.7338, -79.3630),
            LocationModel(0, "Guild Park and Gardens, Toronto", 43.7416, -79.1657),
            LocationModel(0, "Bluffer's Park, Scarborough", 43.7158, -79.2315),
            LocationModel(0, "Scarborough Bluffs Park", 43.7031, -79.2355),
            LocationModel(0, "The Beaches, Toronto", 43.6686, -79.2960),
            LocationModel(0, "Woodbine Beach, Toronto", 43.6655, -79.3015),
            LocationModel(0, "Humber Bay Park, Toronto", 43.6174, -79.4780),
            LocationModel(0, "Trillium Park, Toronto", 43.6290, -79.4075),
            LocationModel(0, "Sugar Beach, Toronto", 43.6435, -79.3704),
            LocationModel(0, "Toronto Islands Ferry", 43.6411, -79.3762),
            LocationModel(0, "Fort York National Historic Site", 43.6373, -79.4070),
            LocationModel(0, "Hockey Hall of Fame, Toronto", 43.6466, -79.3770),
            LocationModel(0, "Scotiabank Arena", 43.6435, -79.3791),
            LocationModel(0, "Rosedale Valley", 43.6750, -79.3750),
            LocationModel(0, "Queen's Park, Toronto", 43.6629, -79.3923),
            LocationModel(0, "University of Toronto St. George", 43.6629, -79.3957),
            LocationModel(0, "Ryerson University", 43.6577, -79.3788),
            LocationModel(0, "George Brown College Waterfront", 43.6440, -79.3687),
            LocationModel(0, "Centennial College Progress Campus", 43.7846, -79.2263),
            LocationModel(0, "Sheridan College Hazel McCallion", 43.5911, -79.6441),
            LocationModel(0, "Square One, Mississauga", 43.5934, -79.6417),
            LocationModel(0, "Mississauga Celebration Square", 43.5890, -79.6441),
            LocationModel(0, "Port Credit, Mississauga", 43.5526, -79.5871),
            LocationModel(0, "Streetsville, Mississauga", 43.5805, -79.7130),
            LocationModel(0, "Erin Mills Town Centre", 43.5567, -79.7127),
            LocationModel(0, "Oakville Harbour", 43.4441, -79.6667),
            LocationModel(0, "Downtown Oakville", 43.4463, -79.6667),
            LocationModel(0, "Brampton City Hall", 43.6833, -79.7606),
            LocationModel(0, "Gage Park, Brampton", 43.6840, -79.7585),
            LocationModel(0, "Bramalea City Centre", 43.7131, -79.7210),
            LocationModel(0, "Vaughan Mills", 43.8252, -79.5383),
            LocationModel(0, "Canada's Wonderland", 43.8430, -79.5390),
            LocationModel(0, "Markville Mall, Markham", 43.8644, -79.3005),
            LocationModel(0, "Downtown Markham", 43.8561, -79.3349),
            LocationModel(0, "Pacific Mall, Markham", 43.8258, -79.3059),
            LocationModel(0, "Richmond Hill Centre", 43.8429, -79.4331),
            LocationModel(0, "Aurora GO Station", 44.0065, -79.4505),
            LocationModel(0, "Newmarket Riverwalk Commons", 44.0565, -79.4594),
            LocationModel(0, "Pickering Town Centre", 43.8355, -79.0860),
            LocationModel(0, "Ajax Waterfront Park", 43.8173, -79.0930),
            LocationModel(0, "Whitby Harbour", 43.8686, -78.9410),
            LocationModel(0, "Oshawa Centre", 43.8975, -78.8636),
            LocationModel(0, "Downtown Oshawa", 43.8976, -78.8658),
            LocationModel(0, "Bowmanville Harbour", 43.9001, -78.6805),
            LocationModel(0, "Milton Town Hall", 43.5083, -79.8774),
            LocationModel(0, "Downtown Milton", 43.5178, -79.8829),
            LocationModel(0, "Georgetown Market Place", 43.6550, -79.9203),
            LocationModel(0, "Caledon Village", 43.8626, -79.8829),
            LocationModel(0, "Bolton, Caledon", 43.8743, -79.7356),
            LocationModel(0, "Orangeville Downtown", 43.9206, -80.0943),
            LocationModel(0, "Uxbridge Downtown", 44.1103, -79.1201),
            LocationModel(0, "Stouffville GO Station", 43.9700, -79.2500),
            LocationModel(0, "Bradford GO Station", 44.1147, -79.5620),
            LocationModel(0, "Keswick, Georgina", 44.2205, -79.4671),
            LocationModel(0, "Sutton, Georgina", 44.3040, -79.3639),
            LocationModel(0, "Innisfil Beach Park", 44.3054, -79.5659),
            LocationModel(0, "Barrie Waterfront", 44.3894, -79.6903),
            LocationModel(0, "Downtown Barrie", 44.3894, -79.6900),
            LocationModel(0, "Collingwood Harbour", 44.5008, -80.2169),
            LocationModel(0, "Wasaga Beach", 44.4981, -80.0160),
            LocationModel(0, "Niagara-on-the-Lake", 43.2557, -79.0713),
            LocationModel(0, "Niagara Falls Clifton Hill", 43.0896, -79.0849),
            LocationModel(0, "Burlington Waterfront", 43.3247, -79.7963),
            LocationModel(0, "Downtown Burlington", 43.3256, -79.7990),
            LocationModel(0, "Hamilton Waterfront", 43.2683, -79.8661),
            LocationModel(0, "McMaster University", 43.2617, -79.9192),
            LocationModel(0, "Mohawk College", 43.2380, -79.8880),
            LocationModel(0, "Dundas Valley", 43.2667, -79.9500),
            LocationModel(0, "Stoney Creek", 43.2177, -79.7663),
            LocationModel(0, "Grimsby Waterfront", 43.2009, -79.5604),
            LocationModel(0, "Port Dalhousie, St. Catharines", 43.2187, -79.2673),
            LocationModel(0, "Downtown St. Catharines", 43.1594, -79.2469),
            LocationModel(0, "Brock University", 43.1188, -79.2477),
            LocationModel(0, "Welland Canal", 42.9916, -79.2496),
            LocationModel(0, "Cambridge City Hall", 43.3601, -80.3144),
            LocationModel(0, "Downtown Guelph", 43.5448, -80.2482),
            LocationModel(0, "University of Guelph", 43.5316, -80.2264),
            LocationModel(0, "Waterloo City Hall", 43.4668, -80.5224),
            LocationModel(0, "University of Waterloo", 43.4723, -80.5449),
            LocationModel(0, "Wilfrid Laurier University", 43.4780, -80.5258),
            LocationModel(0, "Conestoga Mall, Waterloo", 43.4981, -80.5270),
            LocationModel(0, "Kitchener City Hall", 43.4516, -80.4925)
        )

        @Volatile
        private var instance: DBHelper? = null

        // give shared helper instance
        fun getInstance(context: Context): DBHelper {
            return instance ?: synchronized(this) {
                val helper = instance ?: DBHelper(context.applicationContext).also { instance = it }
                helper.seedIfEmpty()
                helper
            }
        }
    }
}
