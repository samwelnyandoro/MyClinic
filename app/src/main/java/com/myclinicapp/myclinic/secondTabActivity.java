package com.myclinicapp.myclinic;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

public class secondTabActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_tab);

        getSupportFragmentManager().beginTransaction().replace(R.id.container1, new secondTabFragment()).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
    }
}
