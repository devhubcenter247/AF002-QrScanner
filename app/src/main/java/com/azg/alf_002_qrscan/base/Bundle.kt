@file:Suppress("DEPRECATION")

package com.azg.alf_002_qrscan.base

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.Size
import android.util.SizeF
import androidx.activity.result.ActivityResult
import androidx.core.content.IntentCompat
import androidx.core.os.BundleCompat
import androidx.fragment.app.Fragment
import java.io.Serializable

inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> BundleCompat.getParcelable(
        this,
        key,
        T::class.java
    )
    else -> getParcelable(key) as? T
}

inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> IntentCompat.getParcelableExtra(
        this,
        key,
        T::class.java
    )
    else -> getParcelableExtra(key) as? T
}

inline fun <reified T : Parcelable> Intent.parcelableList(key: String): List<T>? = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> IntentCompat.getParcelableArrayListExtra(
        this,
        key,
        T::class.java
    )
    else -> getParcelableArrayListExtra(key)
}

inline fun <reified T : Parcelable> Bundle.parcelableList(key: String): List<T>? = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> BundleCompat.getParcelableArrayList(
        this,
        key,
        T::class.java
    )
    else -> getParcelableArrayList(key)
}

inline fun <reified T : Serializable> Bundle.serializable(key: String): T? = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> BundleCompat.getSerializable(
        this,
        key,
        T::class.java
    )
    else -> getSerializable(key) as? T
}

inline fun <reified T : Serializable> Intent.serializable(key: String): T? = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> IntentCompat.getSerializableExtra(
        this,
        key,
        T::class.java
    )
    else -> getSerializableExtra(key) as? T
}

fun Bundle.putAnySafe(key: String, value: Any?) {
    when (value) {
        null -> putSerializable(key, null as Serializable?)
        is Int -> putInt(key, value)
        is Long -> putLong(key, value)
        is Boolean -> putBoolean(key, value)
        is Float -> putFloat(key, value)
        is Double -> putDouble(key, value)
        is Short -> putShort(key, value)
        is Byte -> putByte(key, value)
        is Char -> putChar(key, value)
        is String -> putString(key, value)
        is CharSequence -> putCharSequence(key, value)
        is Bundle -> putBundle(key, value)
        is Parcelable -> putParcelable(key, value)
        is Serializable -> putSerializable(key, value)
        is List<*> -> {
            when {
                value.isEmpty() -> putStringArrayList(key, arrayListOf())
                value.all { it is Parcelable } -> {
                    putParcelableArrayList(
                        key,
                        ArrayList(value as List<Parcelable>)
                    )
                }
                value.all { it is String } -> {
                    putStringArrayList(key, ArrayList(value as List<String>))
                }
                value.all { it is CharSequence } -> {
                    putCharSequenceArrayList(
                        key,
                        ArrayList(value as List<CharSequence>)
                    )
                }
                value.all { it is Serializable } -> {
                    putSerializable(key, ArrayList(value as List<*>))
                }
                else -> {
                    throw IllegalArgumentException("Unsupported mixed List for key=\"$key\"")
                }
            }
        }
        is Size -> putSize(key, value)
        is SizeF -> putSizeF(key, value)
        else -> throw IllegalArgumentException(
            "Unsupported type for key=\"$key\": ${value::class.java.name}"
        )
    }
}

fun Intent.putExtraSmart(key: String, v: Any?) {
    when (v) {
        null -> putExtra(key, null as Serializable?)
        is Int -> putExtra(key, v)
        is Long -> putExtra(key, v)
        is Boolean -> putExtra(key, v)
        is Float -> putExtra(key, v)
        is Double -> putExtra(key, v)
        is Short -> putExtra(key, v)
        is Byte -> putExtra(key, v)
        is Char -> putExtra(key, v)
        is String -> putExtra(key, v)
        is CharSequence -> putExtra(key, v)
        is Bundle -> putExtra(key, v)
        is Parcelable -> putExtra(key, v)
        is Serializable -> putExtra(key, v)
        else -> throw IllegalArgumentException("Unsupported extra type for key=\"$key\": ${v::class.java}")
    }
}

inline fun <reified T : Any?> Activity.getData(key: String?): T? =
    intent?.safeGet<T>(key)

fun Boolean?.orFalse() = this == true

inline fun <reified T : Any> ActivityResult.getResultData(key: String): T? =
    data?.safeGet<T>(key)

