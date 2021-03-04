package com.cs65.homie.ui.chats;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.cs65.homie.MainActivity;


public class ChatsFragment extends Fragment {

    private ChatsViewModel vm;

    public void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);

        this.vm = new ViewModelProvider(this).get(ChatsViewModel.class);

        if (this.vm == null)
        {
            // TODO Handle
            // If it can even happen
            Log.d(
                MainActivity.TAG,
                this.getClass().getCanonicalName()
                    + ".onCreate(), ViewModel is null"
            );
            return;
        }

    }

    public View onCreateView(
        LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
    }
}