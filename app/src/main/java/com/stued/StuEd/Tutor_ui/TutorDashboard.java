package com.stued.StuEd.Tutor_ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.stued.StuEd.R;
import com.stued.StuEd.Student_ui.AccountFragment;
import com.stued.StuEd.Student_ui.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class TutorDashboard extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    private Button addslot3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_tutor_dashboard);
        addslot3=findViewById(R.id.addslotbutton);
        bottomNavigationView=findViewById(R.id.bottomNavigationView);
        if(savedInstanceState==null)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,new TutorHome()).commit();
        }
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
    }

    public void addTopic(View view)
    {

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment selectedFragment = null;
            String fragmentTag = null;
            switch (menuItem.getItemId()) {
                case R.id.nav_home:
                    selectedFragment = new TutorHome();
                    fragmentTag = "FRAGMENT_HOME";
                    break;

                case R.id.nav_add:
                    selectedFragment = new TutorAddSlot();
                    fragmentTag = "FRAGMENT_OTHER";
                    break;

                case R.id.nav_account:
                    selectedFragment = new AccountFragment();
                    fragmentTag = "FRAGMENT_OTHER";
                    break;

                case R.id.settings1:
                    selectedFragment= new SettingsFragment();
                    fragmentTag = "FRAGMENT_SETTINGS";
                    break;
            }

            viewFragment(selectedFragment, fragmentTag);
            return true;
        }
    };


    private void viewFragment(Fragment fragment, String name) {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, fragment, "FRAGMENT");
        // 2. If the fragment is **not** "home type", save it to the stack
        if (name.equals("FRAGMENT_OTHER")) {
            fragmentTransaction.addToBackStack(name);
        } else if (name.equals("FRAGMENT_HOME")) {
            fragmentManager.popBackStack("FRAGMENT_OTHER", fragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        // Commit !
        fragmentTransaction.commit();
        // 3. After the commit, if the fragment is not an "home type" the back stack is changed, triggering the
        // OnBackStackChanged callback
        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                int pos = -1;
                Fragment currentBackStackFragment = fragmentManager.findFragmentByTag("FRAGMENT");
                if (currentBackStackFragment instanceof TutorHome) pos = 0;
                else if (currentBackStackFragment instanceof TutorAddSlot || currentBackStackFragment instanceof TutorTopic || currentBackStackFragment instanceof TutorTopicDescription) pos = 1;
                else if (currentBackStackFragment instanceof AccountFragment) pos = 2;
                BottomNavigationView bottomNav = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
                if(pos!=-1) bottomNav.getMenu().getItem(pos).setChecked(true);
                // If the stack decreases it means I clicked the back button
            }
        });
    }
}
