package me.ewriter.bangumitv.ui.activity;

import android.app.ProgressDialog;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.greenrobot.eventbus.EventBus;

import me.ewriter.bangumitv.R;
import me.ewriter.bangumitv.api.LoginManager;
import me.ewriter.bangumitv.api.response.Token;
import me.ewriter.bangumitv.base.BaseActivity;
import me.ewriter.bangumitv.event.UserLoginEvent;
import me.ewriter.bangumitv.utils.LogUtil;
import me.ewriter.bangumitv.utils.ToastUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Zubin on 2016/8/1.
 */
public class LoginActivity extends BaseActivity {

    Toolbar mToolbar;
    TextInputLayout mNameEdit;
    TextInputLayout mPasswordEdit;
    Button mLoginButton;
    ProgressDialog mProgressDialog;

    @Override
    protected int getContentViewResId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initViews() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mNameEdit = (TextInputLayout) findViewById(R.id.emaiInput);
        mPasswordEdit = (TextInputLayout) findViewById(R.id.passwordInput);
        mLoginButton = (Button) findViewById(R.id.login_button);

        setupToolbar();
        setupLogin();

    }

    private void setupLogin() {
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mNameEdit.getEditText().getText().toString();
                String password = mPasswordEdit.getEditText().getText().toString();

                if (TextUtils.isEmpty(email)) {
                    mNameEdit.setError("请输入邮箱");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    mPasswordEdit.setError("请输入密码");
                    return;
                }

                showLoginProDialog();

                sBangumi.login(email, password).enqueue(new Callback<Token>() {
                    @Override
                    public void onResponse(Call<Token> call, Response<Token> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            LoginManager.saveToken(LoginActivity.this, response.body());
                            // 发送sticky Event 保证能收到事件
                            EventBus.getDefault().postSticky(new UserLoginEvent(response.body()));
                            LoginActivity.this.finish();
                        } else {
                            ToastUtils.showShortToast(LoginActivity.this, getString(R.string.login_fail));
                        }
                        mProgressDialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<Token> call, Throwable t) {
                        ToastUtils.showShortToast(LoginActivity.this, getString(R.string.login_fail));
                        LogUtil.d(LogUtil.ZUBIN, t.toString());
                        mProgressDialog.dismiss();
                    }
                });
            }
        });
    }

    private void showLoginProDialog() {
        mProgressDialog = ProgressDialog.show(this, getString(R.string.login), getString(R.string.login_doing));
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.login));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void initBeforeCreate() {

    }

    @Override
    protected boolean isSubscribeEvent() {
        return false;
    }
}
