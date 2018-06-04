package es.uji.al259348.sliwandroid.wear.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import es.uji.al259348.sliwandroid.core.controller.MainController;
import es.uji.al259348.sliwandroid.core.controller.MainControllerImpl;
import es.uji.al259348.sliwandroid.core.model.Device;
import es.uji.al259348.sliwandroid.core.model.User;
import es.uji.al259348.sliwandroid.core.services.DeviceService;
import es.uji.al259348.sliwandroid.core.services.DeviceServiceImpl;
import es.uji.al259348.sliwandroid.core.services.UserService;
import es.uji.al259348.sliwandroid.core.services.UserServiceImpl;
import es.uji.al259348.sliwandroid.core.view.MainView;
import es.uji.al259348.sliwandroid.wear.R;
import es.uji.al259348.sliwandroid.wear.exceptions.MyExceptionHandler;
import es.uji.al259348.sliwandroid.wear.fragments.ConfirmFragment;
import es.uji.al259348.sliwandroid.wear.fragments.InfoFragment;
import es.uji.al259348.sliwandroid.wear.fragments.LoadingFragment;
import es.uji.al259348.sliwandroid.wear.fragments.MainFragment;

public class MainActivity extends Activity implements
        MainView,
        MainFragment.OnFragmentInteractionListener,
        InfoFragment.OnFragmentInteractionListener,
        ConfirmFragment.OnFragmentInteractionListener {

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private static final String STEP_REGISTER_DEVICE = "stepRegisterDevice";
    private static final String STEP_LINK = "stepLink";
    private static final String STEP_CONFIG = "StepConfig";
    private static final String STEP_OK = "stepOk";

    //private static final String SSID = "seniormonitoring";
    //private static final String PASSWORD = "t3st2018";
    private static final String SSID = "iPhone de Arturo";
    private static final String PASSWORD = "3ict5yfrzqci4";

    private static final int REQUEST_CODE_CONFIG = 1;
    private static final int REQUEST_CODE_FEEDBACK = 2;

    private MainController controller;
    private View fragmentContent;
    private View btnInfo;

    private String step;

    private int wifiID;

    @Override
    protected void onResume() {
        super.onResume();
        //disableBluetooth();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Acceso a ubicación");
                builder.setMessage("¿Conceder acceso a ubicación?.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                    }
                });
            }
        }

        String[] PERMS_INITIAL={
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
        };
        ActivityCompat.requestPermissions(this, PERMS_INITIAL, 127);

        controller = new MainControllerImpl(this);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {

                btnInfo = stub.findViewById(R.id.btnInfo);
                btnInfo.setOnClickListener(v -> btnInfoClickListener(v));

                fragmentContent = stub.findViewById(R.id.fragmentContent);
                controller.decideStep();
            }
        });

        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler(this));

        if (getIntent().getBooleanExtra("crash", false)) {
            Toast.makeText(this, "Reinicio de la aplicación debido a un fallo de la misma.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("Wifi Permission", "Acceso a ubicación concedido.");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Funcionalidad limitada");
                    builder.setMessage("La aplicación no funcionará correctamente en segundo plano.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }
                    });
                    builder.show();
                }
                return;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        controller.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CONFIG) {
            switch (resultCode) {
                case RESULT_OK:
                    Toast.makeText(MainActivity.this, "Configuración terminada.", Toast.LENGTH_SHORT).show();
                    break;

                case RESULT_CANCELED:
                    Toast.makeText(MainActivity.this, "Configuración cancelada.", Toast.LENGTH_SHORT).show();
                    break;
            }
            controller.decideStep();
        }
        else if (requestCode == REQUEST_CODE_FEEDBACK) {
            switch (resultCode) {
                case RESULT_OK:
                    setFragment(LoadingFragment.newInstance("Tomando muestra..."));
                    controller.takeValidSample(data.getStringExtra("location"));
                    break;

                case RESULT_CANCELED:
                    Toast.makeText(MainActivity.this, "Se ha cancelado la validación de muestra.", Toast.LENGTH_SHORT).show();
                    break;
            }
            controller.decideStep();
        }
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(fragmentContent.getId(), fragment);
        transaction.commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void onError(Throwable throwable) {
        Toast.makeText(MainActivity.this, throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        controller.decideStep();
    }

    @Override
    public void hasToRegisterDevice() {
        //disableBluetooth();
        wifiID = connectSpecifiedWifi();
        step = STEP_REGISTER_DEVICE;
        setFragment(ConfirmFragment.newInstance("Es necesario registrar el dispositivo.", "Ok"));
    }

    @Override
    public void onDeviceRegistered(Device device) {
        Toast.makeText(MainActivity.this, "El dispositivo ha sido registrado.", Toast.LENGTH_SHORT).show();
        controller.decideStep();
    }

    @Override
    public void hasToLink() {
        step = STEP_LINK;
        setFragment(ConfirmFragment.newInstance("Es necesario vincular el dispositivo.", "Ok"));
    }

    @Override
    public void onUserLinked(User user) {
        Toast.makeText(MainActivity.this, "Usuario vinculado: " + user.getName(), Toast.LENGTH_SHORT).show();
        removeSpecifiedWifi(wifiID);
        //showMessage("Desconectado de seniormonitoring");
        controller.decideStep();
    }

    @Override
    public void hasToConfigure() {
        step = STEP_CONFIG;
        setFragment(ConfirmFragment.newInstance("Es necesario configurar el dispositivo.", "Ok"));
    }

    @Override
    public void isOk() {
        step = STEP_OK;
        setFragment(MainFragment.newInstance());
    }

    @Override
    public void onConfirm() {
        switch (step) {
            case STEP_REGISTER_DEVICE:
                setFragment(LoadingFragment.newInstance("Registrando dispositivo..."));
                controller.registerDevice();
                break;

            case STEP_LINK:
                setFragment(LoadingFragment.newInstance("Vinculando..."));
                controller.link();
                break;

            case STEP_CONFIG:
                Intent i = new Intent(MainActivity.this, ConfigActivity.class);
                startActivityForResult(i, REQUEST_CODE_CONFIG);
                break;
        }
    }

    @Override
    public void onUnlink() {
        controller.unlink();
        controller.decideStep();
    }

    @Override
    public void takeSample() {
        setFragment(LoadingFragment.newInstance("Tomando muestra..."));
        controller.takeSample();
    }

    @Override
    public void validateSample() {
        Intent i = new Intent(MainActivity.this, FeedbackActivity.class);
        startActivityForResult(i, REQUEST_CODE_FEEDBACK);
    }

    @Override
    public void onTakeSampleCompleted() {
        Toast.makeText(MainActivity.this, "Muestra tomada.", Toast.LENGTH_SHORT).show();
        controller.decideStep();
    }

    @Override
    public void onSampleClassified(String location) {
        Toast.makeText(MainActivity.this, "Muestra clasificada: " + location, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSampleSavedLocally() {
        Toast.makeText(MainActivity.this, "La muestra ha sido guardada localmente.", Toast.LENGTH_SHORT).show();
    }

    public void btnInfoClickListener(View view) {
        DeviceService deviceService = new DeviceServiceImpl(this);
        String deviceId = deviceService.getId();
        deviceService.onDestroy();

        UserService userService = new UserServiceImpl(this);
        String userId = userService.getCurrentLinkedUserId();
        userService.onDestroy();

        btnInfo.setVisibility(View.INVISIBLE);
        setFragment(InfoFragment.newInstance(deviceId, (userId.isEmpty()) ? "-" : userId));
    }

    @Override
    public void closeInfo() {
        btnInfo.setVisibility(View.VISIBLE);
        controller.decideStep();
    }

    public void enableBluetooth() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()){
            mBluetoothAdapter.enable();
            showMessage("Activando bluetooth...");
        }
    }

    public void disableBluetooth() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter.isEnabled()){
            mBluetoothAdapter.disable();
            showMessage("Bluetooth desactivado");
        }
    }


    public void showMessage(String message) {
        Context context = getApplicationContext();
        CharSequence text = message;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public int connectSpecifiedWifi() {
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"" + SSID + "\"";
        conf.preSharedKey = "\""+ PASSWORD +"\"";

        WifiManager wifiManager = (WifiManager)getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        int wifiId = wifiManager.addNetwork(conf);

        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        if (list != null) {
            for (WifiConfiguration i : list) {
                if (i.SSID != null && i.SSID.equals("\"" + SSID + "\"")) {
                    wifiManager.disconnect();
                    wifiManager.enableNetwork(i.networkId, true);
                    wifiManager.reconnect();
                    break;
                }
            }
        }
        return wifiId;
    }

    public void removeSpecifiedWifi(int wifiId) {
        WifiManager wifiManager = (WifiManager)getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for( WifiConfiguration i : list ) {
            if(i.SSID != null && i.SSID.equals("\"" + SSID + "\"")) {
                wifiManager.disconnect();
                break;
            }
        }
        wifiManager.removeNetwork(wifiId);
    }
}
