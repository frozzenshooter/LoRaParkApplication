package de.uniulm.loraparkapplication.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import de.uniulm.loraparkapplication.R;

public class RuleView extends ConstraintLayout {

    private TextView mRuleNameTextView;
    private CheckBox mIsActiveRuleCheckBox;

    public RuleView(Context context){
        super(context);
        initView();
    }

    public RuleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public RuleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public RuleView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView(){
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.rule_view, this, true);

        this.mRuleNameTextView = findViewById(R.id.text_view_rule_name);
        this.mIsActiveRuleCheckBox = findViewById(R.id.checkbox_rule_is_active);

    }

    public void setValues(@NonNull String ruleName, @NonNull boolean isActive){

        this.mRuleNameTextView.setText(ruleName);
        this.mIsActiveRuleCheckBox.setChecked(isActive);

    }
}
