package sample.com.flickr.activity;

import android.app.ProgressDialog;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sample.com.flickr.R;
import sample.com.flickr.adapter.PublicPhotosAdapter;
import sample.com.flickr.networkRequest.ApiClient;
import sample.com.flickr.networkRequest.ApiInterface;
import sample.com.flickr.networkRequest.NetworkStateReceiver;
import sample.com.flickr.responce.PublicPhotosResponce;

public class HomeActivity extends AppCompatActivity implements NetworkStateReceiver.NetworkStateReceiverListener {
    NetworkStateReceiver networkStateReceiver;
    RecyclerView publicPhotsList;
    PublicPhotosAdapter publicPhotosAdapter;
    TextView tvnNoView;
    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        publicPhotsList = findViewById(R.id.public_photo_list);
        tvnNoView = findViewById(R.id.tv_no_photo);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
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
    public void networkAvailable() {
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, "Welcome to Flickr!", Snackbar.LENGTH_LONG)
                .setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    }
                });

        // Changing message text color
        snackbar.setActionTextColor(Color.WHITE);

        // Changing action button text color
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.GREEN);
        snackbar.show();
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

        // Changing action button text color
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        snackbar.show();
    }

    public void getPublicPhotos(String id, String lang, String format, String jsonCallBack) {
        final ProgressDialog pd = new ProgressDialog(HomeActivity.this);
        pd.setCancelable(false);
        pd.setMessage("Processing..");
        pd.show();

        ApiClient.getClient().create(ApiInterface.class).getPublicPhotos(id, lang, format, jsonCallBack).enqueue(new Callback<PublicPhotosResponce>() {
            @Override
            public void onResponse(@NonNull Call<PublicPhotosResponce> call, @NonNull Response<PublicPhotosResponce> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getItems().size() > 0)
                        // publicPhotosAdapter.addAll(response.body().getItems());
                        publicPhotosAdapter = new PublicPhotosAdapter(HomeActivity.this, response.body().getItems());
                    publicPhotsList.setAdapter(publicPhotosAdapter);
                    pd.dismiss();
                } else {
                    tvnNoView.setVisibility(View.VISIBLE);
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

}
