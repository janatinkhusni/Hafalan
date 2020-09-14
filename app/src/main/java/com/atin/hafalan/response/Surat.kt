package com.atin.hafalan.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Surat(
        @SerializedName("nomor") var nomor: String?,
        @SerializedName("nama") var nama: String,
        @SerializedName("ayat") var ayat: String,
        @SerializedName("arti") var arti: String
) : Parcelable
