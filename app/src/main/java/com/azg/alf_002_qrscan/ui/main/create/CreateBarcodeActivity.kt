package com.azg.alf_002_qrscan.ui.main.create

import android.graphics.Bitmap
import android.os.Environment
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.azg.alf_002_qrscan.R
import com.azg.alf_002_qrscan.ads.wrapper.native.NativePlacement
import com.azg.alf_002_qrscan.ads.wrapper.native.view.NativeAdByLayout
import com.azg.alf_002_qrscan.ads.wrapper.native.view.NativeShimmerByLayout
import com.azg.alf_002_qrscan.ads.wrapper.native.view.ShimmerMediaLeftComposeView
import com.azg.alf_002_qrscan.ads.wrapper.reward.RewardPlacement
import com.azg.alf_002_qrscan.app.toastShort
import com.azg.alf_002_qrscan.base.BaseActivity
import com.azg.alf_002_qrscan.base.getData
import com.azg.alf_002_qrscan.style.AppViewTheme
import com.azg.alf_002_qrscan.style.ColumnParentContent
import com.azg.alf_002_qrscan.style.ConstraintParentContent
import com.azg.alf_002_qrscan.style.SpaceH
import com.azg.alf_002_qrscan.style.TextView
import com.azg.alf_002_qrscan.style.black
import com.azg.alf_002_qrscan.style.opacity
import com.azg.alf_002_qrscan.style.paddingBottom
import com.azg.alf_002_qrscan.style.statusBarPadding
import com.azg.alf_002_qrscan.style.white
import com.azg.alf_002_qrscan.ui.component.ButtonCreate
import com.azg.alf_002_qrscan.ui.component.ColorTheme
import com.azg.alf_002_qrscan.ui.component.DialogDeleteConfirm
import com.azg.alf_002_qrscan.ui.component.HeaderCreate
import com.azg.alf_002_qrscan.ui.component.neonState
import com.azg.alf_002_qrscan.ui.main.create.screen.CreateResultScreen
import com.azg.alf_002_qrscan.ui.main.create.screen.EditQrScreen
import com.azg.alf_002_qrscan.ui.main.create.screen.ResultAction
import com.azg.alf_002_qrscan.ui.main.create.screen.SaveQrAction
import com.azg.alf_002_qrscan.ui.main.create.screen.SaveQrSuccessScreen
import com.azg.alf_002_qrscan.utils.BarcodeType
import com.azg.alf_002_qrscan.utils.Constant
import com.azg.alf_002_qrscan.utils.QrCodeHelper
import com.azg.alf_002_qrscan.utils.copyToClipBoard
import com.azg.alf_002_qrscan.utils.saveBitmapToStorage
import com.azg.alf_002_qrscan.utils.shareFileBitmap
import com.azg.alf_002_qrscan.utils.withWhitePadding
import com.azg.alf_002_qrscan.viewmodel.CreateViewModel
import com.azg.alf_002_qrscan.viewmodel.EditQrViewModel
import com.azg.alf_002_qrscan.widget.animComposable
import com.create.custom.qr.QrType
import com.lib.ads.application.ads.ads.AdsCallback
import com.lib.ads.application.ads.ads.wrapper.ApAdError
import com.lib.ads.application.ads.ads.wrapper.ApRewardItem
import com.lib.ads.application.ads.helper.adnative.provider.NativeAdCard
import com.lib.ads.application.ads.helper.adnative.provider.NativeAdPreloadHolder
import com.lib.ads.application.ads.helper.adnative.provider.rememberNativeAdPreload
import com.lib.ads.application.ads.helper.reward.rewardedAdWaterfall
import com.sdp.ssp.android.Sdp
import com.sdp.ssp.android.Ssp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import org.koin.compose.viewmodel.koinViewModel

class CreateBarcodeActivity : BaseActivity() {

    private val viewModel: CreateViewModel by inject()
    private val editQrViewModel: EditQrViewModel by inject()
    override fun initialize() = Unit

    private val reward by rewardedAdWaterfall(
        adUnitIds = RewardPlacement.RewardEdit.listId,
        canShowAds = RewardPlacement.RewardEdit.canShowAds,
        lifecycleOwner = this@CreateBarcodeActivity,
        autoLoad = false
    )


