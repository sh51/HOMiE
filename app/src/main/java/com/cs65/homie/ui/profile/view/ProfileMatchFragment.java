package com.cs65.homie.ui.profile.view;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cs65.homie.MainActivity;
import com.cs65.homie.R;
import com.cs65.homie.Utilities;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class ProfileMatchFragment
    extends ProfileViewFragment
    implements View.OnClickListener
{

    private FloatingActionButton buttonMatch = null;
    private FloatingActionButton buttonReject = null;

    public void onClick(View view)
    {
        // TODO After-matching action, ping to Firebase, "you have a match!"
        // etc happens here
        // For now, toasts
        if (view.equals(buttonMatch))
        {
            Utilities.showErrorToast(R.string.profile_view_match_accept_description, this.getActivity());
        }
        else if (view.equals(this.buttonReject))
        {
            Utilities.showErrorToast(R.string.profile_view_match_reject_description, this.getActivity());
        }
    }

    public View onCreateView(
        LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState
    )
    {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void onViewCreated(View view, Bundle savedInstanceState)
    {

        super.onViewCreated(view, savedInstanceState);

        // FIXME Using fake data, need to set to the other user
        super.vm.setUserId(((MainActivity)this.getActivity()).getFakeUserId());

        // The buttons require more padding
        View topLayout = view.findViewById(R.id.profileViewLayout);
        if (topLayout != null)
        {
            int padding = (int)Math.round(Utilities.pixelDensity(this.getContext(), 80.0));
            topLayout.setPadding(0, 0, 0, padding);
        }

        // Set up the listener for the match buttons
        // And make them visible
        this.buttonMatch = view.findViewById(
            R.id.profileViewButtonMatchRight
        );
        if (this.buttonMatch != null)
        {
            buttonMatch.setOnClickListener(this);
            buttonMatch.setVisibility(View.VISIBLE);
        }
        this.buttonReject = view.findViewById(R.id.profileViewButtonMatchLeft);
        if (this.buttonReject != null)
        {
            buttonReject.setOnClickListener(this);
            buttonReject.setVisibility(View.VISIBLE);
        }

    }

}