import 'package:downloader/download_listener.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'downloader_platform_interface.dart';

/// An implementation of [DownloaderPlatform] that uses method channels.
class MethodChannelDownloader extends DownloaderPlatform {
  MethodChannelDownloader() {
    setMetodCallHandler();
  }

  DownloadListener? _listener;

  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('downloader');

  @override
  Future<String?> getPlatformVersion() async {
    final version =
        await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }

  @override
  Future<bool> download(String url) async {
    final result = await methodChannel.invokeMethod<bool>('download', {
      "url": url,
    });
    return result ?? false;
  }

  @override
  Future<bool> cancel() async {
    final result = await methodChannel.invokeMethod<bool>('cancel');
    return result ?? false;
  }

  @override
  Future<bool> addToLibrary(String audioPath) async {
    final result = await methodChannel.invokeMethod<bool>('addToLibrary', {
      "path": audioPath,
    });
    return result ?? false;
  }

  @override
  void setMetodCallHandler() {
    methodChannel.setMethodCallHandler(
      (call) async {
        switch (call.method) {
          case "error":
            debugPrint("error listener:${call.arguments}");
            _listener?.call(ErrorResult(call.arguments.toString()));
            break;
          case "progress":
            debugPrint("progress listener:${call.arguments}");
            double progress = (call.arguments is int)
                ? (call.arguments as int).toDouble()
                : call.arguments;
            _listener?.call(ProgressResult(progress));
            break;
          case "success":
            debugPrint("success listener:${call.arguments}");
            _listener?.call(SuccessResult(call.arguments));
            break;
          case "cancel":
            debugPrint("cancel listener:${call.arguments}");
            _listener?.call(CancelResult());
            break;
          default:
            debugPrint("Metod:${call.method}");
            debugPrint("Arguments:${call.arguments.toString()}");
        }
      },
    );
  }

  @override
  void setListener(DownloadListener listener) {
    _listener = listener;
  }
}
