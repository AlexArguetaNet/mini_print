package com.example.miniprint;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // Declare class fields
    IHome mListener;
    OkHttpClient client = new OkHttpClient();
    Handler mHandler;
    final String TAG = "demoos";
    ImageView imageViewTopHeadline;
    TextView textViewTopHeadlineTitle;
    TextView textViewTopSource;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        getActivity().setTitle("Home");

        // Initializing class fields
        imageViewTopHeadline = view.findViewById(R.id.imageViewTopHeadline);
        textViewTopHeadlineTitle = view.findViewById(R.id.textViewTopHeadlineTitle);
        textViewTopSource = view.findViewById(R.id.textViewTopSource);

        // Get news headlines
        getNewsHeadlines();
        
        /*
            Thread handler that handles the UI component
            changes from the dynamic data coming from the API call
         */
        mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                
                if (msg.getData().getInt("RESPONSE_STATUS") == 1) {
                    String errMsg = "Please connect to the internet using WIFI or cellular data";
                    createErrorAlert("No Internet", errMsg);

                    view.findViewById(R.id.progressBarTopHeadline).setVisibility(View.GONE);

                    return false;
                }

                // Remove loading icon
                view.findViewById(R.id.progressBarTopHeadline).setVisibility(View.GONE);
                
                // Extract data from API response
                NewsResponse newsRsp = (NewsResponse) msg.getData().getSerializable("NEWS_RESPONSE");
                Article topArticle = newsRsp.articles.get(1);

                // Showing top headline in the top CardView
                Picasso.get().load(topArticle.urlToImage).into(imageViewTopHeadline);
                textViewTopHeadlineTitle.setText(topArticle.title);
                textViewTopSource.setText(topArticle.source.name);
                
                
                return false;
            }
        });


        // Inflate the layout for this fragment
        return view;
    }


    /**
     * Sends an API request and parses the JSON data
     * from the API response.
     *
     */
    public void getNewsHeadlines() {

        Request request = new Request.Builder()
                .url("https://newsapi.org/v2/top-headlines?country=us&category=technology&apiKey=38660587ad8b4aceb8f1695c746330ea")
                .build();
        
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                // API request failed to send
                
                Message failedMsg = new Message();
                Bundle failedBundle = new Bundle();
                
                failedBundle.putInt("RESPONSE_STATUS", 1);
                
                failedMsg.setData(failedBundle);
                mHandler.sendMessage(failedMsg);
                
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                // API request sent successfully

                // Parse JSON data
                Gson gson = new Gson();
                NewsResponse newsResponse = gson.fromJson(response.body().charStream(), NewsResponse.class);

                Message newsMsg = new Message();
                Bundle newsBdl = new Bundle();

                newsBdl.putInt("RESPONSE_STATUS", 0);
                newsBdl.putSerializable("NEWS_RESPONSE", newsResponse);

                newsMsg.setData(newsBdl);
                mHandler.sendMessage(newsMsg);

            }
        });


    }

    /**
     * Creates a new alert dialog meant to prompt an error message
     * @param title
     * @param message
     */
    public void createErrorAlert(String title, String message) {

        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        
        alert.setTitle(title)
                .setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        
                    }
                })
                .create()
                .show();
        
    }




    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof IHome) {
            mListener = (IHome) context;
        } else {
            throw new RuntimeException("MainActivity must implement the IHome interface");
        }
    }

    public interface IHome {

    }

}