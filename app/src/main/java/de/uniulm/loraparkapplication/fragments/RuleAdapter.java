package de.uniulm.loraparkapplication.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import de.uniulm.loraparkapplication.R;
import de.uniulm.loraparkapplication.models.Rule;

public class RuleAdapter extends RecyclerView.Adapter<RuleAdapter.ViewHolder> {

    private Rule[] rules;
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView ruleNameTextView;
        private final TextView ruleDescriptionTextView;
        private final CheckBox ruleIsActiveCheckbox;

        public ViewHolder(View v) {
            super(v);
            this.ruleNameTextView = (TextView)v.findViewById(R.id.text_view_rule_name);
            this.ruleDescriptionTextView = (TextView) v.findViewById(R.id.text_view_rule_description);
            this.ruleIsActiveCheckbox = v.findViewById(R.id.checkbox_rule_is_active);
        }

        public void bind(final Rule rule){
            //TODO: NEEDED FOR SELECTION
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

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rule, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){

        holder.ruleNameTextView.setText(rules[position].getName());
        holder.ruleDescriptionTextView.setText(rules[position].getDescription());
        holder.ruleIsActiveCheckbox.setChecked(rules[position].getIsActive());
    }

    public void updateRules(Rule[] rules){
        this.rules = rules;
        notifyDataSetChanged();
    }
}
