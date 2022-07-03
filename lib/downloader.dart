import 'downloader_platform_interface.dart';

class Downloader {
  Future<String?> getPlatformVersion() {
    return DownloaderPlatform.instance.getPlatformVersion();
  }

  Future<String?> addToLibrary(String audioPath) async {
    return DownloaderPlatform.instance.addToLibrary(audioPath);
  }
}
