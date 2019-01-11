package com.dolan.user.memorize;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Badmeat on 05/03/2018.
 */

public class ShowPhotoActivity extends AppCompatActivity {

    private MyDbManager manager;
    public static final String TAG = ShowPhotoActivity.class.getSimpleName();

    private List<String> tanggal = new ArrayList<>();
    private List<String> urls = new ArrayList<>();
    private List<String> titles = new ArrayList<>();
    private List<Integer> liked = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecycleAdapter adapter;
    private MaterialSearchView searchView;

    /**
     * Button For Paginated
     */
    private Button next;
    private Button prev;
    private List<String> dete;
    private int pageCount;
    private int increment = 0;
    private int TOTAL_LIST_ITEM;
    private int NUM_ITEM_PAGE = 5;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_container);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Memorize");
        manager = new MyDbManager(ShowPhotoActivity.this, null, null, 1);
        ShowData();
        ShowNav();
        searchView = findViewById(R.id.search_view);
        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {
                ShowNav();
            }
        });
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText != null && !newText.isEmpty()) {
                    List<Content> beList = manager.GetContent(newText);
                    List<String> lstTitle = new ArrayList<>();
                    List<String> lstTanggal = new ArrayList<>();
                    List<Integer> lstLiked = new ArrayList<>();
                    List<String> lstUrls = new ArrayList<>();
                    for (Content content : beList) {
                        lstTitle.add(content.getDeskripsi());
                        lstTanggal.add(content.getTanggal());
                        lstLiked.add(content.getLiked());
                        lstUrls.add(content.getKode());
                    }
                    layoutManager = new GridLayoutManager(ShowPhotoActivity.this, 1);
                    recyclerView = findViewById(R.id.my_recycler);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(layoutManager);
                    adapter = new RecycleAdapter(lstUrls, ShowPhotoActivity.this, lstTitle, lstTanggal, lstLiked, lstUrls);
                    recyclerView.setAdapter(adapter);
                } else {
                    ShowNav();
                }
                return true;
            }
        });

        /**
         * Fingding Button
         */
        next = findViewById(R.id.next);
        prev = findViewById(R.id.prev);
        prev.setEnabled(false);

        int val = TOTAL_LIST_ITEM % NUM_ITEM_PAGE;
        val = val == 0 ? 0 : 1;
        pageCount = TOTAL_LIST_ITEM / NUM_ITEM_PAGE + val;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        searchView.setMenuItem(menuItem);
        return true;
    }

    private void ShowData() {
        Cursor result = manager.ShowDataAll();
        if (result.getCount() < 0) {
            Toast.makeText(ShowPhotoActivity.this, "Data Kosong", Toast.LENGTH_SHORT).show();
            return;
        }
        TOTAL_LIST_ITEM = result.getCount();
        int index = 0;
        while (result.moveToNext()) {
            urls.add(result.getString(1));
            titles.add(result.getString(2));
            tanggal.add(result.getString(3));
            liked.add(Integer.valueOf(result.getString(5)));
            Log.d(TAG, "ShowData: " + index);
            if (index > 3) {
                break;
            }
            index++;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ShowPhotoActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void ShowNav() {
        layoutManager = new GridLayoutManager(ShowPhotoActivity.this, 1);
        recyclerView = findViewById(R.id.my_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecycleAdapter(urls, ShowPhotoActivity.this, titles, tanggal, liked, urls);
        recyclerView.setAdapter(adapter);
    }
}