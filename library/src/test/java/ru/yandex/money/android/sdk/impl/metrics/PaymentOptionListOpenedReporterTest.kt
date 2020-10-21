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

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import ru.yandex.money.android.sdk.impl.paymentOptionList.PaymentOptionListSuccessViewModel
import ru.yandex.money.android.sdk.impl.paymentOptionList.PaymentOptionListViewModel
import ru.yandex.money.android.sdk.model.PaymentOption
import ru.yandex.money.android.sdk.model.Presenter
import ru.yandex.money.android.sdk.on
import ru.yandex.money.android.sdk.payment.loadOptionList.PaymentOptionListOutputModel
import ru.yandex.money.android.sdk.payment.loadOptionList.PaymentOptionListSuccessOutputModel

@RunWith(MockitoJUnitRunner.StrictStubs::class)
internal class PaymentOptionListOpenedReporterTest {

    private val testOutputModel = PaymentOptionListSuccessOutputModel(emptyList())
    private val testViewModel = PaymentOptionListSuccessViewModel(emptyList(), false)

    @Mock
    private lateinit var authType: AuthType
    @Mock
    private lateinit var presenter: Presenter<PaymentOptionListOutputModel, PaymentOptionListViewModel>
    @Mock
    private lateinit var mockReporter: Reporter

    @Before
    fun setUp() {
        on(presenter(testOutputModel)).thenReturn(testViewModel)
    }

    @[Test Suppress("UNCHECKED_CAST")]
    fun shouldSendNameAndAuthType() {
        // prepare
        val reporter = PaymentOptionListOpenedReporter({ authType }, presenter, mockReporter)

        // invoke
        reporter(testOutputModel)

        // assert
        verify(mockReporter).report("screenPaymentOptions", listOf(authType))
    }
}
