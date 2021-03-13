package de.uniulm.loraparkapplication.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import de.uniulm.loraparkapplication.DownloadRuleActivity;
import de.uniulm.loraparkapplication.R;
import de.uniulm.loraparkapplication.RuleDetailActivity;
import de.uniulm.loraparkapplication.models.Rule;

public class RuleAdapter extends RecyclerView.Adapter<RuleAdapter.ViewHolder> {

    private Rule[] rules;
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView ruleNameTextView;
        private final TextView ruleDescriptionTextView;
        private final CheckBox ruleIsActiveCheckbox;
        private final View ruleItemView;

        public ViewHolder(View v) {
            super(v);
            this.ruleNameTextView = (TextView)v.findViewById(R.id.text_view_rule_name);
            this.ruleDescriptionTextView = (TextView) v.findViewById(R.id.text_view_rule_description);
            this.ruleIsActiveCheckbox = v.findViewById(R.id.checkbox_rule_is_active);
            this.ruleItemView = v.findViewById(R.id.rule_item_layout);
        }

        public void bind(final Rule rule){
            //TODO: SELECTION

            this.ruleNameTextView.setText(rule.getName());
            this.ruleDescriptionTextView.setText(rule.getDescription());
            this.ruleIsActiveCheckbox.setChecked(rule.getIsActive());

            this.ruleItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, RuleDetailActivity.class);
                    intent.putExtra(RuleDetailActivity.RULE_ID_EXTRA, rule.getId());
                    context.startActivity(intent);
                }
            });
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

        holder.bind(rules[position]);
    }

    public void updateRules(Rule[] rules){
        this.rules = rules;
        notifyDataSetChanged();
    }
}
