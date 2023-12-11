package com.example.thiago.chatapp;

        import android.app.DatePickerDialog;
        import android.content.Context;
        import android.content.Intent;

        import java.io.ByteArrayOutputStream;
        import java.text.SimpleDateFormat;
        import java.util.ArrayList;
        import java.util.Calendar;
        import java.util.HashMap;
        import java.util.List;
        import java.util.Locale;
        import java.util.Map;
        import java.util.regex.Matcher;
        import java.util.regex.Pattern;

        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.os.Bundle;
        import android.text.TextUtils;
        import android.util.Base64;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.view.Window;
        import android.widget.AdapterView;
        import android.widget.ArrayAdapter;
        import android.widget.Button;
        import android.widget.DatePicker;
        import android.widget.EditText;
        import android.widget.ProgressBar;
        import android.widget.Spinner;
        import android.widget.TextView;
        import android.widget.Toast;
        import android.content.Context;


        import com.google.android.gms.auth.api.signin.GoogleSignIn;
        import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
        import com.google.android.gms.auth.api.signin.GoogleSignInClient;
        import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
        import com.google.android.gms.common.SignInButton;
        import com.google.android.gms.common.api.ApiException;
        import com.google.android.gms.tasks.OnCompleteListener;
        import com.google.android.gms.tasks.Task;
        import com.google.firebase.auth.AuthCredential;
        import com.google.firebase.auth.AuthResult;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;
        import com.google.firebase.auth.GoogleAuthProvider;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.IgnoreExtraProperties;

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;
        import org.json.JSONTokener;
//import com.google.firebase.firestore.FirebaseFirestore;
        import androidx.annotation.NonNull;
        import androidx.annotation.Nullable;
        import androidx.appcompat.app.AppCompatActivity;
        import com.example.thiago.chatapp.Users;


/**
 * Created by Thiago on 10/10/2016.
 */


public class SignUpActivity extends AppCompatActivity {
    private EditText inputEmail, inputPassword, signupInputUsername, age, gender;
    private String image;
    private Button btnLinkLogin, btnSignUp, btnResetPassword;
    private static final int RC_SIGN_IN = 1001;
    GoogleSignInClient googleSignInClient;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private Spinner sexSpinner;
    private ArrayAdapter adapter;
    String gender_selected, mCurrent_user_id;
    String defaultTextForSpinner = "Sex";
    String[] arrayForSpinner = {"Male", "Female", "Other"};
    DatabaseReference reference;


    private DatabaseReference mDatabase;
    Calendar mcurrentDate = Calendar.getInstance();
    DatePickerDialog datePickerDialog;
    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            mcurrentDate.set(Calendar.YEAR, year);
            mcurrentDate.set(Calendar.MONTH, monthOfYear);
            mcurrentDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        // Build a GoogleSignInClient with the options specified by gso.
        //mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        //**********************************************************


        //**********************************************************



        auth = FirebaseAuth.getInstance();
        DatabaseReference reference;
        mDatabase = FirebaseDatabase.getInstance().getReference();

        inputEmail = (EditText) findViewById(R.id.signup_input_email);
        signupInputUsername = (EditText) findViewById(R.id.signup_input_username);
        inputPassword = (EditText) findViewById(R.id.signup_input_password);
        age = (EditText) findViewById(R.id.signup_input_birthday);
        gender = (EditText) findViewById(R.id.signup_input_gender);
        sexSpinner = (Spinner)findViewById(R.id.spinner);
        sexSpinner.setAdapter(new CustomSpinnerAdapter(this, R.layout.spinner_row, arrayForSpinner, defaultTextForSpinner));

        btnSignUp = (Button) findViewById(R.id.btnSignUp2);
        btnLinkLogin = (Button) findViewById(R.id.btnorlogin);
        progressBar = (ProgressBar) findViewById(R.id.determinateBar2);

        List<JSONObject> array = new ArrayList<JSONObject>();


        //proceed to rest off app if user is logged in.
        if(auth.getCurrentUser() != null){
            //close this activity
            finish();
            //opening profile activity
            startActivity(new Intent(getApplicationContext(), FragmentActivity.class));
        }

