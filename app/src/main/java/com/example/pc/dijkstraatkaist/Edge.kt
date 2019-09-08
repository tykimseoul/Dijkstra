package com.example.pc.dijkstraatkaist

import android.arch.persistence.room.*
import com.naver.maps.geometry.LatLng

@Entity(tableName = "edge_table")
data class Edge(
    @PrimaryKey @ColumnInfo(name = "id") val id: Int,
    @Embedded(prefix = "first_")
    val first: Node,
    @Embedded(prefix = "second_")
    val second: Node
) {
    val length: Double
        get() = first.coordinates.distanceTo(second.coordinates)
    @Ignore
    var highlight: Boolean = false

    override fun toString(): String {
        return "Edge($id, ${first.idx} <=> ${second.idx}, $length m)"
    }

    override fun equals(other: Any?): Boolean {
        if (other as? Edge == null) {
            return false
        }
        return ((first == other.first) and (second == other.second)) or ((first == other.second) and (second == other.first))
    }
}

@Dao
interface EdgeDAO {
    @Query("SELECT * from edge_table ORDER BY id ASC")
    fun getAllEdges(): List<Edge>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(edge: Edge)

    @Query("DELETE FROM edge_table")
    fun clear()
}

class Converters {
    @TypeConverter
    fun latLngToString(value: LatLng?): String? {
        return value?.let { "${it.latitude}, ${it.longitude}" }
    }

    @TypeConverter
    fun stringToLatLng(latLng: String?): LatLng? {
        return latLng?.split(",")?.let { LatLng(it[0].toDouble(), it[1].toDouble()) }
    }
}