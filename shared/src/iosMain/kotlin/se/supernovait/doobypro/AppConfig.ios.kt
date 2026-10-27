package se.supernovait.doobypro

import kotlin.experimental.ExperimentalNativeApi

@OptIn(ExperimentalNativeApi::class)
actual val platformDebugMode: Boolean
    get() = Platform.isDebugBinary
