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

package ru.yandex.money.android.sdk.impl.payment.tokenize

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.WindowManager
import android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
import android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.FragmentManager
import ru.yandex.money.android.sdk.R
import ru.yandex.money.android.sdk.impl.AppModel
import ru.yandex.money.android.sdk.impl.BackPressedAppCompatDialog
import ru.yandex.money.android.sdk.impl.WithBackPressedListener
import ru.yandex.money.android.sdk.impl.extensions.hideSoftKeyboard
import ru.yandex.money.android.sdk.impl.extensions.inTransaction
import ru.yandex.money.android.sdk.impl.paymentOptionInfo.BankCardFragment
import ru.yandex.money.android.sdk.impl.paymentOptionInfo.EditBankCardFragment
import ru.yandex.money.android.sdk.impl.paymentOptionInfo.NewBankCardFragment
import ru.yandex.money.android.sdk.impl.paymentOptionInfo.PaymentOptionInfoBankCardViewModel
import ru.yandex.money.android.sdk.impl.paymentOptionInfo.PaymentOptionInfoLinkedCardViewModel
import ru.yandex.money.android.sdk.impl.paymentOptionInfo.PaymentOptionInfoViewModel

private const val EDIT_BANK_CARD_FRAGMENT = "edit_bank_card_fragment"
private const val NEW_BANK_CARD_FRAGMENT = "new_bank_card_fragment"

internal class BankCardDialogFragment : AppCompatDialogFragment() {

    private val paymentOptionInfoListener: (PaymentOptionInfoViewModel) -> Unit = {
        childFragmentManager.takeIf { !isStateSaved }?.apply {
            when (it) {
                is PaymentOptionInfoBankCardViewModel -> showNewBankCardFragment()
                is PaymentOptionInfoLinkedCardViewModel -> {
                    showEditBankCardFragment()
                    with((findFragmentByTag(EDIT_BANK_CARD_FRAGMENT) as EditBankCardFragment)) {
                        pan = it.pan
                        titleText = getString(it.title)
                    }
                }
            }
        }
    }

    init {
        setStyle(STYLE_NO_FRAME, R.style.ym_FullscreenDialogTheme)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
    }

    override fun onDetach() {
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        super.onDetach()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?) =
        BackPressedAppCompatDialog(requireContext(), theme).apply {
            window?.setSoftInputMode(SOFT_INPUT_ADJUST_RESIZE or SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FrameLayout(requireContext()).apply {
            id = R.id.ym_container
            val isNotTablet = !resources.getBoolean(R.bool.ym_isTablet)
            layoutParams = FrameLayout.LayoutParams(
                resources.getDimensionPixelSize(R.dimen.ym_dialogWidth),
                MATCH_PARENT.takeIf { isNotTablet } ?: resources.getDimensionPixelSize(R.dimen.ym_dialogHeight)
            )
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val closeListener = {
            AppModel.selectPaymentOptionController.retry()
        }

        if (savedInstanceState == null) {
            childFragmentManager.inTransaction {
                EditBankCardFragment().also {
                    add(R.id.ym_container, it, EDIT_BANK_CARD_FRAGMENT)
                    hide(it)
                    it.closeListener = closeListener
                }

                NewBankCardFragment().also {
                    add(R.id.ym_container, it, NEW_BANK_CARD_FRAGMENT)
                    hide(it)
                    it.closeListener = closeListener
                }
            }
        } else {
            with(childFragmentManager) {
                (findFragmentByTag(EDIT_BANK_CARD_FRAGMENT) as BankCardFragment?)?.closeListener = closeListener
                (findFragmentByTag(NEW_BANK_CARD_FRAGMENT) as BankCardFragment?)?.closeListener = closeListener
            }
        }

        AppModel.listeners += paymentOptionInfoListener

        dialog?.setCanceledOnTouchOutside(false)
        (dialog as? WithBackPressedListener)?.onBackPressed = {
            closeListener()
            true
        }
    }

    override fun onDestroyView() {
        AppModel.listeners -= paymentOptionInfoListener

        (dialog as? WithBackPressedListener)?.onBackPressed = null

        super.onDestroyView()
    }

    override fun onDismiss(dialog: DialogInterface) {
        view?.hideSoftKeyboard()

        super.onDismiss(dialog)
    }

    override fun dismissAllowingStateLoss() {
        if (context?.resources?.getBoolean(R.bool.ym_isTablet) == false) {
            super.dismissAllowingStateLoss()
        }
    }

    private fun FragmentManager.showNewBankCardFragment() {
        inTransaction {
            hide(findFragmentByTag(EDIT_BANK_CARD_FRAGMENT)!!)
            show(findFragmentByTag(NEW_BANK_CARD_FRAGMENT)!!)
        }
    }

    private fun FragmentManager.showEditBankCardFragment() {
        inTransaction {
            hide(findFragmentByTag(NEW_BANK_CARD_FRAGMENT)!!)
            show(findFragmentByTag(EDIT_BANK_CARD_FRAGMENT)!!)
        }
    }
}
