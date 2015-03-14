package emirates.later;

import android.app.AlertDialog;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.AlarmClock;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;


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

    SharedPreferences settings;
    private String mName = "";
    private String mFlightNum = "";
    private JSONObject mOffer = null;

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://192.168.1.67:3000");
        } catch (URISyntaxException e) {}
    }

    private Emitter.Listener onConnectionStarted = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    print("Connection Started.");
                    JSONObject mUser = new JSONObject();
                    try {
                        mUser.put("name", mName);
                        mUser.put("flightNum", mFlightNum);
                    } catch (Exception e) {
                        print("Something went wrong.\n Please restart the app.");
                    }
                    mSocket.emit("CONNECTION_CONFIRMED", mUser);
                }
            });
        }
    };

    private Emitter.Listener onNoFlightNum = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    print("Please enter your flight number.");
                    setFlightNum();
                }
            });
        }
    };

    private Emitter.Listener onSetAlarm = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject time = (JSONObject) args[0];
                    try {
                        int hour = time.getInt("hour");
                        int min = time.getInt("minute");
                        print("Setting alarm for: " + hour + "" + min);
                        setAlarm(hour, min, "Boarding gates for " + mFlightNum + " closing in 15 minutes.");
                    } catch (Exception e) {
                        print("Something went wrong in setting alarm.");
                    }

                }
            });
        }
    };



    private Emitter.Listener onSendOffer = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mOffer = (JSONObject) args[0];
                    try {
                        print(mOffer.getString("title"));
                    } catch (Exception e) {
                        print("error seeing title");
                    }

                    print("Receving Offer...");
//                    showOffer(mOffer);
                }
            });
        }
    };

    private Emitter.Listener onOfferVoucher = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    print("Your offer voucher is ...");
                }
            });
        }
    };

    private Emitter.Listener onRejectConfirmed = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    print("You have rejected the offer.\nHope to get better ones. :)");
                }
            });
        }
    };

    private void sendReport() {
        String[] beaconIDs = getBeaconIDs();
        mSocket.emit("REPORT", beaconIDs);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        settings = getPreferences(MODE_PRIVATE);
        mName = settings.getString("mName", mName);
        mFlightNum = settings.getString("mFlightNum", mFlightNum);

        mSocket.on("CONNECTION_STARTED", onConnectionStarted);
        mSocket.on("NO_FLIGHT_NUM", onNoFlightNum);
        mSocket.on("SET_ALARM", onSetAlarm);
        mSocket.on("SEND_OFFER", onSendOffer);
        mSocket.on("OFFER_VOUCHER", onOfferVoucher);
        mSocket.on("REJECT_CONFIRMED", onRejectConfirmed);
        mSocket.connect();

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                mSocket.emit("REPORT", "hi");
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

        switch (id) {
            case R.id.set_name:
                setName();
                return true;
            case R.id.set_flight_num:
                setFlightNum();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void onPause() {
        super.onPause();

        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("mName", mName);
        editor.putString("mFlightNum", mFlightNum);

        // Commit the edits!
        editor.commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mSocket.disconnect();
    }

    private String[] getBeaconIDs() {
        return null;
    }

    private void showOffer(JSONObject offer) {
        TextView offerDescView = (TextView) findViewById(R.id.offerDesc);
        TextView offerTitleView = (TextView) findViewById(R.id.offerTitle);
        ImageView offerImageView = (ImageView) findViewById(R.id.offerImage);
        String offerTitle = "";
        String offerDesc = "";
        String offerImage = "";
        try {
            offerTitle = offer.getString("title");
            offerDesc = offer.getString("description");
            offerImage = offer.getString("image");
        } catch (Exception e) {
            print("something went wrong in showing offer.");
        }

        offerTitleView.setText(offerTitle);
        offerDescView.setText(offerDesc);
        byte[] decodedString = Base64.decode(offerImage, Base64.DEFAULT);
        Bitmap offerImageDecoded = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        offerImageView.setImageBitmap(offerImageDecoded);

        setContentView(R.layout.activity_offer);

        Button acceptButton = (Button) findViewById(R.id.button);
        acceptButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                offerReply(true);
            }
        });
        Button declineButton = (Button) findViewById(R.id.button);
        declineButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                offerReply(false);
            }
        });

    }

    private void offerReply(boolean response) {
        JSONObject offerResponse = new JSONObject();

        try {
            offerResponse.put("hasAccepted", response);
            offerResponse.put("id", mOffer.get("id"));
            mSocket.emit("OFFER_DECISION", offerResponse);
            setContentView(R.layout.activity_main);
        } catch (Exception e) {
            print("Something went wrong with showing the offer.");
        }

    }

    private void setName() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Enter Name:");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setText(mName);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                mName = input.getText().toString();
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
    }

    private void setFlightNum() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Enter Flight Number:");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(mFlightNum);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                mFlightNum = input.getText().toString();
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
    }

    private void setAlarm(int hour, int minute, String message) {
        Intent i = new Intent(AlarmClock.ACTION_SET_ALARM);
        i.putExtra(AlarmClock.EXTRA_MESSAGE, message);
        i.putExtra(AlarmClock.EXTRA_HOUR, hour);
        i.putExtra(AlarmClock.EXTRA_MINUTES, minute);
        startActivity(i);
        finish();
    }

    private void print(String data) {
        Toast.makeText(getApplicationContext(), data,
                Toast.LENGTH_SHORT).show();
    }

}
