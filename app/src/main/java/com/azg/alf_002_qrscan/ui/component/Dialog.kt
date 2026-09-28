package com.azg.alf_002_qrscan.ui.component

import android.annotation.SuppressLint
import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices.PIXEL_4_XL
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.azg.alf_002_qrscan.R
import com.azg.alf_002_qrscan.app.rateApp
import com.azg.alf_002_qrscan.base.AppDialog
import com.azg.alf_002_qrscan.style.AppViewTheme
import com.azg.alf_002_qrscan.style.InputValue
import com.azg.alf_002_qrscan.style.RatingBar
import com.azg.alf_002_qrscan.style.SpaceH
import com.azg.alf_002_qrscan.style.SpaceW
import com.azg.alf_002_qrscan.style.TextView
import com.azg.alf_002_qrscan.style.black
import com.azg.alf_002_qrscan.style.gray
import com.azg.alf_002_qrscan.style.lightGrey
import com.azg.alf_002_qrscan.style.onClick
import com.azg.alf_002_qrscan.style.opacity
import com.azg.alf_002_qrscan.style.paddingEnd
import com.azg.alf_002_qrscan.style.paddingStart
import com.azg.alf_002_qrscan.style.white
import com.azg.alf_002_qrscan.utils.QrPrintLayout
import com.azg.alf_002_qrscan.widget.color86869c
import com.sdp.ssp.android.Sdp
import com.sdp.ssp.android.Ssp

@Preview(device = PIXEL_4_XL)
@Composable
fun PreviewDialog() {
    AppViewTheme {
        Column {
            DialogQuitApp(true, ColorTheme.BLUE,{}) {
            }
//            SpaceH(18.Sdp)
//            DatePickerDialog(
//                onDismiss = { },
//                onDateSelected = {
//                    // handle selected date
//                }
//            )
        }
    }
}

