package final_se.com.ui.shared;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<String> userId = new MutableLiveData<>();

    public void setUserId(String userId) {
        this.userId.setValue(userId);
    }

    public LiveData<String> getUserId() {
        return userId;
    }
}
