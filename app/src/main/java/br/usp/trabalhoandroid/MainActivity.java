package br.usp.trabalhoandroid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.FloatRange;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerViewDrawer;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private Map<String, Fragment> fragmentMap;
    TextView TVWelcome;
    ImageView profilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TVWelcome = findViewById(R.id.TVWelcome);
        TVWelcome.setText(getResources().getString(R.string.welcome) + " "+Constants.NAME);

        recyclerViewDrawer = findViewById(R.id.drawer_recycler_view);
        recyclerViewDrawer.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewDrawer.setLayoutManager(llm);
        DrawerItem[] myDataset = new DrawerItem[]
            {
                    new DrawerItem(getResources().getString(R.string.profile), DrawerItem.TYPE_TITLE),
                    new DrawerItem(getResources().getString(R.string.exercises), DrawerItem.TYPE_TITLE),
                    new DrawerItem(getResources().getString(R.string.settings), DrawerItem.TYPE_TITLE),
                    new DrawerItem(getResources().getString(R.string.sair), DrawerItem.TYPE_TITLE)
            };
        RecyclerDrawerAdapter mAdapter = new RecyclerDrawerAdapter(myDataset);
        recyclerViewDrawer.setAdapter(mAdapter);

        drawerLayout = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                loadImageFromStorage();
            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        fragmentMap = new HashMap<>();

        fragmentMap.put(getResources().getString(R.string.exercises), new ExerciseFragment());
        fragmentMap.put(getResources().getString(R.string.profile), new ProfileFragment());
        fragmentMap.put(getResources().getString(R.string.settings), new SettingsFragment());
        fragmentMap.put(getResources().getString(R.string.sair), new SettingsFragment());
        drawerLayout.openDrawer(GravityCompat.START);
        profilePic = findViewById(R.id.profilePicIcon);

        loadImageFromStorage();

        replaceFragment(fragmentMap.get(getResources().getString(R.string.exercises)));
    }

    private void replaceFragment(Fragment fragment)
    {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.main_container, fragment)
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(toggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    private class RecyclerDrawerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    {
        private DrawerItem[] mDataset;
        private View lastClicked;

        RecyclerDrawerAdapter(DrawerItem[] myDataset) {
            mDataset = myDataset;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
            // create a new view
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.drawer_row, parent, false);
            return new ViewHolderMenu(view);
        }

        @Override
        public int getItemViewType(int position) {
            return mDataset[position].getItemType();
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
        {
            int pos = holder.getAdapterPosition();
            addItemTypeTitle(holder, pos);
        }

        private void addItemTypeTitle(RecyclerView.ViewHolder viewHolder, int position)
        {
            final String itemText = mDataset[position].getItemText();
            ViewHolderMenu holder = (ViewHolderMenu)viewHolder;
            holder.mTextView.setText(itemText);
            holder.mTextView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    if(itemText.equals(getResources().getString(R.string.sair)))
                    {
                        SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences(Constants.LOGIN_PREFS, MODE_PRIVATE).edit();
                        editor.clear();
                        editor.commit();
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                    if(lastClicked != null)
                    {
                        ((TextView)lastClicked).setTextColor(getResources().getColor(R.color.colorPrimary));
                        ((TextView)lastClicked).setTypeface(Typeface.DEFAULT);
                        lastClicked.setBackgroundColor(getResources().getColor(R.color.transparent));
                    }
                    lastClicked = view;
                    replaceFragment(fragmentMap.get(itemText));
                    drawerLayout.closeDrawers();
                    ((TextView)view).setTextColor(getResources().getColor(R.color.colorPrimary));
                    ((TextView)view).setTypeface(Typeface.DEFAULT_BOLD);
                    view.setBackgroundColor(getResources().getColor(R.color.lightGray));
                }
            });
        }

        @Override
        public int getItemCount() {
            return mDataset.length;
        }

        class ViewHolderMenu extends RecyclerView.ViewHolder
        {
            TextView mTextView;
            ViewHolderMenu(View v)
            {
                super(v);
                mTextView = v.findViewById(R.id.tv_row_title);
            }
        }
    }

    private class DrawerItem
    {
        public static final short TYPE_TITLE = 0;
        private String itemText;
        private short itemType;

        public DrawerItem (String text, short type)
        {
            this.itemText = text;
            this.itemType = type;
        }

        public short getItemType() {
            return itemType;
        }

        public String getItemText() {
            return itemText;
        }
    }

    public void loadImageFromStorage()
    {
        if (profilePic == null)
            Log.d("debug", "iame NULL");
        File mypath = new File(Environment.getExternalStorageDirectory(),Constants.USERNAME + ".jpg");
        Picasso.with(this).load(mypath).into(profilePic);
    }

}
