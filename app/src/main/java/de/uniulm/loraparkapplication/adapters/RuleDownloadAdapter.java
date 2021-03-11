package de.uniulm.loraparkapplication.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import de.uniulm.loraparkapplication.R;
import de.uniulm.loraparkapplication.models.DownloadRule;

public class RuleDownloadAdapter extends  RecyclerView.Adapter<RuleDownloadAdapter.ViewHolder> {

    private DownloadRule[] rules;
    private HashSet<Integer> checkedRules;

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

            this.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Integer position = getAdapterPosition();
                    if(checkedRules.contains(position)){
                        checkedRules.remove(position);
                        selectedIcon.setVisibility(View.INVISIBLE);
                    }else{
                        checkedRules.add(position);
                        selectedIcon.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

    public RuleDownloadAdapter(DownloadRule[] rules){
        this.rules = rules;
        this.checkedRules = new HashSet<>();
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

    public void updateRules(DownloadRule[] rules){
        this.rules = rules;
        notifyDataSetChanged();
    }


    public List<DownloadRule> getSelectedDownloadRules(){
        List<DownloadRule> selectedRules = new ArrayList<>();

        for(Integer position: checkedRules){
            selectedRules.add(rules[position]);
        }

        return selectedRules;
    }

}
