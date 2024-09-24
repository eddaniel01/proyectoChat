package com.example.proyectochatmovil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class AccesoFragments extends FragmentPagerAdapter {
    public AccesoFragments(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
                ChatFragment chatFragment= new ChatFragment();
                return chatFragment;

            case 1:
                GruposFragment gruposFragment= new GruposFragment();
                return gruposFragment;

            case 2:
                AmigosFragment amigosFragment= new AmigosFragment();
                return amigosFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Chats";

            case 1:

                return "Grupos";

            case 2:

                return "Amigos";

            default:
                return null;
        }
    }
}
