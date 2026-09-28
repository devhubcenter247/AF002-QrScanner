package com.azg.alf_002_qrscan.ui.main.create

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.azg.alf_002_qrscan.R
import com.azg.alf_002_qrscan.ads.wrapper.reward.RewardPlacement
import com.azg.alf_002_qrscan.app.toastShort
import com.azg.alf_002_qrscan.base.BaseActivity
import com.azg.alf_002_qrscan.base.getData
import com.azg.alf_002_qrscan.database.AppRepository
import com.azg.alf_002_qrscan.style.statusBarPadding
import com.azg.alf_002_qrscan.model.LocationUtils
import com.azg.alf_002_qrscan.model.MessageUtils
import com.azg.alf_002_qrscan.model.addEventToCalendar
import com.azg.alf_002_qrscan.model.connectToWifi
import com.azg.alf_002_qrscan.model.insertContact
import com.azg.alf_002_qrscan.model.shareViaEmail
import com.azg.alf_002_qrscan.model.toQrString
import com.azg.alf_002_qrscan.style.AppViewTheme
import com.azg.alf_002_qrscan.style.BoxParentContent
import com.azg.alf_002_qrscan.style.paddingBottom
import com.azg.alf_002_qrscan.style.statusBarPadding
import com.azg.alf_002_qrscan.ui.component.ButtonCreate
import com.azg.alf_002_qrscan.ui.component.ColorTheme
import com.azg.alf_002_qrscan.ui.component.DialogDeleteConfirm
import com.azg.alf_002_qrscan.ui.component.HeaderCreate
import com.azg.alf_002_qrscan.ui.main.create.screen.CreateResultScreen
import com.azg.alf_002_qrscan.ui.main.create.screen.EditQrScreen
import com.azg.alf_002_qrscan.ui.main.create.screen.InputQrContact
import com.azg.alf_002_qrscan.ui.main.create.screen.InputQrEmail
import com.azg.alf_002_qrscan.ui.main.create.screen.InputQrEvent
import com.azg.alf_002_qrscan.ui.main.create.screen.InputQrLocation
import com.azg.alf_002_qrscan.ui.main.create.screen.InputQrMessage
import com.azg.alf_002_qrscan.ui.main.create.screen.InputQrPhone
import com.azg.alf_002_qrscan.ui.main.create.screen.InputQrText
import com.azg.alf_002_qrscan.ui.main.create.screen.InputQrUrl
import com.azg.alf_002_qrscan.ui.main.create.screen.InputQrWifi
import com.azg.alf_002_qrscan.ui.main.create.screen.ResultAction
import com.azg.alf_002_qrscan.ui.main.create.screen.SaveQrAction
import com.azg.alf_002_qrscan.ui.main.create.screen.SaveQrSuccessScreen
import com.azg.alf_002_qrscan.utils.Constant
import com.azg.alf_002_qrscan.utils.QrCodeHelper
import com.azg.alf_002_qrscan.utils.copyToClipBoard
import com.azg.alf_002_qrscan.utils.saveBitmapToStorage
import com.azg.alf_002_qrscan.utils.shareFileBitmap
import com.azg.alf_002_qrscan.viewmodel.CreateViewModel
import com.azg.alf_002_qrscan.viewmodel.EditQrViewModel
import com.azg.alf_002_qrscan.viewmodel.fakeDataHistoryDao
import com.azg.alf_002_qrscan.widget.animComposable
import com.azg.alf_002_qrscan.widget.navigationTo
import com.create.custom.qr.QrType
import com.lib.ads.application.ads.ads.AdsCallback
import com.lib.ads.application.ads.ads.wrapper.ApAdError
import com.lib.ads.application.ads.helper.reward.rewardedAdWaterfall
import com.sdp.ssp.android.Sdp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject

class InputDataActivity : BaseActivity() {
    override fun initialize() = Unit
    private val viewModel: CreateViewModel by inject()
    private val editQrViewModel: EditQrViewModel by inject()

