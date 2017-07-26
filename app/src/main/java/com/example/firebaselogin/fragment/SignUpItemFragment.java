package com.example.firebaselogin.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.firebaselogin.R;
import com.example.firebaselogin.model.Register;

import java.util.ArrayList;
import java.util.regex.Pattern;


/**
 * Created by albertsanchez on 25/7/17.
 */

public class SignUpItemFragment extends Fragment {

    private static final String TAG = "SignUpItemFr agment";
    private int pageNumber;
    private TextInputLayout etName, etLasName, etEmail, etUsername, etPass, etPass2;
    private OnListener mCallback;
    private Register signup;

    public static SignUpItemFragment newInstance(int page) {
        SignUpItemFragment fragment = new SignUpItemFragment();
        fragment.setPage(page);
        return fragment;
    }

    public interface OnListener {
        void onRegister(String pass);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnHeadlineSelectedListener");
        }
    }

    public void setPage(int page) {
        pageNumber = page;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.viewpager_signup, container, false);

        etName = (TextInputLayout) view.findViewById(R.id.name_signup);
        etLasName = (TextInputLayout) view.findViewById(R.id.lastname_signup);
        etEmail = (TextInputLayout) view.findViewById(R.id.email_signup);
        etUsername = (TextInputLayout) view.findViewById(R.id.username_signup);
        etPass = (TextInputLayout) view.findViewById(R.id.pass1_signup);
        etPass2 = (TextInputLayout) view.findViewById(R.id.pass2_signup);

        switch (pageNumber) {
            case 0:
                etName.setVisibility(View.VISIBLE);
                etLasName.setVisibility(View.VISIBLE);
                break;
            case 1:
                etEmail.setVisibility(View.VISIBLE);
                etUsername.setVisibility(View.VISIBLE);
                break;
            case 2:
                etPass.setVisibility(View.VISIBLE);
                etPass2.setVisibility(View.VISIBLE);
                break;
        }
        return view;

    }

    public boolean validateUser() {

        String username = etUsername.getEditText().getText().toString();
        String email = etEmail.getEditText().getText().toString();

        if (!esUsernameValido(username)) {
        } else {
            if (!esValidateEmail(email)) {
            } else {
                return true;
            }
        }
        return false;
    }

    public void validatePass() {
        String password = etPass.getEditText().getText().toString();
        String passwo2 = etPass2.getEditText().getText().toString();
        if (!esValidatePass(password)) {
        } else {
            if (!esValidatePass2(passwo2)) {
            } else {
                if (!passwo2.equals(password)) {
                    etPass2.setError("Passwords are not the same");
                } else {
                    Log.d("passs", "correcto");
                    etPass2.setError(null);
                    mCallback.onRegister(password);
                }
            }
        }

    }

    public boolean validateNames() {

        String name = etName.getEditText().getText().toString();
        String lastname = etLasName.getEditText().getText().toString();

        if (!esNombreValido(name)) {
        } else {
            if (!esApellidoValido(lastname)) {
            } else {
                return true;
            }
        }
        return false;
    }


    private boolean esUsernameValido(String username) {
        if (username.isEmpty()) {
            etUsername.setError("Username Empty");
            return false;
        }
        etUsername.setError(null);
        return true;
    }

    private boolean esNombreValido(String nombre) {

        if (nombre.isEmpty() || nombre.length() > 30) {
            etName.setError("Name Empty");
            return false;
        }
        etName.setError(null);
        return true;
    }

    private boolean esApellidoValido(String lastname) {
        Pattern patron = Pattern.compile("^[a-zA-Z ]+$");
        if (!patron.matcher(lastname).matches() || lastname.length() > 30) {
            etLasName.setError("Lastname Empty");
            return false;
        }
        etLasName.setError(null);
        return true;
    }


    private boolean esValidateEmail(String email) {
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("E-mail Invalidate");
            return false;
        }
        etEmail.setError(null);
        return true;
    }

    private boolean esValidatePass(String pass) {
        if (pass.isEmpty()) {
            etPass.setError("Password Empty");
            return false;
        }
        etPass.setError(null);
        return true;
    }

    private boolean esValidatePass2(String pass) {
        if (pass.isEmpty()) {
            etPass2.setError("Password Empty");
            return false;
        }
        etPass2.setError(null);
        return true;
    }

    public ArrayList<String> getAttributes() {
        ArrayList<String> attributes = new ArrayList<>();

        switch (pageNumber) {
            case 0:
                attributes.add(etName.getEditText().getText().toString());
                attributes.add(etLasName.getEditText().getText().toString());
                break;
            case 1:
                attributes.add(etEmail.getEditText().getText().toString());
                attributes.add(etUsername.getEditText().getText().toString());
                break;
            case 2:
                attributes.add(etPass.getEditText().getText().toString());
                break;
        }
        return attributes;
    }

}
