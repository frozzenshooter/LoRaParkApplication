package de.uniulm.loraparkapplication.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import de.uniulm.loraparkapplication.R;

public class KeyValueView extends LinearLayout {

    private String mKeyString;
    private String mValueString;
    private String mUnitString;

    private TextView mKeyTextView;
    private TextView mValueTextView;
    private TextView mUnitTextView;

    public KeyValueView(Context context) {
        super(context);
        initView();
    }

    public KeyValueView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public KeyValueView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public KeyValueView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.key_value_view, this, true);

        this.mKeyTextView = findViewById(R.id.key_value_view_key);
        this.mValueTextView = findViewById(R.id.key_value_view_value);
        this.mUnitTextView = findViewById(R.id.key_value_view_unit);

        this.mKeyTextView.setVisibility(GONE);
        this.mValueTextView.setVisibility(GONE);
        this.mUnitTextView.setVisibility(GONE);
    }

    public void setValues(@NonNull String key, @NonNull String value,@Nullable String unit){
        this.mKeyString = key;
        this.mValueString = value;
        this.mUnitString = unit;

        setupView();
    }

    public void setValueTextSize(float size){
        this.mValueTextView.setTextSize(size);
    }


    private void setupView(){

        //TODO: ONCLICK Listener to show timestamp (has to be also added in set values as optional parameter)

        if(this.mKeyString != null){
            this.mKeyTextView.setVisibility(VISIBLE);
            this.mKeyTextView.setText(this.mKeyString);
        }

        if(this.mValueString  != null){
            this.mValueTextView.setVisibility(VISIBLE);
            this.mValueTextView.setText(this.mValueString);
        }

        if(this.mUnitString != null){
            this.mUnitTextView.setVisibility(VISIBLE);
            this.mUnitTextView.setText(this.mUnitString);
        }
    }


}
