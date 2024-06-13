// Path: final_se/com/ui/livestream/LivestreamFragment.java

package final_se.com.ui.livestream;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import final_se.com.R;
import final_se.com.databinding.FragmentLivestreamBinding;

public class LivestreamFragment extends Fragment {

    private FragmentLivestreamBinding binding;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentLivestreamBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // WebView setup
        WebView webView = root.findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.loadUrl("192.168.175.57:8000"); // Replace with your desired URL

        return root;
    }
}
