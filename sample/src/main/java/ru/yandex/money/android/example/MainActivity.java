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

package ru.yandex.money.android.example;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ScrollView;
import ru.yandex.money.android.example.settings.Settings;
import ru.yandex.money.android.example.settings.SettingsActivity;
import ru.yandex.money.android.example.utils.AmountFormatter;
import ru.yandex.money.android.sdk.Amount;
import ru.yandex.money.android.sdk.Checkout;
import ru.yandex.money.android.sdk.PaymentMethodType;
import ru.yandex.money.android.sdk.ShopParameters;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashSet;
import java.util.Set;

/**
 * All calls to MSDK library are handled through the Checkout class.
 *
 * @see ru.yandex.money.android.sdk.Checkout
 */
public final class MainActivity extends AppCompatActivity {

    private static final BigDecimal MAX_AMOUNT = new BigDecimal("99999.99");
    public static final Currency RUB = Currency.getInstance("RUB");
    public static final String KEY_AMOUNT = "amount";

    @Nullable
    private View buyButton;
    @NonNull
    private BigDecimal amount = BigDecimal.ZERO;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUi();

        // Attach MSDK to supportFragmentManager
        Checkout.attach(getSupportFragmentManager());

        // Set action on MSDK result
        Checkout.setResultCallback((paymentToken, type)
                -> startActivity(SuccessTokenizeActivity.createIntent(this, paymentToken, type.name())));
    }

    private void onBuyClick() {
        if (validateAmount()) {
            final Settings settings = new Settings(this);
            final Set<PaymentMethodType> paymentMethodTypes = getPaymentMethodTypes(settings);

            // Start MSDK to get payment token
            Checkout.tokenize(
                    this,
                    new Amount(amount, RUB),
                    new ShopParameters(
                            getString(R.string.main_product_name),
                            getString(R.string.main_product_description),
                            BuildConfig.MERCHANT_TOKEN,
                            paymentMethodTypes,
                            paymentMethodTypes.isEmpty() || settings.isGooglePayAllowed(),
                            BuildConfig.SHOP_ID,
                            BuildConfig.GATEWAY_ID,
                            settings.showYandexCheckoutLogo()
                    )
            );
        }
    }

    @Override
    protected void onDestroy() {
        // Detach MSDK from supportFragmentManager
        Checkout.detach();

        if (validateAmount()) {
            saveAmount();
        }

        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (R.id.action_settings == item.getItemId()) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initUi() {
        setContentView(R.layout.activity_main);

        setTitle(R.string.main_toolbar_title);

        final ScrollView scrollContainer = findViewById(R.id.scroll_container);
        if (scrollContainer != null) {
            scrollContainer.post(() -> scrollContainer.fullScroll(ScrollView.FOCUS_DOWN));
        }

        buyButton = findViewById(R.id.buy);
        buyButton.setOnClickListener(v -> onBuyClick());

        final EditText priceEdit = findViewById(R.id.price);
        priceEdit.setInputType(InputType.TYPE_CLASS_TEXT);
        priceEdit.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        priceEdit.addTextChangedListener(new AmountFormatter(priceEdit, this::onAmountChange, RUB, MAX_AMOUNT));
        priceEdit.setOnEditorActionListener((v, actionId, event) -> {
            final boolean isActionDone = actionId == EditorInfo.IME_ACTION_DONE;
            if (isActionDone) {
                onBuyClick();
            }
            return isActionDone;
        });

        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        priceEdit.setText(sp.getString(KEY_AMOUNT, BigDecimal.ZERO.toString()));
    }

    private void onAmountChange(@NonNull BigDecimal newAmount) {
        amount = newAmount;

        if (buyButton != null) {
            buyButton.setEnabled(validateAmount());
        }
    }

    private void saveAmount() {
        PreferenceManager.getDefaultSharedPreferences(this).edit()
                .putString(KEY_AMOUNT, amount.toPlainString())
                .apply();
    }

    private boolean validateAmount() {
        return amount.compareTo(BigDecimal.ZERO) > 0;
    }

    @NonNull
    private static Set<PaymentMethodType> getPaymentMethodTypes(Settings settings) {
        final Set<PaymentMethodType> paymentMethodTypes = new HashSet<>();

        if (settings.isYandexMoneyAllowed()) {
            paymentMethodTypes.add(PaymentMethodType.YANDEX_MONEY);
        }

        if (settings.isNewCardAllowed()) {
            paymentMethodTypes.add(PaymentMethodType.BANK_CARD);
        }

        if (settings.isSberbankOnlineAllowed()) {
            paymentMethodTypes.add(PaymentMethodType.SBERBANK);
        }

        if (settings.isGooglePayAllowed()) {
            paymentMethodTypes.add(PaymentMethodType.GOOGLE_PAY);
        }

        return paymentMethodTypes;
    }
}
