package bluez.bluez;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class MenuActivity extends ActionBarActivity {
    private final static int REQUEST_CODE_ENABLE_BLUETOOTH = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        final Button btnConnection = (Button) findViewById(R.id.btnConnection);
        btnConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
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
}
