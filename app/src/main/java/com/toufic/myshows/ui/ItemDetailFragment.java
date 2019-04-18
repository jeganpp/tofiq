package com.toufic.myshows.ui;

import android.app.SearchManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.toufic.myshows.R;
import com.toufic.myshows.TvShowsViewModel;
import com.toufic.myshows.db.Episode;
import com.toufic.myshows.net.ApiResponse;
import com.toufic.myshows.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import static com.toufic.myshows.utils.Utils.cycleTextViewExpansion;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link ItemListActivity}
 * in two-pane mode (on tablets) or a {@link ItemDetailActivity}
 * on handsets.
 */
public class ItemDetailFragment extends Fragment {
    /**
     * The fragment argument representing the Tv Show title.
     */
    public static final String ARG_SERIES_TITLE = "series_title";

    /**
     * The fragment argument representing the Tv Show season number.
     */
    public static final String ARG_SEASON_NUMBER = "season_number";

    /**
     * The fragment argument representing the Tv Show series Id.
     */
    public static final String ARG_SERIES_ID = "series_id";

    private String mSeriesTitle;
    private String mSeasonNumber;
    private String mSeriesId;

    /**
     * ViewModel responsible for loading data and notifying of changes.
     */
    private TvShowsViewModel mTvShowsViewModel;

    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public ItemDetailFragment() {
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.searchview_menu, menu);
        SearchManager searchManager = (SearchManager) getActivity()
                .getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getActivity().getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                ItemDetailFragment.SimpleItemRecyclerViewAdapter adapter =
                        (SimpleItemRecyclerViewAdapter) mRecyclerView.getAdapter();
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                ItemDetailFragment.SimpleItemRecyclerViewAdapter adapter =
                        (SimpleItemRecyclerViewAdapter) mRecyclerView.getAdapter();
                adapter.getFilter().filter(query);
                return false;
            }
        });
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null && getArguments().containsKey(ARG_SERIES_TITLE)) {
            mSeriesTitle = getArguments().getString(ARG_SERIES_TITLE);
            mSeasonNumber = getArguments().getString(ARG_SEASON_NUMBER);
            mSeriesId = getArguments().getString(ARG_SERIES_ID);

            mTvShowsViewModel = ViewModelProviders.of(getActivity()).get(TvShowsViewModel.class);
            mTvShowsViewModel.loadSeasonInfo(mSeriesId, mSeasonNumber);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mRootView = inflater.inflate(R.layout.item_detail, container, false);

        mSwipeRefreshLayout = mRootView.findViewById(R.id.swiperefresh);
        CollapsingToolbarLayout appBarLayout = getActivity().findViewById(R.id.toolbar_layout);
        if (appBarLayout != null) {
            appBarLayout.setTitle(mSeriesTitle + " Season " + mSeasonNumber);
        }

        mProgressBar = mRootView.findViewById(R.id.pb_loading);
        mRecyclerView = mRootView.findViewById(R.id.item_detail_list);
        mTvShowsViewModel.getSeason().observe(getActivity(), new Observer<ApiResponse>() {
            @Override
            public void onChanged(@Nullable ApiResponse apiResponse) {
                if (apiResponse != null && apiResponse.episodes != null) {
                    setupRecyclerView(mRecyclerView, apiResponse.episodes);
                    Utils.runLayoutAnimation(mRecyclerView, R.anim.layout_fall_down_anim);
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
                            Utils.showLongToast(getActivity(), getResources().getString(R.string.error_loading));
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
                        mTvShowsViewModel.loadSeasonInfo(mSeriesId, mSeasonNumber);
                    }
                }
        );

        return mRootView;
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView, List<Episode> items) {
        recyclerView.setAdapter(new ItemDetailFragment.SimpleItemRecyclerViewAdapter(items));
    }

    /**
     * Recycler view adapter implements filterable to allow searching from search bar.
     */
    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<ItemDetailFragment.SimpleItemRecyclerViewAdapter.ViewHolder>
            implements Filterable {

        private final List<Episode> mValues;
        private List<Episode> mFilteredValues;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Online streaming is not supported...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        };

        SimpleItemRecyclerViewAdapter(List<Episode> items) {
            mValues = items;
            mFilteredValues = items;
        }

        @Override
        public ItemDetailFragment.SimpleItemRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_detail_list_content, parent, false);
            return new ItemDetailFragment.SimpleItemRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ItemDetailFragment.SimpleItemRecyclerViewAdapter.ViewHolder holder, int position) {
            holder.mTitle.setText(getResources().getString(R.string.episode,
                    mFilteredValues.get(position).getEpisodeNumber(), mFilteredValues.get(position).getTitle()));
            holder.mPlot.setText(mFilteredValues.get(position).getPlot());
            holder.mDirector.setText(getResources().getString(R.string.director,
                    mFilteredValues.get(position).getDirector()));
            holder.mWriter.setText(getResources().getString(R.string.writer,
                    mFilteredValues.get(position).getWriter()));
            holder.mRating.setText(getResources().getString(R.string.rating,
                    mFilteredValues.get(position).getRating()));

            Glide.with(getActivity())
                    .load(Utils.changeHttpToHttps(mFilteredValues.get(position).getPosterPath()))
                    .apply(new RequestOptions()
                            .placeholder(R.mipmap.ic_launcher)
                            .fitCenter())
                    .into(holder.mPoster);
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        @Override
        public int getItemCount() {
            return mFilteredValues.size();
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    String filterString = constraint.toString();
                    if (filterString.isEmpty()) {
                        mFilteredValues = mValues;
                    } else {
                        List<Episode> filteredList = new ArrayList<>();
                        for (Episode ep : mValues) {
                            if (ep.getTitle().toLowerCase().contains(filterString.trim().toLowerCase())) {
                                filteredList.add(ep);
                            }
                        }
                        mFilteredValues = filteredList;
                    }
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = mFilteredValues;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    mFilteredValues = (List<Episode>) results.values;
                    notifyDataSetChanged();
                }
            };
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mTitle;
            final TextView mPlot;
            final TextView mDirector;
            final TextView mWriter;
            final TextView mRating;
            final ImageView mPoster;

            ViewHolder(View view) {
                super(view);
                mTitle = view.findViewById(R.id.tv_title);
                mPlot = view.findViewById(R.id.tv_plot);
                mDirector = view.findViewById(R.id.tv_director);
                mWriter = view.findViewById(R.id.tv_writer);
                mRating = view.findViewById(R.id.tv_rating);
                mPoster = view.findViewById(R.id.img_episode_poster);

                mWriter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cycleTextViewExpansion(mWriter);
                    }
                });
            }
        }

    }
}
