package com.create.custom.qr

import android.os.Parcel
import android.os.Parcelable

enum class QrType : Parcelable {
    Barcode,
    Website,
    Wifi,
    Email,
    Contact,
    Text,
    Phone,
    Message,
    Location,
    Address,
    Event,
    Demo;

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<QrType> {
            override fun createFromParcel(parcel: Parcel): QrType {
                return entries[parcel.readInt()]
            }

            override fun newArray(size: Int): Array<QrType?> {
                return arrayOfNulls(size)
            }
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(ordinal)
    }

    override fun describeContents(): Int = 0
}