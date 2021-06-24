package com.zooro.mvvmnewsapp.db

import androidx.room.TypeConverter
import com.zooro.mvvmnewsapp.models.Source

class Converters {

    @TypeConverter
    fun fromSource(source: Source): String = source.name

    @TypeConverter
    fun toSource(name: String) : Source = Source(name,name)
}