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

package ru.yoo.sdk.kassa.payments.tmx

import android.content.Context

/**
 * Profiling tool.
 */
internal interface ProfilingTool {

    /**
     * Initializes tool.
     *
     * @param context application context
     */
    fun init(context: Context)

    /**
     * Requests session id asynchronously. If profiling tool was not correctly initialized this call will fail.
     *
     * @param listener listens for session id
     */
    fun requestSessionId(listener: SessionIdListener)

    /**
     * Listener for session id.
     */
    interface SessionIdListener {

        /**
         * Called when session id is available.
         *
         * @param sessionId session id
         */
        fun onProfilingSessionId(sessionId: String)

        /**
         * Called when an error occurred when obtaining session id.
         *
         * @param status error name
         */
        fun onProfilingError(status: String)
    }
}