@SuppressLint("AutoboxingStateCreation")
@Composable
fun RatingDialog(
      activity: Activity,
      theme: ColorTheme = ColorTheme.BLUE,
      isShowRate: Boolean = false,
      onDismiss: () -> Unit = {},
      rateApp: () -> Unit,
      sendEmailFeedback: () -> Unit
) {
    var rating by remember { mutableIntStateOf(0) }
    val titleRate by remember(rating) {
        mutableIntStateOf(
            when (rating) {
                1, 2 -> R.string.rating_title_1_2
                3 -> R.string.rating_title_3
                4 -> R.string.rating_title_4
                5 -> R.string.rating_title_5
                else -> R.string.rating_title_0
            }
        )
    }
    val contentRate by remember(rating) {
        mutableIntStateOf(
            when (rating) {
                1, 2 -> R.string.rating_description_1_2
                3 -> R.string.rating_description_3
                4 -> R.string.rating_description_4
                5 -> R.string.rating_description_5
                else -> R.string.rating_description_0
            }
        )
    }
    if (isShowRate) {
        AppDialog(onDismiss = onDismiss) {
            Column(
                modifier = Modifier.padding(24.dp) .background(white),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(16.dp))

                Text(
                    text = stringResource(titleRate),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily(Font(R.font.rb_500)),
                    fontSize = 14.Ssp,
                    color = Color.Black
                )
                SpaceH(18.Sdp)
                Text(
                    text = stringResource(contentRate),
                    fontSize = 12.Ssp,
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily(Font(R.font.rb_400)),
                    color = Color.Gray,
                    minLines = 2
                )

                Spacer(Modifier.height(16.dp))

                RatingBar(
                    rating = rating,
                    onRatingChanged = { rating = it }
                )
                Spacer(Modifier.height(18.Sdp))
                TextView(
                    text = if (rating >= 4) stringResource(R.string.rate_on_google_play) else stringResource(
                        R.string.rate_us
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = theme.buttonColor, CircleShape
                        )
                        .padding(12.Sdp),
                    color = theme.colorText,
                ) {
                    when {
                        rating == 0 -> {
                            Toast.makeText(
                                activity,
                                activity.getString(R.string.please_rate_us),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        rating >= 4 -> {
                            activity.rateApp {
                                rateApp()
                            }
                            onDismiss()
                        }
                        else -> {
                            sendEmailFeedback()
                            onDismiss()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DialogQuitApp(isShowDialog: Boolean,theme: ColorTheme, onDismiss: () -> Unit, onQuit: () -> Unit) {
    if (isShowDialog) {
        AppDialog(onDismiss) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(white)
                    .padding(12.Sdp)
            ) {
                TextView(
                    stringResource(R.string.quit),
                    font = R.font.rb_600,
                    textSize = 14.Ssp,
                    color = black,
                    modifier = Modifier.fillMaxWidth()
                )
                SpaceH(12.Sdp)
                TextView(
                    stringResource(R.string.are_you_sure_want_to_quit_app),
                    font = R.font.rb_500,
                    textSize = 12.Ssp,
                    color = gray,
                    modifier = Modifier.fillMaxWidth()
                )
                SpaceH(12.Sdp)

                Row(Modifier.fillMaxWidth()) {
                    TextView(
                        stringResource(R.string.cancel),
                        color = color86869c,
                        font = R.font.rb_500,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .border(
                                1.dp, color86869c,
                                CircleShape
                            )
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(12.Sdp)
                    ) {
                        onDismiss.invoke()
                    }
                    SpaceW(16.Sdp)
                    TextView(
                        stringResource(R.string.ok),
                        color = theme.colorText,
                        font = R.font.rb_500,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .background(theme.buttonColor, CircleShape)
                            .weight(1f)
                            .padding(12.Sdp)
                    ) {
                        onQuit.invoke()
                    }
                }
            }
        }
    }
}

@Composable
fun DialogDeleteConfirm(
      isShowDialog: Boolean,
      onDismiss: () -> Unit,
      onConfirm: () -> Unit
) {
    if (isShowDialog) {
        AppDialog(onDismiss) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(white)
                    .padding(12.Sdp)
            ) {
                TextView(
                    stringResource(R.string.delete),
                    font = R.font.rb_600,
                    textSize = 14.Ssp,
                    color = black,
                    modifier = Modifier.fillMaxWidth()
                )
                SpaceH(12.Sdp)
                TextView(
                    stringResource(R.string.are_you_sure_want_to_delete),
                    font = R.font.rb_500,
                    textSize = 12.Ssp,
                    color = color86869c,
                    modifier = Modifier.fillMaxWidth()
                )
                SpaceH(12.Sdp)

                Row(Modifier.fillMaxWidth()) {
                    TextView(
                        stringResource(R.string.cancel),
                        color = color86869c,
                        font = R.font.rb_500,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .border(1.dp, color86869c, CircleShape)
                            .weight(1f)
                            .padding(12.Sdp)
                    ) {
                        onDismiss.invoke()
                    }
                    SpaceW(10.Sdp)
                    TextView(
                        stringResource(R.string.delete),
                        color = white,
                        font = R.font.rb_500,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .background(Color(0xFFE53935), CircleShape)
                            .weight(1f)
                            .padding(12.Sdp)
                    ) {
                        onConfirm.invoke()
                        onDismiss.invoke()
                    }
                }
            }
        }
    }
}

@Composable
fun DialogRenameQr(
    isShowDialog: Boolean,
    initialName: String,
    theme: ColorTheme,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    if (isShowDialog) {
        var name by remember(initialName) { mutableStateOf(initialName) }
        val submit = {
            onConfirm(name.trim())
            onDismiss()
        }

        AppDialog(onDismiss) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(white)
                    .padding(12.Sdp)
            ) {
                TextView(
                    stringResource(R.string.rename_qr),
                    font = R.font.rb_600,
                    textSize = 14.Ssp,
                    color = black,
                    modifier = Modifier.fillMaxWidth()
                )
                SpaceH(12.Sdp)
                InputValue(
                    value = name,
                    hint = stringResource(R.string.enter_qr_name),
                    maxLines = 1,
                    textSize = 13.Ssp,
                    boxModifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, lightGrey, RoundedCornerShape(10.Sdp))
                        .background(white, RoundedCornerShape(10.Sdp)),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { submit() }),
                    onValueChange = { name = it }
                )
                SpaceH(12.Sdp)

                Row(Modifier.fillMaxWidth()) {
                    TextView(
                        stringResource(R.string.cancel),
                        color = color86869c,
                        font = R.font.rb_500,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .border(1.dp, color86869c, CircleShape)
                            .weight(1f)
                            .padding(12.Sdp)
                    ) {
                        onDismiss.invoke()
                    }
                    SpaceW(10.Sdp)
                    TextView(
                        stringResource(R.string.save),
                        color = theme.colorText,
                        font = R.font.rb_500,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .background(theme.buttonColor, CircleShape)
                            .weight(1f)
                            .padding(12.Sdp)
                    ) {
                        submit()
                    }
                }
            }
        }
    }
}
