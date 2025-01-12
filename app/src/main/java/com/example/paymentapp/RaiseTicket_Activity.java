package com.example.paymentapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class RaiseTicket_Activity extends AppCompatActivity {

    private static final String TAG = "arsh";
    private EditText messageEditText;
    private Button raiseTicketButton;
    private RecyclerView recyclerView;
    private List<JSONObject> chatMessages = new ArrayList<>();
    private ChatAdapter chatAdapter;

    private ImageView back_button;
    private static final String SEND_MESSAGE_URL = "https://gk4rbn12-3000.inc1.devtunnels.ms/api/auth/send-message";
    private static final String GET_CHAT_URL = "https://gk4rbn12-3000.inc1.devtunnels.ms/api/auth/get-user-admin-chat";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_raise_ticket);
        Log.d(TAG, "onCreate: Activity started");

        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.startColor));
        back_button=findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        messageEditText = findViewById(R.id.messageby);
        raiseTicketButton = findViewById(R.id.Raiseticketbtn);
        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatAdapter = new ChatAdapter();
        recyclerView.setAdapter(chatAdapter);


        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String memberId = sharedPreferences.getString("memberId", "UP000000");
        String username = sharedPreferences.getString("username", "Hello, !");

        Log.d(TAG, "onCreate: Loaded memberId = " + memberId);

        TextView memberName = findViewById(R.id.memberName);
        TextView userId = findViewById(R.id.memberId);
        memberName.setText(username);
        userId.setText(memberId);

        String ticketId = generateTicketId(memberId);
        Log.d(TAG, "onCreate: Generated ticketId = " + ticketId);

        fetchChatMessages(memberId, ticketId);

        raiseTicketButton.setOnClickListener(v -> {
            String message = messageEditText.getText().toString();
            Log.d(TAG, "raiseTicketButton: Sending message: " + message);
            sendMessageAndFetchChat(memberId, ticketId, message);
        });
    }

    private String generateTicketId(String memberId) {
        String ticketId = "TX" + memberId.substring(2);
        Log.d(TAG, "generateTicketId: " + ticketId);
        return ticketId;
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
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(TAG, "sendMessageAndFetchChat: Message sent successfully");
                            Toast.makeText(RaiseTicket_Activity.this, "Message sent", Toast.LENGTH_SHORT).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG, "sendMessageAndFetchChat: Failed to send message", error);
                            Toast.makeText(RaiseTicket_Activity.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                        }
                    }
            );

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
            Log.d(TAG, "fetchChatMessages: Request body = " + requestBody.toString());

            JsonArrayRequest fetchRequest = new JsonArrayRequest(
                    Request.Method.POST,
                    GET_CHAT_URL,
                    requestBody.names(),
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            Log.d(TAG, "fetchChatMessages: Chat messages fetched successfully");
                            chatMessages.clear();
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    chatMessages.add(response.getJSONObject(i));
                                } catch (JSONException e) {
                                    Log.e(TAG, "fetchChatMessages: JSON exception during parsing", e);
                                }
                            }
                            chatAdapter.notifyDataSetChanged();
                            Log.d(TAG, "fetchChatMessages: Updated chat messages in adapter");
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG, "fetchChatMessages: Failed to fetch messages", error);
                            Toast.makeText(RaiseTicket_Activity.this, "Failed to fetch messages", Toast.LENGTH_SHORT).show();
                        }
                    }
            ) {
                @Override
                public byte[] getBody() {
                    return requestBody.toString().getBytes();
                }

                @Override
                public String getBodyContentType() {
                    return "application/json";
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(fetchRequest);

        } catch (JSONException e) {
            Log.e(TAG, "fetchChatMessages: JSON exception", e);
        }
    }

    private class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

        @Override
        public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
            return new ChatViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ChatViewHolder holder, int position) {
            try {
                JSONObject chatMessage = chatMessages.get(position);
                holder.messageTextView.setText(chatMessage.getString("message"));
                holder.messageByTextView.setText(chatMessage.getString("message_by"));
                holder.createdAtTextView.setText(formatDate(chatMessage.getString("created_at")));
                holder.timeTextView.setText(formatTime(chatMessage.getString("created_at")));

                Log.d(TAG, "onBindViewHolder: Message displayed at position " + position);
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

            public ChatViewHolder(View itemView) {
                super(itemView);

                messageTextView = itemView.findViewById(R.id.message);
                messageByTextView = itemView.findViewById(R.id.messageby);
                createdAtTextView = itemView.findViewById(R.id.dateTimeText);
                timeTextView = itemView.findViewById(R.id.timeText);
                Log.d(TAG, "ChatViewHolder: ViewHolder initialized");
            }
        }
    }
}
