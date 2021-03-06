package com.example.googleandroidsignindemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {

    public static final int SIGN_IN_REQUEST_CODE = 1001;
    public static final String TAG="MyTag";
    private GoogleSignInClient googleSignInClient;

    private TextView mOutputText;
    private Button mBtnSignOut;
    private SignInButton mBtnGoogleSignIn;

    private  ImageView prof_pic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mOutputText=findViewById(R.id.tv_output);
        mBtnSignOut=findViewById(R.id.btn_signout);
        mBtnGoogleSignIn=findViewById(R.id.signInButton);

        prof_pic= findViewById(R.id.prof_pic);

        GoogleSignInOptions googleSignInOptions=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()

                .requestProfile()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this,googleSignInOptions);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
        mBtnGoogleSignIn.setOnClickListener(this::signIn);
        mBtnSignOut.setOnClickListener(this::signOut);

    }

    private void signOut(View view) {
        googleSignInClient.signOut()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "User logged out", Toast.LENGTH_SHORT).show();
                            updateUI(GoogleSignIn.getLastSignedInAccount(MainActivity.this));
                        }else {
                            Toast.makeText(MainActivity.this,"some error",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signIn(View view) {
        Intent singInIntent=googleSignInClient.getSignInIntent();
        startActivityForResult(singInIntent,SIGN_IN_REQUEST_CODE);

    }

    private void updateUI(GoogleSignInAccount account) {


        if (account !=null){
            mBtnSignOut.setVisibility(View.VISIBLE);
            mOutputText.setText(account.getDisplayName() +"\n"+
                    account.getEmail());

            //prof_pic.setImageResource(Integer.parseInt(account.getPhotoUrl().toString()));
            Glide.with(getApplicationContext()).load(account.getPhotoUrl()).into(prof_pic);
            mBtnGoogleSignIn.setVisibility(View.GONE);
        }else {
            mBtnSignOut.setVisibility(View.GONE);
            mOutputText.setText("User is not logged in");
            mBtnGoogleSignIn.setVisibility(View.VISIBLE);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode== SIGN_IN_REQUEST_CODE){
            Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleGoogleSignIn(accountTask);
        }

    }

    private void handleGoogleSignIn(Task<GoogleSignInAccount> accountTask) {
        try {
            GoogleSignInAccount account = accountTask.getResult(ApiException.class);

            Log.d(TAG,"Account"+account);
            updateUI(account);
        } catch (ApiException e) {
            mOutputText.setText(GoogleSignInStatusCodes.getStatusCodeString(e.getStatusCode()));
            Log.d(TAG,"handleGoogleSignIn:Error status code"+e.getStatusCode());
            Log.d(TAG,"handleGoogleSignIn:Error status message"+GoogleSignInStatusCodes.getStatusCodeString(e.getStatusCode()));
        }
    }
}
