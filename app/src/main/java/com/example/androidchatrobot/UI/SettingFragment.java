package com.example.androidchatrobot.UI;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.appcompat.widget.TooltipCompat;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.androidchatrobot.Manager.DataManager;
import com.example.androidchatrobot.R;
import com.example.androidchatrobot.pojo.Setting;
import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingFragment extends Fragment implements SeekBar.OnSeekBarChangeListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private Setting setting;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SettingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    View v;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(getActivity()==null){
            return null;
        }
        setting= DataManager.GetInstance().getSetting(getActivity());
        v= inflater.inflate(R.layout.fragment_setting, container, false);
        setSpinner();
        SetSeekBar();
        SetToken();
        return v;
    }
    List<String> visionModels;
    private void setSpinner(){
        //imageSpinner
        Spinner ImageSpinner=v.findViewById(R.id.spinnerImage);
        ArrayAdapter<CharSequence> arrayAdapter1=ArrayAdapter.createFromResource(
                getActivity(),
                R.array.image_quality,
                android.R.layout.simple_spinner_item
        );
        setting.setDetailName(arrayAdapter1.getItem(setting.getDetailType()).toString());
        arrayAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ImageSpinner.setAdapter(arrayAdapter1);
        ImageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setting.setDetailType(position);
                setting.setDetailName(parent.getItemAtPosition(position).toString());
                DataManager.GetInstance().saveSetting(getActivity());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ImageSpinner.setSelection(setting.getDetailType());


        //modelSpinner
        RelativeLayout ImageSpinnerLayout=v.findViewById(R.id.imageSpinners);
        visionModels=DataManager.GetInstance().getVisionModels();
        Spinner modelSpinner=v.findViewById(R.id.spinnerModel);
        ArrayAdapter<CharSequence> arrayAdapter=ArrayAdapter.createFromResource(
                getActivity(),
                R.array.model_array,
                android.R.layout.simple_spinner_item
        );
        setting.setModelName(arrayAdapter.getItem(setting.getModelType()).toString());
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modelSpinner.setAdapter(arrayAdapter);
        modelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (visionModels.contains(parent.getItemAtPosition(position).toString())){
                    ImageSpinnerLayout.setVisibility(View.VISIBLE);
                }else{
                    ImageSpinnerLayout.setVisibility(View.GONE);
                }
                setting.setModelType(position);
                setting.setModelName(parent.getItemAtPosition(position).toString());
                DataManager.GetInstance().saveSetting(getActivity());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        modelSpinner.setSelection(setting.getModelType());



    }

    private void SetSeekBar(){
        SeekBar ContextSeekBar=v.findViewById(R.id.seekBarContext);
        ContextSeekBar.setOnSeekBarChangeListener(this);
        ContextSeekBar.setProgress(setting.getMaxContextNumber());
        ImageButton ContextInfoButton=v.findViewById(R.id.ContextInfoButton);
        ContextInfoButton.setOnClickListener(
                (View v)->{
                    new AlertDialog.Builder(v.getContext())
                            .setTitle("最大上下文限制数量")
                            .setMessage(getString(R.string.MaxContextCountInfo))
                            .setPositiveButton(android.R.string.ok, null)
                            .show();
                }
        );




        SeekBar TemperatureSeekBar=v.findViewById(R.id.seekBarTemperature);
        TemperatureSeekBar.setOnSeekBarChangeListener(this);
        TemperatureSeekBar.setProgress((int) Math.round(setting.getTemperature()*10));
        ImageButton TemperatureInfoButton=v.findViewById(R.id.TemperatureInfoButton);
        TemperatureInfoButton.setOnClickListener(
                (View v)->{
                    new AlertDialog.Builder(v.getContext())
                            .setTitle("温度参数")
                            .setMessage(getString(R.string.MaxTemperatureInfo))
                            .setPositiveButton(android.R.string.ok, null)
                            .show();
                }
        );
    }

    private void SetToken(){
        EditText TokenEditText=v.findViewById(R.id.edit_text_token);
        TokenEditText.setText(setting.getApiToken());
        CheckBox TokenCheckBox=v.findViewById(R.id.token_show);
        TokenEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(getActivity()==null){
                    return;
                }
                setting.setApiToken(s.toString());
                DataManager.GetInstance().saveSetting(getActivity());
            }
        });
        TokenCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    TokenEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else{
                    TokenEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
        TokenCheckBox.setChecked(false);
        TokenEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(getActivity()==null){
            return;
        }
        int id=seekBar.getId();
        if(id==R.id.seekBarTemperature){
            Double value=(double) progress / 10;
            setting.setTemperature(value);
            TextView TemperatureText=v.findViewById(R.id.TemperatureTextRight);
            TemperatureText.setText(String.format("%.1f",value));

        }else if(id==R.id.seekBarContext){
            setting.setMaxContextNumber(progress);
            TextView ContextText=v.findViewById(R.id.ContextTextRight);
            ContextText.setText(String.valueOf(progress));
        }
        DataManager.GetInstance().saveSetting(getActivity());
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

}