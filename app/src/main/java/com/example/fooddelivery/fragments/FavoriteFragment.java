package com.example.fooddelivery.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fooddelivery.R;
import com.example.fooddelivery.adapters.AddToSaveAdapter;
import com.example.fooddelivery.entities.FoodDetails;
import com.example.fooddelivery.helper.MyButtonClickListener;
import com.example.fooddelivery.helper.MySwipeHelper;
import com.example.fooddelivery.listeners.OnItemClickListener;
import com.example.fooddelivery.models.AddToCart;
import com.example.fooddelivery.models.Post;
import com.example.fooddelivery.viewmodel.DetailViewModel;

import java.util.ArrayList;
import java.util.List;


public class FavoriteFragment extends Fragment implements OnItemClickListener {



    private RecyclerView favRecyclerView;
    private ImageView backButton;
    private DetailViewModel detailViewModel;
    private AddToSaveAdapter cartAdapter;
    private ProgressBar progressBar;
    private LinearLayout noSavedItemLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorite, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        favRecyclerView = view.findViewById(R.id.favRecyclerView);
        backButton = view.findViewById(R.id.favBackImage);
        progressBar  = view.findViewById(R.id.favProgressBar);
        noSavedItemLayout = view.findViewById(R.id.noItemSavedFound);
        cartAdapter = new AddToSaveAdapter(requireContext(), this);
        detailViewModel = new ViewModelProvider(requireActivity()).get(DetailViewModel.class);
        detailViewModel.getAllSavedPost().observe(requireActivity(), postList -> {
            progressBar.setVisibility(View.VISIBLE);
            cartAdapter.submitList(postList);
            if (postList.isEmpty()){
                progressBar.setVisibility(View.GONE);
                noSavedItemLayout.setVisibility(View.VISIBLE);
            }else {
                progressBar.setVisibility(View.GONE);
            }
            cartAdapter.notifyDataSetChanged();
        });
        detailViewModel.getIsAllPostGet().observe(requireActivity(), isAllPostGet -> {
            if (isAllPostGet){
                progressBar.setVisibility(View.GONE);
                noSavedItemLayout.setVisibility(View.GONE);
            }else {
                progressBar.setVisibility(View.VISIBLE);
                noSavedItemLayout.setVisibility(View.GONE);
            }
        });

        setRecyclerView();
        MySwipeHelper swipeHelper = new MySwipeHelper(getContext(), favRecyclerView, 150) {
            @Override
            public void initiatMyButton(RecyclerView.ViewHolder viewHolder, List<MyButton> buffer) {
                buffer.add(new MyButton(
                        getContext(),
                        R.drawable.d_1,
                        Color.parseColor("#00FFFFFF"),
                        new MyButtonClickListener(){
                            @Override
                            public void onClick(int pos) {

                                AddToCart post = cartAdapter.getCurrentList().get(pos);
                                detailViewModel.removePost(post);
                                List<AddToCart> currentList = new ArrayList<>(cartAdapter.getCurrentList());
                                currentList.remove(pos);
                                cartAdapter.submitList(currentList);
                                cartAdapter.notifyItemRemoved(pos);
                                cartAdapter.notifyDataSetChanged();
                                if (currentList.isEmpty()){
                                    noSavedItemLayout.setVisibility(View.VISIBLE);
                                    cartAdapter.notifyDataSetChanged();
                                }
                                Toast.makeText(getContext(), "delete Button clicked!", Toast.LENGTH_SHORT).show();

                            }
                        }
                ));
            }
        };

    }

    private void setRecyclerView() {
        favRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity(),
                LinearLayoutManager.VERTICAL,false));
        favRecyclerView.setAdapter(cartAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        cartAdapter.notifyDataSetChanged();
        noSavedItemLayout.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        progressBar.setVisibility(View.GONE);
        noSavedItemLayout.setVisibility(View.GONE);
    }

    @Override
    public void onItemClicked(int position, Post post) {
        Intent intent = new Intent(requireContext(), FoodDetails.class);
        intent.putExtra("id", post.getId());
        intent.putExtra("category", post.getCategory());
        intent.putExtra("image", post.getImage());
        intent.putExtra("title", post.getTitle());
        intent.putExtra("price", String.valueOf(post.getPrice()));
        startActivity(intent);
    }
}