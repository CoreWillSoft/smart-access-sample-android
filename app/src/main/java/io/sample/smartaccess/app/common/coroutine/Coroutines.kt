package io.sample.smartaccess.app.common.coroutine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

fun createApplicationScope() = CoroutineScope(SupervisorJob() + Dispatchers.Default)
