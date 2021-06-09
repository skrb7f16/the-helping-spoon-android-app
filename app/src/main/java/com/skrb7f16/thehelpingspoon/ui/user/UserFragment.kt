package com.skrb7f16.thehelpingspoon.ui.user

import android.app.Activity
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
import android.widget.Toast
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.navigation.NavigationView
import com.skrb7f16.thehelpingspoon.MainActivity
import com.skrb7f16.thehelpingspoon.R
import com.skrb7f16.thehelpingspoon.adapters.FeedsAdapter
import com.skrb7f16.thehelpingspoon.databinding.FragmentUserBinding
import com.skrb7f16.thehelpingspoon.ui.feeds.FeedsModel
import com.skrb7f16.thehelpingspoon.ui.home.HomeFragment
import org.json.JSONObject
import java.io.IOException
import kotlin.properties.Delegates


class UserFragment : Fragment() {

    lateinit var binding:FragmentUserBinding;
    lateinit var sharedPreferences: SharedPreferences;
    lateinit var editor: SharedPreferences.Editor;
    lateinit var baseUrl:String
    lateinit var token:String
    lateinit var username:String;
    private lateinit var progressDialog: ProgressDialog;
    private lateinit var adapter: FeedsAdapter;
    private lateinit var feeds:MutableList<FeedsModel>;
    private var imageData: ByteArray ?=null;
    private var showPost by Delegates.notNull<Boolean>();


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {


        return inflater.inflate(R.layout.fragment_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showPost=true;
        binding= FragmentUserBinding.bind(view)
        feeds= mutableListOf<FeedsModel>();
        adapter= FeedsAdapter(feeds)
        binding.UserRecyclerView.adapter=adapter;
        val linearLayoutManager= LinearLayoutManager(activity)
        binding.UserRecyclerView.layoutManager=linearLayoutManager
        sharedPreferences= activity?.getPreferences(Context.MODE_PRIVATE)!!;
        baseUrl= sharedPreferences.getString("baseUrl", "").toString();
        token= sharedPreferences.getString("TOKEN", "").toString();
        username= sharedPreferences.getString("username", "").toString();
        editor=sharedPreferences.edit();
        if(token.isEmpty() || username.isEmpty()){
            binding.UserRecyclerView.visibility=View.GONE
            binding.feedForm.visibility=View.GONE
            binding.gotohome.visibility=View.VISIBLE
            binding.logout.visibility=View.GONE
            binding.postBtn.visibility=View.GONE
            binding.wantToPost.visibility=View.GONE
        }
        else{
            binding.userUsername.text=username
            binding.logout.visibility=View.VISIBLE
            binding.postBtn.visibility=View.VISIBLE
            binding.UserRecyclerView.visibility=View.VISIBLE
            binding.feedForm.visibility=View.VISIBLE
            binding.gotohome.visibility=View.GONE
            binding.wantToPost.visibility=View.VISIBLE
            progressDialog= ProgressDialog(activity);
            progressDialog.setTitle("Loading")
            progressDialog.setMessage("Please wait")
            progressDialog.show()
            getFeeds()
        }
        binding.gotohome.setOnClickListener{
            goToHome()
        }

        binding.uploadImage.setOnClickListener{
            launchGallery()
        }

        binding.postBtn.setOnClickListener{
            progressDialog.setTitle("Loading")
            progressDialog.setMessage("Posting....")
            progressDialog.show()
            post()

        }

        binding.wantToPost.setOnClickListener{
            showPost=!showPost;
            if(!showPost){
                binding.UserRecyclerView.visibility=View.GONE
            }
            else{
                binding.UserRecyclerView.visibility=View.VISIBLE
            }
        }
        binding.logout.setOnClickListener{
            editor.remove("username")
            editor.apply()
            editor.remove("TOKEN")
            editor.apply()
            goToHome()
        }
    }

    private fun goToHome(){
        val homeFragment=HomeFragment()
        val navView: NavigationView = activity?.findViewById(R.id.nav_view)!! ;
        if(navView!=null){
            navView.menu[0].isChecked=true
        }
        (requireActivity() as MainActivity).show(homeFragment);

    }


    private fun getFeeds(){
        val requestQueue = Volley.newRequestQueue(activity);
        val jsonArrayRequest=object : JsonArrayRequest(Method.GET, baseUrl + "api/feeds", null, Response.Listener {


            if (it.length() == 0) {
                Toast.makeText(activity, "Sorry don't have any posts ", Toast.LENGTH_SHORT).show()
            } else {
                for (i in 0 until it.length()) {
                    val jsonObject: JSONObject = it.getJSONObject(i);
                    val feedsModel: FeedsModel = FeedsModel(jsonObject)
                    feeds.add(feedsModel)
                }
                progressDialog.hide()
                adapter.notifyDataSetChanged()

            }


        }, Response.ErrorListener {
            progressDialog.hide();
            Log.d("meow", "getFeeds: ${it.javaClass}")
        }){
            override fun getHeaders(): MutableMap<String, String> {
                val params=HashMap<String, String>();
                params["Authorization"]="token $token";
                return params;
            }

        }

        requestQueue.add(jsonArrayRequest)
    }




    private  fun post(){

        if(
                binding.postTitle.text.isEmpty() ||
                binding.postAddress.text.isEmpty() ||
                binding.postPhone.text.isEmpty() ||
                binding.postCity.text.isEmpty() ||
                binding.postDesc.text.isEmpty()||
                        imageData==null
                ){
                    Toast.makeText(context, "Fields could not be empty sorry!!!", Toast.LENGTH_SHORT).show();
                return;
        }


        val request = object : VolleyMulitpartRequest(
                Method.POST,
                baseUrl + "api/feeds",
                Response.Listener {
                    binding.postTitle.setText("")
                    binding.postAddress.setText("")
                    binding.postPhone.setText("")
                    binding.postCity.setText("")
                    binding.postDesc.setText("")
                    imageData = null;
                    binding.postImage.setImageURI(null)
                    binding.postImage.visibility = View.GONE
                    getFeeds()
                },
                Response.ErrorListener {
                    progressDialog.hide()
                    Toast.makeText(context, "$it", Toast.LENGTH_SHORT).show()
                }
        ) {
            override fun getByteData(): MutableMap<String, FileDataPart> {
                var params = HashMap<String, FileDataPart>()
                params["imageFile"] = FileDataPart("image", imageData!!, "jpeg")
                return params
            }

            override fun getParams(): MutableMap<String, String> {
                var params =HashMap<String, String>()
                params["city"]= binding.postCity.text.toString()
                params["title"]= binding.postTitle.text.toString()
                params["phone"]= binding.postPhone.text.toString()
                params["desc"]= binding.postDesc.text.toString()
                params["address"]= binding.postAddress.text.toString()
                return params
            }

            override fun getHeaders(): MutableMap<String, String> {
                val params=HashMap<String, String>();
                params["Authorization"]="token $token";
                return params;
            }
        }
        Volley.newRequestQueue(context).add(request)
    }


    @Throws(IOException::class)
    private fun createImageData(uri: Uri) {
        val inputStream = context?.contentResolver?.openInputStream(uri)
        inputStream?.buffered()?.use {
            imageData = it.readBytes()
        }
    }

    private fun launchGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 999)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == 999) {
            val uri = data?.data
            if (uri != null) {
                binding.postImage.setImageURI(uri)
                binding.postImage.visibility=View.VISIBLE;
                createImageData(uri)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }



}