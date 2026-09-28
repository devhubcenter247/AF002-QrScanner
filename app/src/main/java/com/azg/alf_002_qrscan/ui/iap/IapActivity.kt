package com.azg.alf_002_qrscan.ui.iap

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.azg.alf_002_qrscan.AppQrScanner
import com.azg.alf_002_qrscan.R
import com.azg.alf_002_qrscan.app.AppPreferences
import com.azg.alf_002_qrscan.base.BaseActivity
import com.azg.alf_002_qrscan.style.AppViewTheme
import com.azg.alf_002_qrscan.style.BoxParentContent
import com.azg.alf_002_qrscan.style.IconView
import com.azg.alf_002_qrscan.style.SpaceH
import com.azg.alf_002_qrscan.style.SpaceW
import com.azg.alf_002_qrscan.style.TextView
import com.azg.alf_002_qrscan.style.black
import com.azg.alf_002_qrscan.style.disabled
import com.azg.alf_002_qrscan.style.gray
import com.azg.alf_002_qrscan.style.onClick
import com.azg.alf_002_qrscan.style.opacity
import com.azg.alf_002_qrscan.style.statusBarPadding
import com.azg.alf_002_qrscan.style.white
import com.azg.alf_002_qrscan.ui.component.ColorTheme
import com.azg.alf_002_qrscan.ui.component.SimpleBackTopBar
import com.lib.ads.application.ads.billing.AppPurchase
import com.lib.ads.application.ads.billing.PurchaseListener
import com.sdp.ssp.android.Sdp
import com.sdp.ssp.android.Ssp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class IapUiState(
    val billingReady: Boolean = false,
    val productId: String = "",
    val trialDays: Int = 0,
    val hasTrial: Boolean = false,
    val introPrice: String = "",
    val introCycles: Int = 0,
    val hasIntroPrice: Boolean = false,
    val regularPrice: String = "",
    val billingPeriod: String = "",
    val promoOfferToken: String = "",
    val baseOfferToken: String = "",
    val isLoading: Boolean = false,
    val isPurchased: Boolean = false,
    val errorMessage: String = ""
)

class IapViewModel : ViewModel() {
    private val _state = MutableStateFlow(IapUiState())
    val state: StateFlow<IapUiState> = _state.asStateFlow()

    init {
        loadBillingData()
    }

    private fun loadBillingData() {
        AppPurchase.getInstance().setBillingListener(billingListener = { _ ->
            val products = AppPurchase.getInstance().getProductInfoList()
            Log.d("IapViewModel", "productInfoList: $products")

            val sub = products.firstOrNull { it.productId == AppQrScanner.PRODUCT_DISCOUNT_FM }
                ?: products.firstOrNull { it.productId == AppQrScanner.PRODUCT_MONTHLY_SUB }
                ?: products.firstOrNull { it.type == AppPurchase.TYPE_IAP.SUBSCRIPTION }

            Log.d("IapViewModel", "selected sub: $sub")

            if (sub != null) {
                _state.update {
                    it.copy(
                        billingReady = true,
                        productId = sub.productId,
                        hasTrial = sub.hasTrial,
                        trialDays = parsePeriodToDays(sub.trialPeriod),
                        hasIntroPrice = sub.hasIntroPrice,
                        introPrice = sub.introPrice,
                        introCycles = sub.introCycles,
                        regularPrice = sub.regularPrice,
                        billingPeriod = sub.billingPeriod,
                        promoOfferToken = sub.promoOfferToken,
                        baseOfferToken = sub.baseOfferToken,
                        isPurchased = AppPurchase.getInstance().isPurchased()
                    )
                }
            } else {
                _state.update { it.copy(billingReady = true) }
            }
        }, timeout = 5000)
    }

