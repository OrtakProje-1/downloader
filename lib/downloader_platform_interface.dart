import 'package:downloader/download_listener.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'downloader_method_channel.dart';

typedef DownloadListener = void Function(DownloadResult);

abstract class DownloaderPlatform extends PlatformInterface {
  /// Constructs a DownloaderPlatform.
  DownloaderPlatform() : super(token: _token);

  static final Object _token = Object();

  static DownloaderPlatform _instance = MethodChannelDownloader();

  /// The default instance of [DownloaderPlatform] to use.
  ///
  /// Defaults to [MethodChannelDownloader].
  static DownloaderPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [DownloaderPlatform] when
  /// they register themselves.
  static set instance(DownloaderPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }

  void setMetodCallHandler() {
    throw UnimplementedError('setMetodCallHandler() has not been implemented.');
  }

  Future<bool> download(String url) {
    throw UnimplementedError('download() has not been implemented.');
  }

  void setListener(DownloadListener listener) {
    throw UnimplementedError('download() has not been implemented.');
  }

  Future<bool> cancel() {
    throw UnimplementedError('download() has not been implemented.');
  }

  Future<bool> addToLibrary(String audioPath) {
    throw UnimplementedError('download() has not been implemented.');
  }
}
