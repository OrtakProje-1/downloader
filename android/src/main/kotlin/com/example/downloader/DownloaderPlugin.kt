package com.example.downloader

import android.content.Context
import android.media.MediaScannerConnection
import androidx.annotation.NonNull
import com.example.downloader.file_manager.downloadFile
import com.example.downloader.models.DownloadResult
import com.example.downloader.utils.createHttpClient


import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.ktor.client.*
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking

/** DownloaderPlugin */
class DownloaderPlugin : FlutterPlugin, MethodCallHandler {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private lateinit var channel: MethodChannel
    private var context: Context? = null;
    private var client: HttpClient? = null;
    private var isDonwloading = false;

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "downloader")
        channel.setMethodCallHandler(this)
        context = flutterPluginBinding.applicationContext

    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        if (call.method == "getPlatformVersion") {

            result.success("Android ${android.os.Build.VERSION.RELEASE}")
        } else if (call.method == "download") {
            runBlocking {

                var url = call.argument<String>("url");
                if (url != null) {
                    if (!isDonwloading) {
                        result.success(true);
                        downloadVideo(url);
                    } else {
                        result.success(false);
                    }
                } else {
                    result.success(false);
                }

            }
        } else if (call.method == "cancel") {
            cancelDonwload();
        } else if (call.method == "addToLibrary") {
            var path = call.argument<String>("path");
            if (path != null) {
                addToLibrary(path);
                result.success(true);
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

    private fun cancelDonwload() {
        if (client != null) {
            isDonwloading=false;
            client!!.cancel();
            client = null;
            invoke("cancel", "İndirme işlemi iptal edildi");
        }
    }

    suspend fun downloadVideo(url: String) {
        client = client ?: createHttpClient();
        isDonwloading=true;
        downloadFile(url, client!!).collect {
            when (it) {
                is DownloadResult.Error -> {
                    isDonwloading=false;
                    invoke("error", it.message);
                }

                is DownloadResult.Progress -> {
                    invoke("progress", it.progress);
                }

                is DownloadResult.Success -> {
                    isDonwloading=false;
                    invoke("success", it.byteArray);
                }

            }

        }

    }

    fun addToLibrary(path: String) {
        if (context != null) {
            MediaScannerConnection.scanFile(
                context,
                listOf(path).toTypedArray(), null, null
            )
        }

    }

}
