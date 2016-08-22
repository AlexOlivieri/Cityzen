package ch.hevs.datasemlab.cityzen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


public class NetworkChangeBroadcastReceiver extends BroadcastReceiver {

    private final String TAG = NetworkChangeBroadcastReceiver.class.getSimpleName();

    private Handler handler;

    public NetworkChangeBroadcastReceiver(Handler handler){
        this.handler = handler;
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        final ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        handler.post(new Runnable() {
            @Override
            public void run() {
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                boolean networkState =  networkInfo != null && networkInfo.isConnected();
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putBoolean(CityzenContracts.NETWORK_STATE, networkState);
                msg.setData(bundle);
                handler.sendMessage(msg);
            }
        });
        Log.i(TAG, "On Receive");
    }
}

