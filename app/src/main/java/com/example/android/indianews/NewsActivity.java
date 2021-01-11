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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
    private static final String URI_NEWS = "https://newsapi.org/v2/top-headlines?";
    private static final String API_KEY = "aab465815faa442f9b10a40e195c059f";
    private static final String URI_DEFAULT ="https://newsapi.org/v2/top-headlines?country=in&apiKey=aab465815faa442f9b10a40e195c059f";

    //object used for view binding
    private ActivityMainBinding binding;

    //adapter object
    private final NewsAdapter mAdapter = new NewsAdapter(this);

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
            fetchNews(URI_DEFAULT);
            //mAdapter = new NewsAdapter(this);
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

        // this code sets upt the hamburger icon in the top left of screen.
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_dehaze_24);//setting the hamburger icon.

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
                        fetchNews("https://newsapi.org/v2/top-headlines?country=in&category=business&apiKey=aab465815faa442f9b10a40e195c059f");
                        binding.newsListView.setAdapter(mAdapter);
                        break;

                    case R.id.menu_entertainment:
                        Toast.makeText(getApplicationContext(),"Entertainment News", Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        fetchNews("https://newsapi.org/v2/top-headlines?country=in&category=entertainment&apiKey=aab465815faa442f9b10a40e195c059f");
                        binding.newsListView.setAdapter(mAdapter);
                        break;

                    case R.id.menu_sports:
                        Toast.makeText(getApplicationContext(),"Sports News", Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        fetchNews("https://newsapi.org/v2/top-headlines?country=in&category=sports&apiKey=aab465815faa442f9b10a40e195c059f");
                        binding.newsListView.setAdapter(mAdapter);
                        break;

                    case R.id.menu_science:
                        Toast.makeText(getApplicationContext(),"Science News", Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        fetchNews("https://newsapi.org/v2/top-headlines?country=in&category=science&apiKey=aab465815faa442f9b10a40e195c059f");
                        binding.newsListView.setAdapter(mAdapter);
                        break;

                    case R.id.menu_tech:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        fetchNews("https://newsapi.org/v2/top-headlines?country=in&category=technology&apiKey=aab465815faa442f9b10a40e195c059f");
                        binding.newsListView.setAdapter(mAdapter);
                        break;
                }
                return true;
            }
        });

        //setting the color of the loader
        binding.swipeContainer.setColorSchemeResources(R.color.yellow);
        // SetOnRefreshListener on SwipeRefreshLayout
        binding.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //binding.swipeContainer.setColorSchemeColors(getResources().getColor(R.color.colorOnSecondary));
               // binding.swipeContainer.setColorSchemeColors(Color.BLUE, Color.YELLOW, Color.BLUE);

                binding.swipeContainer.setRefreshing(false);
                fetchNews(URI_DEFAULT);
                try {
                    Thread.sleep(1000);// this is added to improve the reloading process animations inside the recycler view.
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // mAdapter = new NewsAdapter(MAIN_ACTIVITY_CLASS);
                binding.newsListView.setAdapter(mAdapter);
            }
        });
    }

    private void fetchNews(String url){
       // String url = "https://newsapi.org/v2/top-headlines?";
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


    /*
       this method opens the news article in a custom tab inside the app.
       this uses the concept of custom tabs.
     */
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
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public String buildUrl(String category){
        StringBuilder newUrl = new StringBuilder(URI_NEWS);//"https://newsapi.org/v2/top-headlines?"
        newUrl.append("country=in");
        newUrl.append("&");
        newUrl.append("category=").append(category);
        newUrl.append("&");
        newUrl.append("apikey=").append(API_KEY);
        Log.i("Final Url: ",newUrl.toString());
        return newUrl.toString();
    }
}