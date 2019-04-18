package com.toufic.myshows.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.toufic.myshows.R;
import com.toufic.myshows.TvShowsViewModel;
import com.toufic.myshows.db.TvShow;
import com.toufic.myshows.net.ApiResponse;
import com.toufic.myshows.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ItemListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    private TvShowsViewModel mTvShowsModel;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        if (getResources().getBoolean(R.bool.landscape_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        mRecyclerView = findViewById(R.id.item_list);
        mProgressBar = findViewById(R.id.pb_loading);
        mSwipeRefreshLayout = findViewById(R.id.swiperefresh);

        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the activity should be in two-pane mode.
            mTwoPane = true;
        }

        mTvShowsModel = ViewModelProviders.of(this).get(TvShowsViewModel.class);
        mTvShowsModel.loadTvShows();
        mTvShowsModel.getShows().observe(this, new Observer<ApiResponse>() {
            @Override
            public void onChanged(@Nullable ApiResponse apiResponse) {
                if (apiResponse != null && apiResponse.tvshows != null) {
                    setupRecyclerView(mRecyclerView, apiResponse.tvshows);
                    Utils.runLayoutAnimation(mRecyclerView, R.anim.layout_slide_left_to_right_anim);
                }
                if (apiResponse != null && apiResponse.status != null) {
                    switch (apiResponse.status) {
                        case LOADING:
                            mProgressBar.setVisibility(View.VISIBLE);
                            break;
                        case SUCCESS:
                            mProgressBar.setVisibility(View.GONE);
                            mSwipeRefreshLayout.setRefreshing(false);
                            break;
                        case ERROR:
                            mProgressBar.setVisibility(View.GONE);
                            Utils.showLongToast(ItemListActivity.this, getResources().getString(R.string.error_loading));
                            mSwipeRefreshLayout.setRefreshing(false);
                            break;
                    }
                }
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        mTvShowsModel.loadTvShows();
                    }
                }
        );
    }


    private void setupRecyclerView(@NonNull RecyclerView recyclerView, List<TvShow> items) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(this, items, mTwoPane));
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final ItemListActivity mParentActivity;
        private final List<TvShow> mValues;
        private final boolean mTwoPane;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickAction(view);
            }
        };

        SimpleItemRecyclerViewAdapter(ItemListActivity parent,
                                      List<TvShow> items,
                                      boolean twoPane) {
            mValues = items;
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        private void onClickAction(View view) {
            TvShow item = (TvShow) view.getTag(R.id.TAG_VIEW_HOLDER_POSITION);
            if (mTwoPane) {
                Bundle arguments = new Bundle();
                arguments.putString(ItemDetailFragment.ARG_SERIES_TITLE, item.getTitle());
                arguments.putString(ItemDetailFragment.ARG_SERIES_ID, item.getSeriesId());
                arguments.putString(ItemDetailFragment.ARG_SEASON_NUMBER, String.valueOf(
                        ((Spinner) view.getTag(R.id.TAG_VIEW_HOLDER_SPINNER_SELECTED))
                                .getSelectedItemPosition() + 1));
                ItemDetailFragment fragment = new ItemDetailFragment();
                fragment.setArguments(arguments);
                mParentActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.item_detail_container, fragment)
                        .commit();
            } else {
                Context context = view.getContext();
                Intent intent = new Intent(context, ItemDetailActivity.class);
                intent.putExtra(ItemDetailFragment.ARG_SERIES_TITLE, item.getTitle());
                intent.putExtra(ItemDetailFragment.ARG_SERIES_ID, item.getSeriesId());
                intent.putExtra(ItemDetailFragment.ARG_SEASON_NUMBER, String.valueOf(
                        ((Spinner) view.getTag(R.id.TAG_VIEW_HOLDER_SPINNER_SELECTED))
                                .getSelectedItemPosition() + 1));
                context.startActivity(intent);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mTitle.setText(mValues.get(position).getTitle());
            holder.mPlot.setText(mValues.get(position).getPlot());
            holder.mYear.setText(mValues.get(position).getYear());
            holder.mRating.setText(mValues.get(position).getRating());

            Glide.with(ItemListActivity.this)
                    .load(Utils.changeHttpToHttps(mValues.get(position).getPosterPath()))
                    .into(holder.mPoster);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(ItemListActivity.this,
                    android.R.layout.simple_spinner_item,
                    createSpinnerAdapter(mValues.get(position).getSeasonCount()));
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            holder.mSpinner.setAdapter(adapter);

            holder.itemView.setTag(R.id.TAG_VIEW_HOLDER_POSITION, mValues.get(position));
            holder.itemView.setTag(R.id.TAG_VIEW_HOLDER_SPINNER_SELECTED, holder.mSpinner);
            holder.itemView.setOnClickListener(mOnClickListener);
            holder.mSpinner.setSelection(0, false);
            holder.mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    onClickAction(holder.itemView);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        private List<String> createSpinnerAdapter(String seasonCount) {
            List<String> spinnerArray = new ArrayList<>();
            for (int i = 1; i <= Integer.parseInt(seasonCount); i++) {
                spinnerArray.add("Season " + i);
            }
            return spinnerArray;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mTitle;
            final TextView mPlot;
            final TextView mYear;
            final TextView mRating;
            final ImageView mPoster;
            final Spinner mSpinner;

            ViewHolder(View view) {
                super(view);
                mTitle = view.findViewById(R.id.tv_title);
                mPlot = view.findViewById(R.id.tv_plot);
                mYear = view.findViewById(R.id.tv_year);
                mRating = view.findViewById(R.id.tv_rating);
                mPoster = view.findViewById(R.id.img_show_poster);
                mSpinner = view.findViewById(R.id.spr_seasons);
            }
        }
    }
}
