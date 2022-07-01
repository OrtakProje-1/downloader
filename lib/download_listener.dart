import 'dart:typed_data';

// abstract class DownloadListener {
//   void onProgress(double progress);
//   void onDone(Uint8List bytes);
//   void onError(String error);
//   void onCancel();
// }

// class BaseDownloadListener extends DownloadListener {
//   @override
//   void onDone(Uint8List bytes) {}

//   @override
//   void onError(String error) {}

//   @override
//   void onProgress(double progress) {}

//   @override
//   void onCancel() {}
// }

///
abstract class DownloadResult {}

class ErrorResult extends DownloadResult {
  ErrorResult(this.message);
  String message;
}

class ProgressResult extends DownloadResult {
  ProgressResult(this.progress);
  double progress;
}

class CancelResult extends DownloadResult {}

class SuccessResult extends DownloadResult {
  SuccessResult(this.bytes);

  Uint8List bytes;
}
