/*

The MIT License (MIT)

Copyright (c) 2014 hirooka <https://hirooka.pro/>

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.

 */

package pro.hirooka.streaming_server_for_multiple_platforms;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

@WebServlet(name="SSFMP", urlPatterns={"/ss"})
public class SSFMP extends HttpServlet {
	
	private static final long serialVersionUID = 7934886039785610541L;
	static final String FILE_SEPARATOR = System.getProperty("file.separator");
	
	private static Logger log = LoggerFactory.getLogger(SSFMP.class);
	private static Marker MARKER_SSFMP = MarkerFactory.getMarker("SSFMP");
	
	protected void doGet(HttpServletRequest req, HttpServletResponse res){
		
		// シングルトンクラスをグローバル変数的に使用する．
		//  - 他に良い方法を思い付けない．
		//  - サーバ・クライアントが 1 対 1 を前提としているので，よしとする．
		SingletonForSSFMP info = SingletonForSSFMP.getInstance();
		// シングルトンクラスの初期化
		info.init();

		// 後に Waiting サーブレットに対して値を渡すためのサーブレットコンテキストの生成
		ServletContext sc = null;
		sc = getServletContext();
		
		// プロパティファイルから定数情報の取得
		// TODO エラー処理
		PropertiesLoader prop = new PropertiesLoader();
		prop.setProperties("ssfmd.properties");

		int MPEG2_TS_PACKET_LENGTH = Integer.parseInt(prop.getValue("MPEG2_TS_PACKET_LENGTH"));
		double DURATION = Double.parseDouble(prop.getValue("DURATION"));
		int URI_IN_PLAYLIST = Integer.parseInt(prop.getValue("URI_IN_PLAYLIST"));
		log.debug(MARKER_SSFMP, "{} [MPEG2_TS_PACKET_LENGTH] {}", Thread.currentThread().getStackTrace()[1].getMethodName(), MPEG2_TS_PACKET_LENGTH);
		log.debug(MARKER_SSFMP, "{} [DURATION] {}", Thread.currentThread().getStackTrace()[1].getMethodName(), DURATION);
		log.debug(MARKER_SSFMP, "{} [URI_IN_PLAYLIST] {}", Thread.currentThread().getStackTrace()[1].getMethodName(), URI_IN_PLAYLIST);
		
		// HTML, JSP の GUI から送られてくる GET パラメータ
		// TODO パラメータのチェック処理
		String modeLive = req.getParameter("modeLive");
		String file = req.getParameter("file");
		//file = new String(file.getBytes("ISO-8859-1"), "utf-8"); // for GlassFish 4.0
		String capFile = req.getParameter("capFile");
		String capResolution = req.getParameter("capResolution");
		String videoResolution = req.getParameter("videoResolution");
		String videoProfile = req.getParameter("videoProfile");
		String videoBitrate = req.getParameter("videoBitrate");
		String audioCodec = req.getParameter("audioCodec");
		String audioBitrate = req.getParameter("audioBitrate");
		String audioSamplingFreq = req.getParameter("audioSamplingFreq");
		String durationLive = req.getParameter("durationLive");
		String encOrNot = req.getParameter("encOrNot");
		String id = req.getParameter("id");
		
		log.debug(MARKER_SSFMP, "{} [modeLive] {}", Thread.currentThread().getStackTrace()[1].getMethodName(), modeLive);
		log.debug(MARKER_SSFMP, "{} [file] {}", Thread.currentThread().getStackTrace()[1].getMethodName(), file);
		log.debug(MARKER_SSFMP, "{} [capFile] {}", Thread.currentThread().getStackTrace()[1].getMethodName(), capFile);
		log.debug(MARKER_SSFMP, "{} [capResolution] {}", Thread.currentThread().getStackTrace()[1].getMethodName(), capResolution);
		log.debug(MARKER_SSFMP, "{} [videoResolution] {}", Thread.currentThread().getStackTrace()[1].getMethodName(), videoResolution);
		log.debug(MARKER_SSFMP, "{} [videoProfile] {}", Thread.currentThread().getStackTrace()[1].getMethodName(), videoProfile);
		log.debug(MARKER_SSFMP, "{} [videoBitrate] {}", Thread.currentThread().getStackTrace()[1].getMethodName(), videoBitrate);
		log.debug(MARKER_SSFMP, "{} [audioCodec] {}", Thread.currentThread().getStackTrace()[1].getMethodName(), audioCodec);
		log.debug(MARKER_SSFMP, "{} [audioBitrate] {}", Thread.currentThread().getStackTrace()[1].getMethodName(), audioBitrate);
		log.debug(MARKER_SSFMP, "{} [audioSamplingFreq] {}", Thread.currentThread().getStackTrace()[1].getMethodName(), audioSamplingFreq);
		log.debug(MARKER_SSFMP, "{} [durationLive] {}", Thread.currentThread().getStackTrace()[1].getMethodName(), durationLive);
		log.debug(MARKER_SSFMP, "{} [encOrNot] {}", Thread.currentThread().getStackTrace()[1].getMethodName(), encOrNot);
		log.debug(MARKER_SSFMP, "{} [id] {}", Thread.currentThread().getStackTrace()[1].getMethodName(), id);
		
		//if(modeLive != null){
		if(isNotParamNullOrBlank(modeLive)){
			info.setModeLive(modeLive);
		}
		
		// ディレクトリ情報 (一部プロパティファイルから取得)
		String STREAM_PATH_ROOT = this.getServletContext().getRealPath(".");
		String STREAM_PATH = null;
		String TEMP_PATH = prop.getValue("PATH_OF_TEMP");
		String TEMP_PATH_FOR_ENC = null;
		String FILE_PATH = prop.getValue("PATH_OF_FILE_LOCAL");
		String CAP_PATH = prop.getValue("PATH_OF_CAPTURED_LOCAL");
//		String CAP_PATH = getProperties("PATH_OF_CAPTURED_NAS");
		
		// エンコード or トランスコードされた MPEG2-TS を一時的に保存するためのテンポラリなディレクトリの作成
		File tempDir = new File(TEMP_PATH);
		if(tempDir.mkdir()){
			log.debug(MARKER_SSFMP, "{} [TEMP_PATH] Create {} : SUCCESS", Thread.currentThread().getStackTrace()[1].getMethodName(), TEMP_PATH);
			// capturedTimeShifted の場合のみ，予めファイルを作成しておく．
			if(modeLive.equals("capturedTimeShifted")){
				File f = new File(TEMP_PATH + FILE_SEPARATOR + "ssfmp"+videoBitrate+".ts");
				try {
					if(f.createNewFile()){
						log.debug(MARKER_SSFMP, "{} [TEMP_PATH] Create temp file in {} : SUCCESS", Thread.currentThread().getStackTrace()[1].getMethodName(), TEMP_PATH);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}else{

		}
		
		// 分割されたストリーミング用 MPEG2-TS を配置するためのトップディレクトリの作成
		STREAM_PATH_ROOT = STREAM_PATH_ROOT + FILE_SEPARATOR + "stream";
		info.setStreamPathRoot(STREAM_PATH_ROOT);
		log.debug(MARKER_SSFMP, "{} [STREAM_PATH_ROOT] {}", Thread.currentThread().getStackTrace()[1].getMethodName(), STREAM_PATH_ROOT);
		
		File streamDir = new File(STREAM_PATH_ROOT);
		if(streamDir.mkdir()){ 
			log.debug(MARKER_SSFMP, "{} [STREAM_PATH_ROOT] Create {} : SUCCESS", Thread.currentThread().getStackTrace()[1].getMethodName(), STREAM_PATH_ROOT);
		}else{

		}
		
		// 分割されたストリーミング用 MPEG2-TS を配置するためのトップディレクトリにインデックスファイルを作成
		File contentRootDirIndex = new File(STREAM_PATH_ROOT + FILE_SEPARATOR + "index.html");
		try {
			if(contentRootDirIndex.createNewFile()){
				log.debug(MARKER_SSFMP, "{} [STREAM_PATH_ROOT] Create {}{}index.html : SUCCESS", Thread.currentThread().getStackTrace()[1].getMethodName(), STREAM_PATH_ROOT, FILE_SEPARATOR);
			}else{

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Waiting サーブレットへ送る情報
		String[] strForWaiting = new String[5];
		
		// 分割されたストリーミング用 MPEG2-TS を実際に配置するためのディレクトリの名前の設定 (STREAM_PATH_ROOT の下に作成)
		// 加えて，その名前を strForWaitPl[0] に代入．
		if(modeLive.equals("camV") || modeLive.equals("camAV") || modeLive.equals("cap")){
			STREAM_PATH = STREAM_PATH_ROOT + FILE_SEPARATOR + "live";
			TEMP_PATH_FOR_ENC = TEMP_PATH + FILE_SEPARATOR + "live";
			strForWaiting[0] = "live";
		}else if(modeLive.equals("file")){
			STREAM_PATH = STREAM_PATH_ROOT + FILE_SEPARATOR + file;
			TEMP_PATH_FOR_ENC = TEMP_PATH + FILE_SEPARATOR + file;
			strForWaiting[0] = file;
		}else if(modeLive.equals("captured") || modeLive.equals("capturedTimeShifted")){
			STREAM_PATH = STREAM_PATH_ROOT + FILE_SEPARATOR + capFile;
			TEMP_PATH_FOR_ENC = TEMP_PATH + FILE_SEPARATOR + capFile;
			strForWaiting[0] = capFile;
		}
		
		// 分割されたストリーミング用 MPEG2-TS を実際に配置するためのディレクトリの作成
		File contentDir = new File(STREAM_PATH);
		if(contentDir.mkdir()){
			log.debug(MARKER_SSFMP, "{} [STREAM_PATH] Create {} : SUCCESS", Thread.currentThread().getStackTrace()[1].getMethodName(), STREAM_PATH);
		}else{

		}
		
		// 分割されたストリーミング用 MPEG2-TS を実際に配置するためのディレクトリにインデックスファイルを作成
		File contentDirIndex = new File(STREAM_PATH + FILE_SEPARATOR + "index.html");
		try {
			if(contentDirIndex.createNewFile()){
				log.debug(MARKER_SSFMP, "{} [STREAM_PATH] Create {}{}index.html : SUCCESS", Thread.currentThread().getStackTrace()[1].getMethodName(), STREAM_PATH, FILE_SEPARATOR);
			}else{

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// 分割されたストリーミング用 MPEG2-TS を実際に配置するためのディレクトリの作成 (さらにビットレート)
		File contentDir1 = new File(STREAM_PATH + FILE_SEPARATOR + videoBitrate);
		if(contentDir1.mkdir()){
			log.debug(MARKER_SSFMP, "{} [STREAM_PATH] Create {} : SUCCESS", Thread.currentThread().getStackTrace()[1].getMethodName(), STREAM_PATH + FILE_SEPARATOR + videoBitrate);
		}else{

		}
				
		// 分割されたストリーミング用 MPEG2-TS を実際に配置するためのディレクトリにインデックスファイルを作成 (さらにビットレート)
		File contentDirIndex1 = new File(STREAM_PATH + FILE_SEPARATOR + videoBitrate + FILE_SEPARATOR + "index.html");
		try {
			if(contentDirIndex1.createNewFile()){
				log.debug(MARKER_SSFMP, "{} [STREAM_PATH] Create {}{}index.html : SUCCESS", Thread.currentThread().getStackTrace()[1].getMethodName(), STREAM_PATH + FILE_SEPARATOR + videoBitrate, FILE_SEPARATOR);
			}else{

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// x264 でエンコード or トランスコードする際のスレッド数の設定
		// 割と感覚的に決めている．
		// TODO 4スレッド未満の CPU のために CPU 数をプロパティファイル等で読み込ませる方が良いかも．
		String thread = "2";
		int videoBitrateInt = Integer.parseInt(videoBitrate);
		if(videoBitrateInt <= 500){
			thread = "1";
		}else if(videoBitrateInt <= 1500){
			thread = "2";
		}else if(videoBitrateInt <= 3000){
			thread = "3";
		}else if(videoBitrateInt <= 6000){
			thread = "4";
		}else{
			thread = "0";
		}
		log.debug(MARKER_SSFMP, "{} [thread] {}", Thread.currentThread().getStackTrace()[1].getMethodName(), thread);
		
		// タイマーの設定 : Segmenter [ms]
		// TODO パラメータを詰める
		long timerSegmenterDelay = (long)(DURATION * 1000 * (URI_IN_PLAYLIST - 1));
		
		if(modeLive.equals("camV") || modeLive.equals("camAV")){
			timerSegmenterDelay = (long)(DURATION * 1000 * URI_IN_PLAYLIST);
		}
		
		if(modeLive.equals("file") || modeLive.equals("captured")){
			//timerSegmenterDelay = (long)(DURATION * 1000 * (URI_IN_PLAYLIST));
		}
		
		if(modeLive.equals("cap")){
			timerSegmenterDelay = (long)(DURATION * 1000 * (URI_IN_PLAYLIST)) + 1000;
		}
		
		if(modeLive.equals("capturedTimeShifted")){
			timerSegmenterDelay = 1000;
		}
		
		long timerSegmenterPeriod = (long)(DURATION * 1000);
		
		// タイマーの設定 : Playlister [ms]
		// TODO パラメータを詰める
		long timerPlaylisterDelay = timerSegmenterDelay + (Math.round(DURATION) * 1000 * URI_IN_PLAYLIST + 1000);
		
		if(modeLive.equals("capturedTimeShifted")){
			timerPlaylisterDelay = Math.round(DURATION) * 1000 * 4;
		}
		
		long timerPlaylisterPeriod = (long)(DURATION * 1000);
		
		log.debug(MARKER_SSFMP, "{} [timerSegmenterDelay] {}", Thread.currentThread().getStackTrace()[1].getMethodName(), timerSegmenterDelay);
		log.debug(MARKER_SSFMP, "{} [timerSegmenterPeriod] {}", Thread.currentThread().getStackTrace()[1].getMethodName(), timerSegmenterPeriod);
		log.debug(MARKER_SSFMP, "{} [timerPlaylisterDelay] {}", Thread.currentThread().getStackTrace()[1].getMethodName(), timerPlaylisterDelay);
		log.debug(MARKER_SSFMP, "{} [timerPlaylisterPeriod] {}", Thread.currentThread().getStackTrace()[1].getMethodName(), timerPlaylisterPeriod);
		
		strForWaiting[1] = Integer.toString(((int)timerPlaylisterDelay) / 1000); // for meta refresh (timerPlaylisterDelay + 1) [s]
		strForWaiting[2] = Integer.toString((int)timerPlaylisterDelay / 1000); // timerPlaylisterDelay [s]
		strForWaiting[3] = info.getNamePl();
		strForWaiting[4] = videoBitrate;
		
		// 各スレッドを実行するクラスを呼ぶ．
		// TODO 効率的，効果的なスレッドの管理方法を．
		ThreadHandler th = new ThreadHandler(
			prop.getValue("FFMPEG_PATH_FOR_CAM"),
			prop.getValue("FFMPEG_PATH_FOR_FILE"),
			prop.getValue("FFMPEG_PATH_FOR_CAP"),
			prop.getValue("CAPTURE_PROGRAM_PATH"),
			
			STREAM_PATH + FILE_SEPARATOR + videoBitrate,
			TEMP_PATH,
			TEMP_PATH_FOR_ENC + FILE_SEPARATOR + videoBitrate,
			FILE_PATH,
			CAP_PATH,
			
			modeLive,
			file,
			capFile,
			capResolution,
			videoResolution,
			videoProfile,
			videoBitrate,
			audioCodec,
			audioBitrate,
			audioSamplingFreq,
			durationLive,
			encOrNot,
			id,
			thread,
			0,
			
			MPEG2_TS_PACKET_LENGTH,
			DURATION,
			URI_IN_PLAYLIST,
			
			timerSegmenterDelay,
			timerSegmenterPeriod,
			timerPlaylisterDelay,
			timerPlaylisterPeriod
		);
		
		Thread thThread = new Thread(th, "__ThreadHandler__");
		thThread.start();
		log.debug(MARKER_SSFMP, "{} -> ThreadHandler", Thread.currentThread().getStackTrace()[1].getMethodName());
		
		// サーブレットコンテキストに String[] な strForWaiting を入れる．
		sc.setAttribute("strForWaiting", strForWaiting);
		
		// Waiting サーブレットにリダイレクトする．
		// ディスパッチではない理由は，iPhone での再生において，プレーヤから Safari に戻る際に都合が悪いため．
		try {
			res.sendRedirect("wait");
			log.debug(MARKER_SSFMP, "{} sendRedirect -> Waiting", Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	} // doGet()
	
    boolean isNotParamNullOrBlank(String str){
        boolean ret = false;
        if((str != null) && !str.equals("")){
            ret = true;
        }
        return ret;
    }
    
    @SuppressWarnings("unused")
	boolean isInteger(String str){
        try{
            int n = Integer.parseInt(str);
            return true;
        }catch(NumberFormatException e){
            return false;
	}
    }

} // class
