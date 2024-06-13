package final_se.com.ui.signup;

import android.os.Bundle;
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

import final_se.com.databinding.FragmentSignupBinding;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.json.JSONObject;

import java.io.IOException;

public class SignUpFragment extends Fragment {

    private FragmentSignupBinding binding;
    private EditText nameEditText;
    private EditText passwordEditText;
    private Button signUpButton;
    private OkHttpClient client;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SignUpViewModel signUpViewModel =
                new ViewModelProvider(this).get(SignUpViewModel.class);

        binding = FragmentSignupBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textAccount;
        signUpViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        // Initialize UI elements
        nameEditText = binding.nameEditText;
        passwordEditText = binding.passwordEditText;
        signUpButton = binding.signupButton;
        client = new OkHttpClient();

        // Set up the sign-up button click listener
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if (name.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getActivity(), "Please fill in both fields", Toast.LENGTH_SHORT).show();
                } else {
                    signUp(name, password);
                }
            }
        });

        // Find the login text view
        TextView loginTextView = binding.loginTextView;

        // Set onClickListener to the login text view
        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed(); // Go back to the previous fragment
            }
        });

        return root;
    }

    private void signUp(String name, String password) {
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        JSONObject json = new JSONObject();
        try {
            json.put("name", name);
            json.put("password", password);
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(json.toString(), JSON);
        Request request = new Request.Builder()
                .url("http://192.168.175.57:5000/signup")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "Sign up failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseData = response.body().string();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (responseData.equals("User já existe!")) {
                            Toast.makeText(getActivity(), "User already exists", Toast.LENGTH_SHORT).show();
                        } else if (responseData.equals("User criado!")) {
                            Toast.makeText(getActivity(), "Account Created Successfully!", Toast.LENGTH_SHORT).show();
                            getActivity().onBackPressed(); // Go back to the previous fragment
                        } else {
                            Toast.makeText(getActivity(), "Unexpected response", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
