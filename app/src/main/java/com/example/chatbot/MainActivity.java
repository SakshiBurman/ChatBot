package com.example.chatbot;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
     
    private RecyclerView recyclerView;
    private EditText usermagTxt;
    private FloatingActionButton sendmsgFAB;
    private final String BOT_KEY = "bot";
    private final String USER_KEY = "user";
    private ArrayList<chatsModel> chatsModelArrayList;
    private ChatRVAdapter chatRVAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.rv);
        usermagTxt = findViewById(R.id.Edit);
        sendmsgFAB = findViewById(R.id.fb);
        chatsModelArrayList = new ArrayList<>();
        chatRVAdapter = new ChatRVAdapter(chatsModelArrayList,this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(chatRVAdapter);

        sendmsgFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(usermagTxt.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this,"Please enter your message ",Toast.LENGTH_SHORT).show();
                    return;
                }
                getresponse(usermagTxt.getText().toString());
                usermagTxt.setText("");
            }
        });
    }
    private void getresponse(String message){
        chatsModelArrayList.add(new chatsModel(message,USER_KEY));
        chatRVAdapter.notifyDataSetChanged();
        String url = "http://api.brainshop.ai/get?bid=170878&key=9IoRxJs90c1s7LIo&uid=[uid]&msg="+message;
        String BASE_URL = "http://api.brainshop.ai/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitApi retrofitAPI =retrofit.create(RetrofitApi.class);
        Call<msgModel> call = retrofitAPI.getmessage(url);
        call.enqueue(new Callback<msgModel>(){
            @Override
            public void onResponse(Call<msgModel> call, Response<msgModel> response) {
                if (response.isSuccessful()) {
                    msgModel model = response.body();
                    chatsModelArrayList.add(new chatsModel(model.getCnt(), BOT_KEY));
                    chatRVAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<msgModel> call,Throwable t){
                  chatsModelArrayList.add(new chatsModel("Please revert your message",BOT_KEY));
                  chatRVAdapter.notifyDataSetChanged();
            }
        });
        }
    }
