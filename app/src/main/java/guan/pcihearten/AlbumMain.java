package guan.pcihearten;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 1/11/2017.
 */
//Main Activity for the recycler view
public class AlbumMain extends AppCompatActivity {
//    Initialize variable
    private RecyclerView recyclerView;
    private AlbumAdapter adapter;
    private List<album> albumList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_album);

        initCollapsingToolbar();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        albumList = new ArrayList<>();
        adapter = new AlbumAdapter(this, albumList);


        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        prepareAlbums();

        try {
            Glide.with(this).load(R.drawable.ic_menu_camera).into((ImageView) findViewById(R.id.backdrop));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
//Default Constructor
    public AlbumMain (){

    }
//Sets the toolbar to collapse
    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(getString(R.string.app_name));
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }
//populates the recycler
    private void prepareAlbums() {
//        Create array of Img
        int[] covers = new int[]{
                R.drawable.health_1,
                R.drawable.health_8,
                R.drawable.heart_poster,
                R.drawable.ic_menu_share,
                R.drawable.info_heart_bm,
                R.drawable.procedure_3,
                R.drawable.ic_menu_manage,
                R.drawable.health_5,
                R.drawable.sarung_pci_bm,
                R.drawable.post_1,
                R.drawable.post_4};
//Instantiate album project and insert stuff in it.
        album a = new album("True Romance", 13, covers[0]);
        albumList.add(a);

        a = new album("Xscpae", 8, covers[1]);
        albumList.add(a);

        a = new album("Maroon 5", 11, covers[2]);
        albumList.add(a);

        a = new album("Born to Die", 12, covers[3]);
        albumList.add(a);

        a = new album("Honeymoon", 14, covers[4]);
        albumList.add(a);

        a = new album("I Need a Doctor", 1, covers[5]);
        albumList.add(a);

        a = new album("Loud", 11, covers[6]);
        albumList.add(a);

        a = new album("Legend", 14, covers[7]);
        albumList.add(a);

        a = new album("Hello", 11, covers[8]);
        albumList.add(a);

        a = new album("Greatest Hits", 17, covers[9]);
        albumList.add(a);

        adapter.notifyDataSetChanged();
    }

// Not sure, its not used so...
    public AlbumMain(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }
//Spacing for each item
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
//Variable declare
        private int spanCount;
        private int spacing;
        private boolean includeEdge;
//An object with dimension specification
        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }
//
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }
// Translates dp to PX
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
}


