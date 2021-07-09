/*
 * The MIT License (MIT)
 * Copyright © 2021 NBCO YooMoney LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the “Software”), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package ru.yoomoney.sdk.kassa.payments.tmx

import android.content.Context
import com.threatmetrix.TrustDefender.Config
import com.threatmetrix.TrustDefender.THMStatusCode
import com.threatmetrix.TrustDefender.TrustDefender
import ru.yoomoney.sdk.kassa.payments.BuildConfig
import java.util.concurrent.TimeUnit

/**
 * Current implementation of [ProfilingTool].
 */
internal class ThreatMetrixProfilingTool : ProfilingTool {

    override fun init(context: Context) {
        TrustDefender.getInstance().init(
            Config()
                .setContext(context.applicationContext)
                .setOrgId(BuildConfig.THREAT_METRIX_ORIG_ID)
                .setTimeout(10, TimeUnit.SECONDS)
                .setRegisterForLocationServices(false)
                .setFPServer(BuildConfig.THREAT_METRIX_FP_SERVER)
        )
    }

    override fun requestSessionId(listener: ProfilingTool.SessionIdListener) {
        TrustDefender.getInstance().doProfileRequest { result ->
            val status = result.status
            if (status == THMStatusCode.THM_OK) {
                val sessionId = result.sessionID
                listener.onProfilingSessionId(sessionId)
            } else {
                listener.onProfilingError(status.name)
            }
        }
    }
}
