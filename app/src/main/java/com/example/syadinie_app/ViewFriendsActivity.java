package com.example.syadinie_app;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ViewFriendsActivity extends BaseActivity implements SearchView.OnQueryTextListener {

    SearchView searchView;
    ListView listView;
    ImageButton btnBack;
    ImageButton btnVoiceSearch;
    FriendAdapter adapter;
    List<Friend> friendsList;
    DatabaseHelper dbHelper;

    ActivityResultLauncher<Intent> voiceLauncher;
    private static final int REQ_RECORD_AUDIO = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_friends);

        searchView = findViewById(R.id.search_view);
        listView = findViewById(R.id.friend_list);
        btnBack = findViewById(R.id.btnBack);
        btnVoiceSearch = findViewById(R.id.btnVoiceSearch);

        dbHelper = new DatabaseHelper(this);
        friendsList = new ArrayList<>();

        loadFriendsFromDatabase();

        adapter = new FriendAdapter(this, friendsList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Friend selectedFriend = adapter.getItem(position);
            if (selectedFriend != null) {
                Intent intent = new Intent(ViewFriendsActivity.this, ViewDetailsActivity.class);
                intent.putExtra("BUDDY_ID", (long) selectedFriend.getId());
                startActivity(intent);
            }
        });

        searchView.setOnQueryTextListener(this);

        // Make the typed search text and hint white so they're visible on the dark search bar
        int searchTextId = getResources().getIdentifier("search_src_text", "id", "android");
        TextView searchEditText = searchView.findViewById(searchTextId);
        if (searchEditText != null) {
            searchEditText.setTextColor(Color.WHITE);
            searchEditText.setHintTextColor(Color.parseColor("#B3FFFFFF"));
        }

        btnBack.setOnClickListener(v -> finish());

        voiceLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        ArrayList<String> spokenText = result.getData()
                                .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                        if (spokenText != null && !spokenText.isEmpty()) {
                            String name = spokenText.get(0);
                            searchView.setQuery(name, false);
                            adapter.getFilter().filter(name);
                        }
                    }
                });

        btnVoiceSearch.setOnClickListener(v -> startVoiceSearch());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFriendsFromDatabase();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void loadFriendsFromDatabase() {
        friendsList.clear();
        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT id, name FROM friends", null);

            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                    friendsList.add(new Friend(id, name, null, null, null, null, null, null, null));
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Database Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (adapter != null) {
            adapter.getFilter().filter(newText);
        }
        return false;
    }

    private void startVoiceSearch() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO}, REQ_RECORD_AUDIO);
            return;
        }
        launchSpeechRecognizer();
    }

    private void launchSpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say a name to search...");

        if (intent.resolveActivity(getPackageManager()) != null) {
            try {
                voiceLauncher.launch(intent);
            } catch (Exception e) {
                Toast.makeText(this, "Voice search failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No voice recognition app found on this device", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_RECORD_AUDIO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchSpeechRecognizer();
            } else {
                Toast.makeText(this, "Microphone permission needed for voice search", Toast.LENGTH_SHORT).show();
            }
        }
    }

    static class FriendAdapter extends ArrayAdapter<Friend> {
        private List<Friend> originalList;
        private List<Friend> filteredList;

        FriendAdapter(@NonNull Context context, List<Friend> friends) {
            super(context, R.layout.list_item_friend, friends);
            this.originalList = friends;
            this.filteredList = friends;
        }

        @Override
        public Friend getItem(int position) {
            return filteredList.get(position);
        }

        @Override
        public int getCount() {
            return filteredList.size();
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.list_item_friend, parent, false);
            }

            if (position < filteredList.size()) {
                Friend friend = filteredList.get(position);
                TextView tvName = convertView.findViewById(R.id.tvFriendName);
                if (tvName != null) {
                    tvName.setText(friend.getName());
                }
            }
            return convertView;
        }

        @NonNull
        @Override
        public android.widget.Filter getFilter() {
            return new android.widget.Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    List<Friend> results = new ArrayList<>();
                    if (constraint == null || constraint.length() == 0) {
                        results.addAll(originalList);
                    } else {
                        String filterPattern = constraint.toString().toLowerCase().trim();
                        for (Friend f : originalList) {
                            if (f.getName().toLowerCase().contains(filterPattern)) {
                                results.add(f);
                            }
                        }
                    }
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = results;
                    filterResults.count = results.size();
                    return filterResults;
                }

                @Override
                @SuppressWarnings("unchecked")
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    filteredList = (List<Friend>) results.values;
                    notifyDataSetChanged();
                }
            };
        }
    }
}