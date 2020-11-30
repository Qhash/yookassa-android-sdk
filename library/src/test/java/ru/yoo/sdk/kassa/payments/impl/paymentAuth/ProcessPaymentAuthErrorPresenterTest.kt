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

import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import ru.yoo.sdk.kassa.payments.impl.ApiMethodException
import ru.yoo.sdk.kassa.payments.impl.contract.ContractErrorViewModel
import ru.yoo.sdk.kassa.payments.impl.contract.ContractFailViewModel
import ru.yoo.sdk.kassa.payments.impl.contract.ContractPaymentAuthRequiredViewModel
import ru.yoo.sdk.kassa.payments.impl.contract.ContractUserAuthRequiredViewModel
import ru.yoo.sdk.kassa.payments.model.ErrorCode
import kotlin.reflect.KClass

@RunWith(ParameterizedRobolectricTestRunner::class)
internal class ProcessPaymentAuthErrorPresenterTest(
        private val exception: Exception,
        private val expectedClass: KClass<ContractFailViewModel>
) {

    companion object {
        private val allowedErrorCodes = arrayOf(
                ErrorCode.INVALID_CONTEXT,
                ErrorCode.SESSION_DOES_NOT_EXIST,
                ErrorCode.SESSION_EXPIRED,
                ErrorCode.VERIFY_ATTEMPTS_EXCEEDED,
                ErrorCode.INVALID_ANSWER,
                ErrorCode.INVALID_TOKEN,
                ErrorCode.AUTH_EXPIRED,
                ErrorCode.TECHNICAL_ERROR,
                ErrorCode.UNKNOWN)

        @[ParameterizedRobolectricTestRunner.Parameters(name = "{0}") JvmStatic]
        fun data(): Collection<Array<Any>> {
            val ret = mutableListOf<Array<Any>>()

            ErrorCode.values().filterNot { it in allowedErrorCodes }.forEach {
                ret.add(arrayOf(ApiMethodException(it), IllegalStateException::class))
            }


            ret.add(arrayOf(ApiMethodException(ErrorCode.INVALID_CONTEXT), ContractPaymentAuthRequiredViewModel::class))
            ret.add(arrayOf(ApiMethodException(ErrorCode.SESSION_DOES_NOT_EXIST), ContractPaymentAuthRequiredViewModel::class))
            ret.add(arrayOf(ApiMethodException(ErrorCode.SESSION_EXPIRED), ContractPaymentAuthRequiredViewModel::class))
            ret.add(arrayOf(ApiMethodException(ErrorCode.AUTH_EXPIRED), ContractPaymentAuthRequiredViewModel::class))
            ret.add(arrayOf(ApiMethodException(ErrorCode.INVALID_TOKEN), ContractUserAuthRequiredViewModel::class))
            ret.add(arrayOf(ApiMethodException(ErrorCode.VERIFY_ATTEMPTS_EXCEEDED), ContractErrorViewModel::class))
            ret.add(arrayOf(ApiMethodException(ErrorCode.TECHNICAL_ERROR), ContractErrorViewModel::class))
            ret.add(arrayOf(ApiMethodException(ErrorCode.UNKNOWN), ContractErrorViewModel::class))
            ret.add(arrayOf(Exception(), ContractErrorViewModel::class))

            return ret
        }
    }

    private val presenter = ProcessPaymentAuthErrorPresenter(RuntimeEnvironment.application, Exception::toString)

    @Test
    fun name() {
        // prepare

        // invoke
        val result = try {
            presenter(exception)
        } catch (e: IllegalStateException) {
            e
        }

        // assert
        MatcherAssert.assertThat(result, Matchers.instanceOf(expectedClass.java))
    }
}