package com.atin.hafalan

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

//TODO 7 Buat sebuah kelas model

@Parcelize
data class SuratContract(
    val id: Long?,
    val no: String,
    val nama: String,
    val arti: String,
    val ayat: String?,
    val play: Boolean
) : Parcelable {
    companion object{
        const val TABLE_SURAT = "table_surat"
        const val ID = "id"
        const val NO = "no"
        const val NAMA = "nama"
        const val ARTI = "arti"
        const val AYAT = "ayat"
        const val PLAY = "play"
    }
}