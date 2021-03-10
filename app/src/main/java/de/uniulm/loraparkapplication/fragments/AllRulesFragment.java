package de.uniulm.loraparkapplication.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import de.uniulm.loraparkapplication.R;
import de.uniulm.loraparkapplication.RuleOverviewActivity;
import de.uniulm.loraparkapplication.models.Resource;
import de.uniulm.loraparkapplication.models.Rule;
import de.uniulm.loraparkapplication.viewmodels.RuleOverviewViewModel;

public class AllRulesFragment extends Fragment {

    private RuleAdapter adapter;
    private RuleOverviewViewModel mRuleOverviewViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        RecyclerView ruleRecycler = (RecyclerView) inflater.inflate(R.layout.fragment_all_rules, container, false);

        Rule[] rules = {};

        this.adapter = new RuleAdapter(rules);
        ruleRecycler.setAdapter(this.adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(RecyclerView.VERTICAL);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(ruleRecycler.getContext(), layoutManager.getOrientation());
        ruleRecycler.addItemDecoration(dividerItemDecoration);

        ruleRecycler.setLayoutManager(layoutManager);

        return ruleRecycler;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        this.mRuleOverviewViewModel =  new ViewModelProvider(getActivity()).get(RuleOverviewViewModel.class);

        this.mRuleOverviewViewModel.getAllRules().observe(getViewLifecycleOwner(), new Observer<Resource<List<Rule>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<Rule>> rulesResource) {

                if(rulesResource != null) {
                    if (rulesResource.status == Resource.Status.SUCCESS) {

                        if (rulesResource.data != null && rulesResource.data.size() > 0) {
                            Rule[] ruleArray = rulesResource.data.toArray(new Rule[0]);
                            AllRulesFragment.this.adapter.updateRules(ruleArray);
                        }

                    } else if (rulesResource.status == Resource.Status.ERROR) {
                        // Failure to retrieve or parse the data
                        String message = getResources().getString(R.string.error_sensor_descriptions_not_loaded) + " (" + rulesResource.message + ")";
                        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                    } else {
                        // Data loading: future TODO: add loading animation
                    }
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        RuleOverviewActivity parentActivity = (RuleOverviewActivity) getActivity();
        if(parentActivity != null){
            Boolean refreshFragment = parentActivity.getRefreshAllRulesFragments();
            if(refreshFragment){
                this.mRuleOverviewViewModel.refresh();
                parentActivity.setRefreshAllRules(false);
            }
        }
    }
}