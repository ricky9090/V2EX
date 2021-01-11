package cn.denua.v2ex.ui;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.blankj.utilcode.util.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.denua.v2ex.R;
import cn.denua.v2ex.base.BaseNetworkActivity;
import cn.denua.v2ex.interfaces.NextResponseListener;
import cn.denua.v2ex.model.Account;
import cn.denua.v2ex.service.UserService;
import cn.denua.v2ex.Config;
import cn.denua.v2ex.utils.DialogUtil;

/*
 * LoginActivity
 *
 * @author denua
 * @date 2018/10/20
 */
public class LoginActivity extends BaseNetworkActivity implements NextResponseListener<Bitmap,Account> {

    public static final int RESULT_SUCCESS = 6;


    TextInputEditText etAccount;
    TextInputEditText etPassword;
    TextInputEditText etCaptchaCode;
    ImageView ivCaptcha;
    ProgressBar progressBar;

    private UserService loginService;

    private AlertDialog mProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_login);
        ButterKnife.bind(this);
        bindView();

        setTitle(R.string.login);
        mProgressDialog = DialogUtil.getProgress(this,
                getResources().getString(R.string.logging_in), null);
        loginService = new UserService(this,this);
        loginService.preLogin();
        ivCaptcha.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        etAccount.setOnKeyListener(onNextKey);
        etPassword.setOnKeyListener(onNextKey);
        etCaptchaCode.setOnKeyListener(onNextKey);
    }

    private void bindView() {
        etAccount = findViewById(R.id.et_account);
        etPassword = findViewById(R.id.et_password);
        etCaptchaCode = findViewById(R.id.et_check_code);
        ivCaptcha = findViewById(R.id.iv_captcha);
        progressBar = findViewById(R.id.progress_captcha);
        findViewById(R.id.bt_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        ivCaptcha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refresh(null);
            }
        });
    }


    public void login(){

        String mAccount = etAccount.getText().toString().trim();
        String mPassword = etPassword.getText().toString().trim();
        String mCaptcha = etCaptchaCode.getText().toString().trim();

        if (mAccount.equals("")){
            etAccount.setError(getString(R.string.username_format_error));
            return;
        }
        if (mPassword.equals("")){
            etPassword.setError(getString(R.string.password_format_error));
            return;
        }
        if (mCaptcha.equals("")){
            etCaptchaCode.setError(getString(R.string.captcha_format_error));
            return;
        }

        loginService.login(
                etAccount.getText().toString(),
                etPassword.getText().toString(),
                etCaptchaCode.getText().toString());
        mProgressDialog.show();
    }


    public void refresh(ImageView view){
        loginService.preLogin();
        ivCaptcha.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onFailed(String msg) {
        mProgressDialog.dismiss();
        if (msg.equals(UserService.STATUS_WRONG_FIELDS)) {
            loginService.preLogin();
        }
        progressBar.setVisibility(View.GONE);
        ToastUtils.showShort(msg);
        return true;
    }

    @Override
    public void onNextResult(Bitmap next) {
        ivCaptcha.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        ivCaptcha.setImageBitmap(next);
    }

    @Override
    public void onComplete(Account result) {

        mProgressDialog.dismiss();
        mProgressDialog = null;
        Config.setAccount(result);
        Config.getAccount().login();
        setResult(RESULT_SUCCESS);
        Config.persistentAccount(this);
        finish();
    }

    View.OnKeyListener onNextKey = new View.OnKeyListener() {
        @Override
        public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
            if(keyCode == KeyEvent.KEYCODE_ENTER){
                login();
                return true;
            }
            return false;
        }
    };
}
