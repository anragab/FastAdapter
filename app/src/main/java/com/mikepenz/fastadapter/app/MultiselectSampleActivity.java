package com.mikepenz.fastadapter.app;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.HeaderAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.app.items.SampleItem;
import com.mikepenz.fastadapter.helpers.ActionModeHelper;
import com.mikepenz.fastadapter.helpers.UndoHelper;
import com.mikepenz.itemanimators.SlideDownAlphaAnimator;
import com.mikepenz.materialize.MaterializeBuilder;
import com.mikepenz.materialize.util.UIUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MultiselectSampleActivity extends AppCompatActivity {
    //save our FastAdapter
    private FastAdapter<SampleItem> mFastAdapter;

    private UndoHelper mUndoHelper;

    private ActionModeHelper mActionModeHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        //improve ui
        findViewById(android.R.id.content).setSystemUiVisibility(findViewById(android.R.id.content).getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        // Handle Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.sample_multi_select);

        //style our ui
        new MaterializeBuilder().withActivity(this).build();

        //create our FastAdapter
        mFastAdapter = new FastAdapter<>();

        //
        mUndoHelper = new UndoHelper(mFastAdapter, new UndoHelper.UndoListener<SampleItem>() {
            @Override
            public void commitRemove(Set<Integer> positions, ArrayList<FastAdapter.RelativeInfo<SampleItem>> removed) {
                Log.e("UndoHelper", "Positions: " + positions.toString() + " Removed: " + removed.size());
            }
        });

        //we init our ActionModeHelper
        mActionModeHelper = new ActionModeHelper(mFastAdapter, R.menu.cab, new ActionBarCallBack());

        //create our adapters
        ItemAdapter<SampleItem> itemAdapter = new ItemAdapter<>();
        final HeaderAdapter<SampleItem> headerAdapter = new HeaderAdapter<>();

        //configure our mFastAdapter
        //as we provide id's for the items we want the hasStableIds enabled to speed up things
        mFastAdapter.setHasStableIds(true);
        mFastAdapter.withMultiSelect(true);
        mFastAdapter.withSelectOnLongClick(true);
        mFastAdapter.withOnPreClickListener(new FastAdapter.OnClickListener<SampleItem>() {
            @Override
            public boolean onClick(View v, IAdapter<SampleItem> adapter, SampleItem item, int position) {
                //we handle the default onClick behavior for the actionMode. This will return null if it didn't do anything and you can handle a normal onClick
                Boolean res = mActionModeHelper.onClick(item, position);
                return res != null ? res : false;
            }
        });
        mFastAdapter.withOnPreLongClickListener(new FastAdapter.OnLongClickListener<SampleItem>() {
            @Override
            public boolean onLongClick(View v, IAdapter<SampleItem> adapter, SampleItem item, int position) {
                ActionMode actionMode = mActionModeHelper.onLongClick(MultiselectSampleActivity.this, position);

                if (actionMode != null) {
                    //we want color our CAB
                    findViewById(R.id.action_mode_bar).setBackgroundColor(UIUtils.getThemeColorFromAttrOrRes(MultiselectSampleActivity.this, R.attr.colorPrimary, R.color.material_drawer_primary));
                }

                //if we have no actionMode we do not consume the event
                return actionMode != null;
            }
        });

        //get our recyclerView and do basic setup
        RecyclerView rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setItemAnimator(new SlideDownAlphaAnimator());
        rv.setAdapter(itemAdapter.wrap(headerAdapter.wrap(mFastAdapter)));

        //fill with some sample data
        headerAdapter.add(new SampleItem().withName("Header").withIdentifier(1));
        List<SampleItem> items = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            items.add(new SampleItem().withName("Test " + i).withIdentifier(100 + i));
        }
        itemAdapter.add(items);

        //restore selections (this has to be done after the items were added
        mFastAdapter.withSavedInstanceState(savedInstanceState);

        //set the back arrow in the toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);

        //inform that longClick is required
        Toast.makeText(this, "LongClick to enable Multi-Selection", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the adapter to the bundel
        outState = mFastAdapter.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //handle the click on the back arrow click
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Our ActionBarCallBack to showcase the CAB
     */
    class ActionBarCallBack implements ActionMode.Callback {

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            mUndoHelper.remove(findViewById(android.R.id.content), "Item removed", "Undo", Snackbar.LENGTH_LONG, mFastAdapter.getSelections());
            //return true as we consumed the event
            return true;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(UIUtils.getThemeColorFromAttrOrRes(MultiselectSampleActivity.this, R.attr.colorPrimaryDark, R.color.material_drawer_primary_dark));
            }
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(Color.TRANSPARENT);
            }
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }
    }
}
