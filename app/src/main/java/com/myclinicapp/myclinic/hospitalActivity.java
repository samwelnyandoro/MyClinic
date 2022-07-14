package com.myclinicapp.myclinic;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;

import android.os.Bundle;

import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import com.mancj.materialsearchbar.MaterialSearchBar;
import com.myclinicapp.myclinic.models.doctorInfo;

import java.util.ArrayList;
import java.util.List;

import me.drakeet.materialdialog.MaterialDialog;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

public class hospitalActivity extends AppCompatActivity {

    private Toolbar hospitalToolbar;
    private ListView hospitalSearchList;
    private TabLayout hospitalTabLayout;
    private ViewPager hospitalPager;
    private MaterialSearchBar hospitalSearchBar;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    public hospitalListAdapter hospitalListAdapter;

    private ConnectivityManager connectivityManager;

    private MaterialDialog mMaterialDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital);

        hospitalTabLayout=(TabLayout) findViewById(R.id.hospitalTabLayout);
        hospitalPager=  findViewById(R.id.hospitalPager);
        hospitalSearchBar = findViewById(R.id.hospitalSearchBar);

        doctorCategoryAdapter adapter=new doctorCategoryAdapter(this,getSupportFragmentManager());
        hospitalPager.setAdapter(adapter);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hospitalTabLayout.setBackgroundColor(getColor(R.color.actionBar));
        }
        hospitalTabLayout.setupWithViewPager(hospitalPager);

        hospitalToolbar = findViewById(R.id.hospitalToolbar);
        hospitalSearchList = findViewById(R.id.hospitalSearchList);
        hospitalSearchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String[] tags = (String[]) view.getTag();
                Intent intent = new Intent(hospitalActivity.this , hospitalDetailActivity.class);
                intent.putExtra("tag", tags);
                startActivity(intent);
            }
        });


        /////////////////////////////////////
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        ///////////////

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        List<doctorInfo> hospitalInfo = new ArrayList<>();
        hospitalListAdapter = new hospitalListAdapter(this, R.layout.patients_list_item, hospitalInfo);
        hospitalSearchList.setAdapter(hospitalListAdapter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            hospitalToolbar.setElevation(0);
        }
        setSupportActionBar(hospitalToolbar);

        createSearchBar();

    }

    @SuppressLint("NewApi")
    private void createSearchBar() {

        hospitalSearchBar.setHint("Search Clinics Name");
        hospitalSearchBar.setElevation(10);
        hospitalSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        hospitalSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if(!enabled){
                    hospitalListAdapter.clear();
                    hospitalToolbar.setVisibility(View.VISIBLE);
                    hospitalPager.setVisibility(View.VISIBLE);
                    hospitalTabLayout.setVisibility(View.VISIBLE);
                    hospitalSearchBar.setVisibility(View.GONE);
                    hospitalSearchList.setVisibility(View.GONE);

                }else {
                    hospitalPager.setVisibility(View.GONE);
                    hospitalTabLayout.setVisibility(View.GONE);
                    hospitalSearchList.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if(networkInfo == null){
                    Toast.makeText(hospitalActivity.this, "No Internet", Toast.LENGTH_SHORT).show();
                    return;
                }


                hospitalListAdapter.clear();
                startSearching(text.toString().toLowerCase());
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });
    }

    private void startSearching(final String s) {
        final boolean[] hasData = {false};
        // TODO: Send messages on click
        Query hekkQuery = databaseReference;
        hekkQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    if (snapshot.hasChild("appointments")){

                    }else {
                        for (DataSnapshot snapshot1: snapshot.getChildren()){
                            snapshot1.getRef().orderByChild("instituteName")
                                    .startAt(s)
                                    .endAt(s+"\uf8ff").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot2) {
                                    if(snapshot2.child("doctorInfo").getValue() == null){
                                    }else {
                                        hasData[0] = true;
                                        doctorInfo info = snapshot2.child("doctorInfo").getValue(doctorInfo.class);
                                        hospitalListAdapter.add(info);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        if(hasData[0]){

        }else {
            Toast.makeText(hospitalActivity.this, "No Results Found", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.patientmenu, menu);

        if(doctorPreference.getIsTapTargetShown(hospitalActivity.this)){

        }else {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    final View view = findViewById(R.id.searchBar);

                    new MaterialTapTargetPrompt.Builder(hospitalActivity.this)
                            .setTarget(view)
                            .setBackgroundColour(getResources().getColor(R.color.actionBar))
                            .setPrimaryText("Search Clinics")
                            .setSecondaryText("Search by Speciality, Name, Gender and Schedule")
                            .setOnHidePromptListener(new MaterialTapTargetPrompt.OnHidePromptListener()
                            {
                                @Override
                                public void onHidePrompt(MotionEvent event, boolean tappedTarget)
                                {
                                    //TODO: Store in SharedPrefs so you don't show this prompt again.
                                    doctorPreference.saveIsTapTargetShown(hospitalActivity.this, true);
                                }

                                @Override
                                public void onHidePromptComplete()
                                {
                                    doctorPreference.saveIsTapTargetShown(hospitalActivity.this, true);
                                }
                            })
                            .show();

                }
            });
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.logout1:
                logoutPatient();
                break;
            case R.id.searchBar:
                hospitalToolbar.setVisibility(View.GONE);
                hospitalSearchBar.setVisibility(View.VISIBLE);
                hospitalSearchBar.enableSearch();
                hospitalSearchBar.hideSuggestionsList();
                break;
            case R.id.pprofile:
                Intent intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }



    private void logoutPatient() {
        doctorPreference.savePhoneNumberInSP(this, null);
        doctorPreference.saveBooleanInSP(this, false);
        doctorPreference.saveIsTapTargetShown(this, false);
        FirebaseAuth.getInstance().getCurrentUser().delete();
        finish();
    }

}
