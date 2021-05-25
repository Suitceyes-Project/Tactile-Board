package com.be.better.tactileboard.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.be.better.tactileboard.utils.DictEntryRecyclerViewAdapter;
import com.be.better.tactileboard.R;
import com.be.better.tactileboard.databinding.FragmentDictionaryBinding;
import com.be.better.tactileboard.viewmodels.DictionaryViewModel;

import java.util.List;

public class DictionaryFragment extends Fragment {
    private FragmentDictionaryBinding binding;
    private DictionaryViewModel viewModel;
    private DictEntryRecyclerViewAdapter adapter;

    public DictionaryFragment(){
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = new ViewModelProvider(this).get(DictionaryViewModel.class);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        viewModel.getWords().observe(getViewLifecycleOwner(), new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                adapter.notifyDataSetChanged();
            }
        });

        initRecyclerView();
    }

    private void initRecyclerView(){
        RecyclerView recyclerView = getView().findViewById(R.id.recycler_view);
        adapter = new DictEntryRecyclerViewAdapter(getContext(), viewModel.getWords().getValue());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDictionaryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}