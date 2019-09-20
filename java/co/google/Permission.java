package co.google;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.audiofx.BassBoost;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.SparseIntArray;
import android.view.View;

public abstract  class AbsPermission  extends Activity {
    private SparseIntArray mErrorString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mErrorString = new SparseIntArray();
    }

    public abstract void onPermissiosGranted(int requestCode);

    public void requestAppPermission(final String[] requestedPermission, final int stringId, final int requestCode) {
        mErrorString.put(requestCode, stringId);

        int PermissionCheck = PackageManager.PERMISSION_GRANTED;

        boolean showRequestPermissions = false;
        for (String permisison : requestedPermission) {
            PermissionCheck = PermissionCheck + ContextCompat.checkSelfPermission(this, permisison);
            showRequestPermissions = showRequestPermissions || ActivityCompat.shouldShowRequestPermissionRationale(this, permisison);
        }

        if (PermissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (showRequestPermissions) {
                Snackbar.make(findViewById(android.R.id.content), stringId, Snackbar.LENGTH_INDEFINITE).setAction("Grant", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityCompat.requestPermissions(AbsPermission.this, requestedPermission, requestCode);
                    }
                }).show();
            } else {
                ActivityCompat.requestPermissions(this, requestedPermission, requestCode);
            }
        } else {
            onPermissiosGranted(requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        for (int permission : grantResults) {
            permissionCheck = permissionCheck + permission;
        }

        if ((grantResults.length > 0) && PackageManager.PERMISSION_GRANTED == permissionCheck) {
            onPermissiosGranted(requestCode);
        } else {
            Snackbar.make(findViewById(android.R.id.content) , mErrorString.get(requestCode) ,
                    Snackbar.LENGTH_INDEFINITE ).setAction("Enable", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent();
                    i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    i.setData(Uri.parse("package:" + getPackageName()));
                    i.addCategory(Intent.CATEGORY_DEFAULT);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    startActivity(i);
                }
            }).show();
        }
    }
}