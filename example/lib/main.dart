import 'dart:typed_data';

import 'package:downloader/download_listener.dart';
import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:downloader/downloader.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
  final _downloaderPlugin = Downloader();
  bool dowloading = false;

  // String url =  "https://img.freepik.com/free-vector/shining-circle-purple-lighting-isolated-dark-background_1441-2396.jpg?t=st=1656605719~exp=1656606319~hmac=369fa4603bc7fce7ca796f1f9c1d3a0b9d3e0262d8242110e9a882e5e0672ab0&w=1060";
  // String url = "https://wallpaperaccess.com/full/345330.jpg";
  String url =
      "https://raw.githubusercontent.com/Projeyonetimiiii/MusicPlayer/Master/video/example_video.gif";
  @override
  void initState() {
    super.initState();
    initPlatformState();
    setListener();
  }

  startDownload() async {
    bool res = await _downloaderPlugin.download(url);
    dowloading = res;
    start = DateTime.now();
    setState(() {});
    debugPrint("indirme işlemi ${res ? "BAŞLATILDI" : "BAŞLATILAMADI"}");
  }

  setListener() {
    _downloaderPlugin.setListener(listener);
  }

  void listener(DownloadResult result) {
    if (result is ErrorResult) {
      debugPrint(result.message);
    }
    if (result is ProgressResult) {
      debugPrint(result.progress.toString());
    }
    if (result is SuccessResult) {
      debugPrint(result.bytes.toString());
      debugPrint('onDone bytes: ${result.bytes}');
      setState(() {
        bytes = result.bytes;
      });
      DateTime end = DateTime.now();
      print(end.difference(start));
    }
    if (result is CancelResult) {
      debugPrint("cancelled");
    }
  }

  late DateTime start;

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    // We also handle the message potentially returning null.
    try {
      platformVersion = await _downloaderPlugin.getPlatformVersion() ??
          'Unknown platform version';
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  Uint8List? bytes;

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
          actions: [
            IconButton(
              onPressed: () async {
                bool res = await _downloaderPlugin.cancel();
                print("indirme işlemi iptal ${res ? "edildi" : "edilemedi"}");
              },
              icon: const Icon(Icons.cancel),
            ),
            IconButton(
              onPressed: () {
                if (bytes != null) {
                  print(bytes!.buffer.lengthInBytes);
                  int lenth = bytes!.buffer.lengthInBytes;
                  double sizeInMb = lenth / (1024 * 1024);
                  print("${sizeInMb.toStringAsFixed(3)} mb");
                }
              },
              icon: const Icon(Icons.info),
            ),
          ],
        ),
        body: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Center(
              child: Text('Running on: $_platformVersion\n'),
            ),
            if (bytes != null) ...[
              Image.memory(bytes!),
            ],
          ],
        ),
        floatingActionButton: FloatingActionButton(
          onPressed: startDownload,
          child: const Icon(Icons.download_rounded),
        ),
      ),
    );
  }
}
