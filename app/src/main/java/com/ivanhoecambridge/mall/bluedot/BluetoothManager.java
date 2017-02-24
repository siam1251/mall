package com.ivanhoecambridge.mall.bluedot;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;

import com.ivanhoecambridge.kcpandroidsdk.utils.KcpUtility;
import com.ivanhoecambridge.mall.R;
import com.ivanhoecambridge.mall.views.AlertDialogForInterest;

/**
 * Created by Kay on 2017-01-11.
 */

public class BluetoothManager {

    private Context mContext;
    public static boolean mDidAskToTurnOnBluetooth = false;

    public BluetoothManager(Context context){
        this.mContext = context;
    }


    private boolean isBluetoothSupported() {
        return BluetoothAdapter.getDefaultAdapter() != null ? true : false;
    }

    public static boolean isBluetoothEnabled() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter
                .getDefaultAdapter();
        if (bluetoothAdapter != null) {
            return bluetoothAdapter.isEnabled();
        }

        return false;
    }

    public boolean turnOnBluetooth() {
        if(isBluetoothSupported()) {
            mDidAskToTurnOnBluetooth = true;
            AlertDialogForInterest alertDialogForInterest = new AlertDialogForInterest();
            alertDialogForInterest.getAlertDialog(
                    mContext,
                    mContext.getString(R.string.title_enable_bluetooth),
                    mContext.getString(R.string.warning_turn_on_bluetooth),
                    mContext.getString(R.string.action_ok),
                    mContext.getString(R.string.action_cancel),
                    new AlertDialogForInterest.DialogAnsweredListener() {
                        @Override
                        public void okClicked() {
                            BluetoothAdapter bluetoothAdapter = BluetoothAdapter
                                    .getDefaultAdapter();
                            if (bluetoothAdapter != null) {
                                bluetoothAdapter.enable();
                            }
                        }
                    });

        }
        return false;
    }
}
