package in.kay.flixtube.UI.HomeUI;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.gdacciaro.iOSDialog.iOSDialog;
import com.gdacciaro.iOSDialog.iOSDialogBuilder;
import com.gdacciaro.iOSDialog.iOSDialogClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import in.kay.flixtube.Adapter.FeatureAdapter;
import in.kay.flixtube.Adapter.MovieAdapter;
import in.kay.flixtube.Adapter.SeriesAdapter;
import in.kay.flixtube.Model.MovieModel;
import in.kay.flixtube.Model.SeriesModel;
import in.kay.flixtube.R;
import in.kay.flixtube.UI.IntroUI.LandingActivity;
import in.kay.flixtube.Utils.Helper;

public class MainActivity extends AppCompatActivity {
    DatabaseReference rootRef;
    TextView tvName, tvFeatured, tvMovies, tvSeries;
    RecyclerView rvFeatured, rvMovies, rvSeries;
    MovieAdapter movieAdapter;
    FeatureAdapter featureAdapter;
    SeriesAdapter seriesAdapter;
    int size;
    Helper helper;
    String name, violation, membership, mobileUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InitzAll();
    }

    private void initz() {
        LoadViews();
        LoadMovies();
        LoadFeatured();
        LoadSeries();
        GetSizeRV();
    }

    private void InitzAll() {
        rootRef = FirebaseDatabase.getInstance().getReference();
        helper = new Helper();
        rootRef.child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name = snapshot.child("Name").getValue(String.class);
                membership = snapshot.child("Membership").getValue(String.class);
                mobileUid = snapshot.child("MobileUid").getValue(String.class);
                violation = snapshot.child("Violation").getValue(String.class);
                String strmobileUid = helper.decryptedMsg(name, mobileUid);
                if (strmobileUid.equalsIgnoreCase(helper.deviceId(MainActivity.this))) {
                    findViewById(R.id.nsv_main).setVisibility(View.VISIBLE);
                    initz();
                } else {
                    PopUp();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Log.d("UserValue", "Outside: " + name);
    }

    private void PopUp() {
        Typeface font = Typeface.createFromAsset(this.getAssets(), "Gilroy-ExtraBold.ttf");
        new iOSDialogBuilder(MainActivity.this)
                .setTitle("Privacy Issue")
                .setSubtitle("This isn't your device. Due to security permission, we can't let you to use this account.")
                .setCancelable(false)
                .setFont(font)
                .setPositiveListener(getString(R.string.ok), new iOSDialogClickListener() {
                    @Override
                    public void onClick(iOSDialog dialog) {
                        dialog.dismiss();
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(MainActivity.this, LandingActivity.class));

                    }
                })
                .build().show();
    }


    private void GetSizeRV() {
        FnSeries();
        FnMovie();
    }

    private void FnSeries() {
        rootRef.child("Webseries").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                size = (int) snapshot.getChildrenCount();
                rvSeries.smoothScrollToPosition(size);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void FnMovie() {
        rootRef.child("Movies").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                size = (int) snapshot.getChildrenCount();
                rvMovies.smoothScrollToPosition(size);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    private void LoadViews() {
        /////
        Typeface font = Typeface.createFromAsset(this.getAssets(), "Gilroy-ExtraBold.ttf");
        /////
        tvName = findViewById(R.id.tv_name);
        tvFeatured = findViewById(R.id.tv_featured);
        tvSeries = findViewById(R.id.tv_series);
        tvMovies = findViewById(R.id.tv_movies);
        /////
        rvFeatured = findViewById(R.id.rv_featured);
        rvSeries = findViewById(R.id.rv_series);
        rvMovies = findViewById(R.id.rv_movies);
        /////
        rvMovies.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
        rvFeatured.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvFeatured.setOnFlingListener(null);
        SnapHelper snapHelpernew = new PagerSnapHelper();
        snapHelpernew.attachToRecyclerView(rvFeatured);
        rvSeries.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
        /////
        tvName.setTypeface(font);
        tvName.setText("Hey, " + name);
        tvFeatured.setTypeface(font);
        tvMovies.setTypeface(font);
        tvSeries.setTypeface(font);
    }

    private void LoadMovies() {
        FirebaseRecyclerOptions<MovieModel> options = new FirebaseRecyclerOptions.Builder<MovieModel>()
                .setQuery(rootRef.child("Movies"), MovieModel.class)
                .build();
        movieAdapter = new MovieAdapter(options, this);
        rvMovies.setAdapter(movieAdapter);
        movieAdapter.startListening();
    }

    private void LoadFeatured() {
        FirebaseRecyclerOptions<MovieModel> options = new FirebaseRecyclerOptions.Builder<MovieModel>()
                .setQuery(rootRef.child("Movies").orderByChild("featured").equalTo("Yes"), MovieModel.class)
                .build();
        featureAdapter = new FeatureAdapter(options, this);
        rvFeatured.setAdapter(featureAdapter);
        featureAdapter.startListening();
    }

    private void LoadSeries() {
        FirebaseRecyclerOptions<SeriesModel> options = new FirebaseRecyclerOptions.Builder<SeriesModel>()
                .setQuery(rootRef.child("Webseries"), SeriesModel.class)
                .build();
        seriesAdapter = new SeriesAdapter(options, this);
        rvSeries.setAdapter(seriesAdapter);
        seriesAdapter.startListening();
    }


    public void ViewAllSeries(View view) {
        Intent intent = new Intent(this, ViewAllActivity.class);
        intent.putExtra("type", "Webseries");
        startActivity(intent);
        Animatoo.animateFade(this);
    }

    public void ViewAllMovies(View view) {
        Intent intent = new Intent(this, ViewAllActivity.class);
        intent.putExtra("type", "Movies");
        startActivity(intent);
        Animatoo.animateFade(this);
    }

    @Override
    public void onBackPressed() {
        CloseApp();
    }

    private void CloseApp() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
        int pid = android.os.Process.myPid();
        android.os.Process.killProcess(pid);
    }

}