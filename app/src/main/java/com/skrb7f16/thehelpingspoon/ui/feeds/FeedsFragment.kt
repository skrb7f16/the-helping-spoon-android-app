package com.skrb7f16.thehelpingspoon.ui.feeds

import android.app.ProgressDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.skrb7f16.thehelpingspoon.R
import com.skrb7f16.thehelpingspoon.adapters.FeedsAdapter
import com.skrb7f16.thehelpingspoon.databinding.FragmentFeedsBinding
import org.json.JSONArray
import org.json.JSONObject

class FeedsFragment : Fragment() {

    lateinit var binding: FragmentFeedsBinding;
    lateinit var sharedPreferences:SharedPreferences;
    lateinit var editor:SharedPreferences.Editor;
    lateinit var baseUrl:String
    private lateinit var progressDialog: ProgressDialog;
    private lateinit var adapter:FeedsAdapter;
    private lateinit var feeds:MutableList<FeedsModel>;
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_feeds, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding= FragmentFeedsBinding.bind(view);
        feeds= mutableListOf<FeedsModel>();
        adapter= FeedsAdapter(feeds)
        binding.feedsRecycler.adapter=adapter;
        val linearLayoutManager=LinearLayoutManager(activity)
        binding.feedsRecycler.layoutManager=linearLayoutManager
        sharedPreferences= activity?.getPreferences(Context.MODE_PRIVATE)!!;
        baseUrl= sharedPreferences.getString("baseUrl","").toString();
        editor=sharedPreferences.edit();
        progressDialog= ProgressDialog(activity);
        progressDialog.setTitle("Loading")
        progressDialog.setMessage("Please wait")
        progressDialog.show()
        getFeeds()
    }

    private fun getFeeds(){

        val requestQueue = Volley.newRequestQueue(activity);
        val jsonArrayRequest=JsonArrayRequest(Request.Method.GET, baseUrl + "api/allFeeds", null, { response ->
            if (response.length() == 0) {
                Toast.makeText(activity, "Sorry don't have any posts ", Toast.LENGTH_SHORT).show()
            } else {
                for (i in 0 until response.length()) {
                    val jsonObject: JSONObject = response.getJSONObject(i);
                    Log.d("meow", "getFeeds: $jsonObject")
                    val feedsModel: FeedsModel = FeedsModel(jsonObject)
                    feeds.add(feedsModel)
                }
                progressDialog.hide()
                adapter.notifyDataSetChanged()

            }
        }, { error ->
            progressDialog.hide()
            Log.d("meow", "getFeeds: $error")
        })
        requestQueue.add(jsonArrayRequest)
    }




}