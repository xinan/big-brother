package emirates.later;

import android.content.Context;
import android.content.Intent;
import android.provider.AlarmClock;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.github.nkzawa.*;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;


public class MainActivity extends ActionBarActivity {

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://192.168.1.67:3000");
        } catch (URISyntaxException e) {}
    }
//
    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    String message;
                    try {
                        username = data.getString("username");
                        message = data.getString("message");
                    } catch (JSONException e) {
                        return;
                    }
                    // add the message to view
                    addMessage(username, message);
                }
            });
        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        setAlarm(13, 47);



        mSocket.on("new message", onNewMessage);
        mSocket.connect();

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                mSocket.emit("foo", "hi");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mSocket.disconnect();
        mSocket.off("new message", onNewMessage);
    }

    private void addMessage(String username, String message) {
        TextView usernameView = (TextView) findViewById(R.id.username);
        TextView messageView = (TextView) findViewById(R.id.message);

        usernameView.setText(username);
        messageView.setText(message);
    }

    private void setAlarm(int hour, int minute) {
        Intent openNewAlarm = new Intent(AlarmClock.ACTION_SET_ALARM);
        openNewAlarm.putExtra(AlarmClock.EXTRA_MESSAGE, "Alarm Set By Big Brother.");
        openNewAlarm.putExtra(AlarmClock.EXTRA_HOUR, 1);
        openNewAlarm.putExtra(AlarmClock.EXTRA_MINUTES, 15);
        startActivity(openNewAlarm);
    }

}
