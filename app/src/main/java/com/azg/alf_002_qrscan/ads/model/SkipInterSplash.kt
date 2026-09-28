package com.azg.alf_002_qrscan.ads.model

import com.google.gson.annotations.SerializedName

data class SkipInterSplash(
    @SerializedName("enable")
    val enable: Boolean = true,
    @SerializedName("time_out_ms")
    val timeOutMS : Long = 0L
){
    companion object{
        val defaultData = SkipInterSplash(
            true,
            5000
        )
    }
}