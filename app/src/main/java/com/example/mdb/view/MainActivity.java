package com.example.mdb.view;

import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.mdb.PaginationScrollListener;
import com.example.mdb.R;
import com.example.mdb.adapters.PaginationAdapter;
import com.example.mdb.entity.MovieInfo;
import com.example.mdb.view_models.MoviesListViewModel;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();
    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int currentPage = PAGE_START;
    private static final String DEFAULT_TITLE = "Batman";


    private PaginationAdapter paginationAdapter;
    private MoviesListViewModel moviesListViewModel;
    private LinearLayoutManager linearLayoutManager;
    private ProgressBar progressBar;
    private RecyclerView myRecyclerView;
    private static final String EXTRA_TITLE = "title";
    private SwipeRefreshLayout swipeContainer;
    private int TOTAL_ITEMS;
    private String title;
    private TextView errorMessage;
    private SharedPreferences mPreferences;
    private String sharedPrefFile = "com.example.android.movieSharedPreferences";
    private LiveData<List<MovieInfo>> moviesByTitleObservable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        title = mPreferences.getString(EXTRA_TITLE, DEFAULT_TITLE);

        doRefresh();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (title != null) {
            SharedPreferences.Editor preferencesEditor = mPreferences.edit();
            preferencesEditor.putString(EXTRA_TITLE, title);
            preferencesEditor.apply();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setQuery(title, false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                title = query;
                Log.d(TAG, "OnQueryText " + title);
                doRefresh();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                // Signal SwipeRefreshLayout to start the progress indicator
                swipeContainer.setRefreshing(true);
                doRefresh();
        }
        return super.onOptionsItemSelected(item);
    }

    private void doRefresh() {
        Log.d(TAG, "doRefresh");
        currentPage = PAGE_START;
        if (moviesByTitleObservable != null) {
            moviesByTitleObservable.removeObservers(this);
        }
        loadFirstPage();
        swipeContainer.setRefreshing(false);
    }

    private void init() {
        moviesListViewModel = new MoviesListViewModel(getApplication());
        paginationAdapter = new PaginationAdapter(this);
        linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        myRecyclerView = findViewById(R.id.movieRecycleView);
        errorMessage = findViewById(R.id.error_msg);

        progressBar = findViewById(R.id.main_progress);

        swipeContainer = findViewById(R.id.main_content);
        swipeContainer.setColorSchemeResources(android.R.color.holo_orange_dark);
        swipeContainer.setOnRefreshListener(this::doRefresh);

        myRecyclerView.setItemAnimator(new DefaultItemAnimator());
        myRecyclerView.setAdapter(paginationAdapter);
        myRecyclerView.setLayoutManager(linearLayoutManager);
        myRecyclerView.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;

                loadNextPage();
                Log.d(TAG, "Load more items");
            }

            @Override
            public boolean isLastPage() {
                Log.d(TAG, "IsLastPAge " + isLastPage);
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                Log.d(TAG, "isLoading " + isLoading);
                return isLoading;
            }
        });

    }

    private void loadFirstPage() {
        isLastPage = false;

        paginationAdapter.getMovies().clear();
        paginationAdapter.notifyDataSetChanged();

        moviesByTitleObservable = moviesListViewModel.getMoviesByTitle(title, currentPage);
        Log.d(TAG, "Load first page");
        showErrorMessage();

        moviesByTitleObservable.observe(this, new Observer<List<MovieInfo>>() {
            @Override
            public void onChanged(List<MovieInfo> movieInfos) {
                if (!movieInfos.isEmpty()) {
                    TOTAL_ITEMS = moviesListViewModel.getTotalResults();

                    hideErrorMessage();
                    Log.d(TAG, "OnChanged loadFirst " + movieInfos.size());
                    paginationAdapter.addAll(movieInfos);
                    isLoading = false;
                    moviesByTitleObservable.removeObserver(this);
                }
            }
        });
    }

    private void loadNextPage() {
        Log.d(TAG, "Load page" + currentPage);
        isLoading = true;

        moviesListViewModel.refreshMovies(title, currentPage);
        moviesByTitleObservable.observe(this, new Observer<List<MovieInfo>>() {
            @Override
            public void onChanged(List<MovieInfo> movieInfos) {
                if (!movieInfos.isEmpty() && movieInfos.size() > paginationAdapter.getItemCount() - 1) {

                    paginationAdapter.removeLoadingFooter();
                    isLoading = false;
                    Log.d(TAG, "OnChanged loadNext " + movieInfos.size());
                    for (MovieInfo m : movieInfos) {
                        boolean alreadyExists = paginationAdapter.getMovies().contains(m);
                        if (!alreadyExists) {
                            paginationAdapter.add(m);
                        }
                    }
                    boolean theLastPage = isTheLastPage();
                    if (theLastPage) {
                        isLastPage = true;
                    } else {
                        paginationAdapter.addLoadingFooter();
                    }
                    moviesByTitleObservable.removeObserver(this);
                }
            }
        });
    }

    private void showErrorMessage() {
        if (errorMessage.getVisibility() == View.INVISIBLE) {
            errorMessage.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);

        }
    }

    private void hideErrorMessage() {
        if (errorMessage.getVisibility() == View.VISIBLE) {
            errorMessage.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    private boolean isTheLastPage() {
        Log.d(TAG, "Total items " + TOTAL_ITEMS);
        Log.d(TAG, "Adapter item count " + paginationAdapter.getItemCount());
        return TOTAL_ITEMS - 1 <= paginationAdapter.getItemCount();
    }


}

