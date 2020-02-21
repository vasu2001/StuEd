package com.stued.StuEd.Student_ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.stued.StuEd.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

public class dashboard extends AppCompatActivity implements PaymentResultListener {
    BottomNavigationView bottomNavigationView;
    private static final String TAG = dashboard.class.getSimpleName();


    public static void setHomeItem(Activity activity) {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) activity.findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.home1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Checkout.preload(getApplicationContext());
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        FirebaseMessaging.getInstance().subscribeToTopic("testTopic");


        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new HomeFragment()).commit();
        }
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment selectedFragment = null;
            String fragmentTag = null;
            switch (menuItem.getItemId()) {
                case R.id.home1:
                    selectedFragment = new HomeFragment();
                    fragmentTag = "FRAGMENT_HOME";
                    break;

                case R.id.searxc:
                    selectedFragment = new SearchFragment();
                    fragmentTag = "FRAGMENT_OTHER";
                    break;

                case R.id.acc:
                    selectedFragment = new AccountFragment();
                    fragmentTag = "FRAGMENT_OTHER";
                    break;

                case R.id.settings2:
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
                if (currentBackStackFragment instanceof HomeFragment || currentBackStackFragment instanceof listtopics || currentBackStackFragment instanceof listTeachers || currentBackStackFragment instanceof TeacherDescription)
                    pos = 0;
                else if (currentBackStackFragment instanceof SearchFragment || currentBackStackFragment instanceof TutorSlotBooked)
                    pos = 1;
                else if (currentBackStackFragment instanceof AccountFragment) pos = 2;
                BottomNavigationView bottomNav = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
                if (pos != -1) bottomNav.getMenu().getItem(pos).setChecked(true);
                // If the stack decreases it means I clicked the back button
            }
        });
    }


    @Override
    public void onPaymentSuccess(String s) {

        Toast.makeText(dashboard.this, "razor payId=" + s, Toast.LENGTH_LONG).show();

        TeacherDescription fragment = (TeacherDescription) getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        fragment.onPaymentSuccessful(s);

    }

    @Override
    public void onPaymentError(int i, String s) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}