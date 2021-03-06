package com.atin.hafalan

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

//TODO 7 Buat sebuah kelas model

@Parcelize
data class SuratContract(
    val id: Long?,
    val no: String,
    val nama: String,
    val asma: String,
    val ayat: String?,
    val arti: String,
    val play: Boolean,
    val download: Boolean
) : Parcelable {
    companion object{
        const val TABLE_SURAT = "table_surat"
        const val ID = "id"
        const val NO = "no"
        const val NAMA = "nama"
        const val ASMA = "asma"
        const val AYAT = "ayat"
        const val ARTI = "arti"
        const val PLAY = "play"
        const val DOWNLOAD = "download"
    }
}