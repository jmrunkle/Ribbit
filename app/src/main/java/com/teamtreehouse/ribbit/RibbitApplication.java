package com.teamtreehouse.ribbit;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by jr186037 on 9/4/2015.
 */
public class RibbitApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        Parse.initialize(this,
                "yHdSuFiKbM4P5yZw9EiZYbnBgD17lOwRFdb9volD",
                "tuCAA1julujoS9w0sycar7wqSFZe5ajVcye6q133");

        /*ParseObject testObject = new ParseObject("TestObject");
        testObject.put("foo", "bar");
        testObject.saveInBackground();*/
    }
}
