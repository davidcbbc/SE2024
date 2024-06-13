package final_se.com.ui.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;

import java.io.IOException;
import java.util.Objects;

import final_se.com.R;
import final_se.com.databinding.FragmentAccountBinding;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AccountFragment extends Fragment {
    private FragmentAccountBinding binding;
    private OkHttpClient client;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAccountBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        client = new OkHttpClient();

        final EditText nameEditText = binding.nameEditText;
        final EditText passwordEditText = binding.passwordEditText;
        final TextView signUpTextView = binding.signUpTextView;

        signUpTextView.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.navigation_signup);
        });

        binding.loginButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (name.isEmpty() || password.isEmpty()) {
                Toast.makeText(getActivity(), "Please fill in both fields", Toast.LENGTH_SHORT).show();
            } else {
                attemptLogin(name, password);
            }
        });

        return root;
    }

    private void attemptLogin(String name, String password) {
        JsonObject json = new JsonObject();
        json.addProperty("name", name);
        json.addProperty("password", password);

        String jsonString = json.toString();

        RequestBody body = RequestBody.create(
                jsonString, MediaType.get("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url("http://192.168.175.57:5000/login")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(() ->
                        Toast.makeText(getActivity(), "Login failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    JsonObject jsonResponse = new Gson().fromJson(responseData, JsonObject.class);
                    JsonElement userIdElement = jsonResponse.get("user_id");
                    JsonElement nameElement = jsonResponse.get("name");
                    JsonElement balanceElement = jsonResponse.get("balance");

                    getActivity().runOnUiThread(() -> {
                        if (userIdElement != null && userIdElement.isJsonPrimitive() &&
                                nameElement != null && nameElement.isJsonPrimitive() &&
                                balanceElement != null && balanceElement.isJsonPrimitive()) {
                            String userId = userIdElement.getAsString();
                            String name = nameElement.getAsString();
                            String balance = balanceElement.getAsString();
                            if (Objects.equals(userId, "0")) {
                                Toast.makeText(getActivity(), "Invalid login or missing values", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), "Login successful", Toast.LENGTH_SHORT).show();
                                // Navigate to LoggedInFragment
                                Bundle bundle = new Bundle();
                                bundle.putString("user_id", userId);
                                bundle.putString("name", name);
                                bundle.putString("balance", balance);
                                Navigation.findNavController(getView()).navigate(R.id.navigation_loggedin, bundle);
                            }
                        } else {
                            Toast.makeText(getActivity(), "Invalid response from server", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(getActivity(), "Login failed: " + response.message(), Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