    fun subscribe(activity: IapActivity) {
        val st = _state.value
        if (st.productId.isEmpty()) return
        _state.update { it.copy(isLoading = true, errorMessage = "") }
        AppPurchase.getInstance().setPurchaseListener(object : PurchaseListener {
            override fun onProductPurchased(productId: String?, transactionDetails: String) {
                _state.update { it.copy(isLoading = false, isPurchased = true) }
                activity.finish()
            }

            override fun displayErrorMessage(errorMsg: String) {
                _state.update { it.copy(isLoading = false, errorMessage = errorMsg) }
            }

            override fun onUserCancelBilling() {
                _state.update { it.copy(isLoading = false) }
            }
        })
        AppPurchase.getInstance().subscribe(activity, st.productId)
    }

    fun restorePurchase(activity: IapActivity) {
        _state.update { it.copy(isLoading = true) }
        AppPurchase.getInstance().setUpdatePurchaseListener {
            val purchased = AppPurchase.getInstance().isPurchased()
            _state.update { it.copy(isLoading = false, isPurchased = purchased) }
            if (purchased) activity.finish()
        }
        AppPurchase.getInstance().updatePurchaseStatus()
    }

    private fun parsePeriodToDays(period: String): Int {
        if (period.isEmpty()) return 0
        val regex = Regex("P(?:(\\d+)Y)?(?:(\\d+)M)?(?:(\\d+)W)?(?:(\\d+)D)?")
        val match = regex.find(period) ?: return 0
        val years = match.groupValues[1].toIntOrNull() ?: 0
        val months = match.groupValues[2].toIntOrNull() ?: 0
        val weeks = match.groupValues[3].toIntOrNull() ?: 0
        val days = match.groupValues[4].toIntOrNull() ?: 0
        return years * 365 + months * 30 + weeks * 7 + days
    }
}

class IapActivity : BaseActivity() {
    override fun initialize() {}

    @Composable
    override fun BoxScope.ContentView() {
        val vm: IapViewModel = viewModel()
        val state by vm.state.collectAsState()
        val colorTheme by AppPreferences.themeApp.flow.collectAsStateWithLifecycle(ColorTheme.BLUE)
        BoxParentContent(
            onBack = { finish() },
            modifier = Modifier
                .fillMaxSize()
                .background(colorTheme.bgColor)
                .statusBarPadding()
        ) {
            IapScreen(
                state = state,
                colorTheme = colorTheme,
                onClose = { finish() },
                onSubscribe = { vm.subscribe(this@IapActivity) },
                onRestore = { vm.restorePurchase(this@IapActivity) }
            )
        }
    }
}

