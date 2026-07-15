package com.example.syadinie_app;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;

public class ViewFriendsActivity extends BaseActivity implements SearchView.OnQueryTextListener {

    SearchView searchView;
    ListView listView;
    ImageButton btnBack;
    FriendAdapter adapter;
    List<Friend> friendsList;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_friends);

        searchView = findViewById(R.id.search_view);
        listView = findViewById(R.id.friend_list);
        btnBack = findViewById(R.id.btnBack);

        dbHelper = new DatabaseHelper(this);
        friendsList = new ArrayList<>();

        // 1. Sedut data ringan (id & name sahaja) dari database
        loadFriendsFromDatabase();

        // 2. Pasang data ke adapter
        adapter = new FriendAdapter(this, friendsList);
        listView.setAdapter(adapter);

        // 3. FUNGSI KLIK: Tekan nama, hantar ID, pergi ke ViewDetailsActivity
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Friend selectedFriend = adapter.getItem(position);
            if (selectedFriend != null) {
                Intent intent = new Intent(ViewFriendsActivity.this, ViewDetailsActivity.class);
                intent.putExtra("BUDDY_ID", (long) selectedFriend.getId());
                startActivity(intent);
            }
        });

        searchView.setOnQueryTextListener(this);
        btnBack.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFriendsFromDatabase();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    // Taktik Selamat: Sedut 'id' dan 'name' sahaja untuk elak crash ejaan kolum alamat
    private void loadFriendsFromDatabase() {
        friendsList.clear();
        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            // Kita cuma minta ID dan Nama sahaja dari table friends
            Cursor cursor = db.rawQuery("SELECT id, name FROM friends", null);

            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));

                    // Kolum lain kita letak null/kosong sebab skrin ni cuma nak pakai Nama sahaja
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

    static class FriendAdapter extends ArrayAdapter<Friend> {
        private List<Friend> originalList;
        private List<Friend> filteredList;

        FriendAdapter(@NonNull Context context, List<Friend> friends) {
            super(context, android.R.layout.simple_list_item_1, friends);
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
                        .inflate(android.R.layout.simple_list_item_1, parent, false);
            }

            if (position < filteredList.size()) {
                Friend friend = filteredList.get(position);
                TextView tvName = convertView.findViewById(android.R.id.text1);

                if (tvName != null) {
                    tvName.setText(friend.getName());
                    tvName.setTextColor(Color.WHITE); // Tulisan nama warna putih
                    tvName.setTextSize(16);
                    tvName.setPadding(8, 12, 8, 12);
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