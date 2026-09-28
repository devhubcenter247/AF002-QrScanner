package com.azg.alf_002_qrscan.model

import com.create.custom.qr.QrType

data class ResultScan(
    val type: QrType,
    val value: String,
    val listContent: List<String>,
)
