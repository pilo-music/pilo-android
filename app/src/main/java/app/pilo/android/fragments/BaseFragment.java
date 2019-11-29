package app.pilo.android.fragments;

import android.content.Context;
import android.os.Bundle;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class BaseFragment extends Fragment {

    public static final String ARGS_INSTANCE = "app.pilo.android";


    static FragmentNavigation mFragmentNavigation;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        if (context instanceof FragmentNavigation) {
            mFragmentNavigation = (FragmentNavigation) context;
        }
    }

    public interface FragmentNavigation {
        void pushFragment(Fragment fragment);
    }



    public static void pushFragment(Fragment fragment){
        if (mFragmentNavigation != null) {
            mFragmentNavigation.pushFragment(fragment);
        }
    }

}