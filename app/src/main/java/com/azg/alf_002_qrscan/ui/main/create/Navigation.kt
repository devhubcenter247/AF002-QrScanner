package com.azg.alf_002_qrscan.ui.main.create

import android.net.Uri

sealed class CreateNavigator(val router: String) {
    object InputDataScreen : CreateNavigator("input_data_screen")
    object CreateQrScreen : CreateNavigator("create_qr_screen/{content}") {
        fun createRouter(content: String): String =
            "create_qr_screen/${Uri.encode(content)}"   // rất quan trọng
    }
    object EditQrScreen : CreateNavigator("edit_qr_screen")
    object SaveQrSuccessScreen : CreateNavigator("save_qr_success_screen")
}