@Composable
fun IapScreen(
    state: IapUiState,
    colorTheme: ColorTheme = ColorTheme.BLUE,
    topPadding: Dp = 0.dp,
    onClose: () -> Unit = {},
    onSubscribe: () -> Unit = {},
    onRestore: () -> Unit = {}
) {
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SimpleBackTopBar(title = R.string.premium, onBack = onClose)

        Column(
            Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.Sdp)
                .padding(top = topPadding, bottom = 24.Sdp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SpaceH(24)

            Box(
                Modifier
                    .size(78.Sdp)
                    .clip(RoundedCornerShape(18.Sdp))
                    .background(white),
                contentAlignment = Alignment.Center
            ) {
                IconView(
                    item = R.drawable.ic_sparkle,
                    tint = colorTheme.buttonColor,
                    icSize = 34
                )
            }

            SpaceH(16)

            TextView(
                text = stringResource(R.string.iap_title),
                font = R.font.rb_700,
                textSize = 20.Ssp,
                color = black,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            SpaceH(8)
            TextView(
                text = stringResource(R.string.iap_subtitle),
                font = R.font.rb_400,
                textSize = 13.Ssp,
                color = gray,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            SpaceH(24)

            PricingCard(state = state, colorTheme = colorTheme)

            SpaceH(16)

            FeaturesCard(colorTheme = colorTheme)

            SpaceH(24)

            Box(
                Modifier
                    .fillMaxWidth()
                    .clip(CircleShape)
                    .background(if (!state.isLoading && state.billingReady) colorTheme.buttonColor else disabled)
                    .onClick(enable = !state.isLoading && state.billingReady) { onSubscribe() }
                    .padding(vertical = 16.Sdp),
                contentAlignment = Alignment.Center
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = white,
                        strokeWidth = 2.dp
                    )
                } else {
                    TextView(
                        text = ctaLabel(state),
                        textSize = 14.Ssp,
                        font = R.font.rb_600,
                        color = colorTheme.colorText,
                        textAlign = TextAlign.Center
                    )
                }
            }

            AnimatedVisibility(state.errorMessage.isNotEmpty()) {
                TextView(
                    text = state.errorMessage,
                    textSize = 12.Ssp,
                    font = R.font.rb_400,
                    color = Color(0xFFFF6B6B),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.Sdp)
                )
            }

            SpaceH(12)

            TextView(
                text = stringResource(R.string.iap_cancel_anytime),
                textSize = 12.Ssp,
                font = R.font.rb_400,
                color = gray,
                textAlign = TextAlign.Center
            )

            SpaceH(12)

            Row(horizontalArrangement = Arrangement.spacedBy(18.Sdp)) {
                TextView(
                    text = stringResource(R.string.iap_restore),
                    textSize = 12.Ssp,
                    font = R.font.rb_500,
                    color = colorTheme.buttonColor,
                    modifier = Modifier.onClick { onRestore() }
                )
                TextView(
                    text = stringResource(R.string.term_of_service),
                    textSize = 12.Ssp,
                    font = R.font.rb_500,
                    color = colorTheme.buttonColor
                )
                TextView(
                    text = stringResource(R.string.privacy_policy),
                    textSize = 12.Ssp,
                    font = R.font.rb_500,
                    color = colorTheme.buttonColor
                )
            }
        }
    }
}

@Composable
private fun ctaLabel(state: IapUiState): String = when {
    !state.billingReady -> stringResource(R.string.loading)
    state.hasTrial && state.trialDays > 0 -> stringResource(R.string.iap_start_trial, state.trialDays)
    state.hasIntroPrice -> stringResource(R.string.iap_get_intro_offer)
    else -> stringResource(R.string.iap_subscribe_now)
}

@Composable
private fun PricingCard(state: IapUiState, colorTheme: ColorTheme) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.Sdp))
            .background(white)
            .padding(16.Sdp)
    ) {
        TextView(
            text = stringResource(R.string.iap_billing_title),
            textSize = 14.Ssp,
            font = R.font.rb_600,
            color = black
        )
        SpaceH(12)

        if (state.billingReady) {
            if (state.hasTrial && state.trialDays > 0) {
                PricingPhaseRow(
                    dayLabel = stringResource(R.string.iap_today),
                    title = stringResource(R.string.iap_trial_days_free, state.trialDays),
                    subtitle = stringResource(R.string.iap_no_charge_trial),
                    accentColor = colorTheme.buttonColor
                )
                PhaseDivider()
            }

            if (state.hasIntroPrice && state.introPrice.isNotEmpty()) {
                val phaseLabel = if (state.hasTrial) {
                    stringResource(R.string.iap_day_number, state.trialDays + 1)
                } else {
                    stringResource(R.string.iap_today)
                }
                val cycleNote =
                    if (state.introCycles > 1) stringResource(R.string.iap_for_months, state.introCycles) else stringResource(R.string.iap_for_first_month)
                PricingPhaseRow(
                    dayLabel = phaseLabel,
                    title = stringResource(R.string.iap_price_period, state.introPrice, billingPeriodLabel(state.billingPeriod)),
                    subtitle = stringResource(R.string.iap_intro_offer, cycleNote),
                    accentColor = colorTheme.buttonColor
                )
                PhaseDivider()
            }

            val regularLabel = when {
                state.hasTrial && state.hasIntroPrice -> stringResource(R.string.iap_ongoing)
                state.hasTrial -> stringResource(R.string.iap_day_number, state.trialDays + 1)
                state.hasIntroPrice -> stringResource(R.string.iap_after_intro)
                else -> stringResource(R.string.iap_today)
            }
            PricingPhaseRow(
                dayLabel = regularLabel,
                title = if (state.regularPrice.isNotEmpty()) {
                    stringResource(R.string.iap_price_period, state.regularPrice, billingPeriodLabel(state.billingPeriod))
                } else {
                    stringResource(R.string.iap_regular_price)
                },
                subtitle = stringResource(R.string.iap_auto_renew),
                accentColor = colorTheme.buttonColor.opacity(0.55f)
            )
        } else {
            repeat(3) { i ->
                if (i > 0) PhaseDivider()
                PricingPhaseRow(
                    dayLabel = "...",
                    title = stringResource(R.string.iap_loading_price),
                    subtitle = "",
                    accentColor = gray.opacity(0.3f)
                )
            }
        }
    }
}

