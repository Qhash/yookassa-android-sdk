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

package ru.yoo.sdk.kassa.payments.payment.changeOption

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.Test
import org.mockito.Mockito.mock
import ru.yoo.sdk.kassa.payments.PaymentMethodType
import ru.yoo.sdk.kassa.payments.createAmount
import ru.yoo.sdk.kassa.payments.impl.paymentMethodInfo.MockPaymentInfoGateway
import ru.yoo.sdk.kassa.payments.impl.paymentOptionList.MockPaymentOptionListGateway
import ru.yoo.sdk.kassa.payments.model.AuthorizedUser
import ru.yoo.sdk.kassa.payments.on
import ru.yoo.sdk.kassa.payments.payment.CurrentUserGateway
import ru.yoo.sdk.kassa.payments.payment.InMemoryPaymentOptionListGateway
import ru.yoo.sdk.kassa.payments.payment.loadOptionList.LoadPaymentOptionListUseCase
import ru.yoo.sdk.kassa.payments.payment.loadOptionList.PaymentOptionAmountInputModel
import ru.yoo.sdk.kassa.payments.payment.loadOptionList.PaymentOptionListSuccessOutputModel

class ChangePaymentOptionIntegrationTest {

    private val paymentOptionsGateway = MockPaymentOptionListGateway(3, null)
    private val currentUserGateway = mock(CurrentUserGateway::class.java).apply {
        on(currentUser).thenReturn(AuthorizedUser())
    }
    private val paymentMethodInfoGateway = MockPaymentInfoGateway()
    private val restrictions = mutableSetOf<PaymentMethodType>()
    private val loadUseCase = LoadPaymentOptionListUseCase(
        paymentOptionListRestrictions = restrictions,
        paymentOptionListGateway = paymentOptionsGateway,
        paymentMethodInfoGateway = paymentMethodInfoGateway,
        saveLoadedPaymentOptionsListGateway = InMemoryPaymentOptionListGateway,
        currentUserGateway = currentUserGateway
    )
    private val changeUseCase = ChangePaymentOptionUseCase(InMemoryPaymentOptionListGateway)

    @Test
    fun `mock change payment option no restrictions`() {
        // prepare

        // invoke
        val paymentOptions = loadUseCase(PaymentOptionAmountInputModel(createAmount())) as PaymentOptionListSuccessOutputModel
        val paymentOptionsAfterChange = changeUseCase(ChangePaymentOptionInputModel)

        // assert
        assertThat(paymentOptions.options, equalTo(paymentOptionsAfterChange))
    }

    @Test
    fun `mock change payment option with restrictions`() {
        // prepare
        restrictions.add(PaymentMethodType.YOO_MONEY)

        // invoke
        val paymentOptions = loadUseCase(PaymentOptionAmountInputModel(createAmount())) as PaymentOptionListSuccessOutputModel
        val paymentOptionsAfterChange = changeUseCase(ChangePaymentOptionInputModel)

        // assert
        assertThat(paymentOptions.options, equalTo(paymentOptionsAfterChange))
    }
}
