package de.uniulm.loraparkapplication.fragments;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import de.uniulm.loraparkapplication.R;
import de.uniulm.loraparkapplication.models.Rule;
import de.uniulm.loraparkapplication.views.RuleView;

public class RuleAdapter extends RecyclerView.Adapter<RuleAdapter.ViewHolder> {

    private Rule[] rules;
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private RuleView ruleView;

        public ViewHolder(RuleView v) {
            super(v);
            ruleView = v;
        }
    }

    public RuleAdapter(Rule[] rules){
        this.rules = rules;
    }

    @Override
    public int getItemCount(){
        return this.rules.length;
    }

    @Override
    public RuleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){

        RuleView ruleView = (RuleView) LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_rule_view, parent, false);
        return new ViewHolder(ruleView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        RuleView ruleView = holder.ruleView;
        TextView ruleNameTextView = (TextView)ruleView.findViewById(R.id.text_view_rule_name);
        ruleNameTextView.setText(rules[position].getName());

        CheckBox ruleIsActiveCheckbox = ruleView.findViewById(R.id.checkbox_rule_is_active);
        ruleIsActiveCheckbox.setChecked(rules[position].getIsActive());
    }

    public void updateRules(Rule[] rules){
        this.rules = rules;
        notifyDataSetChanged();
    }

}
