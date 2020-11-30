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

package ru.yoo.sdk.kassa.payments.impl.paymentOptionInfo

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.ym_fragment_bank_card.*
import ru.yoo.sdk.kassa.payments.impl.extensions.showSoftKeyboard
import ru.yoo.sdk.kassa.payments.model.LinkedCardInfo
import kotlin.properties.Delegates

internal class EditBankCardFragment : BankCardFragment() {

    var pan by Delegates.observable<CharSequence?>(null) { _, _, _ ->
        showBankCardNumber()
    }

    var titleText by Delegates.observable<CharSequence?>(null) { _, _, _ ->
        showTitleText()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(cardNumberEditText) {
            isEnabled = false
            showBankCardNumber()
        }

        with(expiryEditText) {
            isEnabled = false
            setText("**/**")
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        if (!hidden) {
            cscEditText?.apply {
                requestFocus()
                showSoftKeyboard()
            }
        }
    }

    private fun showBankCardNumber() {
        cardNumberEditText?.setText(pan)
    }

    override fun isCardNumberCorrect() = true

    override fun isExpiryCorrect() = true

    override fun collectPaymentOptionInfo() =
        LinkedCardInfo(cscEditText.text.toString())

    private fun showTitleText() {
        titleText?.let {
            setTitle(it)
        }

    }
}
