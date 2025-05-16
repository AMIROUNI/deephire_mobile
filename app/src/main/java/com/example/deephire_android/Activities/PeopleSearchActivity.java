package com.example.deephire_android.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.deephire_android.Models.Profile;
import com.example.deephire_android.R;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PeopleSearchActivity extends AppCompatActivity {

    private String email;
    private List<Profile> profiles;
    private Toolbar toolbar;
    private Chip chipProfile;
    private AutoCompleteTextView autoCompleteSearch;
    private ListView listViewPeople;
    private ArrayAdapter<String> specialtiesAdapter;
    private ArrayAdapter<String> profilesAdapter; // Changed to String to display profile info
    private Set<String> specialtiesSet = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_people_search);

        // Initialize views
        chipProfile = findViewById(R.id.chipProfile);
        autoCompleteSearch = findViewById(R.id.autoCompleteSearch);
        toolbar = findViewById(R.id.toolbar);
        listViewPeople = findViewById(R.id.listViewPeople);

        // Set up toolbar
        setSupportActionBar(toolbar);

        // Get email from intent
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        if (email == null || email.isEmpty()) {
            Toast.makeText(this, "Error: No email provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set email in profile chip
        chipProfile.setText(email);

        // Initialize adapters
        specialtiesAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, new ArrayList<>());
        autoCompleteSearch.setAdapter(specialtiesAdapter);

        // Changed profilesAdapter to use String instead of Profile
        profilesAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, new ArrayList<>());
        listViewPeople.setAdapter(profilesAdapter);

        // Load all profiles
        loadAllProfiles();

        // Set up search functionality
        autoCompleteSearch.setOnItemClickListener((parent, view, position, id) -> {
            String selectedSpecialty = (String) parent.getItemAtPosition(position);
            filterProfilesBySpecialty(selectedSpecialty);
        });
    }

    private void loadAllProfiles() {
        Profile.getAllProfiles(new Profile.AllProfilesCallback() {
            @Override
            public void onProfilesLoaded(List<Profile> loadedProfiles) {
                profiles = loadedProfiles != null ? loadedProfiles : new ArrayList<>();
                updateSpecialtiesList(profiles);
                updateProfilesList(profiles);
            }

            @Override
            public void onError(Exception e) {
                Log.e("PeopleSearchActivity", "Error loading profiles: " + e.getMessage(), e);
                Toast.makeText(PeopleSearchActivity.this,
                        "Error loading profiles: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
                // Initialize with empty list to prevent crashes
                profiles = new ArrayList<>();
                updateSpecialtiesList(profiles);
                updateProfilesList(profiles);
            }
        });
    }

    private void updateSpecialtiesList(List<Profile> profiles) {
        specialtiesSet.clear();
        for (Profile profile : profiles) {
            if (profile.getSpecialty() != null && !profile.getSpecialty().isEmpty()) {
                specialtiesSet.add(profile.getSpecialty());
            }
        }

        List<String> specialtiesList = new ArrayList<>(specialtiesSet);
        specialtiesAdapter.clear();
        specialtiesAdapter.addAll(specialtiesList);
        specialtiesAdapter.notifyDataSetChanged();
    }

    private void updateProfilesList(List<Profile> profiles) {
        List<String> profileStrings = new ArrayList<>();
        for (Profile profile : profiles) {
            String profileInfo = profile.getEmail() +
                    (profile.getSpecialty() != null && !profile.getSpecialty().isEmpty() ? " - " + profile.getSpecialty() : "");
            profileStrings.add(profileInfo);
        }

        profilesAdapter.clear();
        profilesAdapter.addAll(profileStrings); // Fixed: No casting needed, using List<String>
        profilesAdapter.notifyDataSetChanged();
    }

    private void filterProfilesBySpecialty(String specialty) {
        if (profiles == null) {
            profiles = new ArrayList<>();
        }

        List<Profile> filteredProfiles = new ArrayList<>();
        for (Profile profile : profiles) {
            if (specialty != null && specialty.equals(profile.getSpecialty())) {
                filteredProfiles.add(profile);
            }
        }

        updateProfilesList(filteredProfiles);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_profile) {
            Log.d("PeopleSearchActivity", "Profile menu selected");
            return true;
        } else if (id == R.id.menu_logout) {

            Log.d("PeopleSearchActivity", "Logout selected, finishing activity");
            finish();
            return true;
        } else if (id == R.id.search) {
            // Already in search activity, no action needed
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        Log.d("PeopleSearchActivity", "finish() called", new Throwable("Stack trace"));
        super.finish();
    }

    @Override
    public void onBackPressed() {
        Log.d("PeopleSearchActivity", "Back button pressed");
        super.onBackPressed();
    }
}