    @Composable
    override fun BoxScope.ContentView() {
        val barcodeType = getData<BarcodeType>(Constant.BARCODE_TYPE) ?: BarcodeType.CODE_128
        val currentTheme by viewModel.colorTheme.collectAsStateWithLifecycle(ColorTheme.BLUE)
        var createContent by remember { mutableStateOf("") }
        var isShowDeleteDialog by remember { mutableStateOf(false) }
        val navController = rememberNavController()

        DialogDeleteConfirm(
            isShowDialog = isShowDeleteDialog,
            onDismiss = { isShowDeleteDialog = false },
            onConfirm = {
                viewModel.currentHistoryId.value?.let { id ->
                    lifecycleScope.launch {
                        viewModel.deleteHistory(id)
                        finish()
                    }
                } ?: finish()
            }
        )

        NavHost(
            navController = navController,
            startDestination = CreateNavigator.InputDataScreen.router
        ) {
            animComposable(CreateNavigator.InputDataScreen.router) {
                InputBarcodeScreen(
                    viewModel = viewModel,
                    barcodeType = barcodeType,
                    currentTheme = currentTheme,
                    modifier = Modifier
                        .background(currentTheme.bgColor, RectangleShape)
                        .statusBarPadding(),
                    createAction = { value ->
                        viewModel.createBarcodeValue(
                            value = value,
                            barcodeType = barcodeType,
                            context = this@CreateBarcodeActivity
                        ) { bitmap ->
                            if (bitmap != null) {
                                createContent = value
                                navController.navigate(
                                    CreateNavigator.CreateQrScreen.createRouter(
                                        value
                                    )
                                )
                            } else {
                                toastShort(getString(R.string.cancel_save_file_now))
                            }
                        }
                    },
                    onBack = { finish() }
                )
            }

            animComposable(
                CreateNavigator.CreateQrScreen.router,
                arguments = listOf(navArgument("content") { type = NavType.StringType })
            ) {
                val content = it.arguments?.getString("content") ?: ""
                CreateResultScreen(
                    viewModel = viewModel,
                    qrType = QrType.Barcode,
                    modifier = Modifier
                        .background(currentTheme.bgColor, RectangleShape)
                        .statusBarPadding(),
                    action = object : ResultAction {
                        override fun onBack() {
                            navController.popBackStack()
                        }

                        override fun onDelete() {
                            isShowDeleteDialog = true
                        }

                        override fun onCopy() {
                            super.onCopy()
                            copyToClipBoard(this@CreateBarcodeActivity, content)
                            toastShort(getString(R.string.copy_success))
                        }

                        override fun onEdit() {
                            super.onEdit()
                            reward.waitLoadAndShow(
                                this@CreateBarcodeActivity,
                                showWhenReturnFromBackground = true,
                                callback = object : AdsCallback() {
                                    override fun onUserEarnedReward(rewardItem: ApRewardItem) {
                                        super.onUserEarnedReward(rewardItem)
                                        editQrViewModel.generateQrData(
                                            content,
                                            this@CreateBarcodeActivity
                                        )
                                        createContent = content
                                        navController.navigate(CreateNavigator.EditQrScreen.router)
                                    }
                                    override fun onNextAction() {
                                        super.onNextAction()
                                    }

                                    override fun onAdFailedToShow(adError: ApAdError?) {
                                        super.onAdFailedToShow(adError)
                                        toastShort(getString(R.string.can_t_load_reward_try_again))
                                    }
                                })
                        }

                        override fun onSearch() {
                            super.onSearch()
                            QrCodeHelper.searchOnWeb(
                                this@CreateBarcodeActivity.applicationContext,
                                content
                            )
                        }

                        override fun onSave(bitmap: Bitmap?) {
                            super.onSave(bitmap)
                            bitmap?.let { qrBitmap ->
                                lifecycleScope.launch(Dispatchers.IO) {
                                    saveBitmapToStorage(
                                        context = this@CreateBarcodeActivity.applicationContext,
                                        bitmap = qrBitmap.withBarcodeSavePadding(),
                                        destFileName = "Barcode-Create_${System.currentTimeMillis()}.png",
                                        format = Bitmap.CompressFormat.PNG,
                                        environment = Environment.DIRECTORY_PICTURES,
                                        folderName = getString(R.string.app_name)
                                    )?.run {
                                        withContext(Dispatchers.Main) {
                                            toastShort(getString(R.string.save_file_success))
                                        }
                                    } ?: withContext(Dispatchers.Main) {
                                        toastShort(getString(R.string.cancel_save_file_now))
                                    }
                                }
                            } ?: run {
                                toastShort(getString(R.string.cancel_save_file_now))
                            }
                        }

                        override fun onShare(bitmap: Bitmap?) {
                            super.onShare(bitmap)
                            this@CreateBarcodeActivity.shareFileBitmap(
                                "ShareBarcode_${System.currentTimeMillis()}.png",
                                bitmap
                            )
                        }
                    }
                )
            }

            animComposable(CreateNavigator.EditQrScreen.router) {
                val historyId by viewModel.currentHistoryId.collectAsStateWithLifecycle()
                EditQrScreen(
                    viewModel = editQrViewModel,
                    contentEdit = createContent,
                    modifier = Modifier
                        .background(currentTheme.bgColor, RectangleShape)
                        .statusBarPadding(),
                    onSave = { bitmap ->
                        bitmap?.let { qrBitmap ->
                            lifecycleScope.launch(Dispatchers.IO) {
                                saveBitmapToStorage(
                                    context = this@CreateBarcodeActivity.applicationContext,
                                    bitmap = qrBitmap.withBarcodeSavePadding(),
                                    destFileName = "Barcode-Create_${System.currentTimeMillis()}.png",
                                    format = Bitmap.CompressFormat.PNG,
                                    environment = Environment.DIRECTORY_PICTURES,
                                    folderName = getString(R.string.app_name)
                                )?.run {
                                    historyId?.let { id ->
                                        editQrViewModel.saveAndUpdateDatabase(
                                            context = this@CreateBarcodeActivity,
                                            historyId = id,
                                            onSuccess = { },
                                            onError = { }
                                        )
                                    }
                                    withContext(Dispatchers.Main) {
                                        navController.navigate(CreateNavigator.SaveQrSuccessScreen.router)
                                    }
                                } ?: withContext(Dispatchers.Main) {
                                    toastShort(getString(R.string.cancel_save_file_now))
                                }
                            }
                        } ?: run {
                            toastShort(getString(R.string.cancel_save_file_now))
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            animComposable(CreateNavigator.SaveQrSuccessScreen.router) {
                SaveQrSuccessScreen(
                    viewModel = editQrViewModel,
                    modifier = Modifier
                        .background(currentTheme.bgColor, RectangleShape)
                        .statusBarPadding(),
                    action = object : SaveQrAction {
                        override fun onBack() {
                            navController.popBackStack()
                        }

                        override fun onGoToHome() {
                            finish()
                        }

                        override fun onContinueEdit() {
                            navController.popBackStack()
                        }

                        override fun onCopy() {
                            super.onCopy()
                            copyToClipBoard(this@CreateBarcodeActivity, createContent)
                            toastShort(getString(R.string.copy_success))
                        }

                        override fun onSave(bitmap: Bitmap?) {
                            super.onSave(bitmap)
                            bitmap?.let { qrBitmap ->
                                lifecycleScope.launch(Dispatchers.IO) {
                                    saveBitmapToStorage(
                                        context = this@CreateBarcodeActivity.applicationContext,
                                        bitmap = qrBitmap.withBarcodeSavePadding(),
                                        destFileName = "Barcode-Create_${System.currentTimeMillis()}.png",
                                        format = Bitmap.CompressFormat.PNG,
                                        environment = Environment.DIRECTORY_PICTURES,
                                        folderName = getString(R.string.app_name)
                                    )?.run {
                                        withContext(Dispatchers.Main) {
                                            toastShort(getString(R.string.save_file_success))
                                        }
                                    } ?: withContext(Dispatchers.Main) {
                                        toastShort(getString(R.string.cancel_save_file_now))
                                    }
                                }
                            } ?: run {
                                toastShort(getString(R.string.cancel_save_file_now))
                            }
                        }

                        override fun onShare(bitmap: Bitmap?) {
                            super.onShare(bitmap)
                            this@CreateBarcodeActivity.shareFileBitmap(
                                "ShareBarcode_${System.currentTimeMillis()}.png",
                                bitmap
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun InputBarcodeScreen(
    viewModel: CreateViewModel = koinViewModel(),
    barcodeType: BarcodeType,
    currentTheme: ColorTheme,
    modifier: Modifier = Modifier,
    createAction: (String) -> Unit = {},
    onBack: () -> Unit = {}
) {
    var barcodeContent by remember { mutableStateOf("") }
    val barcodeError by viewModel.barcodeError.collectAsStateWithLifecycle()
    val (isValid, errorMsg) = remember(barcodeContent, barcodeType) {
        if (barcodeContent.isEmpty()) {
            Pair(false, null)
        } else {
            viewModel.validateBarcodeInput(barcodeContent, barcodeType)
        }
    }

    val nativeAdHolder = rememberNativeAdPreload(
        enabled = NativePlacement.NativeMediumAll.canShowAds,
        tag = NativePlacement.NativeMediumAll.tag,
        fallbackAdUnitIds = NativePlacement.NativeMediumAll.listId,
        autoReloadOnResume = true,
    )
    LaunchedEffect(nativeAdHolder) {
        nativeAdHolder.request()
    }

    InputBarcodeScreenContent(
        barcodeType = barcodeType,
        currentTheme = currentTheme,
        barcodeContent = barcodeContent,
        onBarcodeContentChange = { newValue ->
            barcodeContent = newValue
            viewModel.clearBarcodeError()
        },
        barcodeError = barcodeError,
        isValid = isValid,
        errorMsg = errorMsg,
        nativeAdHolder = nativeAdHolder,
        modifier = modifier,
        createAction = createAction,
        onBack = onBack
    )
}

@Composable
fun InputBarcodeScreenContent(
    barcodeType: BarcodeType,
    currentTheme: ColorTheme,
    barcodeContent: String,
    onBarcodeContentChange: (String) -> Unit,
    barcodeError: String?,
    isValid: Boolean,
    errorMsg: String?,
    nativeAdHolder: NativeAdPreloadHolder?,
    modifier: Modifier = Modifier,
    createAction: (String) -> Unit = {},
    onBack: () -> Unit = {}
) {
    val isButtonEnabled = isValid && barcodeContent.isNotEmpty()

    ColumnParentContent(onBack = { onBack() }, modifier = modifier.fillMaxSize()) {

        HeaderCreate(
            header = stringResource(R.string.create_barcode),
            lefIcon = R.drawable.ic_app_back,
            modifier = Modifier
        ) {
            onBack.invoke()
        }

        Column(
            Modifier
                .padding(horizontal = 12.Sdp)
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SpaceH(16)
            TextView(
                text = stringResource(barcodeType.titleRes),
                font = R.font.rb_600,
                textSize = 14.Ssp,
                color = currentTheme.buttonColor,
                modifier = Modifier.fillMaxWidth()
            )
            SpaceH(8)
            TextView(
                text = getBarcodeHint(barcodeType),
                font = R.font.rb_400,
                textSize = 12.Ssp,
                color = Color(0xFF757575),
                modifier = Modifier.fillMaxWidth()
            )

            SpaceH(16)

           
            TextView(
                text = stringResource(R.string.barcode_content),
                font = R.font.rb_500,
                textSize = 12.Ssp,
                color = black,
                modifier = Modifier.fillMaxWidth()
            )
            SpaceH(8)
            val borderColor = when {
                barcodeContent.isEmpty() -> Color(0xFFE0E0E0)
                isValid -> currentTheme.buttonColor
                else -> Color(0xFFE53935)
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .neonState(
                        true,
                        outerRadius = 5.Sdp,
                        colors = listOf(borderColor.opacity(30f), borderColor.opacity(30f)),
                        shape = RoundedCornerShape(14.Sdp)
                    )
                    .background(white, RoundedCornerShape(14.Sdp))
                    .border(1.dp, borderColor, RoundedCornerShape(14.Sdp))
                    .padding(12.Sdp)
            ) {
                if (barcodeContent.isEmpty()) {
                    Text(
                        text = stringResource(R.string.enter_barcode_content),
                        color = Color(0xFF9E9E9E),
                        style = TextStyle(fontSize = 14.Ssp)
                    )
                }
                BasicTextField(
                    value = barcodeContent,
                    onValueChange = onBarcodeContentChange,
                    singleLine = false,
                    textStyle = TextStyle(
                        color = black,
                        fontSize = 12.Ssp
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = when (barcodeType) {
                            BarcodeType.EAN_8, BarcodeType.EAN_13,
                            BarcodeType.UPC_A, BarcodeType.UPC_E,
                            BarcodeType.ITF -> KeyboardType.Number

                            else -> KeyboardType.Text
                        }
                    ),
                    cursorBrush = SolidColor(currentTheme.buttonColor),
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 48.Sdp)
                )
            }
            SpaceH(8)
            val displayError = errorMsg ?: barcodeError
            if (displayError != null && barcodeContent.isNotEmpty()) {
                TextView(
                    text = displayError,
                    font = R.font.rb_400,
                    textSize = 12.Ssp,
                    color = Color(0xFFE53935),
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                TextView(
                    text = stringResource(R.string.character_count, barcodeContent.length),
                    font = R.font.rb_400,
                    textSize = 12.Ssp,
                    color = if (isValid) currentTheme.buttonColor else Color(0xFF9E9E9E),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        ButtonCreate(
            isEnable = isButtonEnabled,
            theme = currentTheme,
            modifier = Modifier
                .paddingBottom(12.dp)
                .fillMaxWidth(0.75f)
        ) {
            if (isButtonEnabled) {
                createAction(barcodeContent)
            }
        }
        NativeAdCard(
            holder = nativeAdHolder,
            modifier = Modifier
                .fillMaxWidth(),
            loading = { isLoading ->
                if (LocalInspectionMode.current) {
                    ShimmerMediaLeftComposeView(isShowShimmer = true)
                } else {
                    NativeShimmerByLayout(
                        NativePlacement.NativeMediumAll,
                        isLoading,
                        Modifier.fillMaxWidth()
                    )
                }
            },
            nativeView = { ad ->
                if (!LocalInspectionMode.current) {
                    NativeAdByLayout(NativePlacement.NativeMediumAll, ad)
                }
            },
        )
    }
}

@Composable
private fun getBarcodeHint(barcodeType: BarcodeType): String {
    return when (barcodeType) {
        BarcodeType.EAN_8 -> stringResource(R.string.hint_ean8)
        BarcodeType.EAN_13 -> stringResource(R.string.hint_ean13)
        BarcodeType.UPC_A -> stringResource(R.string.hint_upca)
        BarcodeType.UPC_E -> stringResource(R.string.hint_upce)
        BarcodeType.ITF -> stringResource(R.string.hint_itf)
        BarcodeType.CODABAR -> stringResource(R.string.hint_codabar)
        BarcodeType.CODE_39 -> stringResource(R.string.hint_code39)
        BarcodeType.CODE_93 -> stringResource(R.string.hint_code93)
        BarcodeType.CODE_128 -> stringResource(R.string.hint_code128)
        BarcodeType.AZTEC, BarcodeType.PDF_417, BarcodeType.DATA_MATRIX -> stringResource(R.string.hint_2d_barcode)
        BarcodeType.OTHER -> stringResource(R.string.hint_barcode_default)
    }
}

private fun Bitmap.withBarcodeSavePadding(): Bitmap {
    val paddingPx = (minOf(width, height) * 0.1f).toInt().coerceAtLeast(32)
    return withWhitePadding(paddingPx)
}

@Preview
@Composable
fun PreviewInputBarcodeScreen() {
    AppViewTheme {
        InputBarcodeScreenContent(
            barcodeType = BarcodeType.CODE_128,
            currentTheme = ColorTheme.BLUE,
            barcodeContent = "12345678",
            onBarcodeContentChange = {},
            barcodeError = null,
            isValid = true,
            errorMsg = null,
            nativeAdHolder = null
        )
    }
}
