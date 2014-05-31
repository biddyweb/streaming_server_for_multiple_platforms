#SSFMP (Streaming Server For Multiple Platforms)#

##概要##

HTTP Live Streaming ([仕様][1]) を使用して iOS, OS X, Android, VLC にストリーミングするウェブアプリケーションです．

## このサーバの機能

#### 1. マイク付き USB カメラの映像と音声をリアルタイムストリーミング

ただし，Linux が認識してくれる USB カメラに限ります．

#### 2. 任意のビデオファイルをストリーミング

ただし，FFmpeg で MPEG2-TS (H.264/AAC-LC) にトランスコードできるビデオファイルに限ります．

#### 3. キャプチャボードの出力をリアルタイムストリーミング

ただし，キャプチャ内容を標準出力に出力するものに限ります．

## 使い方

Maven の Goal を jetty:run-war とすると，プロジェクトの target ディレクトリに war ファイルが生成されます．生成された war ファイルを Jetty の webapps ディレクトリに置きます．

Jetty を動かすユーザのホームディレクトリ直下に ssfmp.properties というファイルを作成します．その内容は例えば，下記のような感じです．

```java
FFMPEG_PATH_FOR_CAM=/usr/local/ffmpeg-0.11.5/bin/ffmpeg
FFMPEG_PATH_FOR_FILE=/usr/local/bin/ffmpeg
FFMPEG_PATH_FOR_CAP=/usr/local/bin/ffmpeg
CAPTURE_PROGRAM_PATH=/path/to/cap
PATH_OF_FILE_LOCAL=/home/user/Videos
PATH_OF_CAPTURED_LOCAL=
PATH_OF_FILE_NAS=
PATH_OF_CAPTURED_NAS=
NAS=NO
PATH_OF_TEMP=/tmp/ssfmp
MPEG2_TS_PACKET_LENGTH=188
DURATION=3
URI_IN_PLAYLIST=2
ADAPTIVE_BITRATE=3
```

iOS 機器でサーバ (例えば，http:// (サーバの IP アドレス):8080/ssfmp/ 等) にアクセスします．

あとは何となく使用します．

なお，テンポラリなディレクトリとして /tmp/ssfmp を作成し，その中にテンポラリなファイルを作成します．ストリーミングされるファイルは，Jetty の webapps 内に作成されます．プログラム上の不備によりこれらのファイルが自動で削除されない場合があります．また，大事なファイルをトランスコードする際は念のためバックアップを取っておくことをお勧めされます．

## 動作を確認しているハードウェアとソフトウェア

#### サーバ 1 のハードウェア

* CPU: Intel Celeron G1620
* Motherboard: B75
* Memory: 4GB
* SSD
* USB camera: Logicool C910

#### サーバ 2 のハードウェア

* CPU: Intel Core i7-2600K
* Motherboard: H67
* Memory: 16GB
* SSD
* USB camera: Logicool C910

#### サーバのソフトウェア

* OS: Ubuntu 14.04 (64bit)
* Java: Oracle Java SE 8u5
* Java Application Server: Jetty 9.2.0.v20140526
* Trancoder: FFmpeg 2.2.1, 0.11.5
* External codec: x264, libfaac

#### ストリーミングを再生する機器

* iPhone 5s (iOS 7.1.1)
* iPad Air (iOS 7.1.1)
* Apple TV で AirPlay することも可能

[1]: http://tools.ietf.org/html/draft-pantos-http-live-streaming
