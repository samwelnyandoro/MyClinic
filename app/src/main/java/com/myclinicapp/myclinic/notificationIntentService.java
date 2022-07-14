package com.myclinicapp.myclinic;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;


public class notificationIntentService extends IntentService {

    public notificationIntentService() {
        super("notificationIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        executeBackgroundTask.shouldContinue = false;
    }
}
