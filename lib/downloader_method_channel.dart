import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'downloader_platform_interface.dart';

/// An implementation of [DownloaderPlatform] that uses method channels.
class MethodChannelDownloader extends DownloaderPlatform {
  MethodChannelDownloader() {
    setMetodCallHandler();
  }

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
  Future<String?> addToLibrary(String audioPath) async {
    final result = await methodChannel.invokeMethod<String?>('addToLibrary', {
      "path": audioPath,
    });
    return result;
  }

  @override
  void setMetodCallHandler() {
    methodChannel.setMethodCallHandler(
      (call) async {
        switch (call.method) {
          default:
            debugPrint("Metod:${call.method}");
            debugPrint("Arguments:${call.arguments.toString()}");
        }
      },
    );
  }
}
