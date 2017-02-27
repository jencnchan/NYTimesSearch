package com.androidpractice.jennifer.nytimessearch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import com.androidpractice.jennifer.nytimessearch.Article;
import com.androidpractice.jennifer.nytimessearch.ArticleArrayAdapter;
import com.androidpractice.jennifer.nytimessearch.R;
import com.androidpractice.jennifer.nytimessearch.SearchDialogFragment;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

import static com.androidpractice.jennifer.nytimessearch.R.id.ivImage;

public class SearchActivity extends AppCompatActivity implements SearchDialogFragment.SearchDialogFragmentListener {

    EditText etQuery;
    GridView gvResults;
    Button btnSearch;
    ArrayList<Article> articles;
    ArticleArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupViews();
    }

    private void showEditDialog() {
        FragmentManager fm = getSupportFragmentManager();
        SearchDialogFragment searchDialogFragment = new SearchDialogFragment();
        searchDialogFragment.newInstance();
        searchDialogFragment.show(fm, "dialog_fragment");
    }

    // This method is invoked in the activity when the listener is triggered
    // Access the data result passed to the activity here
    @Override
    public void onFinishEditDialog(Bundle bundle) {

        String beginDate_value = bundle.getString("beginDate_value");
        String sortOrder_value = bundle.getString("sortOrder_value");
        String news_value = bundle.getString("news_value");
        String formattedNews = "";
        if (!news_value.isEmpty()) {
            formattedNews = formatString(news_value);
        }

        RequestParams params = new RequestParams();
        params.put("api-key", "a419033d04c741ccbf53858aba05220e");
        params.put("page", 0);

        params.put("begin_date", beginDate_value);
        params.put("sort", sortOrder_value);
        if (!formattedNews.isEmpty()) {
            params.put("fq", formattedNews);
        }

        loadSearch(params);

    }

    private String formatString(String news) {
        String[] array = news.split(" ");

        StringBuilder sb = new StringBuilder();
        sb.append("news_desk:(");
        for (String anArray : array) {
            sb.append("\"");
            sb.append(anArray);
            sb.append("\"");
            sb.append("%20");
        }
        sb.append(")");
        return sb.toString();
    }


    public void setupViews() {
        etQuery = (EditText) findViewById(R.id.etQuery);
        gvResults = (GridView) findViewById(R.id.gvResults);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        articles = new ArrayList<>();
        adapter = new ArticleArrayAdapter(this, articles);
        gvResults.setAdapter(adapter);

        //hook up listener for grid click
        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //create an intent to display the article
                Intent i = new Intent(getApplicationContext(), ArticleActivity.class);
                //get the article to display
                Article article = articles.get(position);
                //pass in that article into intent
                i.putExtra("url", article.getWebUrl());
                //launch the activity
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.actionBarSearch) {
            showEditDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onArticleSearch(View view) {

        String query = etQuery.getText().toString();
        RequestParams params = new RequestParams();
        params.put("api-key", "a419033d04c741ccbf53858aba05220e");
        params.put("page", 0);
        params.put("q", query);

        loadSearch(params);
    }

    private void loadSearch(RequestParams params) {

        AsyncHttpClient client = new AsyncHttpClient();
        String url = "http://api.nytimes.com/svc/search/v2/articlesearch.json";

        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG", response.toString());
                JSONArray articleJsonResults = null;

                try {
                    articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                    adapter.clear();
                    adapter.addAll(Article.fromJSONArray(articleJsonResults));
                    Log.d("DEBUG", articles.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }

}
