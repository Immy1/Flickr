package sample.com.flickr.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sample.com.flickr.R;
import sample.com.flickr.adapter.PublicPhotosAdapter;
import sample.com.flickr.networkRequest.ApiClient;
import sample.com.flickr.networkRequest.ApiInterface;
import sample.com.flickr.networkRequest.NetworkStateReceiver;
import sample.com.flickr.responce.PublicPhotosResponce;

public class AuthorPublisedActivity extends AppCompatActivity implements NetworkStateReceiver.NetworkStateReceiverListener {
    RecyclerView publicPhotsList;
    PublicPhotosAdapter publicPhotosAdapter;
    TextView tvnNoView;
    String authorId;
    String authorName;
    TextView tvAuthorName;
    Toolbar toolbar;
    NetworkStateReceiver networkStateReceiver;
    CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author_published);
        publicPhotsList = findViewById(R.id.photos_list);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        tvnNoView = findViewById(R.id.tv_no_photo);
        tvAuthorName = findViewById(R.id.author_name);
        toolbar = findViewById(R.id.toolbar);
        authorId = (String) Objects.requireNonNull(getIntent().getExtras()).get("authorId");
        authorName = (String) Objects.requireNonNull(getIntent().getExtras()).get("authorName");
        toolbar.setTitle(authorName + "'s Photos");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        setActionBarColor(this);
        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
        registerReceiver(networkStateReceiver, new IntentFilter("android.net.wifi.WIFI_STATE_CHANGED"));
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        publicPhotsList.setLayoutManager(mLayoutManager);
        publicPhotsList.setItemAnimator(new DefaultItemAnimator());
        publicPhotsList.setLayoutManager(mLayoutManager);
    }

    @Override
    protected void onResume() {
        super.onResume();
        tvAuthorName.setText(authorName);
    }

    public void getPublicPhotos(String id, String lang, String format, String jsonCallBack) {
        final ProgressDialog pd = new ProgressDialog(AuthorPublisedActivity.this);
        pd.setCancelable(false);
        pd.setMessage("Processing..");
        pd.show();

        ApiClient.getClient().create(ApiInterface.class).getPublicPhotos(id, lang, format, jsonCallBack).enqueue(new Callback<PublicPhotosResponce>() {
            @Override
            public void onResponse(@NonNull Call<PublicPhotosResponce> call, @NonNull Response<PublicPhotosResponce> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getItems().size() > 0)
                        publicPhotosAdapter = new PublicPhotosAdapter(AuthorPublisedActivity.this, response.body().getItems());
                    publicPhotsList.setAdapter(publicPhotosAdapter);
                    tvnNoView.setVisibility(View.GONE);
                    pd.dismiss();
                } else {
                    tvnNoView.setVisibility(View.VISIBLE);
                    tvnNoView.setText(getResources().getString(R.string.server_error));
                    pd.dismiss();
                }

            }

            @Override
            public void onFailure(@NonNull Call<PublicPhotosResponce> call, @NonNull Throwable t) {
                pd.dismiss();
                tvnNoView.setVisibility(View.VISIBLE);
                tvnNoView.setText(getResources().getString(R.string.server_error));
                Log.i("ServerError", t.toString());
            }
        });
    }

    public static void setActionBarColor(Activity activity) {
        Window window = activity.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(activity, R.color.colorPrimary));
        }

    }

    @Override
    public void networkAvailable() {
        tvnNoView.setVisibility(View.GONE);
        getPublicPhotos("", "en-us", "json", "1");
    }

    @Override
    public void networkUnavailable() {
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, "No internet connection!", Snackbar.LENGTH_LONG)
                .setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    }
                });

        // Changing message text color
        snackbar.setActionTextColor(Color.RED);
    }

}
