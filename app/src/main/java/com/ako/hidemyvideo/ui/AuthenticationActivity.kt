package com.ako.hidemyvideo.ui

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ako.hidemyvideo.Helper.CREATE
import com.ako.hidemyvideo.Helper.LOGIN
import com.ako.hidemyvideo.Helper.SharePref
import com.ako.hidemyvideo.Helper.vibratePhone
import com.ako.hidemyvideo.databinding.ActivityAuthenticationBinding

class AuthenticationActivity : AppCompatActivity() {
    lateinit var binding: com.ako.hidemyvideo.databinding.ActivityAuthenticationBinding
    var savepassword: String? = null
    var recoverPhase: String? = null
    lateinit var sharePref: SharePref
    private lateinit var editText1: EditText
    private lateinit var editText2: EditText
    private lateinit var editText3: EditText
    private lateinit var editText4: EditText
    private lateinit var showinfo: TextView
    var passwordStringbefore = ""
    var passwordString = ""
    var check = false

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthenticationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        sharePref = SharePref(this)
        savepassword = sharePref.getPassword()
        recoverPhase = sharePref.getRecoverPhase()
        editText1 = binding.editText1
        editText2 = binding.editText2
        editText3 = binding.editText3
        editText4 = binding.editText4
        showinfo = binding.showInfo
        Log.d("data", "Store password is $savepassword")
        Log.d("data", "Store phase is $recoverPhase")
        //check new user or old user
        if (savepassword.isNullOrEmpty()) {
            setupEditTextListener(CREATE)
            showinfo.setText("Welcome new user,Enter your password")
        } else {
            setupEditTextListener(LOGIN)
            showinfo.setText("Enter PIN to enter")
        }
        if(recoverPhase.isNullOrEmpty()){
            binding.forgotPassword.visibility=View.GONE
        }
        binding.forgotPassword.setOnClickListener {
            binding.inputPasswordLayout.visibility= View.GONE
            binding.recoveryLayout.visibility=View.VISIBLE
            binding.getRecoveryConfirmButton.setOnClickListener {
                if(binding.addRecoveryPhase.text.toString().isNotEmpty()){
                    if(recoverPhase == binding.addRecoveryPhase.text.toString()){
                        binding.recoveryLayout.visibility=View.GONE
                        binding.addNewPasswordLayout.visibility=View.VISIBLE
                        AddnewPassword()
                    }else{
                        Toast.makeText(applicationContext, "Not same with store data", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(applicationContext, "Enter your nickname early you add", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupEditTextListener(checklogin: String) {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No implementation needed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // No implementation needed
            }

            override fun afterTextChanged(s: Editable?) {
                if (s?.length == 1) {
                    focusNextEditText()
                }else {
                    focusPrevEditText()
                }
            }
        }
        editText1.addTextChangedListener(textWatcher)
        editText2.addTextChangedListener(textWatcher)
        editText3.addTextChangedListener(textWatcher)
        editText4.addTextChangedListener(textWatcher)
        val keyListener = View.OnKeyListener { v, keyCode, event ->
            val value1 = editText1.text.toString()
            val value2 = editText2.text.toString()
            val value3 = editText3.text.toString()
            val value4 = editText4.text.toString()
            passwordString = "$value1$value2$value3$value4"

            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {

                // Handle Enter key press here
                if (checklogin == CREATE) {
                    if (!check) {
                        if (passwordString.length < 4) {
                            vibratePhone(this@AuthenticationActivity, 1000)
                        } else {
                            check = true
                            passwordStringbefore = passwordString
                            showinfo.setText("Comfirm Password")
                            editText1.setText("")
                            editText2.setText("")
                            editText3.setText("")
                            editText4.setText("")
                            editText1.requestFocus()
                        }
                    } else {
                        if (passwordString.length < 4) {
                            vibratePhone(this@AuthenticationActivity, 1000)
                        } else {
                            if (passwordStringbefore == passwordString) {
                                binding.inputPasswordLayout.visibility = View.GONE
                                binding.addRecoveryPhaseLayout.visibility = View.VISIBLE
                                sharePref.setPassword(passwordString)
                                AddRecoveryPhase()
                            } else {
                                Toast.makeText(
                                    applicationContext,
                                    "Enter Same Password",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
                else {
                        if (passwordString == savepassword!!) {
                            Log.d("Data", "Login is Success")
                            startActivity(Intent(this, PrivateActivity::class.java))
                        } else {
                            vibratePhone(this@AuthenticationActivity, 300)

                            val shakeAnimation = ObjectAnimator.ofFloat(binding.passwordLayout, "translationX", 0f, -30f, 30f, -30f, 0f)

                            // Set the duration and interpolator for the animation
                            shakeAnimation.duration = 300 // 2 seconds
                            shakeAnimation.interpolator = LinearInterpolator()

                            // Start the animation
                            shakeAnimation.start()
                            Log.d("Data", "Login is Fail")
                        }

                }

                return@OnKeyListener true
            }
            false
        }
        editText1.setOnKeyListener(keyListener)
        editText2.setOnKeyListener(keyListener)
        editText3.setOnKeyListener(keyListener)
        editText4.setOnKeyListener(keyListener)
    }

    private fun AddnewPassword(){
       binding.confirmNewPasswordButton.setOnClickListener {
           val newpassword=binding.newPassword.text.toString()
           val confirmpassword=binding.confirmNewPassword.text.toString()
           if(newpassword.length == 4 && confirmpassword.length == 4){
               if(newpassword == confirmpassword){
                   sharePref.setPassword(newpassword)
                   startActivity(Intent(this, PrivateActivity::class.java))
                   Toast.makeText(applicationContext, "Welcome Back", Toast.LENGTH_SHORT).show()
               }else{
                   Toast.makeText(applicationContext, "Enter the same password", Toast.LENGTH_SHORT).show()
               }
           }else{
               Toast.makeText(applicationContext, "Enter new four digit password", Toast.LENGTH_SHORT).show()
           }
       }
    }
    private fun AddRecoveryPhase() {
        binding.confirmButton.setOnClickListener {
            if (binding.recoveryPhase.text.toString().isNotEmpty()) {
                sharePref.setRecoverPhase(binding.recoveryPhase.text.toString())
                startActivity(Intent(this, PrivateActivity::class.java))
                Toast.makeText(applicationContext, "Success", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(applicationContext, "Enter YOur nickname", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun focusNextEditText() {
        when (currentFocus) {
            editText1 -> editText2.requestFocus()
            editText2 -> editText3.requestFocus()
            editText3 -> editText4.requestFocus()
            else -> {
                // All EditText fields have been filled, perform desired action
            }
        }
    }
    private fun focusPrevEditText() {
        when (currentFocus) {
            editText4 -> editText3.requestFocus()
            editText3 -> editText2.requestFocus()
            editText2 -> editText1.requestFocus()
            else -> {
                // All EditText fields have been filled, perform desired action
            }
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}

