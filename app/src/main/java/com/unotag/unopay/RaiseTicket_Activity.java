package com.unotag.unopay;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class RaiseTicket_Activity extends BaseActivity {

    private static final String TAG = "arsh";
    private EditText messageEditText;
    private ImageButton raiseTicketButton;
    private RecyclerView recyclerView;
    private List<JSONObject> chatMessages = new ArrayList<>();
    private ChatAdapter chatAdapter;
    private ImageView back_button;
    private static final String SEND_MESSAGE_URL = BuildConfig.api_url_non_auth+"raiseTicket/send-message";
    private static final String GET_CHAT_URL = BuildConfig.api_url_non_auth+"raiseTicket/get-user-admin-chat";
    private  SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_raise_ticket);

        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.endColor));
        back_button=findViewById(R.id.back_button);
        back_button.setOnClickListener(v -> finish());
        messageEditText = findViewById(R.id.messageby);
        raiseTicketButton = findViewById(R.id.Raiseticketbtn);
        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatAdapter = new ChatAdapter();
        recyclerView.setAdapter(chatAdapter);

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String memberId = sharedPreferences.getString("memberId", "UP000000");
        String username = sharedPreferences.getString("username", "Hello, !");

        TextView memberName = findViewById(R.id.memberName);
        TextView userId = findViewById(R.id.memberId);
        memberName.setText(username);
        userId.setText(memberId);

        String ticketId = generateTicketId(memberId);

        fetchChatMessages(memberId, ticketId);


        LottieAnimationView shareAnimation = findViewById(R.id.shareAnimation);
        shareAnimation.setOnClickListener(v->ShareUtil.shareApp(this, memberId));


        raiseTicketButton.setOnClickListener(v -> {
            String message = messageEditText.getText().toString();
            sendMessageAndFetchChat(memberId, ticketId, message);
        });
    }

    private String generateTicketId(String memberId) {
        return "TX" + memberId.substring(2);
    }

    private void sendMessageAndFetchChat(final String memberId, final String ticketId, String message) {
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("member_id", memberId);
            requestBody.put("ticket_id", ticketId);
            requestBody.put("message_by", "USER");
            requestBody.put("message", message);
            Log.d(TAG, "sendMessageAndFetchChat: Request body = " + requestBody.toString());

            JsonObjectRequest sendRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    SEND_MESSAGE_URL,
                    requestBody,
                    response -> {
                        messageEditText.setText("");
                        fetchChatMessages(memberId, ticketId);
                    },
                    error -> {
                        Toast.makeText(RaiseTicket_Activity.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                    }
            ){
                @Override
                public Map<String, String> getHeaders() {
                    String token = sharedPreferences.getString("token","");
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Accept", "application/json");
                    headers.put("Content-Type", "application/json");
                    headers.put("Authorization", "Bearer " + token);
                    return headers;
                }
            };
            sendRequest.setRetryPolicy(new DefaultRetryPolicy(
                    60000,
                    0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(sendRequest);

        } catch (JSONException e) {
            Log.e(TAG, "sendMessageAndFetchChat: JSON exception", e);
        }
    }


    private void fetchChatMessages(String memberId, String ticketId) {
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("member_id", memberId);
            requestBody.put("ticket_id", ticketId);

            @SuppressLint("NotifyDataSetChanged") JsonObjectRequest fetchRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    GET_CHAT_URL,
                    requestBody,
                    response -> {
                        try {
                            if (response.has("data")) {
                                chatMessages.clear();
                                JSONArray messagesArray = response.getJSONArray("data"); // Adjust key as per API response

                                for (int i = 0; i < messagesArray.length(); i++) {
                                    chatMessages.add(messagesArray.getJSONObject(i));
                                }

                                chatAdapter.notifyDataSetChanged();
                                recyclerView.post(() -> recyclerView.scrollToPosition(chatMessages.size() - 1));
                                Log.d(TAG, "fetchChatMessages: Updated chat messages in adapter");
                            } else {
                                Log.e(TAG, "fetchChatMessages: API returned failure response");
                                Toast.makeText(RaiseTicket_Activity.this, "No messages found", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "fetchChatMessages: JSON parsing error", e);
                        }
                    },
                    error -> {
                        Log.e(TAG, "fetchChatMessages: Failed to fetch messages", error);
                        Toast.makeText(RaiseTicket_Activity.this, "Failed to fetch messages", Toast.LENGTH_SHORT).show();
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    String token = sharedPreferences.getString("token", "");
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Accept", "application/json");
                    headers.put("Content-Type", "application/json");
                    headers.put("Authorization", "Bearer " + token);
                    return headers;
                }
            };

            fetchRequest.setRetryPolicy(new DefaultRetryPolicy(
                    60000,
                    0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(fetchRequest);

        } catch (JSONException e) {
            Log.e(TAG, "fetchChatMessages: JSON exception", e);
        }
    }


    private class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

        @NonNull
        @Override
        public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
            return new ChatViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ChatViewHolder holder, int position) {
            try {
                JSONObject chatMessage = chatMessages.get(position);
                Log.d("yehmsg",chatMessage.toString());

                holder.messageTextView.setText(chatMessage.getString("message"));
                holder.messageByTextView.setText(chatMessage.getString("message_by"));
                holder.createdAtTextView.setText(formatDate(chatMessage.getString("created_at")));
                holder.timeTextView.setText(formatTime(chatMessage.getString("created_at")));

                LinearLayout chat_layout = holder.itemView.findViewById(R.id.chat_layout); // LinearLayout containing the CardView

                if (chatMessage.getString("message_by").equals("admin")) {
                    chat_layout.setGravity(Gravity.START);
                    holder.cardView.setBackgroundColor(ContextCompat.getColor(RaiseTicket_Activity.this, R.color.msg_grey));
                } else {
                    chat_layout.setGravity(Gravity.END);
                    holder.cardView.setBackgroundColor(ContextCompat.getColor(RaiseTicket_Activity.this, R.color.endColor));
                }
            } catch (JSONException e) {
                Log.e(TAG, "onBindViewHolder: JSON exception", e);
            }
        }
        public String formatTime(String utcDateString) {
            String inputFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS";
            String outputFormat = "hh:mm a";
            SimpleDateFormat inputDateFormat = new SimpleDateFormat(inputFormat, Locale.getDefault());
            SimpleDateFormat outputDateFormat = new SimpleDateFormat(outputFormat, Locale.getDefault());

            inputDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            outputDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));

            try {
                Date date = inputDateFormat.parse(utcDateString);
                if (date != null) {

                    return outputDateFormat.format(date);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return utcDateString;
        }
        public String formatDate(String dateString) {
            String inputFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
            String outputFormat = "dd MMM yyyy";
            SimpleDateFormat inputDateFormat = new SimpleDateFormat(inputFormat, Locale.getDefault());
            SimpleDateFormat outputDateFormat = new SimpleDateFormat(outputFormat, Locale.getDefault());

            // Input is in UTC
            inputDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            // Output should be in IST
            outputDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));

            try {
                Date date = inputDateFormat.parse(dateString);
                return outputDateFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
                return dateString;
            }
        }

        @Override
        public int getItemCount() {
            int count = chatMessages.size();
            Log.d(TAG, "getItemCount: Total messages count = " + count);
            return count;
        }

        public class ChatViewHolder extends RecyclerView.ViewHolder {
            TextView messageTextView, messageByTextView, createdAtTextView, timeTextView;
            LinearLayout chat_layout,cardView;

            public ChatViewHolder(View itemView) {
                super(itemView);
                cardView = itemView.findViewById(R.id.cardview);
                chat_layout = itemView.findViewById(R.id.chat_layout);
                messageTextView = itemView.findViewById(R.id.message);
                messageByTextView = itemView.findViewById(R.id.messageby);
                createdAtTextView = itemView.findViewById(R.id.dateTimeText);
                timeTextView = itemView.findViewById(R.id.timeText);
                Log.d(TAG, "ChatViewHolder: ViewHolder initialized");
            }
        }
    }
}