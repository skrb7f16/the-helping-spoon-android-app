package com.skrb7f16.thehelpingspoon.ui.home



import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.JsonRequest
import com.android.volley.toolbox.Volley
import com.skrb7f16.thehelpingspoon.R
import com.skrb7f16.thehelpingspoon.databinding.FragmentHomeBinding
import com.skrb7f16.thehelpingspoon.models.User
import org.json.JSONObject


class HomeFragment : Fragment() {


    private  var fragmentHomeBinding:FragmentHomeBinding?=null
    private lateinit var baseUrl:String;
    private lateinit var sharedPreferences: SharedPreferences;
    private lateinit var editor: SharedPreferences.Editor;
    private lateinit var progressDialog: ProgressDialog;
    private lateinit var user: User;
    private lateinit var TOKEN:String;

   override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences= activity?.getPreferences(Context.MODE_PRIVATE)!!;
        baseUrl= sharedPreferences.getString("baseUrl","").toString();
        editor=sharedPreferences.edit();
        TOKEN= sharedPreferences.getString("TOKEN","").toString()
        var binding=FragmentHomeBinding.bind(view)
        fragmentHomeBinding=binding;
        progressDialog= ProgressDialog(activity)
        progressDialog.setTitle("Please Wait!!!!");
        if(TOKEN.isNotEmpty()){
            showLoggedInUser()
        }
        binding.loginButton.setOnClickListener{

            expand(binding.loginBox)
            collapse(binding.signupBox)
            binding.mainLayoutHome.scrollTo(0, View.FOCUS_DOWN)
        }
        binding.signUpbutton.setOnClickListener{
            expand(binding.signupBox)
            collapse(binding.loginBox)

        }
        binding.instabtn.setOnClickListener{
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com/skrb7f16")));
        }
        binding.webbtn.setOnClickListener{
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(baseUrl)));

        }

        binding.fbbtn.setOnClickListener{
            startActivity(Intent(Intent.ACTION_VIEW,Uri.parse("https://www.facebook.com/profile.php?id=100007170302343")))
        }
        binding.whatsappbtn.setOnClickListener{
            startActivity(Intent(Intent.ACTION_VIEW,Uri.parse("api.whatsapp.com/send?phone=+917259709535&text=hi")))
        }

        binding.signin.setOnClickListener{
            progressDialog.setMessage("Logging you in");
            progressDialog.show()
            login(binding.usernameSignin.text.toString(),binding.passwordSignin.text.toString());
        }
        binding.signup.setOnClickListener{
            progressDialog.setMessage("Logging you in");
            progressDialog.show()
            signup(binding.usernameSignup.text.toString(),
                    binding.passwordSignup.text.toString(),
                    binding.confirmPassword.text.toString(),
                    binding.emailSignup.text.toString(),
                    binding.citySignup.text.toString(),
                    if (binding.organizationSignup.isChecked) "Yes" else "No");
        }

    }

    override fun onDestroyView() {
        fragmentHomeBinding=null
        super.onDestroyView()


    }

    private  fun  expand(view: View){
        val matchParentMeasureSpec = View.MeasureSpec.makeMeasureSpec((view.parent as View).width, View.MeasureSpec.EXACTLY)
        val wrapContentMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        view.measure(matchParentMeasureSpec, wrapContentMeasureSpec)
        val targetHeight = view.measuredHeight;
        view.layoutParams.height = 1
        view.visibility = View.VISIBLE
        val a: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                view.layoutParams.height = if (interpolatedTime == 1f) LinearLayout.LayoutParams.WRAP_CONTENT else (targetHeight * interpolatedTime).toInt()
                view.requestLayout()
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }


        a.duration = 3*(targetHeight / view.context.resources.displayMetrics.density).toLong()
        view.startAnimation(a)
    }

    private fun collapse(view: View){
        val initialHeight = view.measuredHeight;

        val a: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                if (interpolatedTime == 1f) {
                    view.visibility = View.GONE
                    fragmentHomeBinding?.homeScroll?.post(Runnable { fragmentHomeBinding?.homeScroll?.fullScroll(ScrollView.FOCUS_DOWN) })
                } else {
                    view.layoutParams.height = (initialHeight - (initialHeight * interpolatedTime)).toInt()
                    view.requestLayout()
                }
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }


        a.duration = 3*(initialHeight / view.context.resources.displayMetrics.density).toLong()
        view.startAnimation(a)
    }

    private fun login(username:String, password:String) {
        if (username.length < 2 || password.length < 2) {
            Toast.makeText(activity, "very short username and password", Toast.LENGTH_SHORT).show();
            return
        }
        val body: JSONObject = JSONObject();
        body.put("username", username);
        body.put("password", password);
        val requestBody = body.toString();
        val requestQueue = Volley.newRequestQueue(activity);
        val jsonObjectRequest=JsonObjectRequest(Request.Method.POST, baseUrl + "api/auth-token/", body, { response ->
            Log.d("meow", "login: $response")
            try {
                editor.putString("TOKEN", response.getString("token"))
                editor.apply()
                fragmentHomeBinding?.usernameSignin?.setText("");
                fragmentHomeBinding?.passwordSignin?.setText("");
                editor.putString("username", username)
                editor.apply()
                showLoggedInUser()

            } catch (e: Exception) {
                Log.d("meow", "login: $e");
            }
            progressDialog.hide()
        }, { error ->
            Log.d("meow", "login: ${error.networkResponse}")
        })
        requestQueue.add(jsonObjectRequest)

    }

    private fun signup(username: String,password: String,confimrPassword:String,email:String,city:String,organization:String){
            if(username.length<2 || password.length<6||confimrPassword.length<6||email.length<2){
                Toast.makeText(activity, "Very Short Credentials", Toast.LENGTH_SHORT).show();
                return
            }
            if(!password.equals(confimrPassword)){
                Toast.makeText(activity, "password and confirm password should match", Toast.LENGTH_SHORT).show();
                return
            }
        val body: JSONObject = JSONObject();
        body.put("username", username);
        body.put("password", password);
        body.put("email",email);
        body.put("organization",organization);
        body.put("city",city);
        val requestQueue = Volley.newRequestQueue(activity);
        val jsonObjectRequest=JsonObjectRequest(Request.Method.POST, baseUrl + "api/register", body, { response ->
            try {

                Toast.makeText(activity,response.getString("msg"),Toast.LENGTH_SHORT).show();
                fragmentHomeBinding?.usernameSignup?.setText("");
                fragmentHomeBinding?.passwordSignup?.setText("");
                fragmentHomeBinding?.confirmPassword?.setText("");
                fragmentHomeBinding?.citySignup?.setText("");
                fragmentHomeBinding?.emailSignup?.setText("");
                fragmentHomeBinding?.organizationSignup?.isChecked = false;
                user=User(username,password,email,organization,city);
                editor.putString("username",username)
                editor.apply()
                login(username, password)

            } catch (e: Exception) {
                progressDialog.hide()
                Log.d("meow", "login: "+e.localizedMessage);
            }

        }, { error ->
            Log.d("meow", "login: ${error.networkResponse}")
        })
        requestQueue.add(jsonObjectRequest)
    }



    private fun showLoggedInUser(){
        fragmentHomeBinding?.loggedInUsernameName?.text   = "Welcome "+sharedPreferences.getString("username","").toString()+ ""
        fragmentHomeBinding?.loggedIn?.layoutParams  =LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT)
        fragmentHomeBinding?.Userlayout?.layoutParams  =LinearLayout.LayoutParams(0,0)
    }
    fun hideLoggedInUser(){
        fragmentHomeBinding?.loggedInUsernameName?.text   = ""
        fragmentHomeBinding?.Userlayout?.layoutParams  =LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT)
        fragmentHomeBinding?.loggedIn?.layoutParams  =LinearLayout.LayoutParams(0,0)
    }

}