    private val reward by rewardedAdWaterfall(
        adUnitIds = RewardPlacement.RewardEdit.listId,
        canShowAds = RewardPlacement.RewardEdit.canShowAds,
        lifecycleOwner = this@InputDataActivity,
        autoLoad = false
    )

    private fun openUrlInBrowser(url: String) {
        try {
            var finalUrl = url.trim()
            if (!finalUrl.startsWith("http://") && !finalUrl.startsWith("https://")) {
                finalUrl = "https://$finalUrl"
            }
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(finalUrl)))
        } catch (e: Exception) {
            e.printStackTrace()
            toastShort(getString(R.string.faild_connect_to))
        }
    }

    private fun dialPhoneNumber(phone: String) {
        try {
            startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone")))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Composable
    override fun BoxScope.ContentView() {
        val createType = getData<QrType>(Constant.CREATE_TYPE) ?: QrType.Text
        val currentTheme by viewModel.colorTheme.collectAsStateWithLifecycle(ColorTheme.BLUE)
        var createContent by remember {
            mutableStateOf("")
        }
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
            navController = navController, startDestination = CreateNavigator.InputDataScreen.router
        ) {
            animComposable(CreateNavigator.InputDataScreen.router) {
                InputDataScreen(
                    viewModel = viewModel,
                    qrType = createType,
                    modifier = Modifier
                        .background(currentTheme.bgColor, RectangleShape)
                        .statusBarPadding(),
                    createAction = { value ->
                        viewModel.generateQr(
                            input = value,
                            context = this@InputDataActivity,
                            qrType = createType
                        ) { success ->
                            if (success) {
                                createContent = value
                                navController.navigationTo(
                                    CreateNavigator.CreateQrScreen.createRouter(value)
                                )
                            } else {
                                toastShort(getString(R.string.qr_generation_failed))
                            }
                        }
                    }) {
                    finish()
                }
            }
            animComposable(
                CreateNavigator.CreateQrScreen.router,
                arguments = listOf(navArgument("content") { type = NavType.StringType })
            )
            {
                val content = it.arguments?.getString("content") ?: ""
                CreateResultScreen(
                    viewModel = viewModel,
                    qrType = createType,
                    modifier = Modifier
                        .background(currentTheme.bgColor, RectangleShape)
                        .statusBarPadding(),
                    action = object : ResultAction {
                        override fun onBack() {
                            navController.popBackStack()
                        }

                        override fun onCopy() {
                            super.onCopy()
                            copyToClipBoard(this@InputDataActivity, content)
                            toastShort(getString(R.string.copy_success))
                        }

                        override fun onEdit() {
                            super.onEdit()
                            reward.waitLoadAndShow(
                                activity = this@InputDataActivity,
                                showWhenReturnFromBackground = true,
                                callback = object : AdsCallback() {
                                    override fun onNextAction() {
                                        super.onNextAction()
                                        editQrViewModel.generateQrData(
                                            content,
                                            this@InputDataActivity
                                        )
                                        createContent = content
                                        navController.navigate(CreateNavigator.EditQrScreen.router)
                                    }

                                    override fun onAdFailedToShow(adError: ApAdError?) {
                                        super.onAdFailedToShow(adError)
                                        toastShort(getString(R.string.can_t_load_reward_try_again))
                                    }
                                })

                        }

                        override fun onDelete() {
                            isShowDeleteDialog = true
                        }

                        override fun onSearch() {
                            super.onSearch()
                            QrCodeHelper.searchOnWeb(
                                this@InputDataActivity.applicationContext, content
                            )
                        }

                        override fun onOpenBrowser(url: String) {
                            super.onOpenBrowser(url)
                            openUrlInBrowser(url)
                        }

                        override fun onCall(phone: String) {
                            super.onCall(phone)
                            dialPhoneNumber(phone)
                        }

                        override fun onSendEmail() {
                            super.onSendEmail()
                            viewModel.emailModel.value.shareViaEmail(this@InputDataActivity)
                        }

                        override fun onSendMessage() {
                            super.onSendMessage()
                            MessageUtils.sendMessage(
                                this@InputDataActivity,
                                viewModel.messageModel.value
                            )
                        }

                        override fun onAddContact() {
                            super.onAddContact()
                            insertContact(this@InputDataActivity, viewModel.contactModel.value)
                        }

                        override fun onConnectWifi() {
                            super.onConnectWifi()
                            val wifi = viewModel.wifiModel.value
                            val connected = connectToWifi(this@InputDataActivity, wifi)
                            toastShort(
                                if (connected) {
                                    "${getString(R.string.connect_to)} ${wifi.name}"
                                } else {
                                    "${getString(R.string.faild_connect_to)} ${wifi.name}"
                                }
                            )
                        }

                        override fun onOpenMap() {
                            super.onOpenMap()
                            LocationUtils.openInMaps(
                                this@InputDataActivity,
                                viewModel.locationModel.value
                            )
                        }

                        override fun onAddEvent() {
                            super.onAddEvent()
                            addEventToCalendar(this@InputDataActivity, viewModel.eventModel.value)
                        }

                        override fun onSave(bitmap: Bitmap?) {
                            super.onSave(bitmap)
                            bitmap?.let { qrBitmap ->
                                lifecycleScope.launch(Dispatchers.IO) {
                                    saveBitmapToStorage(
                                        context = this@InputDataActivity.applicationContext,
                                        bitmap = qrBitmap,
                                        destFileName = "Qr-Create_${System.currentTimeMillis()}.png",
                                        format = Bitmap.CompressFormat.PNG,
                                        environment = Environment.DIRECTORY_PICTURES,
                                        folderName = getString(R.string.app_name)
                                    )?.run {
                                        withContext(Dispatchers.Main) {
                                            toastShort(getString(R.string.save_file_success))
                                        }
                                    }
                                        ?: withContext(Dispatchers.Main) { toastShort(getString(R.string.cancel_save_file_now)) }
                                }
                            } ?: run {
                                toastShort(getString(R.string.cancel_save_file_now))
                            }
                        }

                        override fun onShare(bitmap: Bitmap?) {
                            super.onShare(bitmap)
                            this@InputDataActivity.shareFileBitmap(
                                "ShareFile_${System.currentTimeMillis()}.png", bitmap
                            )
                        }
                    })
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
                            // Save to gallery
                            lifecycleScope.launch(Dispatchers.IO) {
                                saveBitmapToStorage(
                                    context = this@InputDataActivity.applicationContext,
                                    bitmap = qrBitmap,
                                    destFileName = "Qr-Create_${System.currentTimeMillis()}.png",
                                    format = Bitmap.CompressFormat.PNG,
                                    environment = Environment.DIRECTORY_PICTURES,
                                    folderName = getString(R.string.app_name)
                                )?.run {
                                    // Also update the database with the custom QR
                                    historyId?.let { id ->
                                        editQrViewModel.saveAndUpdateDatabase(
                                            context = this@InputDataActivity,
                                            historyId = id,
                                            onSuccess = { },
                                            onError = { }
                                        )
                                    }
                                    withContext(Dispatchers.Main) {
                                        navController.navigationTo(CreateNavigator.SaveQrSuccessScreen.router)
                                    }
                                } ?: withContext(Dispatchers.Main) {
                                    toastShort(getString(R.string.cancel_save_file_now))
                                }
                            }
                        } ?: run {
                            toastShort(getString(R.string.cancel_save_file_now))
                        }
                    }
                ) {
                    navController.popBackStack()
                }
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
                            copyToClipBoard(this@InputDataActivity, createContent)
                            toastShort(getString(R.string.copy_success))
                        }

                        override fun onSave(bitmap: Bitmap?) {
                            super.onSave(bitmap)
                            bitmap?.let { qrBitmap ->
                                lifecycleScope.launch(Dispatchers.IO) {
                                    saveBitmapToStorage(
                                        context = this@InputDataActivity.applicationContext,
                                        bitmap = qrBitmap,
                                        destFileName = "Qr-Create_${System.currentTimeMillis()}.png",
                                        format = Bitmap.CompressFormat.PNG,
                                        environment = Environment.DIRECTORY_PICTURES,
                                        folderName = getString(R.string.app_name)
                                    )?.run {
                                        withContext(Dispatchers.Main) {
                                            toastShort(getString(R.string.save_file_success))
                                        }
                                    }
                                        ?: withContext(Dispatchers.Main) { toastShort(getString(R.string.cancel_save_file_now)) }
                                }
                            } ?: run {
                                toastShort(getString(R.string.cancel_save_file_now))
                            }
                        }

                        override fun onShare(bitmap: Bitmap?) {
                            super.onShare(bitmap)
                            this@InputDataActivity.shareFileBitmap(
                                "ShareFile_${System.currentTimeMillis()}.png", bitmap
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun InputDataScreen(
      viewModel: CreateViewModel = viewModel(),
      qrType: QrType = QrType.Text,
      modifier: Modifier = Modifier,
      createAction: (String) -> Unit = {},
      onBack: () -> Unit = {},
) {
    val buttonState by viewModel.isEnableCreate.collectAsStateWithLifecycle(false)
    val theme by viewModel.colorTheme.collectAsStateWithLifecycle(ColorTheme.BLUE)
    BoxParentContent(
        onBack = {
            onBack()
        }) {
        ConstraintLayout(
            modifier
                .fillMaxSize(1f)
                .paddingBottom(12.Sdp)
        ) {
            val (header, content, button) = createRefs()

            HeaderCreate(
                header = stringResource(R.string.create_qr),
                lefIcon = R.drawable.ic_app_back,
                modifier = Modifier.constrainAs(header) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }) {
                onBack.invoke()
            }

            Column(
                Modifier
                    .constrainAs(content) {
                        top.linkTo(header.bottom)
                        bottom.linkTo(button.top, margin = 12.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        height = Dimension.fillToConstraints
                    }
                    .padding(horizontal = 12.Sdp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally) {
                when (qrType) {
                    QrType.Contact -> {
                        InputQrContact(viewModel)
                    }
                    QrType.Location -> {
                        InputQrLocation(viewModel = viewModel)
                    }
                    QrType.Wifi -> {
                        InputQrWifi(viewModel = viewModel)
                    }
                    QrType.Website -> {
                        InputQrUrl(viewModel)
                    }
                    QrType.Message -> {
                        InputQrMessage(viewModel)
                    }
                    QrType.Email -> {
                        InputQrEmail(viewModel)
                    }
                    QrType.Phone -> {
                        InputQrPhone(viewModel)
                    }
                    QrType.Event -> {
                        InputQrEvent(viewModel)
                    }
                    else -> {
                        InputQrText(viewModel)
                    }
                }
            }

            ButtonCreate(
                isEnable = buttonState,
                theme = theme,
                modifier = Modifier
                    .constrainAs(button) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .fillMaxWidth(0.75f)) {
                if (buttonState) {
                    val string = when (qrType) {
                        QrType.Contact -> viewModel.contactModel.value.toQrString()
                        QrType.Location -> viewModel.locationModel.value.toQrString()
                        QrType.Wifi -> viewModel.wifiModel.value.toQrString()
                        QrType.Website -> viewModel.createQrUrl.value
                        QrType.Message -> viewModel.messageModel.value.toQrString()
                        QrType.Email -> viewModel.emailModel.value.toQrString()
                        QrType.Phone -> viewModel.createQrPhone.value
                        QrType.Event -> viewModel.eventModel.value.toQrString()
                        else -> viewModel.createQrText.value
                    }
                    createAction(string)
                }
            }
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(name = "PreviewInputData", showBackground = true, device = Devices.PIXEL_4_XL)
@Composable
fun PreviewInputData() {
    AppViewTheme {
        val viewModel = CreateViewModel(AppRepository(fakeDataHistoryDao))
        InputDataScreen(
            viewModel = viewModel
        ) {}
    }
}