@Composable
private fun PricingPhaseRow(
    dayLabel: String,
    title: String,
    subtitle: String,
    accentColor: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(accentColor)
        )
        Spacer(Modifier.width(12.Sdp))
        Column(Modifier.weight(1f)) {
            TextView(
                text = dayLabel,
                textSize = 8.Ssp,
                font = R.font.rb_500,
                color = accentColor,
            )
            TextView(
                text = title,
                textSize = 14.Ssp,
                font = R.font.rb_600,
                color = black
            )
            if (subtitle.isNotEmpty()) {
                TextView(
                    text = subtitle,
                    textSize = 12.Ssp,
                    font = R.font.rb_400,
                    color = gray
                )
            }
        }
    }
}

@Composable
private fun PhaseDivider() {
    Row {
        Box(Modifier.width(10.dp), contentAlignment = Alignment.Center) {
            Box(
                Modifier
                    .width(1.dp)
                    .height(20.dp)
                    .background(gray.opacity(0.15f))
            )
        }
        Spacer(Modifier.weight(1f))
    }
}

@Composable
private fun FeaturesCard(colorTheme: ColorTheme) {
    val features = listOf(
        R.string.iap_feature_scan,
        R.string.iap_feature_create,
        R.string.iap_feature_customize,
        R.string.iap_feature_export,
        R.string.iap_feature_no_ads
    )
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.Sdp))
            .background(white)
            .padding(16.Sdp),
        verticalArrangement = Arrangement.spacedBy(12.Sdp)
    ) {
        features.forEach { FeatureRow(text = it, colorTheme = colorTheme) }
    }
}

@Composable
private fun FeatureRow(text: Int, colorTheme: ColorTheme) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(colorTheme.buttonColor.opacity(0.12f)),
            contentAlignment = Alignment.Center
        ) {
            IconView(
                item = R.drawable.ic_rounded_done,
                tint = colorTheme.buttonColor,
                icSize = 12
            )
        }
        SpaceW(12)
        TextView(
            text = stringResource(text),
            textSize = 14.Ssp,
            font = R.font.rb_400,
            color = black
        )
    }
}

@Composable
private fun billingPeriodLabel(period: String): String = when (period) {
    "P1W" -> stringResource(R.string.iap_period_week)
    "P1Y" -> stringResource(R.string.iap_period_year)
    else -> stringResource(R.string.iap_period_month)
}

@Preview(showBackground = true, device = Devices.PIXEL_4_XL)
@Composable
fun IapScreenPreview() {
    AppViewTheme {
        IapScreen(
            state = IapUiState(
                billingReady = true,
                productId = "monthly_sub",
                hasTrial = true,
                trialDays = 3,
                regularPrice = "$0.99",
                billingPeriod = "P1M"
            )
        )
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_4_XL)
@Composable
fun IapScreenIntroPreview() {
    AppViewTheme {
        IapScreen(
            state = IapUiState(
                billingReady = true,
                productId = "monthly_sub_intro",
                hasIntroPrice = true,
                introPrice = "$0.49",
                introCycles = 1,
                regularPrice = "$0.99",
                billingPeriod = "P1M"
            )
        )
    }
}
