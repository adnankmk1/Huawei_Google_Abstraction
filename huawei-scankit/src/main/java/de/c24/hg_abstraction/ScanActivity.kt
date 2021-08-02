package de.c24.hg_abstraction

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.app.ActivityCompat
import com.huawei.hms.hmsscankit.RemoteView
import com.huawei.hms.ml.scan.HmsScan

class ScanActivity : AppCompatActivity() {

    companion object
    {
        private val TAG = "DefinedActivity"
        //declare RemoteView instance
        private var remoteView: RemoteView? = null
        //declare the key ,used to get the value returned from scankit
        val SCAN_RESULT = "scanResult"
        var mScreenWidth = 0
        var mScreenHeight = 0
        //scan_view_finder width & height is  300dp
        val SCAN_FRAME_SIZE = 300
        private val DEFINED_CODE = 222
        private val REQUEST_CODE_SCAN = 0X01
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)
        val list = arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
        ActivityCompat.requestPermissions(this, list, DEFINED_CODE)

    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.size < 2 || grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
            return
        }
        else if (requestCode == DEFINED_CODE) {
            //start your activity for scanning barcode
            startCamera()
        }
    }

    private fun startCamera(){
        //1.get screen density to caculate viewfinder's rect
        val dm = resources.displayMetrics
        //2.get screen size
        val density = dm.density
        mScreenWidth=dm.widthPixels
        mScreenHeight=dm.heightPixels
        var scanFrameSize=(SCAN_FRAME_SIZE*density)
        //3.caculate viewfinder's rect,it's in the middle of the layout
        //set scanning area(Optional, rect can be null,If not configure,default is in the center of layout)
        val rect = Rect()
        apply {
            rect.left = (mScreenWidth / 2 - scanFrameSize / 2).toInt()
            rect.right = (mScreenWidth / 2 + scanFrameSize / 2).toInt()
            rect.top = (mScreenHeight / 2 - scanFrameSize / 2).toInt()
            rect.bottom = (mScreenHeight / 2 + scanFrameSize / 2).toInt()
        }
        //initialize RemoteView instance, and set calling back for scanning result
        remoteView = RemoteView.Builder().setContext(this).setBoundingBox(rect).setFormat(HmsScan.ALL_SCAN_TYPE).build()
        remoteView?.onCreate(null)
        remoteView?.setOnResultCallback { result ->
            if (result != null && result.size > 0 && result[0] != null && !TextUtils.isEmpty(result[0].getOriginalValue())) {
                val intent = Intent()
                intent.apply {
                    putExtra(SCAN_RESULT, result[0]) }
                setResult(Activity.RESULT_OK, intent)
                this.finish()
            }
        }
        // Add the defined RemoteView to the page layout.
        val params = FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        val frameLayout = findViewById<FrameLayout>(R.id.rim1)
        frameLayout.addView(remoteView, params)
    }

    //manage remoteView lifecycle
    override fun onStart() {
        super.onStart()
        remoteView?.onStart()
    }

    override fun onResume() {
        super.onResume()
        remoteView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        remoteView?.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        remoteView?.onDestroy()
    }

    override fun onStop() {
        super.onStop()
        remoteView?.onStop()
    }



}