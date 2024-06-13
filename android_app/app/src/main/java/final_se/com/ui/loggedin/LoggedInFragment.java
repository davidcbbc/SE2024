package final_se.com.ui.loggedin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import final_se.com.R;
import final_se.com.databinding.FragmentLoggedinBinding;
import final_se.com.ui.shared.SharedViewModel;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

public class LoggedInFragment extends Fragment{

    private FragmentLoggedinBinding binding;
    private LoggedInViewModel loggedInViewModel;
    private SharedViewModel sharedViewModel;
    private OkHttpClient client;
    private TextView balanceTextView;
    private TextView refreshBalanceTextView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loggedInViewModel = new ViewModelProvider(this).get(LoggedInViewModel.class);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        client = new OkHttpClient();

        // Retrieve user_id from the bundle
        if (getArguments() != null) {
            String user_id = getArguments().getString("user_id");
            String name = getArguments().getString("name");
            String balance = getArguments().getString("balance");
            System.out.println("user_data = " + user_id + ", " + name + ", " + balance);

            if (user_id != null && name != null && balance != null) {
                loggedInViewModel.setUserId(user_id);
                loggedInViewModel.setName(name);
                loggedInViewModel.setBalance(balance);
                sharedViewModel.setUserId(user_id); // Set user_id in the shared ViewModel
            } else if (user_id != null) {
                fetchAccountDetails(user_id);
                sharedViewModel.setUserId(user_id); // Set user_id in the shared ViewModel
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLoggedinBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ImageView userImageView = binding.userImageView;
        TextView nameTextView = binding.nameTextView;
        balanceTextView = binding.balanceTextView;
        refreshBalanceTextView = binding.refreshBalanceTextView;
        Button addBalanceButton = binding.addBalanceButton;
        Button checkBetHistory = binding.checkBetHistory;
        Button signOutButton = binding.signOutButton;

        // Load default user image
        Picasso.get().load(R.drawable.account).into(userImageView);

        // Set up add balance button
        addBalanceButton.setOnClickListener(v -> showAddBalanceDialog());

        checkBetHistory.setOnClickListener(v -> showBetHistoryDialog());

        // Set up sign out button
        signOutButton.setOnClickListener(v -> {
            sharedViewModel.setUserId("");
            getActivity().onBackPressed();
        });

        // Set up refresh balance text
        refreshBalanceTextView.setOnClickListener(v -> {
            updateBalance();
            Toast.makeText(getActivity(), "Balance Refreshed!", Toast.LENGTH_SHORT).show();
        });

        // Observe the LiveData from the ViewModel
        loggedInViewModel.getName().observe(getViewLifecycleOwner(), nameTextView::setText);

        loggedInViewModel.getBalance().observe(getViewLifecycleOwner(), balanceTextView::setText);

        return root;
    }

    private void fetchAccountDetails(String userId) {
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        JSONObject json = new JSONObject();
        try {
            json.put("user_id", userId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(json.toString(), JSON);
        Request request = new Request.Builder()
                .url("http://192.168.175.57:5000/get_my_data")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                getActivity().runOnUiThread(() ->
                        Toast.makeText(getActivity(), "Failed to fetch account details", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    String responseData = response.body().string();
                    JsonObject jsonResponse = new Gson().fromJson(responseData, JsonObject.class);
                    JsonElement nameElement = jsonResponse.get("name");
                    JsonElement balanceElement = jsonResponse.get("balance");

                    if (nameElement != null && balanceElement != null) {
                        String name = nameElement.getAsString();
                        String balance = balanceElement.getAsString();

                        getActivity().runOnUiThread(() -> {
                            loggedInViewModel.setName(name);
                            loggedInViewModel.setBalance(balance);
                        });
                    } else {
                        getActivity().runOnUiThread(() ->
                                Toast.makeText(getActivity(), "Invalid response from server", Toast.LENGTH_SHORT).show());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showAddBalanceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Balance");

        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_balance, (ViewGroup) getView(), false);
        final EditText input = viewInflated.findViewById(R.id.input);

        builder.setView(viewInflated);

        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            dialog.dismiss();
            String amount = input.getText().toString();
            if (!amount.isEmpty()) {
                addBalance(amount);
            } else {
                Toast.makeText(getActivity(), "Please enter an amount", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void addBalance(String amount) {
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        JSONObject json = new JSONObject();
        try {
            json.put("user_id", sharedViewModel.getUserId().getValue()); // Retrieve user_id from the shared ViewModel
            json.put("amount", Integer.parseInt(amount));
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(json.toString(), JSON);
        Request request = new Request.Builder()
                .url("http://192.168.175.57:5000/add_balance")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                getActivity().runOnUiThread(() ->
                        Toast.makeText(getActivity(), "Transaction failed", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseData = response.body().string();
                getActivity().runOnUiThread(() -> {
                    if (responseData.equals("Balance added successfully")) {
                        Toast.makeText(getActivity(), "Transaction Completed Successfully", Toast.LENGTH_SHORT).show();
                        updateBalance();
                    } else {
                        Toast.makeText(getActivity(), "Unexpected response", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public void updateBalance() {
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        JSONObject json = new JSONObject();
        try {
            json.put("user_id", sharedViewModel.getUserId().getValue()); // Retrieve user_id from the shared ViewModel
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(json.toString(), JSON);
        Request request = new Request.Builder()
                .url("http://192.168.175.57:5000/get_my_data")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                getActivity().runOnUiThread(() ->
                        Toast.makeText(getActivity(), "Failed to update balance", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    String responseData = response.body().string();
                    JsonObject jsonResponse = new Gson().fromJson(responseData, JsonObject.class);
                    JsonElement balanceElement = jsonResponse.get("balance");
                    final String balance = balanceElement.getAsString();
                    getActivity().runOnUiThread(() -> loggedInViewModel.setBalance(balance)); // Update the balance in the ViewModel
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Enable options menu handling for this fragment
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle back arrow click
        if (item.getItemId() == android.R.id.home) {
            // Clear user ID
            sharedViewModel.setUserId("");
            // Go back to parent fragment
            requireActivity().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void showBetHistoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_bet_history, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        TableLayout betHistoryTable = dialogView.findViewById(R.id.betHistoryTable);
        addTableHeaders(betHistoryTable);

        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        JSONObject json = new JSONObject();
        try {
            json.put("user_id", sharedViewModel.getUserId().getValue());
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(json.toString(), JSON);
        Request request = new Request.Builder()
                .url("http://192.168.175.57:5000/get_past_bets")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                getActivity().runOnUiThread(() ->
                        Toast.makeText(getActivity(), "Failed to load bet history", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    System.out.println("ola. funcional");
                    String responseData = response.body().string();
                    System.out.println("responseData = " + responseData);
                    JsonArray jsonResponse = new Gson().fromJson(responseData, JsonArray.class);

                    getActivity().runOnUiThread(() -> {
                        for (JsonElement element : jsonResponse) {
                            JsonObject jsonObject = element.getAsJsonObject();
                            String amount = jsonObject.has("ammount") && !jsonObject.get("ammount").isJsonNull() ? jsonObject.get("ammount").getAsString() : "N/A";
                            String color = jsonObject.has("color") && !jsonObject.get("color").isJsonNull() ? jsonObject.get("color").getAsString() : "null";
                            String result = jsonObject.has("result") && !jsonObject.get("result").isJsonNull() ? jsonObject.get("result").getAsString() : "N/A";
                            String rouletteNumber = jsonObject.has("roulette_number") && !jsonObject.get("roulette_number").isJsonNull() ? jsonObject.get("roulette_number").getAsString() : "null";

                            addTableRow(betHistoryTable, amount, color, result, rouletteNumber);
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        dialog.show();
    }

    private void addTableHeaders(TableLayout table) {
        TableRow headerRow = new TableRow(getActivity());
        String[] headers = {"Amount", "Bet", "Result"};
        for (String header : headers) {
            TextView textView = new TextView(getActivity());
            textView.setText(header);
            textView.setPadding(16, 16, 16, 16);
            headerRow.addView(textView);
        }
        table.addView(headerRow);
    }

    private void addTableRow(TableLayout table, String amount, String color, String result, String rouletteNumber) {
        TableRow row = new TableRow(getActivity());
        String bet = "";
        String finalResult = "";

        if (Objects.equals(rouletteNumber, "10") || Objects.equals(rouletteNumber, "11")){
            bet = color;
        }
        else {
            bet = rouletteNumber;
        }

        String[] data = {amount, bet, result};
        for (String datum : data) {
            TextView textView = new TextView(getActivity());
            textView.setText(datum);
            textView.setPadding(16, 16, 16, 16);
            row.addView(textView);
        }
        System.out.println("row = " + row);
        table.addView(row);
    }


}
