package com.teamtreehouse.ribbit;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RecipientsActivity extends AppCompatActivity {

    private static final String TAG = RecipientsActivity.class.getSimpleName();

    protected List<ParseUser> mFriends;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;
    protected MenuItem mSendMenuItem;
    protected Uri mMediaUri;
    protected String mFileType;

    @Bind(R.id.progressBar)
    protected ProgressBar mProgressBar;
    @Bind(R.id.recipientsListView)
    protected ListView mRecipientsListView;
    @Bind(R.id.emptyTextView)
    protected TextView mEmptyTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipients);
        ButterKnife.bind(this);

        mMediaUri = getIntent().getData();
        mFileType = getIntent().getExtras().getString(ParseConstants.KEY_FILE_TYPE);

        mRecipientsListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        mRecipientsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mRecipientsListView.getCheckedItemCount() > 0) {
                    mSendMenuItem.setVisible(true);
                } else {
                    mSendMenuItem.setVisible(false);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        mProgressBar.setVisibility(View.VISIBLE);

        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);
        ParseQuery<ParseUser> query = mFriendsRelation.getQuery();
        query.addAscendingOrder(ParseConstants.KEY_USERNAME);

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {
                mProgressBar.setVisibility(View.INVISIBLE);
                if (e == null) {
                    mFriends = friends;

                    String[] usernames = new String[mFriends.size()];
                    int i = 0;
                    for (ParseUser user : mFriends) {
                        usernames[i] = user.getUsername();
                        i++;
                    }
                    if (i > 0) {
                        mEmptyTextView.setVisibility(View.INVISIBLE);
                        mRecipientsListView.setVisibility(View.VISIBLE);
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            mRecipientsListView.getContext(),
                            android.R.layout.simple_list_item_checked,
                            usernames);
                    mRecipientsListView.setAdapter(adapter);
                } else {
                    Log.e(TAG, e.getMessage());
                    AlertDialog.Builder builder = new AlertDialog.Builder(RecipientsActivity.this);
                    builder.setMessage(e.getMessage())
                            .setTitle(R.string.error_title)
                            .setPositiveButton(android.R.string.ok, null);

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recipients, menu);
        mSendMenuItem = menu.getItem(0);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_send:
                // upload message to backend on Parse
                ParseObject message = createMessage();
                //send(message);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected ParseObject createMessage() {
        ParseObject message = ParseObject.create(ParseConstants.CLASS_MESSAGES);
        message.put(ParseConstants.KEY_SENDER_ID, ParseUser.getCurrentUser().getObjectId());
        message.put(ParseConstants.KEY_SENDER_NAME, ParseUser.getCurrentUser().getUsername());
        message.put(ParseConstants.KEY_RECIPIENT_IDS, getRecipientIds());
        message.put(ParseConstants.KEY_FILE_TYPE, mFileType);

        return message;
    }

    private ArrayList<String> getRecipientIds() {
        ArrayList<String> recipientIds = new ArrayList<>();
        for (int i = 0; i < mRecipientsListView.getCount(); i++) {
            if (mRecipientsListView.isItemChecked(i)) {
                recipientIds.add(mFriends.get(i).getObjectId());
            }
        }
        return recipientIds;
    }

}
