/*
 * The MIT License (MIT)
 * Copyright © 2020 NBCO YooMoney LLC
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

package ru.yoo.sdk.kassa.payments.impl.paymentAuth

import android.content.Context
import android.os.CountDownTimer
import android.util.AttributeSet
import android.view.View
import android.view.inputmethod.EditorInfo.IME_ACTION_DONE
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.ym_view_payment_auth.view.*
import ru.yoo.sdk.kassa.payments.R
import ru.yoo.sdk.kassa.payments.impl.extensions.clear
import ru.yoo.sdk.kassa.payments.impl.extensions.showSoftKeyboard
import ru.yoo.sdk.kassa.payments.impl.extensions.visible
import ru.yoo.sdk.kassa.payments.utils.SimpleTextWatcher
import java.util.concurrent.TimeUnit

private const val COUNTDOWN_INTERVAL_MS = 1000L

internal class PaymentAuthView
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var retryCountDownTimer: RetryCountdownTimer? = null

    init {
        View.inflate(context, R.layout.ym_view_payment_auth, this)
        accessCode.addTextChangedListener(object: SimpleTextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                error = ""
            }
        })
    }

    var error: CharSequence?
        get() = accessCodeContainer.error
        set(value) {
            accessCodeContainer.error = value
        }

    fun setViewModel(viewModel: Model) {
        accessCode.hint = viewModel.accessCodeHint
        accessCode.clear()
        accessCode.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == IME_ACTION_DONE) {
                viewModel.doneAction()
                true
            } else {
                false
            }
        }
        repeat.visible = false

        when (viewModel) {
            is Model.Retry -> {
                if (viewModel.retryTime == null) {
                    timerText.visible = false
                    repeat.visible = true
                } else {
                    timerText.visible = true
                    repeat.setOnClickListener { viewModel.retryAction() }
                    retryCountDownTimer?.cancel()
                    retryCountDownTimer = RetryCountdownTimer(viewModel.retryTime).apply { start() }
                }
            }
            is Model.NoRetry -> {
                timerText.visible = false
            }
        }
    }

    fun getAccessCode(): String = accessCode.text.toString()

    fun requestFocusAndShowSoftKeyboard() {
        with(accessCode) {
            requestFocus()
            showSoftKeyboard()
        }
    }

    internal inner class RetryCountdownTimer(
        retryTime: Int
    ) : CountDownTimer(TimeUnit.SECONDS.toMillis(retryTime.toLong()), COUNTDOWN_INTERVAL_MS) {

        override fun onFinish() {
            repeat.isEnabled = true
            repeat.visible = true
            timerText.visible = false
        }

        override fun onTick(millisUntilFinished: Long) {
            timerText.text = context.getString(
                R.string.ym_sms_retry_time, TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished).toString()
            )
        }
    }

    internal sealed class Model {
        abstract val accessCodeHint: CharSequence
        abstract val doneAction: () -> Unit

        internal data class Retry(
            override val accessCodeHint: CharSequence,
            override val doneAction: () -> Unit,
            val retryAction: () -> Unit,
            val retryTime: Int?
        ) : Model()

        internal data class NoRetry(
            override val accessCodeHint: CharSequence,
            override val doneAction: () -> Unit
        ) : Model()
    }
}
