package com.example.syadinie_app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;

public class ViewFriendsActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    SearchView searchView;
    ListView listView;
    ImageButton btnBack;
    FriendAdapter adapter;
    List<Friend> friendsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_friends);

        searchView = findViewById(R.id.search_view);
        listView = findViewById(R.id.friend_list);
        btnBack = findViewById(R.id.btnBack);

        friendsList = new ArrayList<>();
        friendsList.add(new Friend(1, "Ahmad Faiz", "Male", "0123456789", "ahmad@test.com",
                "No. 1", "Jalan Test", "43000", "Selangor"));
        friendsList.add(new Friend(2, "Siti Aisyah", "Female", "0129876543", "siti@test.com",
                "No. 2", "Jalan Contoh", "50000", "Kuala Lumpur"));
        friendsList.add(new Friend(3, "Kumar Raj", "Male", "0135551234", "kumar@test.com",
                "No. 3", "Jalan Sample", "40000", "Selangor"));
        friendsList.add(new Friend(4, "Nur Aina", "Female", "0164447890", "aina@test.com",
                "No. 4", "Jalan Demo", "43000", "Selangor"));
        friendsList.add(new Friend(5, "Wei Ling", "Female", "0187778899", "weiling@test.com",
                "No. 5", "Jalan Uji", "43000", "Selangor"));

        adapter = new FriendAdapter(this, friendsList);
        listView.setAdapter(adapter);

        searchView.setOnQueryTextListener(this);

        btnBack.setOnClickListener(v -> finish());
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adapter.getFilter().filter(newText);
        return false;
    }

    // Custom adapter, inner class
    static class FriendAdapter extends ArrayAdapter<Friend> {
        private List<Friend> originalList;
        private List<Friend> filteredList;

        FriendAdapter(@NonNull AppCompatActivity context, List<Friend> friends) {
            super(context, 0, friends);
            this.originalList = new ArrayList<>(friends);
            this.filteredList = friends;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.list_item_friend, parent, false);
            }
            Friend friend = filteredList.get(position);
            TextView tvName = convertView.findViewById(R.id.tvName);
            TextView tvHp = convertView.findViewById(R.id.tvHp);
            tvName.setText(friend.getName());
            tvHp.setText(friend.getHp());
            return convertView;
        }

        @Override
        public int getCount() {
            return filteredList.size();
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