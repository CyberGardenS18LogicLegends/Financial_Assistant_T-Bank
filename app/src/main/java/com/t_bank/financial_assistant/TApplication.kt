package com.t_bank.financial_assistant

import android.app.Application
import com.t_bank.local.room.TransactionDatabase
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TApplication : Application()