package bluez.bluez;

import android.app.ActionBar;
import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

public class MenuActivity extends Activity {

    private static final String TAG = "BluetoothChat";
    private static final boolean D = true;

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 2;
    private static final int REQUEST_ENABLE_BT = 3;
    private int i = 0;
    // Layout Views
    private ListView mConversationView;
    //private EditText mOutEditText;
    //private Button mSendButton;

    // Name of the connected device
    private String mConnectedDeviceName = null;
    // Array adapter for the conversation thread
    private ArrayAdapter<String> mConversationArrayAdapter;
    // String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private BluezService mBluezService = null;
    //private static final int REQUEST_ENABLE_BT = 1;
    private Button onBtn;
    private Button offBtn;
    private Button listBtn;
    private Button findBtn;
    private TextView text;
    //private BluetoothAdapter myBluetoothAdapter;
    private Set<BluetoothDevice> pairedDevices;
    private ListView myListView;
    private BluetoothDevice[] allBluetoothDevices = new BluetoothDevice[20];
    private BluetoothDevice mmDevice ;
    private ArrayAdapter<String> BTArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // take an instance of BluetoothAdapter - Bluetooth radio
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null) {
            onBtn.setEnabled(false);
            offBtn.setEnabled(false);
            listBtn.setEnabled(false);
            findBtn.setEnabled(false);
            text.setText("Statut: non supporté");

            Toast.makeText(getApplicationContext(),"Votre appareil ne supporte pas Bluetooth",
                    Toast.LENGTH_LONG).show();
        } else {
            text = (TextView) findViewById(R.id.textView);
            onBtn = (Button)findViewById(R.id.btnTurnOn);
            onBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    on(v);
                }
            });

            offBtn = (Button)findViewById(R.id.btnTurnOff);
            offBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    off(v);
                }
            });

            listBtn = (Button)findViewById(R.id.btnPaired);
            listBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    list(v);
                }
            });

            findBtn = (Button)findViewById(R.id.btnSearch);
            findBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    find(v);
                }
            });

            myListView = (ListView)findViewById(R.id.listView);
            myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Toast.makeText(MenuActivity.this, "" + allBluetoothDevices[position].getName(), Toast.LENGTH_SHORT).show();
                    mmDevice = allBluetoothDevices[position];
                    connectDevice();

                }
            });

            // create the arrayAdapter that contains the BTDevices, and set it to the ListView
            BTArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
            myListView.setAdapter(BTArrayAdapter);
            mBluezService = new BluezService(this, mHandler);

            // Initialize the buffer for outgoing messages
            mOutStringBuffer = new StringBuffer("");
        }
    }

    public void on(View view){
        if (!mBluetoothAdapter.isEnabled()) {
            Intent turnOnIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOnIntent, REQUEST_ENABLE_BT);
            finish();
            Toast.makeText(getApplicationContext(),"Bluetooth ouvert" ,
                    Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(getApplicationContext(),"Bluetooth est déjà ouvert",
                    Toast.LENGTH_LONG).show();
        }
    }

    private final void setStatus(int resId) {
        final ActionBar actionBar = getActionBar();
        actionBar.setSubtitle(resId);
    }

    private final void setStatus(CharSequence subTitle) {
        final ActionBar actionBar = getActionBar();
        actionBar.setSubtitle(subTitle);
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    if (D)
                        Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case BluezService.STATE_CONNECTED:
                            setStatus("Connecté");
                            mConversationArrayAdapter.clear();
                            break;
                        case BluezService.STATE_CONNECTING:
                            setStatus("Connecting");
                            break;
                        case BluezService.STATE_LISTEN:
                        case BluezService.STATE_NONE:
                            setStatus("Pas connecté");
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    mConversationArrayAdapter.add("Me:  " + writeMessage);
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    mConversationArrayAdapter.add(mConnectedDeviceName + ":  "
                            + readMessage);
                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(),
                            "Connected to " + mConnectedDeviceName,
                            Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(),
                            msg.getData().getString(TOAST), Toast.LENGTH_SHORT)
                            .show();
                    break;
            }
        }
    };
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if(requestCode == REQUEST_ENABLE_BT){
            if(mBluetoothAdapter.isEnabled()) {
                text.setText("Statut: Permis");
            } else {
                text.setText("Statut: Non permis");
            }
        }
        else if(requestCode == REQUEST_CONNECT_DEVICE)
        {
            if (resultCode == Activity.RESULT_OK) {

            }

        }
    }

    public void list(View view){
        // get paired devices
        pairedDevices = mBluetoothAdapter.getBondedDevices();

        // put it's one to the adapter
        for(BluetoothDevice device : pairedDevices)
        {
            BTArrayAdapter.add(device.getName()+ "\n" + device.getAddress());
            allBluetoothDevices[i] = device;
            i++;
        }


        Toast.makeText(getApplicationContext(),"Vos appareils connus",
                Toast.LENGTH_SHORT).show();

    }

    final BroadcastReceiver bReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // add the name and the MAC address of the object to the arrayAdapter
                BTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                BTArrayAdapter.notifyDataSetChanged();
            }
        }
    };

    public void find(View view) {
        if (mBluetoothAdapter.isDiscovering()) {
            // the button is pressed when it discovers, so cancel the discovery
            mBluetoothAdapter.cancelDiscovery();
        }
        else {
            BTArrayAdapter.clear();
            mBluetoothAdapter.startDiscovery();

            registerReceiver(bReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        }
    }

    public void off(View view){
        mBluetoothAdapter.disable();
        text.setText("Statut: Déconnecté");

        Toast.makeText(getApplicationContext(),"Bluetooth fermé",
                Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unregisterReceiver(bReceiver);
    }

    private void connectDevice() {

        mBluezService.connect(mmDevice);
    }




}


