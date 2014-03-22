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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class Segmenter extends TimerTask{
	
	static final String FILE_SEPARATOR = System.getProperty("file.separator");
	
	private static Logger log = LoggerFactory.getLogger(Segmenter.class);
	private static Marker MARKER_Segmenter = MarkerFactory.getMarker("Segmenter");
	
	private String FFMPEG_FOR_CAM;
	private String FFMPEG_FOR_FILE;
	
	private String streamPath;
	private String TEMP_PATH;
	private String TEMP_PATH_FOR_ENC;
	private String FILE_PATH;
	private String CAP_PATH;
	
	private String modeLive;
	private String file;
	private String capFile; 
	private String capResolution; 
	private String videoResolution; 
	private String videoProfile;
	private String videoBitrate;
	private String audioCodec;
	private String audioBitrate; 
	private String audioSamplingFreq;
	private String durationLive;
	private String encOrNot;
	private String thread;
	private int abs;
	
	private int MPEG2_TS_PACKET_LENGTH;
	private double DURATION;
	
	private final String SYNC_WORD = "47";
	
	Segmenter(
			String _FFMPEG_FOR_CAM,
			String _FFMPEG_FOR_FILE,
			
			String _streamPath,
			String _TEMP_PATH,
			String _TEMP_PATH_FOR_ENC,
			String _FILE_PATH,
			String _CAP_PATH,
			
			String _modeLive,
			String _file,
			String _capFile, 
			String _capResolution, 
			String _videoResolution, 
			String _videoProfile, 
			String _videoBitrate,
			String _audioCodec, 
			String _audioBitrate, 
			String _audioSamplingFreq,
			String _durationLive, 
			String _encOrNot, 
			String _thread,
			int _abs,
			
			int _MPEG2_TS_PACKET_LENGTH,
			double _DURATION
			){
		
		this.FFMPEG_FOR_CAM = _FFMPEG_FOR_CAM;
		this.FFMPEG_FOR_FILE = _FFMPEG_FOR_FILE;
		
		this.streamPath =_streamPath;
		this.TEMP_PATH = _TEMP_PATH;
		this.TEMP_PATH_FOR_ENC = _TEMP_PATH_FOR_ENC;
		this.FILE_PATH = _FILE_PATH;
		this.CAP_PATH = _CAP_PATH;
		
		this.modeLive = _modeLive;
		this.file = _file;
		this.capFile = _capFile; 
		this.capResolution = _capResolution; 
		this.videoResolution = _videoResolution; 
		this.videoProfile = _videoProfile;
		this.videoBitrate = _videoBitrate;
		this.audioCodec = _audioCodec;
		this.audioBitrate = _audioBitrate; 
		this.audioSamplingFreq = _audioSamplingFreq;
		this.durationLive = _durationLive;
		this.encOrNot = _encOrNot;
		this.thread = _thread;
		this.abs = _abs;
		
		this.MPEG2_TS_PACKET_LENGTH = _MPEG2_TS_PACKET_LENGTH;
		this.DURATION = _DURATION;
		
		log.debug(MARKER_Segmenter, "{} Construcor of Segmenter : {}", Thread.currentThread().getStackTrace()[1].getMethodName(), abs);
	}

	@Override
	public void run() {
		
		log.debug(MARKER_Segmenter, "{} {}", Thread.currentThread().getStackTrace()[1].getMethodName(), abs);
		
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
		
		if((abs == 0) && (info != null)){
		
			log.debug(MARKER_Segmenter, "{} ({}) Read seqTs BEGIN! : {} / {} byte {} --------------------------------------------------", Thread.currentThread().getStackTrace()[1].getMethodName(), videoBitrate, info.getSeqTs(), info.getReadBytes(), abs);
			long[] infoReadData = readPCR(info.getReadBytes(), info.getSeqTs());
			info = SingletonForSSFMP.getInstance();
			info.setReadBytes(infoReadData[0]);
			info.setSeqTs((int)infoReadData[1]);
			log.debug(MARKER_Segmenter, "{} ({}) Read seqTs END! : {} / {} byte {}", Thread.currentThread().getStackTrace()[1].getMethodName(), videoBitrate, (info.getSeqTs() - 1), info.getReadBytes(), abs);
		
		}else if((abs == 1) && (info2 != null)){
			
			log.debug(MARKER_Segmenter, "{} ({}) Read seqTs BEGIN! : {} / {} byte {} --------------------------------------------------", Thread.currentThread().getStackTrace()[1].getMethodName(), videoBitrate, info2.getSeqTs(), info2.getReadBytes(), abs);
			long[] infoReadData = readPCR(info2.getReadBytes(), info2.getSeqTs());
			info2 = SingletonForSSFMP2.getInstance();
			info2.setReadBytes(infoReadData[0]);
			info2.setSeqTs((int)infoReadData[1]);
			log.debug(MARKER_Segmenter, "{} ({}) Read seqTs END! : {} / {} byte {}", Thread.currentThread().getStackTrace()[1].getMethodName(), videoBitrate, (info2.getSeqTs() - 1), info2.getReadBytes(), abs);
			
		}else if((abs == 2) && (info3 != null)){
			
			log.debug(MARKER_Segmenter, "{} ({}) Read seqTs BEGIN! : {} / {} byte {} --------------------------------------------------", Thread.currentThread().getStackTrace()[1].getMethodName(), videoBitrate, info3.getSeqTs(), info3.getReadBytes(), abs);
			long[] infoReadData = readPCR(info3.getReadBytes(), info3.getSeqTs());
			info3 = SingletonForSSFMP3.getInstance();
			info3.setReadBytes(infoReadData[0]);
			info3.setSeqTs((int)infoReadData[1]);
			log.debug(MARKER_Segmenter, "{} ({}) Read seqTs END! : {} / {} byte {}", Thread.currentThread().getStackTrace()[1].getMethodName(), videoBitrate, (info3.getSeqTs() - 1), info3.getReadBytes(), abs);
			
		}
		
	} // run()
	
	@SuppressWarnings("resource")
	long[] readPCR(long readByteInput, int seqTsInput){
		
		log.debug(MARKER_Segmenter, "{} {}", Thread.currentThread().getStackTrace()[1].getMethodName(), abs);
		
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
		
		long readByte = readByteInput;
		int seqTs = seqTsInput;
		double SEGMENTED_DURATION = DURATION;
		
		try{

			FileInputStream fis = new FileInputStream(TEMP_PATH + FILE_SEPARATOR + "mystream"+videoBitrate+".ts");
			if(!modeLive.equals("capturedTimeShifted")){
				fis = new FileInputStream(TEMP_PATH + FILE_SEPARATOR + "mystream"+videoBitrate+".ts");
			}else if(modeLive.equals("capturedTimeShifted")){
				fis = new FileInputStream(CAP_PATH + FILE_SEPARATOR + capFile);
			}
			
			BufferedInputStream bis = new BufferedInputStream(fis);
			bis.skip(readByte);
			byte[] buf = new byte[MPEG2_TS_PACKET_LENGTH]; 

			boolean flagCreateFile = true;
			FileOutputStream f = null;
			BufferedOutputStream bos = null;
		
			if((abs == 0) && (info != null)){
				info.setFlagSegFullDuration(0);
			}else if((abs == 1) && (info2 != null)){
				info2.setFlagSegFullDuration(0);
			}else if((abs == 2) && (info3 != null)){
				info3.setFlagSegFullDuration(0);
			}
		
			int countPacket = 0;
			boolean flagFirstPCR = false;
		
			@SuppressWarnings("unused")
			int ch; 
			loop : while((ch = bis.read(buf)) != -1){
			
				countPacket++;
				String syncWordString = String.format("%02x", buf[0]).toUpperCase();
			
				// MPEG2-TS 分割ファイルの生成
				if(flagCreateFile){
				
					if(!modeLive.equals("capturedTimeShifted")){
					
						if(encOrNot.equals("1") || encOrNot.equals("3")){
					
							File tempSegDir = new File(TEMP_PATH_FOR_ENC);
							tempSegDir.mkdirs();
							f = new FileOutputStream(TEMP_PATH_FOR_ENC + FILE_SEPARATOR + "fileSequence" + seqTs + ".ts");
							bos = new BufferedOutputStream(f, MPEG2_TS_PACKET_LENGTH);
							log.debug(MARKER_Segmenter, "{} Begin Segmentation of seqTs : {}", Thread.currentThread().getStackTrace()[1].getMethodName(), seqTs);
							flagCreateFile = false;
							seqTs++;
					
						}else if(encOrNot.equals("2") || encOrNot.equals("4")){
					
							f = new FileOutputStream(streamPath + FILE_SEPARATOR + "fileSequence" + seqTs + ".ts");
							bos = new BufferedOutputStream(f, MPEG2_TS_PACKET_LENGTH);
							log.debug(MARKER_Segmenter, "{} Begin Segmentation of seqTs : {}", Thread.currentThread().getStackTrace()[1].getMethodName(), seqTs);
							flagCreateFile = false;
							seqTs++;
					
						}
						
					}else if(modeLive.equals("capturedTimeShifted")){
					
						if(encOrNot.equals("1") || encOrNot.equals("3")){
                        
							File tempSegDir = new File(TEMP_PATH_FOR_ENC);
							tempSegDir.mkdirs();
							f = new FileOutputStream(TEMP_PATH_FOR_ENC + FILE_SEPARATOR + "fileSequence" + seqTs + ".ts");
							bos = new BufferedOutputStream(f, MPEG2_TS_PACKET_LENGTH);
							log.debug(MARKER_Segmenter, "{} Begin Segmentation of seqTs : {}", Thread.currentThread().getStackTrace()[1].getMethodName(), seqTs);
							flagCreateFile = false;
							seqTs++;
                        
						}else if(encOrNot.equals("2") || encOrNot.equals("4")){
                        
							File tempSegDir = new File(TEMP_PATH_FOR_ENC);
							tempSegDir.mkdirs();
							f = new FileOutputStream(TEMP_PATH_FOR_ENC + FILE_SEPARATOR + "fileSequence" + seqTs + ".ts");
							bos = new BufferedOutputStream(f, MPEG2_TS_PACKET_LENGTH);
							log.debug(MARKER_Segmenter, "{} Begin Segmentation of seqTs : {}", Thread.currentThread().getStackTrace()[1].getMethodName(), seqTs);
							flagCreateFile = false;
							seqTs++;
                        
						}

					} // if modeLive
				
				} // if(flagCreateFile)
			
				bos.write(buf, 0, buf.length);
			
				boolean flagAFE = false; // Adaptation Field
				boolean flagPCR = false;
			
				if(syncWordString.equals(SYNC_WORD)){
				
					//  Adaptation Field Exist
					// b00 = 0 
					// b01 = 1
					// b10 = 2 is true
					// b11 = 3 is true
					int adaptation_field_exist = buf[3] >> 4 & 0x3 & 0xff;
					if(adaptation_field_exist == 2 || adaptation_field_exist == 3){
						flagAFE = true;
					}
				
					int adaptation_field_length = buf[4] & 0xff;

					if((flagAFE) && (adaptation_field_length > 0)){
					
						// PCR Flag
						if(((buf[5] >> 4 & 0x1) & 0xff )== 1){
							flagPCR = true;
						}
					
					} // if AFE
				
					if(flagPCR){
					
						// PCR
						BigDecimal pcrSec = new BigDecimal(((buf[6] & 0xff) * 33554432 + (buf[7] & 0xff) * 131072 + (buf[8] & 0xff) * 512 + (buf[9] & 0xff) * 2 + (buf[10] >>> 7 & 0xff)) / 90000.0).setScale(2, BigDecimal.ROUND_HALF_UP);
						if((abs == 0) && (info != null)){
							info.setLastPcrSec(pcrSec);
						}else if((abs == 1) && (info2 != null)){
							info2.setLastPcrSec(pcrSec);
						}else if((abs == 2) && (info3 != null)){
							info3.setLastPcrSec(pcrSec);
						}
				
						if(!flagFirstPCR){
							
							if((abs == 0) && (info != null)){

								if(seqTs == 1){
									info.setInitPcrSecond(pcrSec);
									flagFirstPCR = true;
								}else{
									info.setInitPcrSecond(info.getLastPcrSecond().subtract(info.getDiffPcrSecond()));
									flagFirstPCR = true;
								}
							
							}else if((abs == 1) && (info2 != null)){
								
								if(seqTs == 1){
									info2.setInitPcrSecond(pcrSec);
									flagFirstPCR = true;
								}else{
									info2.setInitPcrSecond(info2.getLastPcrSecond().subtract(info2.getDiffPcrSecond()));
									flagFirstPCR = true;
								}
								
							}else if((abs == 2) && (info3 != null)){
								
								if(seqTs == 1){
									info3.setInitPcrSecond(pcrSec);
									flagFirstPCR = true;
								}else{
									info3.setInitPcrSecond(info3.getLastPcrSecond().subtract(info3.getDiffPcrSecond()));
									flagFirstPCR = true;
								}
								
							}
						
						} // if flagFirstPCR
					
						// Calculate time that has been read before
						BigDecimal readDuration = null;
						if((abs == 0) && (info != null)){
							readDuration = pcrSec.subtract(info.getInitPcrSecond());
						}else if((abs == 1) && (info2 != null)){
							readDuration = pcrSec.subtract(info2.getInitPcrSecond());
						}else if((abs == 2) && (info3 != null)){
							readDuration = pcrSec.subtract(info3.getInitPcrSecond());
						}
					
						// if readDuration crosses DURATION to be segmented, Output segmented ts (close OutputStream)
						if(Double.parseDouble(readDuration.toString()) >= SEGMENTED_DURATION){

							if((abs == 0) && (info != null)){
								
								info.setLastPcrSecond(pcrSec);
								info.setDiffPcrSecond(info.getLastPcrSecond().subtract(info.getInitPcrSecond()).subtract(new BigDecimal(Double.toString(DURATION))));
						
								log.debug(MARKER_Segmenter, "{} [info.getInitPcrSecond()] {}", Thread.currentThread().getStackTrace()[1].getMethodName(), info.getInitPcrSecond());
								log.debug(MARKER_Segmenter, "{} [info.getLastPcrSecond()] {}", Thread.currentThread().getStackTrace()[1].getMethodName(), info.getLastPcrSecond());
								log.debug(MARKER_Segmenter, "{} [info.getDiffPcrSecond()] {}", Thread.currentThread().getStackTrace()[1].getMethodName(), info.getDiffPcrSecond());
								log.debug(MARKER_Segmenter, "{} [Double.toString(DURATION))] {}", Thread.currentThread().getStackTrace()[1].getMethodName(), new BigDecimal(Double.toString(DURATION)));
						
							}else if((abs == 1) && (info2 != null)){
								
								info2.setLastPcrSecond(pcrSec);
								info2.setDiffPcrSecond(info2.getLastPcrSecond().subtract(info2.getInitPcrSecond()).subtract(new BigDecimal(Double.toString(DURATION))));
							
								log.debug(MARKER_Segmenter, "{} [info2.getInitPcrSecond()] {}", Thread.currentThread().getStackTrace()[1].getMethodName(), info2.getInitPcrSecond());
								log.debug(MARKER_Segmenter, "{} [info2.getLastPcrSecond()] {}", Thread.currentThread().getStackTrace()[1].getMethodName(), info2.getLastPcrSecond());
								log.debug(MARKER_Segmenter, "{} [info2.getDiffPcrSecond()] {}", Thread.currentThread().getStackTrace()[1].getMethodName(), info2.getDiffPcrSecond());
								log.debug(MARKER_Segmenter, "{} [Double.toString(DURATION))] {}", Thread.currentThread().getStackTrace()[1].getMethodName(), new BigDecimal(Double.toString(DURATION)));

							}else if((abs == 2) && (info3 != null)){
								
								info3.setLastPcrSecond(pcrSec);
								info3.setDiffPcrSecond(info3.getLastPcrSecond().subtract(info3.getInitPcrSecond()).subtract(new BigDecimal(Double.toString(DURATION))));
							
								log.debug(MARKER_Segmenter, "{} [info3.getInitPcrSecond()] {}", Thread.currentThread().getStackTrace()[1].getMethodName(), info3.getInitPcrSecond());
								log.debug(MARKER_Segmenter, "{} [info3.getLastPcrSecond()] {}", Thread.currentThread().getStackTrace()[1].getMethodName(), info3.getLastPcrSecond());
								log.debug(MARKER_Segmenter, "{} [info3.getDiffPcrSecond()] {}", Thread.currentThread().getStackTrace()[1].getMethodName(), info3.getDiffPcrSecond());
								log.debug(MARKER_Segmenter, "{} [Double.toString(DURATION))] {}", Thread.currentThread().getStackTrace()[1].getMethodName(), new BigDecimal(Double.toString(DURATION)));

							}
							
							bos.close();
							f.close();
						
							log.debug(MARKER_Segmenter, "{} END : Segmentation of seqTs : {} {}", Thread.currentThread().getStackTrace()[1].getMethodName(), (seqTs-1), abs);
						
							if((abs == 0) && (info != null)){
								info.setSeqTsEnc(seqTs - 1);
							}else if((abs == 1) && (info2 != null)){
								info2.setSeqTsEnc(seqTs - 1);
							}else if((abs == 2) && (info3 != null)){
								info3.setSeqTsEnc(seqTs - 1);
							}
						
							// MPEG2-TS 分割後の処理
							if(!modeLive.equals("capturedTimeShifted")){
								
								// 暗号化ありの場合，MPEG2-TS 分割後，Encrypter を実行し暗号化を行う．
								if(encOrNot.equals("1") || encOrNot.equals("3")){
									Encrypter ec = new Encrypter(streamPath, TEMP_PATH_FOR_ENC, modeLive, abs, MPEG2_TS_PACKET_LENGTH);
									Thread ecThread = new Thread(ec);
									ecThread.start();
								}
								
							}else if(modeLive.equals("capturedTimeShifted")){
							
								if((abs == 0) && (info != null)){
									info.setSeqTsCapturedTimeShifted(info.getSeqTsEnc());
								}else if((abs == 1) && (info2 != null)){
									info2.setSeqTsCapturedTimeShifted(info2.getSeqTsEnc());
								}else if((abs == 2) && (info3 != null)){
									info3.setSeqTsCapturedTimeShifted(info3.getSeqTsEnc());
								}
								
								FFmpegRunner rf = new FFmpegRunner(
									FFMPEG_FOR_CAM,
									FFMPEG_FOR_FILE,
									
									streamPath,
									TEMP_PATH,
									TEMP_PATH_FOR_ENC,
									FILE_PATH,
									CAP_PATH,
									
									modeLive,
									file,
									capFile,
									capResolution,
									videoResolution,
									videoBitrate,
									videoProfile, 
									audioCodec,
									audioBitrate,
									audioSamplingFreq,
									durationLive,
									encOrNot,
									thread,
									abs,
									
									MPEG2_TS_PACKET_LENGTH		
								);
								Thread rfThread = new Thread(rf);
								rfThread.start();
	                            
							}
						
							flagCreateFile = true;
							
							if((abs == 0) && (info != null)){
								
								info.setFlagSegFullDuration(1);
							}else if((abs == 1) && (info2 != null)){
								info2.setFlagSegFullDuration(1);
							}else if((abs == 2) && (info3 != null)){
								log.error(MARKER_Segmenter, "{} [info3.getFlagSegFullDuration()] {}", Thread.currentThread().getStackTrace()[1].getMethodName(), info3.getFlagSegFullDuration());
								info3.setFlagSegFullDuration(1);
								log.error(MARKER_Segmenter, "{} [info3.getFlagSegFullDuration()] {}", Thread.currentThread().getStackTrace()[1].getMethodName(), info3.getFlagSegFullDuration());
							}
						
							break loop;
						
						} // if Output segmented ts
					
					} // if flagPCR
				
				}else{ // if SYNC_WORD
				
					log.error(MARKER_Segmenter, "{} TS packet isn't started by 0x47", Thread.currentThread().getStackTrace()[1].getMethodName());
				
				} // if SYNC_WORD
			
			} // loop : while()
		
			bos.close();
			bis.close();
			fis.close();
			f.close();
		
			if((abs == 0) && (info != null) && (!info.getFlagSegFullDuration())){
				log.error(MARKER_Segmenter, "{} [info.getFlagSegFullDuration()] {}", Thread.currentThread().getStackTrace()[1].getMethodName(), info.getFlagSegFullDuration());
				info.setFlagLastTs(1);
			}else if((abs == 1) && (info2 != null) && (!info2.getFlagSegFullDuration())){
				log.error(MARKER_Segmenter, "{} [info2.getFlagSegFullDuration()] {}", Thread.currentThread().getStackTrace()[1].getMethodName(), info2.getFlagSegFullDuration());
				info2.setFlagLastTs(1);
			}else if((abs == 2) && (info3 != null) && (!info3.getFlagSegFullDuration())){
				log.error(MARKER_Segmenter, "{} [info3.getFlagSegFullDuration()] {}", Thread.currentThread().getStackTrace()[1].getMethodName(), info3.getFlagSegFullDuration());
				info3.setFlagLastTs(1);
			}
		
			if((abs == 0) && (info != null) && info.getFlagLastTs()){
			
				info.setSeqTsLast(seqTs-1);
				log.error(MARKER_Segmenter, "{} [getLastPcrSecond()] {}", Thread.currentThread().getStackTrace()[1].getMethodName(), info.getLastPcrSecond());

				if(!modeLive.equals("capturedTimeShifted")){
					if(encOrNot.equals("1") || encOrNot.equals("3")){
						Encrypter ec = new Encrypter(streamPath, TEMP_PATH_FOR_ENC, modeLive, abs, MPEG2_TS_PACKET_LENGTH);
						Thread ecThread = new Thread(ec);
						ecThread.start();
					}
				}else if(modeLive.equals("capturedTimeShifted")){
					info.setSeqTsCapturedTimeShifted(info.getSeqTsEnc());
					FFmpegRunner rf = new FFmpegRunner(
							FFMPEG_FOR_CAM,
							FFMPEG_FOR_FILE,
							
							streamPath,
							TEMP_PATH,
							TEMP_PATH_FOR_ENC,
							FILE_PATH,
							CAP_PATH,
							
							modeLive,
							file,
							capFile,
							capResolution,
							videoResolution,
							videoBitrate,
							videoProfile, 
							audioCodec,
							audioBitrate,
							audioSamplingFreq,
							durationLive,
							encOrNot,
							thread,
							abs,
							
							MPEG2_TS_PACKET_LENGTH		
						);
						Thread rfThread = new Thread(rf);
						rfThread.start();
				}
			
				// Segmenter のタイマーをキャンセルする．
				info.setFlagTimerSegmenter(1);

				log.error(MARKER_Segmenter, "{} ALL SEGMENTATION FINISHED!!! : {}", Thread.currentThread().getStackTrace()[1].getMethodName(), abs);
			
			} // if(seq.getFlagLastTs())
			
			else if((abs == 1) && (info2 != null) && info2.getFlagLastTs()){
				
				info2.setSeqTsLast(seqTs-1);
				log.error(MARKER_Segmenter, "{} [getLastPcrSecond()] {}", Thread.currentThread().getStackTrace()[1].getMethodName(), info2.getLastPcrSecond());
	
				if(!modeLive.equals("capturedTimeShifted")){
					if(encOrNot.equals("1") || encOrNot.equals("3")){
						Encrypter ec = new Encrypter(streamPath, TEMP_PATH_FOR_ENC, modeLive, abs, MPEG2_TS_PACKET_LENGTH);
						Thread ecThread = new Thread(ec);
						ecThread.start();
					}
				}else if(modeLive.equals("capturedTimeShifted")){
					info2.setSeqTsCapturedTimeShifted(info2.getSeqTsEnc());
					FFmpegRunner rf = new FFmpegRunner(
							FFMPEG_FOR_CAM,
							FFMPEG_FOR_FILE,
							
							streamPath,
							TEMP_PATH,
							TEMP_PATH_FOR_ENC,
							FILE_PATH,
							CAP_PATH,
							
							modeLive,
							file,
							capFile,
							capResolution,
							videoResolution,
							videoBitrate,
							videoProfile, 
							audioCodec,
							audioBitrate,
							audioSamplingFreq,
							durationLive,
							encOrNot,
							thread,
							abs,
							
							MPEG2_TS_PACKET_LENGTH		
						);
						Thread rfThread = new Thread(rf);
						rfThread.start();
				}
			
				// Segmenter のタイマーをキャンセルする．
				info2.setFlagTimerSegmenter(1);

				log.error(MARKER_Segmenter, "{} ALL SEGMENTATION FINISHED!!! : {}", Thread.currentThread().getStackTrace()[1].getMethodName(), abs);
			
			} // if(seq.getFlagLastTs()) 
			
			else if((abs == 2) && (info3 != null) && info3.getFlagLastTs()){
				
				info3.setSeqTsLast(seqTs-1);
				log.error(MARKER_Segmenter, "{} [getLastPcrSecond()] {}", Thread.currentThread().getStackTrace()[1].getMethodName(), info3.getLastPcrSecond());
			
				if(!modeLive.equals("capturedTimeShifted")){
					if(encOrNot.equals("1") || encOrNot.equals("3")){
						Encrypter ec = new Encrypter(streamPath, TEMP_PATH_FOR_ENC, modeLive, abs, MPEG2_TS_PACKET_LENGTH);
						Thread ecThread = new Thread(ec);
						ecThread.start();
					}
				}else if(modeLive.equals("capturedTimeShifted")){
					info3.setSeqTsCapturedTimeShifted(info3.getSeqTsEnc());
					FFmpegRunner rf = new FFmpegRunner(
							FFMPEG_FOR_CAM,
							FFMPEG_FOR_FILE,
							
							streamPath,
							TEMP_PATH,
							TEMP_PATH_FOR_ENC,
							FILE_PATH,
							CAP_PATH,
							
							modeLive,
							file,
							capFile,
							capResolution,
							videoResolution,
							videoBitrate,
							videoProfile, 
							audioCodec,
							audioBitrate,
							audioSamplingFreq,
							durationLive,
							encOrNot,
							thread,
							abs,
							
							MPEG2_TS_PACKET_LENGTH		
						);
						Thread rfThread = new Thread(rf);
						rfThread.start();
				}
			
				// Segmenter のタイマーをキャンセルする．
				info3.setFlagTimerSegmenter(1);

				log.error(MARKER_Segmenter, "{} ALL SEGMENTATION FINISHED!!! : {}", Thread.currentThread().getStackTrace()[1].getMethodName(), abs);
			
			} // if(seq.getFlagLastTs()) 
		
			long[] ret = new long[2];
			ret[0] = (MPEG2_TS_PACKET_LENGTH * (countPacket)) + readByte; // これまで読んだ MPEG2-TS の総バイト数
			ret[1] = seqTs;                            // これまで分割した MPEG2-TS のシーケンス数
			return ret;
		
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
		
	} // readPCR()

} // class
