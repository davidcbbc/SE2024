package final_se.com.ui.betting;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;

import final_se.com.databinding.FragmentBettingBinding;
import final_se.com.ui.shared.SharedViewModel;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.json.JSONException;
import org.json.JSONObject;

public class BettingFragment extends Fragment {

    private FragmentBettingBinding binding;
    private BettingViewModel bettingViewModel;
    private SharedViewModel sharedViewModel;
    private OkHttpClient client;
    private String user_id;

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bettingViewModel = new ViewModelProvider(this).get(BettingViewModel.class);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        client = new OkHttpClient();
    }

    @Override
    public void onResume() {
        super.onResume();
        sharedViewModel.getUserId().observe(this, userId -> {
            user_id = userId;
        });
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentBettingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        TextView textView = binding.textBetting;
        Button[] buttons = new Button[]{
                binding.btn0, binding.btn1, binding.btn2, binding.btn3, binding.btn4, binding.btn5,
                binding.btn6, binding.btn7, binding.btn8, binding.btn9, binding.btnBlack, binding.btnRed
        };

        for (int i = 0; i < buttons.length; i++) {
            int finalI = i;
            buttons[i].setOnClickListener(v -> showBetDialog(finalI));
        }

        bettingViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    private void showBetDialog(int buttonNumber) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select the amount you want to bet:");

        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String amount = input.getText().toString();
            sendBetRequest(buttonNumber, amount);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void sendBetRequest(int buttonNumber, String amount) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("user_id", user_id);
            jsonObject.put("amount", Integer.parseInt(amount));
            jsonObject.put("roulette_number", buttonNumber);

            System.out.println("bet_data = " + user_id + "," + amount + "," + buttonNumber);

            RequestBody body = RequestBody.create(JSON, jsonObject.toString());
            Request request = new Request.Builder()
                    .url("http://192.168.175.57:5000/make_bet")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Network error. Please try again.", Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    String responseBody = response.body().string();
                    getActivity().runOnUiThread(() -> {
                        if (response.isSuccessful()) {
                            if (responseBody.equals("Bet placed")) {
                                showMessageDialog("Bet Placed Successfully");
                            } else if (responseBody.equals("Não tem dinheiro suficiente.")) {
                                showMessageDialog("No funds available for this bet");
                            }
                        } else {
                            Toast.makeText(getContext(), "Error: " + responseBody, Toast.LENGTH_SHORT).show();
                        } // Refresh balance after the bet response
                    });
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showMessageDialog(String message) {
        new AlertDialog.Builder(getContext())
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
