package com.modifyfgo.xfgo;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.security.KeyChain;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"
    };

    private EditText serverAddressEdit;
    private EditText uHpEdit;
    private EditText uAtkEdit;
    private EditText uid;
    private EditText pw;
    private EditText enemyActNumto;
    private EditText enemyChargeTurnto;

    private Switch mainSwitch;
    private Switch tdLvSwitch;
    private Switch skillLvSwitch;
    private Switch battleCancelSwitch;
    private Switch limitCountSwitch;
    private Switch enemyActNumSwitch;
    private Switch enemyChargeTurnSwitch;
    private Switch replaceSvtSwitch;
    private Switch replaceSvt1;
    private Switch replaceSvt2;
    private Switch replaceSvt3;
    private Switch replaceSvt4;
    private Switch replaceSvt5;
    private Switch replaceSvt6;
    private Switch replaceCraftSwitch;

    private Spinner replaceSvtSpinner;
    private Spinner replaceCraftSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        initButtons();
    }

    private void init() {

        verifyStoragePermissions(this);

        File dir = getExternalFilesDir(null);
        if (!dir.exists()){
            dir.mkdir();
        }

        serverAddressEdit=(EditText)findViewById(R.id.serverAddressEdit);
        uid=(EditText)findViewById(R.id.uid);
        pw=(EditText)findViewById(R.id.pw);
        uHpEdit=(EditText)findViewById(R.id.uHpEdit);
        uAtkEdit=(EditText)findViewById(R.id.uAtkEdit);
        enemyActNumto=(EditText)findViewById(R.id.enemyActNumto);
        enemyChargeTurnto=(EditText)findViewById(R.id.enemyChargeTurnto);

        mainSwitch=(Switch)findViewById(R.id.mainSwitch);
        tdLvSwitch=(Switch)findViewById(R.id.tdlvSwitch);
        skillLvSwitch=(Switch)findViewById(R.id.skilllvSwitch);
        battleCancelSwitch=(Switch)findViewById(R.id.battleCancelSwitch);
        limitCountSwitch=(Switch)findViewById(R.id.limitCountSwitch);
        enemyActNumSwitch=(Switch)findViewById(R.id.enemyActNumSwitch);
        enemyChargeTurnSwitch=(Switch)findViewById(R.id.enemyChargeTurnSwitch);
        replaceSvtSwitch=(Switch)findViewById(R.id.replaceSvtSwitch);
        replaceSvt1=(Switch)findViewById(R.id.replaceSvt1);
        replaceSvt2=(Switch)findViewById(R.id.replaceSvt2);
        replaceSvt3=(Switch)findViewById(R.id.replaceSvt3);
        replaceSvt4=(Switch)findViewById(R.id.replaceSvt4);
        replaceSvt5=(Switch)findViewById(R.id.replaceSvt5);
        replaceSvt6=(Switch)findViewById(R.id.replaceSvt6);
        replaceCraftSwitch=(Switch)findViewById(R.id.replaceCraftSwitch);

        replaceSvtSpinner=(Spinner)findViewById(R.id.replaceSvtSpinner);
        replaceCraftSpinner=(Spinner)findViewById(R.id.replaceCraftSpinner);

        String oldOptionsStr = FileUtil.getFileDataFromSdcard("options");
        if(oldOptionsStr!=null){
            try {
                JSONObject oldOptions = new JSONObject(oldOptionsStr);
                serverAddressEdit.setText(oldOptions.getString("serverAddress"));
                mainSwitch.setChecked(string2bool(oldOptions.getString("main")));
                uHpEdit.setText(oldOptions.getString("uHp"));
                uAtkEdit.setText(oldOptions.getString("uAtk"));
                uid.setText(oldOptions.getString("uid"));
                pw.setText(oldOptions.getString("pw"));

                tdLvSwitch.setChecked(string2bool(oldOptions.getString("tdLv")));
                skillLvSwitch.setChecked(string2bool(oldOptions.getString("skillLv")));
                battleCancelSwitch.setChecked(string2bool(oldOptions.getString("battleCancel")));
                limitCountSwitch.setChecked(string2bool(oldOptions.getString("limitCountSwitch")));

                enemyActNumSwitch.setChecked(string2bool(oldOptions.getString("enemyActNumSwitch")));
                enemyActNumto.setText(oldOptions.getString("enemyActNumto"));
                enemyChargeTurnSwitch.setChecked(string2bool(oldOptions.getString("enemyChargeTurnSwitch")));
                enemyChargeTurnto.setText(oldOptions.getString("enemyChargeTurnto"));

                replaceSvtSwitch.setChecked(string2bool(oldOptions.getString("replaceSvtSwitch")));
                replaceSvtSpinner.setSelection(Integer.parseInt(oldOptions.getString("replaceSvtSpinner")),true);
                replaceSvt1.setChecked(string2bool(oldOptions.getString("replaceSvt1")));
                replaceSvt2.setChecked(string2bool(oldOptions.getString("replaceSvt2")));
                replaceSvt3.setChecked(string2bool(oldOptions.getString("replaceSvt3")));
                replaceSvt4.setChecked(string2bool(oldOptions.getString("replaceSvt4")));
                replaceSvt5.setChecked(string2bool(oldOptions.getString("replaceSvt5")));
                replaceSvt6.setChecked(string2bool(oldOptions.getString("replaceSvt6")));

                replaceCraftSwitch.setChecked(string2bool(oldOptions.getString("replaceCraftSwitch")));
                replaceCraftSpinner.setSelection(Integer.parseInt(oldOptions.getString("replaceCraftSpinner")),true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void initButtons(){

        Button submitButton = (Button) findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String serverAddress = serverAddressEdit.getText().toString();
                final JSONObject newOptions = new JSONObject();
                try {
                    newOptions.put("serverAddress",serverAddress);
                    newOptions.put("main",mainSwitch.isChecked());
                    newOptions.put("uid",uid.getText().toString());
                    newOptions.put("pw",pw.getText().toString());
                    newOptions.put("uHp",Integer.parseInt(uHpEdit.getText().toString()));
                    newOptions.put("uAtk",Integer.parseInt(uAtkEdit.getText().toString()));
                    newOptions.put("tdLv",tdLvSwitch.isChecked());
                    newOptions.put("skillLv",skillLvSwitch.isChecked());
                    newOptions.put("battleCancel",battleCancelSwitch.isChecked());
                    newOptions.put("limitCountSwitch",limitCountSwitch.isChecked());
                    newOptions.put("enemyActNumSwitch",enemyActNumSwitch.isChecked());
                    newOptions.put("enemyActNumto",Integer.parseInt(enemyActNumto.getText().toString()));
                    newOptions.put("enemyChargeTurnSwitch",enemyChargeTurnSwitch.isChecked());
                    newOptions.put("enemyChargeTurnto",Integer.parseInt(enemyChargeTurnto.getText().toString()));
                    newOptions.put("replaceSvtSwitch",replaceSvtSwitch.isChecked());
                    newOptions.put("replaceSvtSpinner",replaceSvtSpinner.getSelectedItemPosition());
                    newOptions.put("replaceSvt1",replaceSvt1.isChecked());
                    newOptions.put("replaceSvt2",replaceSvt2.isChecked());
                    newOptions.put("replaceSvt3",replaceSvt3.isChecked());
                    newOptions.put("replaceSvt4",replaceSvt4.isChecked());
                    newOptions.put("replaceSvt5",replaceSvt5.isChecked());
                    newOptions.put("replaceSvt6",replaceSvt6.isChecked());
                    newOptions.put("replaceCraftSwitch",replaceCraftSwitch.isChecked());
                    newOptions.put("replaceCraftSpinner",replaceCraftSpinner.getSelectedItemPosition());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                FileUtil.saveFileToSDcard("options",newOptions.toString());

                Toast.makeText(getApplicationContext(),HttpUtil.post(serverAddress, newOptions.toString()),Toast.LENGTH_SHORT).show();
            }
        });

        Button getRootCAButton = (Button) findViewById(R.id.getRootCAButton);
        getRootCAButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String certContext = HttpUtil.get(serverAddressEdit.getText()+"?getRootCA");
                        if(certContext!=null){
                            try {
                                installCert(certContext);
                            } catch (CertificateException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            }
        });

    }

    // 安卓6.0及以上动态申请权限
    public static void verifyStoragePermissions(Activity activity) {
        try {
            int permission = ActivityCompat.checkSelfPermission(activity, "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void installCert(String certContext) throws CertificateException {
        byte [] cert = certContext.getBytes();
        X509Certificate x509 = X509Certificate.getInstance(cert);
        Intent intent = KeyChain.createInstallIntent();
        intent.putExtra(KeyChain.EXTRA_CERTIFICATE, x509.getEncoded());
        intent.putExtra(KeyChain.EXTRA_NAME, "modifyfgo");
        startActivity(intent);
    }

    private Boolean string2bool(String string){
        return string.equals("true");
    }

}