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
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

class Playlister extends TimerTask{
	
	static final String FILE_SEPARATOR = System.getProperty("file.separator");
	
	private static Logger log = LoggerFactory.getLogger(Playlister.class);
	private static Marker MARKER_Playlister = MarkerFactory.getMarker("Playlister");
	
	private String streamPath;
	
	private String encOrNot;
	private int abs;
	
	private double DURATION;
	private int URI_IN_PLAYLIST;
	
	Playlister(
			
		String _streamPath,
		
		String _encOrNot,
		int _abs,
		
		double _DURATION,
		int _URI_IN_PLAYLIST
		){
		
		this.streamPath = _streamPath;
		
		this.encOrNot = _encOrNot;
		this.abs = _abs;
		
		this.DURATION = _DURATION;
		this.URI_IN_PLAYLIST = _URI_IN_PLAYLIST;
		
		log.debug(MARKER_Playlister, "{} Construcor of Playlister : {}", Thread.currentThread().getStackTrace()[1].getMethodName(), abs);
		
	} // Constructor

	public void run(){
		
		SingletonForSSFMP info = null;
		SingletonForSSFMP2 info2 = null;
		SingletonForSSFMP3 info3 = null;
		
		switch(abs){
		case 0:
			info = SingletonForSSFMP.getInstance();
			break;
		case 1:
			info2 = SingletonForSSFMP2.getInstance();
			break;
		case 2:
			info3 = SingletonForSSFMP3.getInstance();
			break;
		default:
			//info = SingletonForMyStreamer.getInstance();
			break;
		}
		
		try {
			
			log.debug(MARKER_Playlister, "{} [STREAM_PATH] {}", Thread.currentThread().getStackTrace()[1].getMethodName(), streamPath);
			
			int seqPl = 0;
			String namePl = "";
			if((abs == 0) && (info != null)){
				seqPl = info.getSeqPl();
				namePl = info.getNamePl();
			}else if((abs == 1) && (info2 != null)){
				seqPl = info2.getSeqPl();
				namePl = info2.getNamePl();
			}else if((abs == 2) && (info3 != null)){
				seqPl = info3.getSeqPl();
				namePl = info3.getNamePl();
			}

			log.debug(MARKER_Playlister, "{} Begin : generation of seqPl : {}, abs {}", Thread.currentThread().getStackTrace()[1].getMethodName(), seqPl, abs);
			
			// Cam or Cap : 最新シーケンスのみ
			// File       : すべてのシーケンス
				
			if(encOrNot.equals("1") || encOrNot.equals("3")){
				
				File f = new File(streamPath + FILE_SEPARATOR + namePl);
				FileWriter fw = new FileWriter(f);
				BufferedWriter bw = new BufferedWriter(fw);
				
				bw.write("#EXTM3U");
				bw.newLine();
				bw.write("#EXT-X-VERSION:3");
				bw.newLine();
				bw.write("#EXT-X-TARGETDURATION:" + Long.toString(Math.round(DURATION)));
				bw.newLine();
				
				if(encOrNot.equals("1")){
					bw.write("#EXT-X-MEDIA-SEQUENCE:" + seqPl);
				}else if(encOrNot.equals("3")){
					bw.write("#EXT-X-MEDIA-SEQUENCE:0");
				}else{
					
				}
				
				bw.newLine();
				
				int initSeqPl = 0;
				if(encOrNot.equals("1")){
					initSeqPl = seqPl;
				}else if(encOrNot.equals("3")){
					initSeqPl = 0;
				}else{
					
				}
				
				for(int i = initSeqPl; i < (seqPl + URI_IN_PLAYLIST); i++){
					bw.write("#EXT-X-KEY:METHOD=AES-128,URI=");
					
					if((abs == 0) && (info != null)){
						bw.write("\"" + "" + info.getKeyArrayList(i) + i + ".key\"" + ",IV=0x");
						bw.write(info.getIvArrayList(i));
					}else if((abs == 1) && (info2 != null)){
						bw.write("\"" + "" + info2.getKeyArrayList(i) + i + ".key\"" + ",IV=0x");
						bw.write(info2.getIvArrayList(i));
					}else if((abs == 2) && (info3 != null)){
						bw.write("\"" + "" + info3.getKeyArrayList(i) + i + ".key\"" + ",IV=0x");
						bw.write(info3.getIvArrayList(i));
					}
					
					bw.newLine();
					bw.write("#EXTINF:" + Double.toString(DURATION) + ",");
					bw.newLine();
					bw.write("fileSequenceEnc" + i + ".ts");
					bw.newLine();
				}
				
				if((abs == 0) && (info != null) && info.getFlagLastTs()){
					if(seqPl >= (info.getSeqTsLast() - (URI_IN_PLAYLIST - 1))){
						bw.write("#EXT-X-ENDLIST");
						info.setFlagLastPl(1);
					}
				}
				if((abs == 1) && (info2 != null) && info2.getFlagLastTs()){
					if(seqPl >= (info2.getSeqTsLast() - (URI_IN_PLAYLIST - 1))){
						bw.write("#EXT-X-ENDLIST");
						info2.setFlagLastPl(1);
					}
				}
				if((abs == 2) && (info3 != null) && info3.getFlagLastTs()){
					if(seqPl >= (info3.getSeqTsLast() - (URI_IN_PLAYLIST - 1))){
						bw.write("#EXT-X-ENDLIST");
						info3.setFlagLastPl(1);
					}
				}
				bw.close();
				fw.close();
				log.debug(MARKER_Playlister, "{} End : generation of seqPl : {}", Thread.currentThread().getStackTrace()[1].getMethodName(), seqPl);
				
				if((abs == 0) && (info != null) && info.getFlagLastPl()){
					info.setFlagTimerPlaylister(1);
					log.debug(MARKER_Playlister, "{} {} : ALL GENERATION FINISHED!!!", Thread.currentThread().getStackTrace()[1].getMethodName(), abs);
				}
				if((abs == 1) && (info2 != null) && info2.getFlagLastPl()){
					info2.setFlagTimerPlaylister(1);
					log.debug(MARKER_Playlister, "{} {} : ALL GENERATION FINISHED!!!", Thread.currentThread().getStackTrace()[1].getMethodName(), abs);
				}
				if((abs == 2) && (info3 != null) && info3.getFlagLastPl()){
					info3.setFlagTimerPlaylister(1);
					log.debug(MARKER_Playlister, "{} {} : ALL GENERATION FINISHED!!!", Thread.currentThread().getStackTrace()[1].getMethodName(), abs);
				}
				
			}else if(encOrNot.equals("2") || encOrNot.equals("4")){
				
				File f = new File(streamPath + FILE_SEPARATOR + namePl);
				FileWriter fw = new FileWriter(f);
				BufferedWriter bw = new BufferedWriter(fw);
				
				bw.write("#EXTM3U");
				bw.newLine();
				bw.write("#EXT-X-VERSION:3");
				bw.newLine();
				bw.write("#EXT-X-TARGETDURATION:" + Long.toString(Math.round(DURATION)));
				bw.newLine();
				
				if(encOrNot.equals("2")){
					bw.write("#EXT-X-MEDIA-SEQUENCE:" + seqPl);
				}else if(encOrNot.equals("4")){
					bw.write("#EXT-X-MEDIA-SEQUENCE:0");
				}else{
					
				}
				
				bw.newLine();
				
				int initSeqPl = 0;
				if(encOrNot.equals("2")){
					initSeqPl = seqPl;
				}else if(encOrNot.equals("4")){
					initSeqPl = 0;
				}else{
					
				}
				
				for(int i = initSeqPl; i < (seqPl + URI_IN_PLAYLIST); i++){
					bw.write("#EXTINF:" + Double.toString(DURATION) + ",");
					bw.newLine();
					bw.write("fileSequence" + i + ".ts");
					bw.newLine();
				}
				
				if((abs == 0) && (info != null) && info.getFlagLastTs()){
					if(seqPl >= (info.getSeqTsLast() - (URI_IN_PLAYLIST - 1))){
						bw.write("#EXT-X-ENDLIST");
						info.setFlagLastPl(1);
					}
				}
				if((abs == 1) && (info2 != null) && info2.getFlagLastTs()){
					if(seqPl >= (info2.getSeqTsLast() - (URI_IN_PLAYLIST - 1))){
						bw.write("#EXT-X-ENDLIST");
						info2.setFlagLastPl(1);
					}
				}
				if((abs == 2) && (info3 != null) && info3.getFlagLastTs()){
					if(seqPl >= (info3.getSeqTsLast() - (URI_IN_PLAYLIST - 1))){
						bw.write("#EXT-X-ENDLIST");
						info3.setFlagLastPl(1);
					}
				}
				bw.close();
				fw.close();
				log.debug(MARKER_Playlister, "{} End : generation of seqPl : {}", Thread.currentThread().getStackTrace()[1].getMethodName(), seqPl);
				
				if((abs == 0) && (info != null) && info.getFlagLastPl()){
					info.setFlagTimerPlaylister(1);
					log.debug(MARKER_Playlister, "{} {} : ALL GENERATION FINISHED!!!", Thread.currentThread().getStackTrace()[1].getMethodName(), abs);
				}
				if((abs == 1) && (info2 != null) && info2.getFlagLastPl()){
					info2.setFlagTimerPlaylister(1);
					log.debug(MARKER_Playlister, "{} {} : ALL GENERATION FINISHED!!!", Thread.currentThread().getStackTrace()[1].getMethodName(), abs);
				}
				if((abs == 2) && (info3 != null) && info3.getFlagLastPl()){
					info3.setFlagTimerPlaylister(1);
					log.debug(MARKER_Playlister, "{} {} : ALL GENERATION FINISHED!!!", Thread.currentThread().getStackTrace()[1].getMethodName(), abs);
				}
				
			} else {

			}
				
			seqPl++;
			if((abs == 0) && (info != null)){
				info.setSeqPl(seqPl);	
			}
			if((abs == 1) && (info2 != null)){
				info2.setSeqPl(seqPl);	
			}
			if((abs == 2)  && (info3 != null)){
				info3.setSeqPl(seqPl);	
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} // try
		
	} // run()
	
} // class