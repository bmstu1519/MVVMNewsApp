package com.zooro.mvvmnewsapp.data.db

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun fromSource(newsSource: NewsSourceDto): String = newsSource.name

    @TypeConverter
    fun toSource(name: String) : NewsSourceDto = NewsSourceDto(name,name)
}