        gender.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View arg0, boolean hasFocus) {
                // TODO Auto-generated method stub
                if(hasFocus){
                    sexSpinner.setVisibility(View.VISIBLE);

                }
            }
        });

        sexSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
                //gender_selected = (String) sexSpinner.getSelectedItem().toString();
                gender.setText(gender_selected);
                gender.setText(sexSpinner.getSelectedItem().toString()); //this is taking the first value of the spinner by default.

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });


        age.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(SignUpActivity.this, date, mcurrentDate
                        .get(Calendar.YEAR), mcurrentDate.get(Calendar.MONTH),
                        mcurrentDate.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        gender.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                sexSpinner.performClick();
            }
        });

        btnLinkLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(SignUpActivity.this, LogInActivity.class);
                startActivity(i);
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                String username = signupInputUsername.getText().toString().trim();
                String genderString = gender.getText().toString().trim();
                String ageString = age.getText().toString().trim();

                String validemail = "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +

                        "\\@" +

                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +

                        "(" +

                        "\\." +

                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +

                        ")+";
                Matcher matcherObj = Pattern.compile(validemail).matcher(email);


                if (matcherObj.matches()) {

                    //Toast.makeText(getApplicationContext(), "enter all details", Toast.LENGTH_SHORT).show();

                }

                else
                {
                    Toast.makeText(getApplicationContext(),"Please enter a valid email",Toast.LENGTH_SHORT).show();

                }

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password.", Toast.LENGTH_SHORT).show();
                    return;
                }
                //Check if password is atleast 6 characters long.
                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Additional checks can go here, such as checking for a specific pattern or combination of characters in the password.

                progressBar.setVisibility(View.VISIBLE);
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    FirebaseUser user = auth.getCurrentUser();
                                    String userid = user.getUid();
                                    if (image != null) {
                                        image.trim();
                                    } else {
                                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.profile_thumbnail);
                                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                        byte[] data = baos.toByteArray();
                                        String image = Base64.encodeToString(data, Base64.DEFAULT);
                                        mDatabase.child("users").child(userid).child("image").setValue(image);
                                    }
                                    writeNewUser(userid, username, email, password,
                                            gender.getText().toString(), age.getText().toString(), image);
                                    //mDatabase.child("users").child(userid).setValue(newuser);
                                    Intent intent=new Intent(SignUpActivity.this,FragmentActivity.class);
                                    startActivity(intent);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(SignUpActivity.this, "Authentication failed." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                }
                                progressBar.setVisibility(View.GONE);
                            }
                        });

            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    public class CustomSpinnerAdapter extends ArrayAdapter<String> {

        Context context;
        String[] objects;
        String firstElement;
        boolean isFirstTime;

        public CustomSpinnerAdapter(Context context, int textViewResourceId, String[] objects, String defaultText) {
            super(context, textViewResourceId, objects);
            this.context = context;
            this.objects = objects;
            this.isFirstTime = true;
            setDefaultText(defaultText);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            if(isFirstTime) {
                objects[0] = firstElement;
                isFirstTime = false;
            }
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            notifyDataSetChanged();
            return getCustomView(position, convertView, parent);
        }

        public void setDefaultText(String defaultText) {
            this.firstElement = objects[0];
            objects[0] = defaultText;
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = inflater.inflate(R.layout.spinner_row, parent, false);
            TextView label = (TextView) row.findViewById(R.id.spinner_text);
            label.setText(objects[position]);

            return row;
        }



    }


    private void writeNewUser(String userId, String username, String email, String password, String gender, String age, String image) {
        Users newuser = new Users(userId,username, email, password, gender, age, image);
        mDatabase.child("users").child(userId).setValue(newuser);
    }




    private void setDateTimeField() {
        Calendar newCalendar = mcurrentDate;
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mcurrentDate.set(year, monthOfYear, dayOfMonth, 0, 0);
                age.setText(new StringBuilder().append(monthOfYear).append("/").append(dayOfMonth+1).append("/").append(year));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setTitle("Select birthday");
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.getDatePicker().setLayoutMode(1);
        //age.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
    }

    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        age.setText(sdf.format(mcurrentDate.getTime()));
    }




}
