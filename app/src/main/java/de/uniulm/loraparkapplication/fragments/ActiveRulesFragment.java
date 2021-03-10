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
import de.uniulm.loraparkapplication.SensorOverviewActivity;
import de.uniulm.loraparkapplication.models.Resource;
import de.uniulm.loraparkapplication.models.Rule;
import de.uniulm.loraparkapplication.models.SensorDescription;
import de.uniulm.loraparkapplication.viewmodels.RuleOverviewViewModel;

public class ActiveRulesFragment extends Fragment {

    public static ActiveRulesFragment newInstance() {
        return new ActiveRulesFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        RecyclerView ruleRecycler = (RecyclerView) inflater.inflate(R.layout.fragment_active_rules, container, false);

        Rule rule1 = new Rule();
        rule1.setName("Rule 1");
        rule1.setIsActive(true);

        Rule rule2 = new Rule();
        rule2.setName("Rule 2");
        rule2.setIsActive(true);

        Rule rule3 = new Rule();
        rule3.setName("Rule 3");
        rule3.setIsActive(true);

        Rule rule4 = new Rule();
        rule4.setName("Rule 4");
        rule4.setIsActive(true);

        Rule rule5 = new Rule();
        rule5.setName("Rule 5");
        rule5.setIsActive(true);

        Rule rule6 = new Rule();
        rule6.setName("Rule 6");
        rule6.setIsActive(true);

        Rule rule7 = new Rule();
        rule7.setName("Rule 7");
        rule7.setIsActive(true);

        Rule rule8 = new Rule();
        rule8.setName("Rule 8");
        rule8.setIsActive(true);

        Rule rule9 = new Rule();
        rule9.setName("Rule 9");
        rule9.setIsActive(true);

        Rule rule10 = new Rule();
        rule10.setName("Rule 10");
        rule10.setIsActive(true);

        Rule[] rules = {rule1, rule2, rule3, rule4, rule5, rule6, rule7, rule8, rule9, rule10};

        RuleAdapter adapter = new RuleAdapter(rules);
        ruleRecycler.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(RecyclerView.VERTICAL);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(ruleRecycler.getContext(), layoutManager.getOrientation());
        ruleRecycler.addItemDecoration(dividerItemDecoration);

        ruleRecycler.setLayoutManager(layoutManager);

        RuleOverviewViewModel mRuleOverviewViewModel =  new ViewModelProvider(this.getActivity()).get(RuleOverviewViewModel.class);

        mRuleOverviewViewModel.getAllRules().observe(getViewLifecycleOwner(), new Observer<Resource<List<Rule>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<Rule>> rulesResource) {

                if(rulesResource.status == Resource.Status.SUCCESS) {

                    Rule[] ruleArray = rulesResource.data.toArray(new Rule[0]);
                    adapter.updateRules(ruleArray);

                }else if (rulesResource.status == Resource.Status.ERROR){
                    // Failure to retrieve or parse the data
                    String message = getResources().getString(R.string.error_sensor_descriptions_not_loaded) + " ("+ rulesResource.message +")";
                    Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                }else{
                    // Data loading: future TODO: add loading animation
                }
            }
        });

        return ruleRecycler;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO: Use the ViewModel
    }

}