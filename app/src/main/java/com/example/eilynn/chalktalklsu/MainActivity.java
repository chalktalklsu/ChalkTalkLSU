package com.example.eilynn.chalktalklsu;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Context context = this;
    FirebaseAuth auth;
    private RecyclerView postList;
    private DatabaseReference PostsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newPost = new Intent(MainActivity.this, PostActivity.class);
                startActivity(newPost);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        postList = (RecyclerView) findViewById(R.id.recycler_view);
        postList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postList.setLayoutManager(linearLayoutManager);

        displayAllUsersPosts();
    }

    private void displayAllUsersPosts() {
        FirebaseRecyclerOptions<Posts> posts = new FirebaseRecyclerOptions.Builder<Posts>().setQuery(PostsRef, Posts.class).build();
        FirebaseRecyclerAdapter<Posts, PostViewHolder> adapter = new FirebaseRecyclerAdapter<Posts, PostViewHolder>(posts) {
            @Override
            protected void onBindViewHolder(@NonNull PostViewHolder holder, int position, @NonNull Posts model) {
                holder.description.setText(model.getDesc());
                Picasso.get().load(model.getImageURL()).into(holder.postImage);
            }

            @NonNull
            @Override
            public PostViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_items, viewGroup, false);
                PostViewHolder viewHolder = new PostViewHolder(view);
                return  viewHolder;
            }
        };

        postList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder{
        TextView description;
        ImageView postImage;
        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            description = itemView.findViewById(R.id.description);
            postImage = itemView.findViewById(R.id.post_image);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            startActivity(new Intent(MainActivity.this,myProfileActivity.class));

        } else if(id == R.id.create_post) {
            SendUserToPostActivity();

        } else if(id == R.id.nav_logout) {
            final FirebaseAuth auth;
            auth = FirebaseAuth.getInstance();
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Do you want to log out?").setTitle("Log Out")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            auth.signOut();
                            startActivity(new Intent(MainActivity.this,LoginActivity.class));
                            finish();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            builder.create();
            builder.show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void SendUserToPostActivity() {
        Intent newPost = new Intent(MainActivity.this, PostActivity.class);
        startActivity(newPost);
    }
}
