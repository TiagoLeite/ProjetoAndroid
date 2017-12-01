package br.usp.trabalhoandroid;

import android.graphics.Typeface;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerViewDrawer;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private Map<String, Fragment> fragmentMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerViewDrawer = (RecyclerView) findViewById(R.id.drawer_recycler_view);
        recyclerViewDrawer.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewDrawer.setLayoutManager(llm);
        DrawerItem[] myDataset = new DrawerItem[]
            {
                    new DrawerItem("Perfil", DrawerItem.TYPE_TITLE),
                    new DrawerItem("Exercícios", DrawerItem.TYPE_TITLE),
                    new DrawerItem("Vídeos", DrawerItem.TYPE_TITLE),
            };
        RecyclerDrawerAdapter mAdapter = new RecyclerDrawerAdapter(myDataset);
        recyclerViewDrawer.setAdapter(mAdapter);

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        fragmentMap = new HashMap<>();

        fragmentMap.put("Exercícios", new ExerciseFragment());
        fragmentMap.put("Vídeos", new VideoRecordFragment());
        fragmentMap.put("Perfil", new ProfileFragment());

        drawerLayout.openDrawer(GravityCompat.START);

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
                    view.setBackgroundColor(getResources().getColor(R.color.gray));
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
                mTextView = (TextView) v.findViewById(R.id.tv_row_title);
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
}
