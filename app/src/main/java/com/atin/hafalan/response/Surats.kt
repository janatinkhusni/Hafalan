package com.atin.hafalan.response

import com.google.gson.annotations.SerializedName

data class Surats(
        @SerializedName("hasil") var hasil: List<Surat>)