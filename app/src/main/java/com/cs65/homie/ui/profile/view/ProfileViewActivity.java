package com.cs65.homie.ui.profile.view;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentTransaction;

import com.cs65.homie.R;


/**
 * Wrapper activity around the ProfileViewFragment, so there can be an
 * activity that supports profile views
 */
public class ProfileViewActivity extends AppCompatActivity
{

    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);

        Fragment profileViewFrag = new ProfileViewFragment();

        if (savedInstanceState != null)
        {
            profileViewFrag.setArguments(savedInstanceState);
        }
        else if (
            this.getIntent() != null
            && this.getIntent().getExtras() != null
        )
        {
            profileViewFrag.setArguments(this.getIntent().getExtras());
        }

        this.setContentView(R.layout.activity_profile);

        FragmentContainerView fragContainer
            = this.findViewById(R.id.profileViewActivityFragmentView);
        if (fragContainer != null)
        {

            FragmentTransaction transaction
                = this.getSupportFragmentManager().beginTransaction();
            transaction.add(
                R.id.profileViewActivityFragmentView, profileViewFrag
            );
            transaction.commit();
            this.getSupportFragmentManager().executePendingTransactions();

        }

        // TODO Once we have real data that's gettable, set the title of the
        // activity to the name of the user of the profile
        //this.setTitle();

    }

    // We don't need to worry about multiple onCreate calls because the
    // contained fragment saves the IDs into its ViewModel
    // So we don't need to save the IDs

}