package com.example.downloader

import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import androidx.annotation.NonNull


import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

/** DownloaderPlugin */
class DownloaderPlugin : FlutterPlugin, MethodCallHandler {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private lateinit var channel: MethodChannel
    private var context: Context? = null;

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "downloader")
        channel.setMethodCallHandler(this)
        context = flutterPluginBinding.applicationContext

    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        if (call.method == "getPlatformVersion") {

            result.success("Android ${android.os.Build.VERSION.RELEASE}")
        } else if (call.method == "addToLibrary") {
            var path = call.argument<String>("path");
            if (path != null) {
                addToLibrary(path,result);
            } else {
                result.success(false);
            }
        } else {
            result.notImplemented()
        }
    }

    fun invoke(name: String, arguments: Any) {
        channel.invokeMethod(name, arguments, null)
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }



    fun addToLibrary(path: String,result:Result) {
        if (context != null) {
            MediaScannerConnection.scanFile(
                context,
                listOf(path).toTypedArray(), null, Complete(result=result),
            )
        }

    }

}
class  Complete: MediaScannerConnection.OnScanCompletedListener{

    private var result : Result? = null

    constructor(result:Result?){
        this.result=result;
    }
    override fun onScanCompleted(path: String?, uri: Uri?) {
        if(result!=null){
            result!!.success(uri?.path);
        }
    }

}