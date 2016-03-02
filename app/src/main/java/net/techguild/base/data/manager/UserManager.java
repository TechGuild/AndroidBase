package net.techguild.base.data.manager;

import net.techguild.base.data.api.UserService;
import net.techguild.base.data.model.User;
import net.techguild.base.data.request.LoginRequest;
import net.techguild.base.data.response.LoginResponse;
import net.techguild.base.data.response.UserResponse;
import net.techguild.base.util.ApiResponseObserver;
import net.techguild.base.util.CLog;

import rx.functions.Action1;

public class UserManager {

    private final UserStore userStore;
    private final UserService userService;

    public UserManager(UserStore userStore,
                       UserService userService) {
        this.userStore = userStore;
        this.userService = userService;
        userStore.initToken();
    }

    public void login(String email, String password) {
        // Sample api request
        ApiResponseObserver.observe(
                userService.login(new LoginRequest(email, password)),
                new Action1<LoginResponse>() {
                    @Override public void call(LoginResponse loginResponse) {
                        // Success handler
                        userStore.updateUserCredentials(loginResponse);
                    }
                },
                new Action1<Throwable>() {
                    @Override public void call(Throwable throwable) {
                        // Error Handler
                        CLog.e("T", "Login error", throwable);
                    }
                }
        );
    }

    public synchronized void fetchUser(final FetchUserResponseHandler handler) {
        // If user is logged in return the user from store or fetch from api
        if (userStore.isLoggedIn()) {
            if (userStore.getUser() == null) {
                fetchUserFromServer(handler);
            } else {
                handler.onFetchUserSuccess(userStore.getUser());
            }
        } else {
            handler.onFetchError();
        }
    }

    private void fetchUserFromServer(final FetchUserResponseHandler handler) {
        // Sample api request
        ApiResponseObserver.observe(
                userService.getUser(),
                new Action1<UserResponse>() {
                    @Override public void call(UserResponse userResponse) {
                        // Success handler
                        userStore.setUser(userResponse.user);
                        handler.onFetchUserSuccess(userResponse.user);
                    }
                },
                new Action1<Throwable>() {
                    @Override public void call(Throwable throwable) {
                        // Error Handler
                        CLog.e("T", "Fetch User error", throwable);
                        handler.onFetchError();
                    }
                }
        );
    }

    public void logout() {
        User user = userStore.getUser();
        userStore.onLogout();
    }

    public boolean isLoggedIn() {
        return userStore.isLoggedIn();
    }

    public void onUserDataUpdated() {
        // Clear user data
        userStore.setUser(null);
    }

    public interface FetchUserResponseHandler {
        void onFetchUserSuccess(User user);

        void onFetchError();
    }
}
