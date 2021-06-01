package app.pilo.android.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import app.pilo.android.helpers.UserSharedPrefManager;

public class BaseFragment extends Fragment {

    public static final String ARGS_INSTANCE = "app.pilo.android";
    public static FragmentNavigation mFragmentNavigation;
    protected Activity activity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof FragmentNavigation) {
            mFragmentNavigation = (FragmentNavigation) context;
        }

        if (context instanceof Activity) {
            activity = (Activity) context;
        }
    }

    public interface FragmentNavigation {
        void pushFragment(Fragment fragment);
    }


    public boolean checkView() {
        return getActivity() != null && getView() != null;
    }
}