package com.androiddevs.mvvmnewsapp.db

import androidx.room.TypeConverter
import com.androiddevs.mvvmnewsapp.models.Source


class SourceConverter {

    @TypeConverter
    fun from(source: Source): String = source.name

    @TypeConverter
    fun to(name: String) = Source(name, name)
}