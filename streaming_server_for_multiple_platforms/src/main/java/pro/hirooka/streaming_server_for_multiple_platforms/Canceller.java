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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class Canceller{
	
	private static Logger log = LoggerFactory.getLogger(Canceller.class);
	private static Marker MARKER_Canceller = MarkerFactory.getMarker("Canceller");
	
	private int flagAbs;
	
	Canceller(int _flagAbs){
		this.flagAbs = _flagAbs;
		log.debug(MARKER_Canceller, "{} Construcor of Calceller : {}", Thread.currentThread().getStackTrace()[1].getMethodName(), flagAbs);
	} // Constructor

	public void doCancel(){
		
		log.debug(MARKER_Canceller, "{}", Thread.currentThread().getStackTrace()[1].getMethodName());
		
		SingletonForSSFMP info = SingletonForSSFMP.getInstance();
		
		SingletonForSSFMP2 info2 = null;
		SingletonForSSFMP3 info3 = null;
		if(flagAbs == 1){
			info2 = SingletonForSSFMP2.getInstance();
			info3 = SingletonForSSFMP3.getInstance();
		}
		
		// Segmenter と Playlister のタイマーキャンセルフラグの設定
		// フラグを 1 に設定すると，スケジュールタイマー後の While ループを抜ける．
		info.setFlagTimerSegmenter(1);
		info.setFlagTimerPlaylister(1);
		log.debug(MARKER_Canceller, "{} [info.getFlagTimerSegmenter()] {}", Thread.currentThread().getStackTrace()[1].getMethodName(), info.getFlagTimerSegmenter());
		log.debug(MARKER_Canceller, "{} [info.getFlagTimerPlaylister()] {}", Thread.currentThread().getStackTrace()[1].getMethodName(), info.getFlagTimerPlaylister());
		
		if(info.getModeLive().equals("capturedTimeShifted")){
			info.setFlagRemoveFile(1);
			log.debug(MARKER_Canceller, "{} capturedTimeShifted - setFlagRemoveFile(1)", Thread.currentThread().getStackTrace()[1].getMethodName());
		}
		
		// ABS 時，すべての Segmenter と Playlister のタイマーキャンセルフラグの設定
		if(flagAbs == 1){
			info2.setFlagTimerSegmenter(1);
			info2.setFlagTimerPlaylister(1);
			log.debug(MARKER_Canceller, "{} [info2.getFlagTimerSegmenter()] {}", Thread.currentThread().getStackTrace()[1].getMethodName(), info2.getFlagTimerSegmenter());
			log.debug(MARKER_Canceller, "{} [info2.getFlagTimerPlaylister()] {}", Thread.currentThread().getStackTrace()[1].getMethodName(), info2.getFlagTimerPlaylister());
			
			if(info.getModeLive().equals("capturedTimeShifted")){
				info2.setFlagRemoveFile(1);
				log.debug(MARKER_Canceller, "{} capturedTimeShifted - setFlagRemoveFile(1)", Thread.currentThread().getStackTrace()[1].getMethodName());
			}
			
			info3.setFlagTimerSegmenter(1);
			info3.setFlagTimerPlaylister(1);
			log.debug(MARKER_Canceller, "{} [info3.getFlagTimerSegmenter()] {}", Thread.currentThread().getStackTrace()[1].getMethodName(), info3.getFlagTimerSegmenter());
			log.debug(MARKER_Canceller, "{} [info3.getFlagTimerPlaylister()] {}", Thread.currentThread().getStackTrace()[1].getMethodName(), info3.getFlagTimerPlaylister());
			
			if(info.getModeLive().equals("capturedTimeShifted")){
				info3.setFlagRemoveFile(1);
				log.debug(MARKER_Canceller, "{} capturedTimeShifted - setFlagRemoveFile(1)", Thread.currentThread().getStackTrace()[1].getMethodName());
			}
		}
		
		// FFmpeg を停止させる．
		FFmpegStopper sf = new FFmpegStopper();
		Thread sfThread = new Thread(sf);
		sfThread.start();
		log.debug(MARKER_Canceller, "{} -> StopFFmpeg", Thread.currentThread().getStackTrace()[1].getMethodName());
		
	} // doCancel()

} // class