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

package ru.yandex.money.android.sdk.payment.changeOption

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import ru.yandex.money.android.sdk.createWalletPaymentOption
import ru.yandex.money.android.sdk.on
import ru.yandex.money.android.sdk.payment.GetLoadedPaymentOptionListGateway

class ChangePaymentOptionUseCaseTest {
    @Test
    fun shouldInvokeGateway() {
        // prepare
        val paymentOption = createWalletPaymentOption(0)
        val testOutput = listOf(paymentOption)
        val gateway = mock(GetLoadedPaymentOptionListGateway::class.java)
        on(gateway.getLoadedPaymentOptions()).thenReturn(testOutput)
        val useCase = ChangePaymentOptionUseCase(gateway)

        // invoke
        val outputModel = useCase(Unit)

        // assert
        assertThat(outputModel.size, equalTo(1))
        assertThat(outputModel[0], equalTo(paymentOption))
        verify(gateway).getLoadedPaymentOptions()
        verifyNoMoreInteractions(gateway)
    }
}
