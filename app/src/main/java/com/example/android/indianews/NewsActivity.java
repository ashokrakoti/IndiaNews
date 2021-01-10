package com.example.android.indianews;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.android.indianews.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewsActivity extends AppCompatActivity implements NewsItemClicked {

    private static final String MAIN_ACTIVITY_CLASS = "MainActivity.class";

    //object used for view binding
    private ActivityMainBinding binding;

    //adapter object
    private  NewsAdapter mAdapter;

    //these are for navigation drawer
    NavigationView nav;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        binding.progressBar.setVisibility(View.VISIBLE);

        binding.newsListView.setLayoutManager(new LinearLayoutManager(this));

        //checking the internet connection
        if(getActiveNetworkInfo()){
            fetchNews();
            mAdapter = new NewsAdapter(this);
            binding.newsListView.setAdapter(mAdapter);
        }else{
            binding.progressBar.setVisibility(View.GONE);
            binding.emptyView.setText(R.string.no_internet_tag);
        }


        //setting the custom toolbar
        setSupportActionBar(binding.toolbar);
        nav = binding.navMenu;
        drawerLayout = binding.drawer;

        //this is to toggle the nav menu
        toggle = new ActionBarDrawerToggle(this, drawerLayout, binding.toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId())
                {
                    case R.id.menu_top_news:
                        Toast.makeText(getApplicationContext(),"Top News", Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.menu_business:
                        Toast.makeText(getApplicationContext(),"Business News", Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.menu_entertainment:
                        Toast.makeText(getApplicationContext(),"Entertainment News", Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.menu_sports:
                        Toast.makeText(getApplicationContext(),"Sports News", Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.menu_science:
                        Toast.makeText(getApplicationContext(),"Science News", Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                }
                return true;
            }
        });
    }

    private void fetchNews(){
        String url = "https://newsapi.org/v2/top-headlines?country=in&apiKey=aab465815faa442f9b10a40e195c059f";
        List<NewsItem> newsItemList = new ArrayList<>();
        JsonObjectRequest jsonRequest = new JsonObjectRequest(
                Request.Method.GET,
                url, null,
                response -> {
                    //parsing the json response
                    try {
                        JSONArray articlesArray = response.getJSONArray("articles");

                        for (int i=0; i< articlesArray.length(); i++){
                            //extracting data out of each item at a time
                            JSONObject singleItem = articlesArray.getJSONObject(i);
                            NewsItem newsItem = new NewsItem(
                                    singleItem.getString("author"),
                                    singleItem.getString("title"),
                                    singleItem.getString("url"),
                                    singleItem.getString("urlToImage")
                            );
                            newsItemList.add(newsItem);
                        }
                         mAdapter.updateNews(newsItemList);
                        binding.progressBar.setVisibility(View.GONE);
                    } catch (JSONException e) {
                        Log.e(MAIN_ACTIVITY_CLASS, "Json Parsing error : "+ e.getMessage());
                    }
                }, error -> Log.e(MAIN_ACTIVITY_CLASS, "Volley error occurred :"+ error.getMessage())
        )
                {
                    /**
                     * Passing some request headers
                     */
                    @Override
                    public Map<String, String> getHeaders() {
                        HashMap<String, String> headers = new HashMap<>();
                        //headers.put("Content-Type", "application/json");
                        headers.put("User-Agent", "Mozilla/5.0");
                        return headers;
                    }
                };
        MySingleton.getInstance(this).addToRequestQueue(jsonRequest);
    }

    @Override
    public void OnItemClicked(NewsItem item) {
        //Toast.makeText(this, "item Clicked :"+item, Toast.LENGTH_LONG).show();
        //String url = ¨https://paul.kinlan.me/¨;
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(this, Uri.parse(item.getUrl()));
    }


    //this piece of code checks the internet connection availability.
    public boolean getActiveNetworkInfo() {
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }
}