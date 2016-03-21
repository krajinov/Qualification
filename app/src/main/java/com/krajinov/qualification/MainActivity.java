package com.krajinov.qualification;

import android.content.Context;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import layout.MyProfileFragment;
import model.Person;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    public static final String ENDPOINT = "http://job-griffon.nsoft.ba";
    public static final String PERSON_NAME = "person_name";
    public static final String MAP_LONG = "map_long";
    public static final String MAP_LAT = "map_lat";
    public static final int PREFERENCES_REQUEST_CODE = 1001;
    static final int REQUEST_IMAGE_CAPTURE = 1002;

    private Boolean isFabOpen = false;
    private FloatingActionButton fab, fab1, fab2;
    private Animation fab_open, fab_close, rotate_forward, rotate_backward;

    private List<Person> personList;
    private ListView lv = null;

    private PopupWindow popupWindow = null;
    private LayoutInflater inflater;
    private DrawerLayout drawerLayout;

    private NavigationView navigationView = null;
    private Toolbar toolbar = null;
    CircleImageView cir;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadPreferences();
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.setTitle(R.string.staff_list);

        //FAB postavke
        fabSetup();
        fabDisable();

        StaffFragment fragment = new StaffFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, fragment);
        fragmentTransaction.commit();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerLayout = navigationView.getHeaderView(0);

        TextView nameSur = (TextView) headerLayout.findViewById(R.id.nameSurname);
        nameSur.setText("Name Surname");

        cir = (CircleImageView) headerLayout.findViewById(R.id.profile_image1);
        cir.setImageBitmap(setProfilePhoto());

        staffListData();
    }

    private Bitmap setProfilePhoto() {
        Bitmap profile = loadImageFromStorage(this);
        if (profile != null) {
            return profile;
        } else {
            profile = BitmapFactory.decodeResource(getResources(), R.drawable.profile_img);
            return profile;
        }
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_staff_list) {

            StaffFragment fragment = new StaffFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
            this.setTitle(R.string.staff_list);
            staffListData();
            if (isFabOpen) {
                animateFAB();
            }
            fabDisable();

        } else if (id == R.id.nav_my_profile) {

            MyProfileFragment fragment = new MyProfileFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
            this.setTitle(R.string.my_profile);

            fabEnable();

        } else if (id == R.id.nav_settings) {

            Intent intent = new Intent(this, MyPrefsActivity.class);
            startActivityForResult(intent, 1001);

        } else if (id == R.id.nav_clear_cache) {
            clearApplicationData();
            StaffFragment fragment = new StaffFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
            super.recreate();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void staffListData() {
        if (isOnline()) {
            requestData();
        } else {
            Toast.makeText(this, getString(R.string.network_status), Toast.LENGTH_SHORT).show();
        }
    }

    //Dohvaćanje podataka pomoću retrofit-a
    private void requestData() {
        RestAdapter adapter = new RestAdapter.Builder().setEndpoint(ENDPOINT).build();
        PersonAPI api = adapter.create(PersonAPI.class);

        api.getFeed(new Callback<List<Person>>() {
            @Override
            public void success(List<Person> persons, Response response) {
                personList = persons;
                updateDisplay();
            }

            @Override
            public void failure(RetrofitError retrofitError) {

            }
        });
    }

    //Postavljanje sadržaja ekrana
    public void updateDisplay() {
        PersonAdapter adapter = new PersonAdapter(this, android.R.layout.simple_list_item_1, personList);
        lv = (ListView) findViewById(R.id.listView);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                inflater = (LayoutInflater) getApplicationContext()
                        .getSystemService(LAYOUT_INFLATER_SERVICE);
                drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
                ViewGroup container = (ViewGroup) inflater.inflate(R.layout.popup, null);

                setPop(position, container);
            }
        });

    }

    //Postavljanje PopupWindow-a
    private void setPop(int position, ViewGroup container) {

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = (int) (dm.widthPixels * 0.9);
        int height = (int) (dm.heightPixels * 0.6);


        popupWindow = new PopupWindow(container, width, height, true);
        popupWindow.setAnimationStyle(android.R.anim.fade_in);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAtLocation(drawerLayout, Gravity.CENTER_HORIZONTAL,
                0, 0);

        container.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });

        View popupView = popupWindow.getContentView();
        final Person person = personList.get(position);

        TextView name = (TextView) popupView.findViewById(R.id.namePop);
        name.setText(person.getFirst_name() + " " + person.getLast_name());

        TextView desc = (TextView) popupView.findViewById(R.id.textViewPop);
        desc.setText(person.getDescription());

        Button email = (Button) popupView.findViewById(R.id.buttonPopEmail);
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] addresses = {person.getEmail()};
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL, addresses);
                intent.putExtra(Intent.EXTRA_SUBJECT, "Subject request");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

        Button maps = (Button) popupView.findViewById(R.id.buttonPopMap);
        maps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Float> map = person.getMap();
                float lon = map.get("long");
                float lat = map.get("lat");
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                intent.putExtra(PERSON_NAME, person.getFirst_name() + " " + person.getLast_name());
                intent.putExtra(MAP_LONG, lon);
                intent.putExtra(MAP_LAT, lat);
                startActivity(intent);
            }
        });
    }

    //Provjeravanje da li postoji internet veza
    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.fab:
                animateFAB();
                break;

            case R.id.fab1:
                capturePhoto();
                break;

            case R.id.fab2:

                break;
        }
    }

    public void animateFAB() {

        if (isFabOpen) {

            fab.startAnimation(rotate_backward);
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            isFabOpen = false;

        } else {

            fab.startAnimation(rotate_forward);
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            isFabOpen = true;

        }
    }

    private void fabSetup() {
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_backward);
        fab.setOnClickListener(this);
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);
    }

    private void fabDisable() {
        fab.hide();
        fab1.hide();
        fab2.hide();
        fab.setEnabled(false);
        fab1.setEnabled(false);
        fab2.setEnabled(false);
    }

    private void fabEnable() {
        fab.show();
        fab.setEnabled(true);
        fab1.setEnabled(true);
        fab2.setEnabled(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PREFERENCES_REQUEST_CODE) {
            loadPreferences();
            super.recreate();
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            saveToInternalStorage(imageBitmap);

            ImageView mImageView = (ImageView) findViewById(R.id.cameraPhoto);
            cir.setImageBitmap(imageBitmap);

            mImageView.setImageBitmap(imageBitmap);

        }

    }

    //Učitavanje postavki
    private void loadPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String language = sharedPreferences.getString("language", "en");
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
    }

    //Camera Intent
    public void capturePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }
    }

    //Spremanje uslikane slike
    private void saveToInternalStorage(Bitmap bitmapImage) {

        FileOutputStream fos = null;
        try {
            fos = openFileOutput("profile.png", MODE_PRIVATE);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("MotoG2", e.getMessage());
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //Učitavanje slike iz Internal Storage-a
    private Bitmap loadImageFromStorage(Context context) {
        try {
            FileInputStream fis = context.openFileInput("profile.png");
            Bitmap b = BitmapFactory.decodeStream(fis);
            return b;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.d("MotoG2", e.getMessage());
            return null;
        }

    }

    public void clearApplicationData() {
        File cache = getCacheDir();
        File appDir = new File(cache.getParent());
        if (appDir.exists()) {
            String[] children = appDir.list();
            for (String s : children) {
                if (!s.equals("lib")) {
                    deleteDir(new File(appDir, s));
                }
            }
        }
        Toast.makeText(MainActivity.this,
                R.string.app_data_deleted, Toast.LENGTH_SHORT).show();
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        return dir.delete();
    }
}
