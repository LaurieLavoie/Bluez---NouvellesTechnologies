package bluez.bluez;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.bluetooth.BluetoothDevice;
import java.util.Set;
import java.util.zip.Inflater;

import android.widget.RelativeLayout.LayoutParams;


public class MenuActivity extends ActionBarActivity {
    private final static int REQUEST_CODE_ENABLE_BLUETOOTH = 0;
    Set<BluetoothDevice> devices;
    BluetoothAdapter bluetoothAdapter;
    BroadcastReceiver bluetoothReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final RelativeLayout layoutMenu = (RelativeLayout) RelativeLayout.inflate(this, R.layout.activity_menu, null);
        setContentView(layoutMenu);
        final Button btnConnection = (Button) findViewById(R.id.btnConnection);
        btnConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v)
            {
                bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (bluetoothAdapter == null)
                {
                    Toast.makeText(MenuActivity.this, "Vous n'avez pas le Bluetooth sur votre cellulaire.",
                    Toast.LENGTH_SHORT).show();
                } else
                {
                    if (!bluetoothAdapter.isEnabled()) {
                        Intent enableBlueTooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBlueTooth, REQUEST_CODE_ENABLE_BLUETOOTH);
                    }

                    devices = bluetoothAdapter.getBondedDevices();
                    for (BluetoothDevice blueDevice : devices)
                    {
                        Button btnAdversaire = new Button(MenuActivity.this);
                        btnAdversaire.setText(blueDevice.getName());
                        LayoutParams params1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        params1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        btnAdversaire.setLayoutParams(params1);
                        layoutMenu.addView(btnAdversaire);

                       // Toast.makeText(MenuActivity.this, "Device = " + blueDevice.getName(), Toast.LENGTH_SHORT).show();
                    }

                      bluetoothReceiver = new BroadcastReceiver() {
                       // LinearLayout linearLayout = new LinearLayout(MenuActivity.this);
                       PopupMenu menu = new PopupMenu(MenuActivity.this, v);
                        public void onReceive(Context context, Intent intent) {
                            String action = intent.getAction();
                            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);


                                menu.getMenu().add(device.getName());

                                //Button btnAdversaire = new Button(MenuActivity.this);
                                //btnAdversaire.setText(device.getName());
                               // LayoutParams params1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                               // params1.addRule(RelativeLayout.CENTER_IN_PARENT);
                               // btnAdversaire.setLayoutParams(params1);
                               // layoutMenu.addView(btnAdversaire);
                            
                            }

                            menu.show();
                        }




                    };

                    IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    registerReceiver(bluetoothReceiver, filter);

                    bluetoothAdapter.startDiscovery();

                    // être visible
                   // Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                   // discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                   // startActivity(discoverableIntent);
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_menu, menu);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != REQUEST_CODE_ENABLE_BLUETOOTH)
            return;
        if (resultCode == RESULT_OK) {
            Toast.makeText(MenuActivity.this, "Le Bluetooth est maintenant activé.",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MenuActivity.this, "Vous n'avez pas activé le Bluetooth.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
       // super.onDestroy();
        //bluetoothAdapter.cancelDiscovery();
        //unregisterReceiver(bluetoothReceiver);
    }
}
