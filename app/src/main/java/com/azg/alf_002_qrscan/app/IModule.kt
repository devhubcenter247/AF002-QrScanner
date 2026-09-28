package com.azg.alf_002_qrscan.app

import androidx.room.Room
import com.azg.alf_002_qrscan.database.AppDatabase
import com.azg.alf_002_qrscan.database.AppRepository
import com.azg.alf_002_qrscan.database.MIGRATION_1_2
import com.azg.alf_002_qrscan.viewmodel.CreateViewModel
import com.azg.alf_002_qrscan.viewmodel.EditQrViewModel
import com.azg.alf_002_qrscan.viewmodel.HomeViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::CreateViewModel)
    viewModelOf(::EditQrViewModel)
}
val dataModule = module {
    single { SharedPreference(androidContext()) }
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "${androidContext().applicationContext.packageName}_history_db"
        ).addMigrations(MIGRATION_1_2).build()
    }
    single { get<AppDatabase>().history() }
    singleOf(::AppRepository)
}
