package de.uniulm.loraparkapplication.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import de.uniulm.loraparkapplication.R;
import de.uniulm.loraparkapplication.models.DownloadRule;

public class RuleDownloadAdapter extends  RecyclerView.Adapter<RuleDownloadAdapter.ViewHolder> {

    private DownloadRule[] rules;
    private HashSet<String> selectedRuleIds;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView ruleNameTextView;
        private final TextView ruleDescriptionTextView;
        private final View view;
        private final ImageView selectedIcon;

        public ViewHolder(View v) {
            super(v);
            this.ruleNameTextView = (TextView)v.findViewById(R.id.text_view_download_rule_name);
            this.ruleDescriptionTextView = (TextView) v.findViewById(R.id.text_view_download_rule_description);
            this.view = v.findViewById(R.id.item_download_rule);
            this.selectedIcon = v.findViewById(R.id.rule_download_selection);
        }

        public void bind(final DownloadRule rule){
            this.ruleNameTextView.setText(rule.getName());
            this.ruleDescriptionTextView.setText(rule.getDescription() == null ? "" : rule.getDescription() );

            if(selectedRuleIds.contains(rule.getId())){
                rule.setSelected(true);
                selectedIcon.setVisibility(View.VISIBLE);
            }else{
                rule.setSelected(false);
            }

            this.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(rule.isSelected()){
                        rule.setSelected(false);
                        selectedIcon.setVisibility(View.INVISIBLE);
                    }else{
                        rule.setSelected(true);
                        selectedIcon.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

    public RuleDownloadAdapter(DownloadRule[] rules){
        this.rules = rules;
        this.selectedRuleIds = new HashSet<>();
    }

    @Override
    public int getItemCount(){
        return this.rules.length;
    }

    @NotNull
    @Override
    public RuleDownloadAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_download_rule, parent, false);
        return new RuleDownloadAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RuleDownloadAdapter.ViewHolder holder, int position){
        holder.bind(rules[position]);
    }

    public void updateRules(@NonNull DownloadRule[] rules, @Nullable HashSet<String> prSelectedRules){
        this.rules = rules;
        if(prSelectedRules != null){
            this.selectedRuleIds = prSelectedRules;
        }
        notifyDataSetChanged();
    }


    public List<String> getSelectedRuleIds(){
        List<String> selectedRulesForExport = new ArrayList<>();

        for (DownloadRule rule : rules) {
            if (rule.isSelected()) {
                selectedRulesForExport.add(rule.getId());
            }
        }

        return selectedRulesForExport;
    }

}
