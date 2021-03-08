package com.cs65.homie.ui.profile.view;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.List;


public class ProfileViewActivity extends AppCompatActivity
{

    public static final String BUNDLE_KEY_MY_ID
        = "PROFILE_VIEW_ACTIVITY_BUNDLE_KEY_MY_ID";
    public static final String BUNDLE_KEY_USER_ID
        = "PROFILE_VIEW_ACTIVITY_BUNDLE_KEY_USER_ID";

    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);

        List<Fragment> fragments
            = this.getSupportFragmentManager().getFragments();
        if (fragments != null && !fragments.isEmpty())
        {
            Fragment fragment = fragments.get(0);
            if (savedInstanceState != null)
            {
                fragment.setArguments(savedInstanceState);
            }
            else if (
                this.getIntent() != null
                && this.getIntent().getExtras() != null
            )
            {
                fragment.setArguments(this.getIntent().getExtras());
            }
        }

    }

    // We don't need to worry about multiple onCreate calls because the
    // contained fragment saves the Ids into its ViewModel

}
