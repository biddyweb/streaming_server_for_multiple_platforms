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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

class FFmpegStopper implements Runnable{
	
	private static Logger log = LoggerFactory.getLogger(FFmpegStopper.class);
	private static Marker MARKER_FFmpegStopper = MarkerFactory.getMarker("FFmpegStopper");
	
	private int flagAbs;
	
	FFmpegStopper(){
		
		log.debug(MARKER_FFmpegStopper, "{} Construcor of FFmpegStopper", Thread.currentThread().getStackTrace()[1].getMethodName());
		
	}
	
	FFmpegStopper(int _flagAbs){
		if(_flagAbs == 1) this.flagAbs = _flagAbs;
	}
	
	public void run(){
		
		SingletonForSSFMP info = SingletonForSSFMP.getInstance();
		
		// Show Process
		String[] cmdArray = {
			"ps", "aux"	
		};
		
		ProcessBuilder pb = new ProcessBuilder(cmdArray);
		
		try {
			
			Process pr = pb.start();
			InputStream is = pr.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			
			// Search FFmpeg Process and Identify its Process ID
			String s = "";
			while((s = br.readLine()) != null){
				log.debug(MARKER_FFmpegStopper, "{} {}", Thread.currentThread().getStackTrace()[1].getMethodName(), s);
				String sTrim = s.trim();
				ArrayList<String> arrayPID = null;

				if(sTrim.matches(".*libx264.*") && sTrim.matches(".*mpegts.*") ){
					String[] sTrimSplit = sTrim.split(" ");
					arrayPID = new ArrayList<String>();
					for(int i = 0; i < sTrimSplit.length; i++ ){
						//log.debug(MARKER_StopFFmpeg, "{} {}", Thread.currentThread().getStackTrace()[1].getMethodName(), s);
						//log.debug(MARKER_StopFFmpeg, "{} {}", Thread.currentThread().getStackTrace()[1].getMethodName(), sTrimSplit[i]);
						if(!(sTrimSplit[i].equals(""))){
							arrayPID.add(sTrimSplit[i]);
						}
					}
					String PID = arrayPID.get(1);
					log.debug(MARKER_FFmpegStopper, "{} {}", Thread.currentThread().getStackTrace()[1].getMethodName(), PID);
					stopPID(PID);
				}
			}
			
			br.close();
			isr.close();
			is.close();
			pr.destroy();
			pr = null;
			pb = null;
			
			if(info.getModeLive().equals("capturedTimeShifted") && SingletonForSSFMP.getInstance().getFlagRemoveFile()){
				
				if(flagAbs != 1){
					
					Remover r = new Remover(0);
					r.doRemove();
					log.debug(MARKER_FFmpegStopper, "{} capturedTimeShifted - removeFiles()", Thread.currentThread().getStackTrace()[1].getMethodName());
					
				}else{
					
					if(!SingletonForSSFMP2.getInstance().getVideoBitrate().equals("") && SingletonForSSFMP3.getInstance().getVideoBitrate().equals("")){
						if(SingletonForSSFMP2.getInstance().getFlagRemoveFile()){
							Remover r = new Remover(1);
							r.doRemove();
							log.debug(MARKER_FFmpegStopper, "{} capturedTimeShifted - removeFiles() - ABS", Thread.currentThread().getStackTrace()[1].getMethodName());
						}
					}
					
					if(SingletonForSSFMP2.getInstance().getVideoBitrate().equals("") && !SingletonForSSFMP3.getInstance().getVideoBitrate().equals("")){
						if(SingletonForSSFMP3.getInstance().getFlagRemoveFile()){
							Remover r = new Remover(1);
							r.doRemove();
							log.debug(MARKER_FFmpegStopper, "{} capturedTimeShifted - removeFiles() - ABS", Thread.currentThread().getStackTrace()[1].getMethodName());
						}
					}
					
					if(!SingletonForSSFMP2.getInstance().getVideoBitrate().equals("") && !SingletonForSSFMP3.getInstance().getVideoBitrate().equals("")){
						if(SingletonForSSFMP2.getInstance().getFlagRemoveFile() && SingletonForSSFMP3.getInstance().getFlagRemoveFile()){
							Remover r = new Remover(1);
							r.doRemove();
							log.debug(MARKER_FFmpegStopper, "{} capturedTimeShifted - removeFiles() - ABS", Thread.currentThread().getStackTrace()[1].getMethodName());
						}
					}
					
				} // if(flagAbs
				
			} // if
			
		} catch (IOException e) {
			e.printStackTrace();
		} // try
		
	} // run
	
	void stopPID(String PID) throws IOException{
		
		log.debug(MARKER_FFmpegStopper, "{}", Thread.currentThread().getStackTrace()[1].getMethodName());
		
		SingletonForSSFMP info = SingletonForSSFMP.getInstance();
		SingletonForSSFMP2 info2 = null;
		SingletonForSSFMP3 info3 = null;
		if(flagAbs == 1){
			info2 = SingletonForSSFMP2.getInstance();
			info3 = SingletonForSSFMP3.getInstance();
		}
		
		info.setFlagRemoveFile(1);
		if(info2 != null && !info2.getVideoBitrate().equals("")) info2.setFlagRemoveFile(1);
		if(info3 != null && !info3.getVideoBitrate().equals("")) info3.setFlagRemoveFile(1);
		
		String[] cmdArrayPID = {"kill", "-KILL", PID };
		ProcessBuilder pbPID = new ProcessBuilder(cmdArrayPID);
		@SuppressWarnings("unused")
		Process prPID = pbPID.start();
		//prPID.destroy();
		//prPID = null;
		//pbPID = null;
		
	} // stopPID

} // class StopFFmpeg