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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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

/*
 * このクラスは EXPERIMENTAL
 */
@WebServlet(name="SSFMPAbs", urlPatterns={"/ss_abs"})
public class SSFMPAbs extends HttpServlet{
	
	private static final long serialVersionUID = 89583920627175713L;
	static final String FILE_SEPARATOR = System.getProperty("file.separator");

	private static Logger log = LoggerFactory.getLogger(SSFMPAbs.class);
	private static Marker MARKER_SSFMPAbs = MarkerFactory.getMarker("SSFMPAbs");
	
	private final String ERROR_HTML = "error.html";
	
	protected void doGet(HttpServletRequest req, HttpServletResponse res){
		
		PropertiesLoader prop = new PropertiesLoader();
        prop.setProperties("ssfmd.properties");
        
		// シングルトンクラスをグローバル変数的に使用する．
		SingletonForSSFMP info = SingletonForSSFMP.getInstance();
		SingletonForSSFMP2 info2 = SingletonForSSFMP2.getInstance();
		SingletonForSSFMP3 info3 = SingletonForSSFMP3.getInstance();
		info.init();
		info2.init();
		info3.init();
		int NUM_FOR_ABS = Integer.parseInt(prop.getValue("NUM_FOR_ABS"));
		
		// 後に Waiting サーブレットに対して値を渡すためのサーブレットコンテキストの生成
		ServletContext sc = null;
		sc = getServletContext();
		
		// プロパティファイルから定数情報の取得
		// TODO エラー処理
		int MPEG2_TS_PACKET_LENGTH = Integer.parseInt(prop.getValue("MPEG2_TS_PACKET_LENGTH"));
		double DURATION = Double.parseDouble(prop.getValue("DURATION"));
		int URI_IN_PLAYLIST = Integer.parseInt(prop.getValue("URI_IN_PLAYLIST"));
		log.debug(MARKER_SSFMPAbs, "{} [MPEG2_TS_PACKET_LENGTH] {}", Thread.currentThread().getStackTrace()[1].getMethodName(), MPEG2_TS_PACKET_LENGTH);
		log.debug(MARKER_SSFMPAbs, "{} [DURATION] {}", Thread.currentThread().getStackTrace()[1].getMethodName(), DURATION);
		log.debug(MARKER_SSFMPAbs, "{} [URI_IN_PLAYLIST] {}", Thread.currentThread().getStackTrace()[1].getMethodName(), URI_IN_PLAYLIST);
		
		// HTML, JSP の GUI から送られてくる GET パラメータ
		// TODO パラメータのチェック処理
		String modeLive = req.getParameter("modeLive");
		String file = req.getParameter("file");
		//file = new String(file.getBytes("ISO-8859-1"), "utf-8"); // for GlassFish 4.0
		String capFile = req.getParameter("capFile");
		String capResolution = req.getParameter("capResolution");
		String videoResolution = req.getParameter("videoResolution");
		String videoProfile = req.getParameter("videoProfile");
		
		// TODO 今のところビットレートのみ条件を変更する．HTML フォームからのビットレート値を配列として受け渡したい．
		String[] videoBitrate = new String[NUM_FOR_ABS];
		videoBitrate[0] = req.getParameter("videoBitrate");
		videoBitrate[1] = req.getParameter("videoBitrate2");
		videoBitrate[2] = req.getParameter("videoBitrate3");
		if(videoBitrate[0] != null){
			try {
				res.sendRedirect(ERROR_HTML);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		String audioCodec = req.getParameter("audioCodec");
		String audioBitrate = req.getParameter("audioBitrate");
		String audioSamplingFreq = req.getParameter("audioSamplingFreq");
		String durationLive = req.getParameter("durationLive");
		String encOrNot = req.getParameter("encOrNot");
		String id = req.getParameter("id");
		
		if(modeLive != null){
			info.setModeLive(modeLive);
			info2.setModeLive(modeLive);
			info3.setModeLive(modeLive);
		}else{
			try {
				res.sendRedirect(ERROR_HTML);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		// ディレクトリ情報 (一部プロパティファイルから取得)
		String STREAM_PATH_ROOT = this.getServletContext().getRealPath(".");
		String STREAM_PATH = null;
		String TEMP_PATH = prop.getValue("PATH_OF_TEMP");
		String TEMP_PATH_FOR_ENC = null;
		String FILE_PATH = prop.getValue("PATH_OF_FILE_LOCAL");
		String CAP_PATH = prop.getValue("PATH_OF_CAPTURED_LOCAL");
		
		// エンコード or トランスコードされた MPEG2-TS を一時的に保存するためのテンポラリなディレクトリの作成
		File tempDir = new File(TEMP_PATH);
		if(tempDir.mkdir()){
			log.debug(MARKER_SSFMPAbs, "{} [TEMP_PATH] Create {} : SUCCESS", Thread.currentThread().getStackTrace()[1].getMethodName(), TEMP_PATH);
			// capturedTimeShifted の場合のみ，予めファイルを作成しておく．
			for(int i = 0; i < NUM_FOR_ABS; i++){
				if(modeLive.equals("capturedTimeShifted")){
					File f = new File(TEMP_PATH + FILE_SEPARATOR + "ssfmp"+videoBitrate[i]+".ts");
					try {
						if(f.createNewFile()){
							log.debug(MARKER_SSFMPAbs, "{} [TEMP_PATH] Create temp file in {} : SUCCESS", Thread.currentThread().getStackTrace()[1].getMethodName(), TEMP_PATH);
						}
					} catch (IOException e) {
						e.printStackTrace();
						try {
							res.sendRedirect(ERROR_HTML);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		}else{
			try {
				res.sendRedirect(ERROR_HTML);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		// 分割されたストリーミング用 MPEG2-TS を配置するためのトップディレクトリの作成
		STREAM_PATH_ROOT = STREAM_PATH_ROOT + FILE_SEPARATOR + "stream";
		info.setStreamPathRoot(STREAM_PATH_ROOT);
		info2.setStreamPathRoot(STREAM_PATH_ROOT);
		info3.setStreamPathRoot(STREAM_PATH_ROOT);

		File streamDir = new File(STREAM_PATH_ROOT);
		if(streamDir.mkdir()){ 
			log.debug(MARKER_SSFMPAbs, "{} [STREAM_PATH_ROOT] Create {} : SUCCESS", Thread.currentThread().getStackTrace()[1].getMethodName(), STREAM_PATH_ROOT);
		}else{
			try {
				res.sendRedirect(ERROR_HTML);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		// 分割されたストリーミング用 MPEG2-TS を配置するためのトップディレクトリにインデックスファイルを作成
		File contentRootDirIndex = new File(STREAM_PATH_ROOT + FILE_SEPARATOR + "index.html");
		try {
			if(contentRootDirIndex.createNewFile()){
				log.debug(MARKER_SSFMPAbs, "{} [STREAM_PATH_ROOT] Create {}{}index.html : SUCCESS", Thread.currentThread().getStackTrace()[1].getMethodName(), STREAM_PATH_ROOT, FILE_SEPARATOR);
			}else{
				try {
					res.sendRedirect(ERROR_HTML);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			try {
				res.sendRedirect(ERROR_HTML);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
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
			log.debug(MARKER_SSFMPAbs, "{} [STREAM_PATH] Create {} : SUCCESS", Thread.currentThread().getStackTrace()[1].getMethodName(), STREAM_PATH);
		}else{
			try {
				res.sendRedirect(ERROR_HTML);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		// 分割されたストリーミング用 MPEG2-TS を実際に配置するためのディレクトリにインデックスファイルを作成
		File contentDirIndex = new File(STREAM_PATH + FILE_SEPARATOR + "index.html");
		try {
			if(contentDirIndex.createNewFile()){
				log.debug(MARKER_SSFMPAbs, "{} [STREAM_PATH] Create {}{}index.html : SUCCESS", Thread.currentThread().getStackTrace()[1].getMethodName(), STREAM_PATH, FILE_SEPARATOR);
			}else{
				try {
					res.sendRedirect(ERROR_HTML);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			try {
				res.sendRedirect(ERROR_HTML);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
		// 分割されたストリーミング用 MPEG2-TS を実際に配置するためのディレクトリの作成 (さらにビットレート)
		File[] contentDirAbs = new File[NUM_FOR_ABS];
		for(int i = 0; i < contentDirAbs.length; i++){
			if(videoBitrate[i] != null){
				contentDirAbs[i] = new File(STREAM_PATH + FILE_SEPARATOR + videoBitrate[i]);
				if(contentDirAbs[i].mkdir()){
					log.debug(MARKER_SSFMPAbs, "{} [STREAM_PATH] Create {} : SUCCESS", Thread.currentThread().getStackTrace()[1].getMethodName(), STREAM_PATH + FILE_SEPARATOR + videoBitrate[i]);
				}else{
					try {
						res.sendRedirect(ERROR_HTML);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
				
		// 分割されたストリーミング用 MPEG2-TS を実際に配置するためのディレクトリにインデックスファイルを作成 (さらにビットレート)
		File[] contentDirIndexAbs = new File[NUM_FOR_ABS];
		for(int i = 0; i < contentDirIndexAbs.length; i++){
			if(videoBitrate[i] != null){
				contentDirIndexAbs[i] = new File(STREAM_PATH + FILE_SEPARATOR + videoBitrate[i] + FILE_SEPARATOR + "index.html");
				try {
					if(contentDirIndexAbs[i].createNewFile()){
						log.debug(MARKER_SSFMPAbs, "{} [STREAM_PATH] Create {}{}index.html : SUCCESS", Thread.currentThread().getStackTrace()[1].getMethodName(), STREAM_PATH + FILE_SEPARATOR + videoBitrate[i], FILE_SEPARATOR);
					}else{
						try {
							res.sendRedirect(ERROR_HTML);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
					try {
						res.sendRedirect(ERROR_HTML);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		}
		
		// x264 でエンコード or トランスコードする際のスレッド数の設定
		// 割と感覚的に決めている．
		// TODO 4スレッド未満の CPU のために CPU 数をプロパティファイル等で読み込ませる方が良いかも．
		String thread = "2";
		for(int i = 0; i < NUM_FOR_ABS; i++){
			if(videoBitrate[i] != null){
				int videoBitrateInt = Integer.parseInt(videoBitrate[i]);
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
				log.debug(MARKER_SSFMPAbs, "{} [thread] {}", Thread.currentThread().getStackTrace()[1].getMethodName(), thread);
			}
		}
		
		// タイマーの設定 : Segmenter [ms]
		// TODO パラメータを詰める
		long timerSegmenterDelay = (long)(DURATION * 1000 * (URI_IN_PLAYLIST - 1));
		
		if(modeLive.equals("camV") || modeLive.equals("camAV")){
			timerSegmenterDelay = (long)(DURATION * 1000 * URI_IN_PLAYLIST);
		}
		
		if(modeLive.equals("file") || modeLive.equals("captured")){
			timerSegmenterDelay = (long)(DURATION * 1000 * (URI_IN_PLAYLIST));
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
		
		log.debug(MARKER_SSFMPAbs, "{} [timerSegmenterDelay] {}", Thread.currentThread().getStackTrace()[1].getMethodName(), timerSegmenterDelay);
		log.debug(MARKER_SSFMPAbs, "{} [timerSegmenterPeriod] {}", Thread.currentThread().getStackTrace()[1].getMethodName(), timerSegmenterPeriod);
		log.debug(MARKER_SSFMPAbs, "{} [timerPlaylisterDelay] {}", Thread.currentThread().getStackTrace()[1].getMethodName(), timerPlaylisterDelay);
		log.debug(MARKER_SSFMPAbs, "{} [timerPlaylisterPeriod] {}", Thread.currentThread().getStackTrace()[1].getMethodName(), timerPlaylisterPeriod);
		
		strForWaiting[1] = Integer.toString(((int)timerPlaylisterDelay) / 1000); // for meta refresh (timerPlaylisterDelay + 1) [s]
		strForWaiting[2] = Integer.toString((int)timerPlaylisterDelay / 1000); // timerPlaylisterDelay [s]
		strForWaiting[3] = ""; // namePl()
		strForWaiting[4] = ""; // videoBitrate
		
		// 各スレッドを実行するクラスを呼ぶ．
		// TODO 効率的，効果的なスレッドの管理方法を．
		ThreadHandler[] th = new ThreadHandler[NUM_FOR_ABS];
		for(int i = 0; i < NUM_FOR_ABS; i++){
			if(videoBitrate[i] != null){
				//ThreadHandler th = new ThreadHandler(
				th[i] = new ThreadHandler(
						prop.getValue("FFMPEG_PATH_FOR_CAM"),
						prop.getValue("FFMPEG_PATH_FOR_FILE"),
						prop.getValue("FFMPEG_PATH_FOR_CAP"),
						prop.getValue("CAPTURE_PROGRAM_PATH"),
			
						STREAM_PATH + FILE_SEPARATOR + videoBitrate[i],
						TEMP_PATH,
						TEMP_PATH_FOR_ENC + FILE_SEPARATOR + videoBitrate[i],
						FILE_PATH,
						CAP_PATH,
			
						modeLive,
						file,
						capFile,
						capResolution,
						videoResolution,
						videoProfile,
						videoBitrate[i],
						audioCodec,
						audioBitrate,
						audioSamplingFreq,
						durationLive,
						encOrNot,
						id,
						thread,
						i,
			
						MPEG2_TS_PACKET_LENGTH,
						DURATION,
						URI_IN_PLAYLIST,
			
						timerSegmenterDelay,
						timerSegmenterPeriod,
						timerPlaylisterDelay,
						timerPlaylisterPeriod
				);
				
				Thread thThread = new Thread(th[i], "__ThreadHandler__");
				thThread.start();
				log.debug(MARKER_SSFMPAbs, "{} -> ThreadHandler", Thread.currentThread().getStackTrace()[1].getMethodName());
				
			}
		} // for
		
		// サーブレットコンテキストに String[] な strForWaiting を入れる．
		sc.setAttribute("strForWaiting", strForWaiting);
		
		// Waiting サーブレットにリダイレクトする．
		// ディスパッチではない理由は，iPhone での再生において，プレーヤから Safari に戻る際に都合が悪いため．
		try {
			
			// Variant Playlist の作成
			String nameVariantPl = "pl.m3u8";
			strForWaiting[3] = nameVariantPl;
			String namePl = "pl.m3u8";
			
			String BANDWIDTH = videoBitrate[0] + "000";
			String BANDWIDTH2 = null;
			String BANDWIDTH3 = null;
			if(videoBitrate[1] != null) BANDWIDTH2 = videoBitrate[1] + "000";
			if(videoBitrate[2] != null) BANDWIDTH3 = videoBitrate[2] + "000";
			
			File pl = new File(STREAM_PATH + FILE_SEPARATOR + namePl);
			BufferedWriter bw = new BufferedWriter(new FileWriter(pl));
			bw.write("#EXTM3U");
			bw.newLine();
			bw.write("#EXT-X-STREAM-INF:PROGRAM-ID=1,BANDWIDTH=" + BANDWIDTH);
			bw.newLine();
			bw.write(videoBitrate[0] + FILE_SEPARATOR + namePl);
			bw.newLine();
			log.debug(MARKER_SSFMPAbs, "{} [videoBitrate[0]] {}", Thread.currentThread().getStackTrace()[1].getMethodName(), videoBitrate[0]);
			
			if(videoBitrate[1] != null){
				bw.write("#EXT-X-STREAM-INF:PROGRAM-ID=1,BANDWIDTH=" + BANDWIDTH2);
				bw.newLine();
				bw.write(videoBitrate[1] + FILE_SEPARATOR  + namePl);
				bw.newLine();
				log.debug(MARKER_SSFMPAbs, "{} [videoBitrate[1]] {}", Thread.currentThread().getStackTrace()[1].getMethodName(), videoBitrate[1]);
			}
			if(videoBitrate[2] != null){
				bw.write("#EXT-X-STREAM-INF:PROGRAM-ID=1,BANDWIDTH=" + BANDWIDTH3);
				bw.newLine();
				bw.write(videoBitrate[2] + FILE_SEPARATOR  + namePl);
				bw.newLine();
				log.debug(MARKER_SSFMPAbs, "{} [videoBitrate[2]] {}", Thread.currentThread().getStackTrace()[1].getMethodName(), videoBitrate[2]);
			}
			bw.close();
			
			res.sendRedirect("wait_abs");
			log.debug(MARKER_SSFMPAbs, "{} sendRedirect -> WaitingAbs", Thread.currentThread().getStackTrace()[1].getMethodName());
			
		} catch (IOException e) {
			e.printStackTrace();
			try {
				res.sendRedirect(ERROR_HTML);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} // try
		
	} // doGet()

} // class
