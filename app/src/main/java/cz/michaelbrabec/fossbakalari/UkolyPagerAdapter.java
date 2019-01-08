package cz.michaelbrabec.fossbakalari;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class UkolyPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public UkolyPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                UkolyTabFragment1 tab1 = new UkolyTabFragment1();
                return tab1;
            case 1:
                UkolyTabFragment2 tab2 = new UkolyTabFragment2();
                return tab2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
