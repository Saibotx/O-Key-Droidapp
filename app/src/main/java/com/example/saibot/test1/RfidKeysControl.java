package com.example.saibot.test1;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class RfidKeysControl extends Activity {

    // UUIDs for UAT service and associated characteristics.
    public static UUID UART_UUID = UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb");
    public static UUID TX_UUID = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb");
    public static UUID RX_UUID = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb");
    // UUID for the BTLE client characteristic which is necessary for notifications.
    public static UUID CLIENT_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    // UI elements
    private TextView keyView;
    private EditText input;

    // BTLE state
    private BluetoothAdapter adapter;
    private BluetoothGatt gatt;
    private BluetoothGattCharacteristic tx;
    private BluetoothGattCharacteristic rx;
    private boolean mConnected = false;

    // Main BTLE device callback where much of the logic occurs.
    private BluetoothGattCallback callback = new BluetoothGattCallback() {
        // Called whenever the device connection state changes, i.e. from disconnected to connected.
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (newState == BluetoothGatt.STATE_CONNECTED) {
                writeLine("Connected!");
                mConnected = true;
                // Discover services.
                if (!gatt.discoverServices()) {
                    writeLine("Failed to start discovering services!");
                }
            }
            else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                writeLine("Disconnected!");
                mConnected = false;

            }
            else {
                writeLine("Connection state changed.  New state: " + newState);
            }
        }

        // Called when services have been discovered on the remote device.
        // It seems to be necessary to wait for this discovery to occur before
        // manipulating any services or characteristics.
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                writeLine("Service discovery completed!");
            }
            else {
                writeLine("Service discovery failed with status: " + status);
            }
            // Save reference to each characteristic.
            tx = gatt.getService(UART_UUID).getCharacteristic(TX_UUID);
            rx = gatt.getService(UART_UUID).getCharacteristic(RX_UUID);
            // Setup notifications on RX characteristic changes (i.e. data received).
            // First call setCharacteristicNotification to enable notification.
            if (!gatt.setCharacteristicNotification(rx, true)) {
                writeLine("Couldn't set notifications for RX characteristic!");
            }
            // Next update the RX characteristic's client descriptor to enable notifications.
            if (rx.getDescriptor(CLIENT_UUID) != null) {
                BluetoothGattDescriptor desc = rx.getDescriptor(CLIENT_UUID);
                desc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                if (!gatt.writeDescriptor(desc)) {
                    writeLine("Couldn't write RX client descriptor value!");
                }
            }
            else {
                writeLine("Couldn't get RX client descriptor!");
            }
        }

        // Called when a remote characteristic changes (like the RX characteristic).
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            writeLine("Received: " + characteristic.getStringValue(0));
        }
    };

    // BTLE device scanning callback.
    private BluetoothAdapter.LeScanCallback scanCallback = new BluetoothAdapter.LeScanCallback() {
        // Called when a device is found.
        @Override
        public void onLeScan(BluetoothDevice bluetoothDevice, int i, byte[] bytes) {
            writeLine("Found device: " + bluetoothDevice.getAddress());
            // Check if the device has the UART service.
            if (parseUUIDs(bytes).contains(UART_UUID)) {
                // Found a device, stop the scan.
                adapter.stopLeScan(scanCallback);
                writeLine("Found UART service!");
                // Connect to the device.
                // Control flow will now go to the callback functions when BTLE events occur.
                gatt = bluetoothDevice.connectGatt(getApplicationContext(), false, callback);
            }
        }
    };
    private void writeLine(final CharSequence text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                keyView.append(text);
                keyView.append("\n");
            }
        });
    }
    // Filtering by custom UUID is broken in Android 4.3 and 4.4, see:
    //   http://stackoverflow.com/questions/18019161/startlescan-with-128-bit-uuids-doesnt-work-on-native-android-ble-implementation?noredirect=1#comment27879874_18019161
    // This is a workaround function from the SO thread to manually parse advertisement data.
    private List<UUID> parseUUIDs(final byte[] advertisedData) {
        List<UUID> uuids = new ArrayList<UUID>();

        int offset = 0;
        while (offset < (advertisedData.length - 2)) {
            int len = advertisedData[offset++];
            if (len == 0)
                break;

            int type = advertisedData[offset++];
            switch (type) {
                case 0x02: // Partial list of 16-bit UUIDs
                case 0x03: // Complete list of 16-bit UUIDs
                    while (len > 1) {
                        int uuid16 = advertisedData[offset++];
                        uuid16 += (advertisedData[offset++] << 8);
                        len -= 2;
                        uuids.add(UUID.fromString(String.format("%08x-0000-1000-8000-00805f9b34fb", uuid16)));
                    }
                    break;
                case 0x06:// Partial list of 128-bit UUIDs
                case 0x07:// Complete list of 128-bit UUIDs
                    // Loop through the advertised 128-bit UUID's.
                    while (len >= 16) {
                        try {
                            // Wrap the advertised bits and order them.
                            ByteBuffer buffer = ByteBuffer.wrap(advertisedData, offset++, 16).order(ByteOrder.LITTLE_ENDIAN);
                            long mostSignificantBit = buffer.getLong();
                            long leastSignificantBit = buffer.getLong();
                            uuids.add(new UUID(leastSignificantBit,
                                    mostSignificantBit));
                        } catch (IndexOutOfBoundsException e) {
                            // Defensive programming.
                            //Log.e(LOG_TAG, e.toString());
                            continue;
                        } finally {
                            // Move the offset to read the next uuid.
                            offset += 15;
                            len -= 16;
                        }
                    }
                    break;
                default:
                    offset += (len - 1);
                    break;
            }
        }
        return uuids;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rfid_keys_control);
        keyView = (TextView) findViewById(R.id.keyView);
        input = (EditText) findViewById(R.id.editText);

        adapter = BluetoothAdapter.getDefaultAdapter();
    }

    protected void onStop() {
        super.onStop();
        if (gatt != null) {
            // For better reliability be careful to disconnect and close the connection.
            gatt.disconnect();
            gatt.close();
            gatt = null;
            tx = null;
            rx = null;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_rfid_keys_control, menu);
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
    public void BTScan(View view){
        writeLine("Scanning for devices...");
        adapter.startLeScan(scanCallback);
    }
    public void AddButton(View view){
        new AddRFID().execute();
    }
    private class AddRFID extends AsyncTask {

        @Override
        protected Object doInBackground(Object... arg0) {
            try {
                AddStuff();
                return null;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    private void AddStuff() throws Exception {

        String device = "rfid";
        EditText UserID = (EditText)findViewById(R.id.editText);
        String action = "store";
        String id = UserID.getText().toString();

        URL ourServer = new URL("https://warm-sierra-7577.herokuapp.com/" +
                device + "?" + action + "=" + id);

        Log.d("mymsg", "https://warm-sierra-7577.herokuapp.com/" +
                device + "?" + action + "=" + id);

        URLConnection yc = ourServer.openConnection();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(yc.getInputStream()));
//      THIS IS FOR VERTICAL READING
//      int inputLine;
        String inputLine;
        final StringBuffer sb = new StringBuffer("");


        while ((inputLine = in.readLine()) != null)
            sb.append(inputLine + "\n");
        in.close();
// THIS FOR VERTICAL READING
/*        while ((inputLine = in.read()) != -1) {
            if (inputLine = " ") {
                sb.append((char) inputLine + "\n");
            } else {
                sb.append((char) inputLine);
            }
        }
        in.close();
*/
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                TextView t;

                t = (TextView) findViewById(R.id.keyView);
                t.setText(sb);
            }
        });
    }




    public void RemoveButton(View view){
        new RemoveRFID().execute();
    }
    private class RemoveRFID extends AsyncTask {

        @Override
        protected Object doInBackground(Object... arg0) {
            try {
                RemoveStuff();
                return null;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    private void RemoveStuff() throws Exception {

        String device = "rfid";
        EditText UserID = (EditText)findViewById(R.id.editText);
        String action = "remove";
        String id = UserID.getText().toString();

        URL ourServer = new URL("https://warm-sierra-7577.herokuapp.com/" +
                device + "?" + action + "=" + id);

        Log.d("mymsg", "https://warm-sierra-7577.herokuapp.com/" +
                device + "?" + action + "=" + id);

        URLConnection yc = ourServer.openConnection();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(yc.getInputStream()));
//      THIS IS FOR VERTICAL READING
//      int inputLine;
        String inputLine;
        final StringBuffer sb = new StringBuffer("");


        while ((inputLine = in.readLine()) != null)
            sb.append(inputLine + "\n");
        in.close();
// THIS FOR VERTICAL READING
/*        while ((inputLine = in.read()) != -1) {
            if (inputLine = " ") {
                sb.append((char) inputLine + "\n");
            } else {
                sb.append((char) inputLine);
            }
        }
        in.close();
*/
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                TextView t;

                t = (TextView) findViewById(R.id.keyView);
                t.setText(sb);
            }
        });
    }

    public void refresButton(View view){
        new refresRFID().execute();
    }
    private class refresRFID extends AsyncTask {

        @Override
        protected Object doInBackground(Object... arg0) {
            try {
                refresStuff();
                return null;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    private void refresStuff() throws Exception {

        String device = "rfid";
        EditText UserID = (EditText)findViewById(R.id.editText);
        String action = "pull";
        String id = UserID.getText().toString();

        URL ourServer = new URL("https://warm-sierra-7577.herokuapp.com/" +
                device + "?" + action + "=" + id);

        Log.d("mymsg", "https://warm-sierra-7577.herokuapp.com/" +
                device + "?" + action + "=" + id);

        URLConnection yc = ourServer.openConnection();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(yc.getInputStream()));
//      THIS IS FOR VERTICAL READING
//      int inputLine;
        String inputLine;
        final StringBuffer sb = new StringBuffer("");


        while ((inputLine = in.readLine()) != null)
            sb.append(inputLine + "\n");
        in.close();
// THIS FOR VERTICAL READING
/*        while ((inputLine = in.read()) != -1) {
            if (inputLine = " ") {
                sb.append((char) inputLine + "\n");
            } else {
                sb.append((char) inputLine);
            }
        }
        in.close();
*/
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                TextView t;

                t = (TextView) findViewById(R.id.keyView);
                t.setText(sb);
            }
        });
    }



    public void ProgramButton(View view){
        EditText UserID = (EditText)findViewById(R.id.editText);
        String id = UserID.getText().toString();
        writeLine("Flashing the following:" + id);
        sendData("!RFID:" + id + "#");
    }

    private void sendData(String string) {
        Log.d("BluetoothRFID", "Sending = " + string);
        final byte[] trans = string.getBytes();
        if(mConnected) {
            tx.setValue(trans);
            gatt.writeCharacteristic(tx);
            gatt.setCharacteristicNotification(rx,true);
        }

    }
}
