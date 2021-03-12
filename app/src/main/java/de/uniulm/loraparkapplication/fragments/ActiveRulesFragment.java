package de.uniulm.loraparkapplication.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import de.uniulm.loraparkapplication.R;
import de.uniulm.loraparkapplication.RuleOverviewActivity;
import de.uniulm.loraparkapplication.adapters.RuleAdapter;
import de.uniulm.loraparkapplication.models.Resource;
import de.uniulm.loraparkapplication.models.Rule;
import de.uniulm.loraparkapplication.viewmodels.RuleOverviewViewModel;

public class ActiveRulesFragment extends Fragment {

    private RuleAdapter adapter;
    private RuleOverviewViewModel mRuleOverviewViewModel;

    public static ActiveRulesFragment newInstance() {
        return new ActiveRulesFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        RecyclerView ruleRecycler = (RecyclerView) inflater.inflate(R.layout.fragment_active_rules, container, false);

        Rule[] rules = {};

        this.adapter = new RuleAdapter(rules);
        ruleRecycler.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(RecyclerView.VERTICAL);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(ruleRecycler.getContext(), layoutManager.getOrientation());
        ruleRecycler.addItemDecoration(dividerItemDecoration);

        ruleRecycler.setLayoutManager(layoutManager);

        return ruleRecycler;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.mRuleOverviewViewModel =  new ViewModelProvider(getActivity()).get(RuleOverviewViewModel.class);

        this.mRuleOverviewViewModel.getActiveRules().observe(getViewLifecycleOwner(), new Observer<List<Rule>>() {
            @Override
            public void onChanged(@Nullable List<Rule> rules) {

                if(rules != null) {
                    Rule[] ruleArray = rules.toArray(new Rule[0]);
                    adapter.updateRules(ruleArray);
                }
            }
        });
    }
}