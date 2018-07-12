/*
 * The MIT License (MIT)
 * Copyright © 2018 NBCO Yandex.Money LLC
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

package ru.yandex.money.android.sdk.impl.metrics

import ru.yandex.money.android.sdk.UseCase
import ru.yandex.money.android.sdk.impl.extensions.toTokenizeScheme
import ru.yandex.money.android.sdk.payment.selectOption.SelectPaymentOptionInputModel
import ru.yandex.money.android.sdk.payment.selectOption.SelectPaymentOptionOutputModel
import ru.yandex.money.android.sdk.payment.selectOption.SelectedPaymentOptionOutputModel

internal class SelectPaymentOptionTokenizeSchemeSetter(
    private val useCase: UseCase<SelectPaymentOptionInputModel, SelectPaymentOptionOutputModel>,
    private val setTokenizeScheme: (TokenizeScheme) -> Unit
) : UseCase<SelectPaymentOptionInputModel, SelectPaymentOptionOutputModel> {
    override fun invoke(input: SelectPaymentOptionInputModel) = useCase(input).also {
        if (it is SelectedPaymentOptionOutputModel) {
            setTokenizeScheme(it.paymentOption.toTokenizeScheme())
        }
    }
}
