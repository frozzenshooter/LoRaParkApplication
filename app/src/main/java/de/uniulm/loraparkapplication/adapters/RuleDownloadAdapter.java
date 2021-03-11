package de.uniulm.loraparkapplication.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import de.uniulm.loraparkapplication.R;
import de.uniulm.loraparkapplication.models.DownloadRule;

public class RuleDownloadAdapter extends  RecyclerView.Adapter<RuleDownloadAdapter.ViewHolder> {

    private DownloadRule[] rules;
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView ruleNameTextView;
        private final TextView ruleDescriptionTextView;

        public ViewHolder(View v) {
            super(v);
            this.ruleNameTextView = (TextView)v.findViewById(R.id.text_view_rule_name);
            this.ruleDescriptionTextView = (TextView) v.findViewById(R.id.text_view_rule_description);
        }

        public void bind(final DownloadRule rule){
            //TODO: NEEDED FOR SELECTION
        }
    }

    public RuleDownloadAdapter(DownloadRule[] rules){
        this.rules = rules;
    }

    @Override
    public int getItemCount(){
        return this.rules.length;
    }

    @NotNull
    @Override
    public RuleDownloadAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rule, parent, false);
        return new RuleDownloadAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RuleDownloadAdapter.ViewHolder holder, int position){

        holder.ruleNameTextView.setText(rules[position].getName());
        holder.ruleDescriptionTextView.setText(rules[position].getDescription() == null ? "" : rules[position].getDescription() );
    }

    public void updateRules(DownloadRule[] rules){
        this.rules = rules;
        notifyDataSetChanged();
    }


}
