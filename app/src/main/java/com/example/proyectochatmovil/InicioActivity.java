package com.example.proyectochatmovil;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Switch;
import android.widget.TextView;


import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.proyectochatmovil.Fragments.ChatsFragment;
import com.example.proyectochatmovil.Fragments.ContactosFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class InicioActivity extends AppCompatActivity {

    CircleImageView imagen_deperfil;
    TextView username;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_inicio);

        Toolbar toolbar = findViewById(R.id.toolbar_inicio);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");


        imagen_deperfil = findViewById(R.id.imagen_deperfil);
        username = findViewById(R.id.username);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                if (user != null) {
                    // Verifica si el nombre de usuario es nulo
                    if (user.getUsername() != null) {
                        username.setText(user.getUsername());
                    } else {
                        username.setText("Usuario desconocido");  // Valor por defecto
                    }

                    // Verifica si el URL de la imagen es nulo
                    if (user.getImagenURL() != null && user.getImagenURL().equals("default")) {
                        imagen_deperfil.setImageResource(R.mipmap.ic_launcher);
                    } else if (user.getImagenURL() != null) {
                        Glide.with(InicioActivity.this).load(user.getImagenURL()).into(imagen_deperfil);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejo del error de la base de datos si es necesario

            }
        });

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager viewPager = findViewById(R.id.view_pager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewPagerAdapter.addFragments(new ChatsFragment(), "Chats");
        viewPagerAdapter.addFragments(new ContactosFragment(), "Contactos");

        viewPager.setAdapter(viewPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item) {


     int id= item.getItemId();

     if (id == R.id.cerrar_sesion){
         FirebaseAuth.getInstance().signOut();
         startActivity(new Intent(InicioActivity.this, PrincipalActivity.class));
         finish();
         return true;
     }
        return false;
    }

    class ViewPagerAdapter extends FragmentPagerAdapter{

        private ArrayList <Fragment> fragments;
        private ArrayList <String> titulos;

        ViewPagerAdapter (FragmentManager fragmentManager){
            super(fragmentManager);
            this.fragments = new ArrayList<>();
            this.titulos = new ArrayList<>();
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragments (Fragment fragment, String titulo){
            fragments.add(fragment);
            titulos.add(titulo);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {

            return titulos.get(position);
        }
    }
}