package com.yxing.bean

import android.os.Parcel
import android.os.Parcelable

class ScanRect(val left: Int, val top: Int, val right: Int, val bottom: Int) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(left)
        parcel.writeInt(top)
        parcel.writeInt(right)
        parcel.writeInt(bottom)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ScanRect> {
        override fun createFromParcel(parcel: Parcel): ScanRect {
            return ScanRect(parcel)
        }

        override fun newArray(size: Int): Array<ScanRect?> {
            return arrayOfNulls(size)
        }
    }

}