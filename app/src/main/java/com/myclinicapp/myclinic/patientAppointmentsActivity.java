package com.myclinicapp.myclinic;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

public class patientAppointmentsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_appointments);

        getSupportFragmentManager().beginTransaction().replace(R.id.container1, new patientAppointmentsFragment()).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
    }
}
