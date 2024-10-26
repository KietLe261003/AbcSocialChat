package com.example.abcscialchat;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class FriendsPagerAdapter extends FragmentStateAdapter {

    public FriendsPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new FriendsListFragment();
            case 1:
                return new FriendRequestsFragment();
            case 2:
                return new AddFriendsFragment();
            default:
                return new FriendsListFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3; // Có 3 tab
    }
}
