package final_se.com.ui.loggedin;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LoggedInViewModel extends ViewModel {

    private final MutableLiveData<String> userId = new MutableLiveData<>();
    private final MutableLiveData<String> name = new MutableLiveData<>();
    private final MutableLiveData<String> balance = new MutableLiveData<>();

    public void setUserId(String userId) {
        this.userId.setValue(userId);
    }

    public LiveData<String> getUserId() {
        return userId;
    }

    public void setName(String name) {
        this.name.setValue(name);
    }

    public LiveData<String> getName() {
        return name;
    }

    public void setBalance(String balance) {
        this.balance.setValue(balance);
    }

    public LiveData<String> getBalance() {
        return balance;
    }
}
