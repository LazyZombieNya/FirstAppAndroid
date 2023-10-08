package ru.netology.nmedia.util

import android.os.Bundle
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

object LongArg: ReadWriteProperty<Bundle, Long?> {

    override fun setValue(thisRef: Bundle, property: KProperty<*>, value: Long?) {
        thisRef.putString(property.name, value.toString())
    }

    override fun getValue(thisRef: Bundle, property: KProperty<*>): Long? =
        thisRef.getLong(property.name)



}