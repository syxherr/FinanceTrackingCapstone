package com.example.myscan.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ScanModel(
    val id: String = "",
    val resultScan: String = "",
    val image: String = "",
    val timestamp: Long = 0
) : Parcelable

