/*
 * Copyright (C) 2016 Alexander Krol stfalcon.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package stfalcon.universalpickerdialog;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;

/*
 * Created by troy379 on 23.08.16.
 */
public class UniversalPickerDialog
        implements DialogInterface.OnShowListener {

    protected Builder builder;
    protected ArrayList<MaterialNumberPicker> pickers;
    protected AlertDialog dialog;
    private LinearLayout layout;

    protected UniversalPickerDialog(Builder builder) {
        this.builder = builder;

        initPickers(builder.inputs);
        createView();
        createDialog();
    }

    /**
     * Displays the dialog
     * */
    public void show() {
        dialog.show();
    }

    /**
     * Cancel the dialog
     * */
    public void cancel() {
        dialog.cancel();
    }

    private void initPickers(Input... inputs) {
        this.pickers = new ArrayList<>();
        if (inputs != null) {
            for (Input input : inputs) {
                pickers.add(getPicker(input));
            }
        }
    }

    private MaterialNumberPicker
    getPicker(final Input input) {
        MaterialNumberPicker.Builder builder = new MaterialNumberPicker.Builder(this.builder.context);
        builder.minValue(0);
        builder.maxValue(input.list.size() - 1);
        builder.defaultValue(input.defaultPosition);
        builder.wrapSelectorWheel(true);
        builder.backgroundColor(Color.TRANSPARENT);

        if (this.builder.contentTextSize != 0) {
            builder.textSize(this.builder.contentTextSize);
        }
        if (this.builder.contentTextColor != 0) {
            builder.textColor(this.builder.contentTextColor);
        }

        if (input.formatter != null) {
            builder.formatter(input.formatter);
        } else {
            builder.formatter(new NumberPicker.Formatter() {
                @Override
                public String format(int value) {
                    return input.list.get(value).toString();
                }
            });
        }

        return builder.build();
    }

    private void createView() {
        layout = new LinearLayout(builder.context);
        layout.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        layout.setWeightSum((float)pickers.size());
        layout.setLayoutParams(params);

        for (MaterialNumberPicker picker : pickers) {
            LinearLayout.LayoutParams pickerParams = new LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.WRAP_CONTENT);

            pickerParams.weight = 1.0f;
            picker.setLayoutParams(pickerParams);

            layout.addView(picker);
        }
    }

    protected void createDialog() {
        this.dialog = new AlertDialog.Builder(builder.context)
                .setTitle(getTitle())
                .setView(layout)
                .setCancelable(true)
                .setNegativeButton(
                        getNegativeButtonText(),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                .setPositiveButton(
                        getPositiveButtonText(),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int[] selectedValues = new int[pickers.size()];
                                for (int i = 0; i < selectedValues.length; i++) {
                                    selectedValues[i] = pickers.get(i).getValue();
                                }
                                if (builder.listener != null)
                                    builder.listener.onPick(selectedValues, builder.key);
                            }
                        })
                .create();

        if (builder.backgroundColor != 0) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(builder.backgroundColor));
        }

        dialog.setOnShowListener(this);

    }

    private Spannable getTitle() {
        Spannable title = null;
        if (builder.title != null) {
            title = new SpannableString(builder.title);
            if (builder.titleColor != 0) {
                title.setSpan(
                        new ForegroundColorSpan(builder.titleColor),
                        0, title.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return title;
    }

    private String getNegativeButtonText() {
        return builder.negativeButtonText != null
                ? builder.negativeButtonText
                : builder.context.getString(android.R.string.cancel);
    }

    private String getPositiveButtonText() {
        return builder.positiveButtonText != null
                ? builder.positiveButtonText
                : builder.context.getString(android.R.string.ok);
    }

    @Override
    public void onShow(DialogInterface dialog) {
        if (builder.negativeButtonColor != 0) {
            this.dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
                    .setTextColor(builder.negativeButtonColor);
        }
        if (builder.positiveButtonColor != 0) {
            this.dialog.getButton(DialogInterface.BUTTON_POSITIVE)
                    .setTextColor(builder.positiveButtonColor);
        }
    }

    /**
     * Interface definition for a callback to be invoked when data is picked
     * */
    public interface OnPickListener {

        /**
         * Called when data has been picked
         *
         * @param selectedValues array with selected indices in the order in which {@link Input}s were added

         * */
        void onPick(int[] selectedValues, int key);
    }

    /**
     * Wrapper for representing a data set with default position in list
     * */
    public static class Input {

        private int defaultPosition;
        private AbstractList<?> list;
        private NumberPicker.Formatter formatter;

        /**
         * Constructor for data represented in {@link AbstractList}
         *
         * @param defaultPosition is a position of item which selected by default
         * @param list list of objects
         * */
        public Input(int defaultPosition, AbstractList<?> list) {
            this.defaultPosition = defaultPosition;
            this.list = list;
        }

        /**
         * Constructor for data represented in array
         *
         * @param defaultPosition is a position of item which selected by default
         * @param array array of objects
         * */
        public <T> Input(int defaultPosition, T[] array) {
            this.defaultPosition = defaultPosition;
            this.list = new ArrayList<>(Arrays.asList(array));
        }

        /**
         * Set {@link android.widget.NumberPicker.Formatter} for format current value into a string for presentation
         * */
        public void setFormatter(NumberPicker.Formatter formatter) {
            this.formatter = formatter;
        }

    }

    /**
     * Builder class for {@link UniversalPickerDialog}
     * */
    public static class Builder {

        private Context context;
        private @ColorInt int positiveButtonColor, negativeButtonColor,
                titleColor, backgroundColor, contentTextColor;
        private float contentTextSize;
        private int key;
        private String title;
        private String negativeButtonText, positiveButtonText;
        private OnPickListener listener;
        private Input[] inputs;

        /**
         * Constructor using a context for this builder and the {@link UniversalPickerDialog} it creates.
         */
        public Builder(Context context) {
            this.context = context;
        }

        /**
         * Set text color resource for negative and positive buttons
         *
         * @return This Builder object to allow for chaining of calls to set methods
         * */
        @SuppressWarnings("deprecation")
        public Builder setButtonsColorRes(@ColorRes int color) {
            return this.setButtonsColor(context.getResources().getColor(color));
        }

        /**
         * Set text color int for negative and positive buttons
         *
         * @return This Builder object to allow for chaining of calls to set methods
         * */
        public Builder setButtonsColor(@ColorInt int color) {
            this.positiveButtonColor = negativeButtonColor = color;
            return this;
        }

        /**
         * Set text color resource for positive button
         *
         * @return This Builder object to allow for chaining of calls to set methods
         * */
        @SuppressWarnings("deprecation")
        public Builder setPositiveButtonColorRes(@ColorRes int color) {
            return this.setPositiveButtonColor(context.getResources().getColor(color));
        }

        /**
         * Set text color int for positive button
         *
         * @return This Builder object to allow for chaining of calls to set methods
         * */
        public Builder setPositiveButtonColor(@ColorInt int color) {
            this.positiveButtonColor = color;
            return this;
        }

        /**
         * Set text color resource for negative button
         *
         * @return This Builder object to allow for chaining of calls to set methods
         * */
        @SuppressWarnings("deprecation")
        public Builder setNegativeButtonColorRes(@ColorRes int color) {
            return this.setNegativeButtonColor(context.getResources().getColor(color));
        }

        /**
         * Set text color int for negative button
         *
         * @return This Builder object to allow for chaining of calls to set methods
         * */
        public Builder setNegativeButtonColor(@ColorInt int color) {
            this.negativeButtonColor = color;
            return this;
        }

        /**
         * Set text color resource for title
         *
         * @return This Builder object to allow for chaining of calls to set methods
         * */
        @SuppressWarnings("deprecation")
        public Builder setTitleColorRes(@ColorRes int color) {
            return this.setTitleColor(context.getResources().getColor(color));
        }

        /**
         * Set text color int for title
         *
         * @return This Builder object to allow for chaining of calls to set methods
         * */
        public Builder setTitleColor(@ColorInt int color) {
            this.titleColor = color;
            return this;
        }

        /**
         * Set background color resource for dialog
         *
         * @return This Builder object to allow for chaining of calls to set methods
         * */
        @SuppressWarnings("deprecation")
        public Builder setBackgroundColorRes(@ColorRes int color) {
            return this.setBackgroundColor(context.getResources().getColor(color));
        }

        /**
         * Set background color int for dialog
         *
         * @return This Builder object to allow for chaining of calls to set methods
         * */
        public Builder setBackgroundColor(@ColorInt int color) {
            this.backgroundColor = color;
            return this;
        }

        /**
         * Set text color resource data set items
         *
         * @return This Builder object to allow for chaining of calls to set methods
         * */
        @SuppressWarnings("deprecation")
        public Builder setContentTextColorRes(@ColorRes int color) {
            return this.setContentTextColor(context.getResources().getColor(color));
        }

        /**
         * Set text color int for data set items
         *
         * @return This Builder object to allow for chaining of calls to set methods
         * */
        public Builder setContentTextColor(@ColorInt int color) {
            this.contentTextColor = color;
            return this;
        }

        /**
         * Set text size sp for data set items
         *
         * @return This Builder object to allow for chaining of calls to set methods
         * */
        public Builder setContentTextSize(float size) {
            this.contentTextSize = size;
            return this;
        }

        /**
         * Set key(tag) for dialog.
         * It may be helpful if you using more than one picker in your activity/fragment.
         * This key will be returned in {@link OnPickListener#onPick(int[], int)}
         *
         * @return This Builder object to allow for chaining of calls to set methods
         * */
        public Builder setKey(int key) {
            this.key = key;
            return this;
        }

        /**
         * Set title text resource
         *
         * @return This Builder object to allow for chaining of calls to set methods
         * */
        public Builder setTitle(@StringRes int title) {
            return setTitle(context.getString(title));
        }

        /**
         * Set title text string
         *
         * @return This Builder object to allow for chaining of calls to set methods
         * */
        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        /**
         * Set positive button text resource
         *
         * @return This Builder object to allow for chaining of calls to set methods
         * */
        public Builder setPositiveButtonText(@StringRes int text) {
            return setPositiveButtonText(context.getString(text));
        }

        /**
         * Set positive button text string
         *
         * @return This Builder object to allow for chaining of calls to set methods
         * */
        public Builder setPositiveButtonText(String text) {
            this.positiveButtonText = text;
            return this;
        }

        /**
         * Set negative button text resource
         *
         * @return This Builder object to allow for chaining of calls to set methods
         * */
        public Builder setNegativeButtonText(@StringRes int text) {
            return setNegativeButtonText(context.getString(text));
        }

        /**
         * Set negative button text string
         *
         * @return This Builder object to allow for chaining of calls to set methods
         * */
        public Builder setNegativeButtonText(String text) {
            this.negativeButtonText = text;
            return this;
        }

        /**
         * Set {@link OnPickListener} for picker.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         * */
        public Builder setListener(OnPickListener listener) {
            this.listener = listener;
            return this;
        }

        /**
         * Set list of {@link Input}'s using varargs.
         * Each {@link Input} is representing an spinner in dialog with list of items from its {@link Input#list}
         *
         * @return This Builder object to allow for chaining of calls to set methods
         * */
        public Builder setInputs(Input... inputs) {
            this.inputs = inputs;
            return this;
        }

        /**
         * Creates a {@link UniversalPickerDialog} with the arguments supplied to this builder. It does not
         * {@link UniversalPickerDialog#show()} the dialog. This allows the user to do any extra processing
         * before displaying the dialog. Use {@link #show()} if you don't have any other processing
         * to do and want this to be created and displayed.
         */
        public UniversalPickerDialog build() {
            return new UniversalPickerDialog(this);
        }

        /**
         * Creates a {@link UniversalPickerDialog} with the arguments supplied to this builder and
         * {@link UniversalPickerDialog#show()}'s the dialog.
         */
        public UniversalPickerDialog show() {
            UniversalPickerDialog dialog = build();
            dialog.show();
            return dialog;
        }
    }
}
