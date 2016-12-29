package guan.pcihearten;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class main_page_no_chat extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        languageSceen mainFlag = new languageSceen();
        if (mainFlag.getLanguageSelected()=="bm") {
            setContentView(R.layout.activity_main_page_bm);
        }
        else
            setContentView(R.layout.activity_main_page_no_chat);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        //Set a flag for language
        languageSceen navFlag = new languageSceen();
        if (navFlag.getLanguageSelected() == "bm") {
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_bm);
            navigationView.setNavigationItemSelectedListener(this);
        }
        else{
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
        }



    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_information) {
            // Handle the camera action
            languageSceen mainFlag = new languageSceen();
            if (mainFlag.getLanguageSelected()=="bm") {
                Intent intent = new Intent("guan.pcihearten.scroll_bm");
                startActivity(intent);
            }
            else {
                Intent intent = new Intent("guan.pcihearten.ScrollingActivity");
                startActivity(intent);
            }
        } else if (id == R.id.nav_prepci) {
            languageSceen mainFlag = new languageSceen();
            if (mainFlag.getLanguageSelected()=="bm")
            {
                Intent intent = new Intent("guan.pcihearten.pre_bm");
                startActivity(intent);
            }
            else
            {
                Intent intent = new Intent("guan.pcihearten.preProcedure");
                startActivity(intent);
            }

        } else if (id == R.id.nav_pciprocedure) {
            languageSceen mainFlag = new languageSceen();
            if (mainFlag.getLanguageSelected()=="bm")
            {
                Intent intent = new Intent("guan.pcihearten.procedure_bm");
                startActivity(intent);
            }
            else
            {
                Intent intent = new Intent("guan.pcihearten.procedure");
                startActivity(intent);
            }
        } else if (id == R.id.nav_postpci) {
            languageSceen mainFlag = new languageSceen();
            if (mainFlag.getLanguageSelected()=="bm")
            {
                Intent intent = new Intent("guan.pcihearten.post_bm");
                startActivity(intent);
            }
            else
            {
                Intent intent = new Intent("guan.pcihearten.post_procedure");
                startActivity(intent);
            }

        } else if (id == R.id.nav_health) {
            languageSceen mainFlag = new languageSceen();
            if (mainFlag.getLanguageSelected()=="bm")
            {
                Intent intent = new Intent("guan.pcihearten.health_bm");
                startActivity(intent);
            }
            else
            {
                Intent intent = new Intent("guan.pcihearten.health");
                startActivity(intent);
            }
        }
        else if (id == R.id.nav_mock) {
            startActivity(new Intent(this, pci_mcq.class));

        } else if (id == R.id.nav_dash) {
            Intent intent = new Intent("guan.pcihearten.dashboard");
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            /**mFirebaseAuth.signOut();
             Auth.GoogleSignInApi.signOut(mGoogleApiClient);
             mUsername = ANONYMOUS;**/
            finish();
            // startActivity(new Intent(this, loginScreen.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




}
