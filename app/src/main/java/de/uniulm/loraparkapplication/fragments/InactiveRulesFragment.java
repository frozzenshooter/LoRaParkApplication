package de.uniulm.loraparkapplication.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.uniulm.loraparkapplication.R;
import de.uniulm.loraparkapplication.models.Rule;

public class InactiveRulesFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        RecyclerView ruleRecycler = (RecyclerView) inflater.inflate(R.layout.fragment_inactive_rules, container, false);

        Rule rule1 = new Rule();
        rule1.setName("Rule 1");
        rule1.setIsActive(false);

        Rule rule2 = new Rule();
        rule2.setName("Rule 2");
        rule2.setIsActive(false);

        Rule rule3 = new Rule();
        rule3.setName("Rule 3");
        rule3.setIsActive(false);

        Rule rule4 = new Rule();
        rule4.setName("Rule 4");
        rule4.setIsActive(false);

        Rule rule5 = new Rule();
        rule5.setName("Rule 5");
        rule5.setIsActive(false);

        Rule rule6 = new Rule();
        rule6.setName("Rule 6");
        rule6.setIsActive(false);

        Rule rule7 = new Rule();
        rule7.setName("Rule 7");
        rule7.setIsActive(false);

        Rule rule8 = new Rule();
        rule8.setName("Rule 8");
        rule8.setIsActive(false);

        Rule rule9 = new Rule();
        rule9.setName("Rule 9");
        rule9.setIsActive(false);

        Rule rule10 = new Rule();
        rule10.setName("Rule 10");
        rule10.setIsActive(false);

        Rule[] rules = {rule1, rule2, rule3, rule4, rule5, rule6, rule7, rule8, rule9, rule10};

        RuleAdapter adapter = new RuleAdapter(rules);
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
        // TODO: Use the ViewModel
    }

}