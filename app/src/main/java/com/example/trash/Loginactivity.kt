package com.example.trash


import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_loginactivity.*
import kotlinx.android.synthetic.main.item_detail.*

class Loginactivity : AppCompatActivity() {
    var auth : FirebaseAuth? = null
    private var editTextEmail: EditText? = null
    private var editTextPassword: EditText? = null

    private var email = ""
    private var password = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_loginactivity)
        auth = FirebaseAuth.getInstance()
        editTextEmail = findViewById(R.id.email_edittext);
        editTextPassword = findViewById(R.id.password_edittext);
        val user = FirebaseAuth.getInstance().currentUser;
        if (user != null) {
            // User is signed in
            moveMainPage(user)
        } else {
            // No user is signed in
        }
        email_login_button.setOnClickListener {
            signinEmail()
        }
        email_register_button.setOnClickListener {
            signinAndSignup()
        }
    }
    
    fun signinAndSignup(){
        email = editTextEmail?.getText().toString();
        password = editTextPassword?.getText().toString();
        auth?.createUserWithEmailAndPassword(email, password)
            ?.addOnCompleteListener {
                    task ->
                if(task.isSuccessful){
                    //Creating a user account

                    moveMainPage(task.result?.user)
                }else if(!task.exception?.message.isNullOrEmpty()){
                    //Show the error message
                    Toast.makeText(this,task.exception?.message, Toast.LENGTH_LONG).show()
                }else{
                    //Login if you have account
                    signinEmail()
                }
            }
    }
    fun signinEmail(){
        email = editTextEmail?.getText().toString();
        password = editTextPassword?.getText().toString();
        auth?.signInWithEmailAndPassword(email, password)
            ?.addOnCompleteListener {
                    task ->
                if(task.isSuccessful){
                    //Login
                    moveMainPage(task.result?.user)
                }else{
                    //Show the error message
                    Toast.makeText(this,task.exception?.message,Toast.LENGTH_LONG).show()
                }
            }
    }
    fun moveMainPage(user: FirebaseUser?){
        if(user != null){
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
    }
}