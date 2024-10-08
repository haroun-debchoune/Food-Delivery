package com.example.fooddelivery.fragments.loginfragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.fooddelivery.R;
import com.example.fooddelivery.helper.SharedPreferences;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class GetStartedFragment extends Fragment {

    private Button getStartedButton;
    private ProgressBar progressBar;
    private SharedPreferences preferences;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_get_started, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getStartedButton = view.findViewById(R.id.getStartedButton);
        progressBar = view.findViewById(R.id.getStartedProgressBar);
        progressBar.setVisibility(View.GONE);
        NavController navController = Navigation.findNavController(view);
        preferences = new SharedPreferences(requireContext());
        if (preferences.getBoolean("isNotFirst")){
            navController.navigate(R.id.action_getStartedFragment_to_loginFragment);
        }

        getStartedButton.setOnClickListener(v -> {
            preferences.putBoolean("isNotFirst", true);
            progressBar.setVisibility(View.VISIBLE);
            getStartedButton.setText("");
            navController.navigate(R.id.action_getStartedFragment_to_loginFragment);
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

    }
}