inline fun <reified T : Any?> Intent.safeGet(key: String?): T? {
    if (key.isNullOrEmpty()) return null
    val extras = extras ?: return null
    if (!extras.containsKey(key)) return null

    when (T::class) {
        Int::class -> return getIntExtra(key, 0) as T
        Long::class -> return getLongExtra(key, 0L) as T
        Boolean::class -> return getBooleanExtra(key, false) as T
        Float::class -> return getFloatExtra(key, 0f) as T
        Double::class -> return getDoubleExtra(key, 0.0) as T
        Char::class -> return getCharExtra(key, Char.MIN_VALUE) as T
        String::class -> return getStringExtra(key) as T
        CharSequence::class -> return getCharSequenceExtra(key) as T
    }
    if (Parcelable::class.java.isAssignableFrom(T::class.java)) {
        return if (Build.VERSION.SDK_INT >= 33) {
            getParcelableExtra(key, T::class.java as Class<out Parcelable>) as T?
        } else {
            getParcelableExtra(key) as? T
        }
    }
    if (Serializable::class.java.isAssignableFrom(T::class.java)) {
        return if (Build.VERSION.SDK_INT >= 33) {
            getSerializableExtra(key, T::class.java as Class<out Serializable>) as T?
        } else {
            getSerializableExtra(key) as? T
        }
    }

    when (T::class.java) {
        IntArray::class.java -> return getIntArrayExtra(key) as T?
        LongArray::class.java -> return getLongArrayExtra(key) as T?
        BooleanArray::class.java -> return getBooleanArrayExtra(key) as T?
        FloatArray::class.java -> return getFloatArrayExtra(key) as T?
        DoubleArray::class.java -> return getDoubleArrayExtra(key) as T?
        CharArray::class.java -> return getCharArrayExtra(key) as T?
        ByteArray::class.java -> return getByteArrayExtra(key) as T?
        Array<String>::class.java -> return getStringArrayExtra(key) as T?
        Array<CharSequence>::class.java -> return getCharSequenceArrayExtra(key) as T?
    }
    runCatching {
        getStringArrayListExtra(key) as T?
            ?: getCharSequenceArrayListExtra(key) as T?
            ?: if (Build.VERSION.SDK_INT >= 33) {
                getParcelableArrayListExtra(key, Parcelable::class.java)?.let { it as T? }
            } else {
                getParcelableArrayListExtra<Parcelable>(key)?.let { it as T? }
            }
            ?: getIntegerArrayListExtra(key) as T?
    }.getOrNull()?.let { return it }

    return extras.get(key) as? T
}

inline fun <reified T : Any> Fragment.getData(key: String?): T? =
    arguments?.safeGet<T>(key)
inline fun <reified T : Any> Activity.getData(key: String, default: T): T =
    intent?.safeGet<T>(key) ?: default
inline fun <reified T : Any> Fragment.getData(key: String, default: T): T =
    arguments?.safeGet<T>(key) ?: default

inline fun <reified T : Any> Bundle.safeGet(key: String?): T? {
    if (key.isNullOrEmpty() || !containsKey(key)) return null


    when (T::class) {
        Int::class -> return getInt(key) as T
        Long::class -> return getLong(key) as T
        Boolean::class -> return getBoolean(key) as T
        Float::class -> return getFloat(key) as T
        Double::class -> return getDouble(key) as T
        Char::class -> return getChar(key) as T
        String::class -> return getString(key) as T
        CharSequence::class -> return getCharSequence(key) as T
        Bundle::class -> return getBundle(key) as T
    }

    if (Parcelable::class.java.isAssignableFrom(T::class.java)) {
        return if (Build.VERSION.SDK_INT >= 33) {
            getParcelable(key, T::class.java as Class<out Parcelable>) as T?
        } else {
            getParcelable<Parcelable>(key) as? T
        }
    }
    if (Serializable::class.java.isAssignableFrom(T::class.java)) {
        return if (Build.VERSION.SDK_INT >= 33) {
            getSerializable(key, T::class.java as Class<out Serializable>) as T?
        } else {
            getSerializable(key) as? T
        }
    }


    when (T::class.java) {
        IntArray::class.java -> return getIntArray(key) as T?
        LongArray::class.java -> return getLongArray(key) as T?
        BooleanArray::class.java -> return getBooleanArray(key) as T?
        FloatArray::class.java -> return getFloatArray(key) as T?
        DoubleArray::class.java -> return getDoubleArray(key) as T?
        CharArray::class.java -> return getCharArray(key) as T?
        ByteArray::class.java -> return getByteArray(key) as T?
        Array<String>::class.java -> return getStringArray(key) as T?
        Array<CharSequence>::class.java -> return getCharSequenceArray(key) as T?
        Array<Parcelable>::class.java -> return getParcelableArray(key) as T?
    }
    runCatching {
        getStringArrayList(key) as T?
            ?: getCharSequenceArrayList(key) as T?
            ?: getIntegerArrayList(key) as T?
            ?: if (Build.VERSION.SDK_INT >= 33) {
                getParcelableArrayList(key, Parcelable::class.java)?.let { it as T? }
            } else {
                getParcelableArrayList<Parcelable>(key)?.let { it as T? }
            }
    }.getOrNull()?.let { return it }

    return get(key) as? T
}

