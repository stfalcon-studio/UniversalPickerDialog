package stfalcon.universalpickerdialogsample.ui;

import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.Toast;

import java.util.ArrayList;

import stfalcon.universalpickerdialog.UniversalPickerDialog;
import stfalcon.universalpickerdialogsample.R;
import stfalcon.universalpickerdialogsample.data.City;
import stfalcon.universalpickerdialogsample.data.Developer;
import stfalcon.universalpickerdialogsample.data.FakeData;
/*
 * Created by troy379 on 23.08.16.
 */
public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, UniversalPickerDialog.OnPickListener {

    private static final int KEY_SINGLE_PICK = 1;
    private static final int KEY_MULTI_PICK = 2;

    private ArrayList<City> citiesList;
    private Developer.Level[] levels;
    private Developer.Specialization[] specializations;

    @SuppressWarnings("all")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        citiesList = FakeData.getCitiesList();
        levels = FakeData.getDeveloperLevels();
        specializations = FakeData.getDeveloperSpecializations();

        findViewById(R.id.singlePickDialogButton)
                .setOnClickListener(this);

        findViewById(R.id.multiPickDialogButton)
                .setOnClickListener(this);

        findViewById(R.id.multiPickDefaultDialogButton)
                .setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.singlePickDialogButton:
                showCustomizedPicker(
                        R.string.single_pick_title,
                        KEY_SINGLE_PICK,
                        new UniversalPickerDialog.Input(0, citiesList));
                break;
            case R.id.multiPickDialogButton:
                showCustomizedPicker(
                        R.string.multi_pick_title,
                        KEY_MULTI_PICK,
                        new UniversalPickerDialog.Input(2, levels),
                        new UniversalPickerDialog.Input(0, specializations),
                        getFormattedCitiesInput()
                );
                break;
            case R.id.multiPickDefaultDialogButton:
                showDefaultPicker(
                        R.string.show_multi_pick_default_style,
                        KEY_MULTI_PICK,
                        new UniversalPickerDialog.Input(2, levels),
                        new UniversalPickerDialog.Input(0, specializations),
                        getFormattedCitiesInput()
                );
                break;
        }
    }

    @Override
    public void onPick(int[] selectedValues, int key) {
        String result = "";

        switch (key) {
            case KEY_SINGLE_PICK:
                result = String.format(
                        "%s (%s)",
                        citiesList.get(selectedValues[0]).getName(),
                        citiesList.get(selectedValues[0]).getCountry());
                break;
            case KEY_MULTI_PICK:
                Developer developer = new Developer(
                        levels[selectedValues[0]],
                        specializations[selectedValues[1]],
                        citiesList.get(selectedValues[2])
                );
                result = "You're looking for " + developer.toString();
                break;
        }

        Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
    }

    private UniversalPickerDialog.Input getFormattedCitiesInput() {
        UniversalPickerDialog.Input citiesInput =
                new UniversalPickerDialog.Input(0, citiesList);
        citiesInput.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                return getString(R.string.in) + " " + citiesList.get(value).getName();
            }
        });
        return citiesInput;
    }

    private void showCustomizedPicker(@StringRes int title, int key, UniversalPickerDialog.Input... inputs) {
        new UniversalPickerDialog.Builder(this)
                .setTitle(title)
                .setTitleColorRes(R.color.colorAccent)
                .setBackgroundColorRes(R.color.colorPrimary)
                .setContentTextColorRes(R.color.accentText)
                .setPositiveButtonText(R.string.positive_button)
                .setNegativeButtonText(R.string.negative_button)
                .setPositiveButtonColorRes(R.color.colorAccent)
                .setNegativeButtonColorRes(R.color.colorSecondary)
                .setContentTextSize(16)
                .setListener(this)
                .setInputs(inputs)
                .setKey(key)
                .show();
    }

    private void showDefaultPicker(@StringRes int title, int key, UniversalPickerDialog.Input... inputs) {
        new UniversalPickerDialog.Builder(this)
                .setTitle(title)
                .setListener(this)
                .setInputs(inputs)
                .setKey(key)
                .show();
    }
}
