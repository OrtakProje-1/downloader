import 'package:downloader/download_listener.dart';
import 'package:flutter/material.dart';

import 'downloader_platform_interface.dart';

class Downloader {
  Future<String?> getPlatformVersion() {
    return DownloaderPlatform.instance.getPlatformVersion();
  }

  Future<bool> download(String url) {
    return DownloaderPlatform.instance.download(url);
  }

  ///
  ///   ```
  /// setListener(listener);
  ///
  /// void listener(DownloadResult result) {
  ///  if (result is ErrorResult) {
  ///    debugPrint(result.message);
  ///  }
  ///  if (result is ProgressResult) {
  ///    debugPrint(result.progress.toString());
  ///  }
  ///  if (result is SuccessResult) {
  ///    debugPrint(result.bytes.toString());
  ///  }
  ///  if (result is CancelResult) {
  ///    debugPrint("cancelled");
  ///  }
  ///}
  ///  ```
  ///
  void setListener(DownloadListener listener) {
    DownloaderPlatform.instance.setListener(listener);
  }

  Future<bool> cancel() async {
    return DownloaderPlatform.instance.cancel();
  }

  Future<bool> addToLibrary(String audioPath) async {
    return DownloaderPlatform.instance.addToLibrary(audioPath);
  }
}
