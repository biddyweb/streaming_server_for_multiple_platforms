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

import java.math.BigDecimal;
import java.util.ArrayList;

public class SingletonForSSFMP {
	
	private SingletonForSSFMP(){
		
	}
	
	private static SingletonForSSFMP instance = null;
	
	public static synchronized SingletonForSSFMP getInstance(){
		
		if(SingletonForSSFMP.instance == null){
			SingletonForSSFMP.instance = new SingletonForSSFMP();
		}
		
		return SingletonForSSFMP.instance;

	}
	
	// Configuration
	private String modeLive;
	private String streamPathRoot;
	private String videoBitrate;
	
	// Segmenter
	private long readBytes;
	private int seqTs;
	private int seqTsEnc;
	private int seqTsCapturedTimeShifted;
	private int seqTsLast;
	boolean flagSegFullDuration;
	boolean flagLastTs;
	private BigDecimal initPcrSecond;
	private BigDecimal lastPcrSecond;
	private BigDecimal diffPcrSecond;
	private BigDecimal lastPcrSec;
	
	// Encrypter
	private ArrayList<String> keyArrayList;
	private ArrayList<String> ivArrayList;
	
	// Playlister
	private int seqPl;
	private String namePl;
	boolean flagLastPl;
	
	// Flag for Timer
	private boolean flagTimerSegmenter;
	private boolean flagTimerPlaylister;
	
	// Remover
	private boolean flagRemoveFile;
	
	public void init(){
		
		// Configuration
		this.modeLive = "";
		this.streamPathRoot = "";
		this.videoBitrate = "";
		
		// Segmenter
		this.readBytes = 0;
		this.seqTs = 0;
		this.seqTsEnc = 0;
		this.seqTsCapturedTimeShifted = 0;
		this.seqTsLast = 0;
		this.flagSegFullDuration = false;
		this.flagLastTs = false;
		this.initPcrSecond = new BigDecimal("0.0");
		this.lastPcrSecond = new BigDecimal("0.0");
		this.diffPcrSecond = new BigDecimal("0.0");
		this.lastPcrSec = new BigDecimal("0.0");
		
		// Encrypter
		keyArrayList = new ArrayList<String>();
		ivArrayList = new ArrayList<String>();
		
		// Playlister
		this.seqPl = 0;
		this.namePl = "pl.m3u8";
		this.flagLastPl = false;
		
		// Flag for Timer
		this.flagTimerSegmenter = false;
		this.flagTimerPlaylister = false;
		
		// Remover
		this.flagRemoveFile = false;
		
	} // init()
	
	// ===== SETTER ========================================
	
	// Configuration
	public void setModeLive(String _modeLive){
		this.modeLive = _modeLive;
	}
	
	public void setStreamPathRoot(String _streamPathRoot){
		this.streamPathRoot = _streamPathRoot;
	}
	
	public void setVideoBitrate(String _videoBitrate){
		this.videoBitrate = _videoBitrate;
	}
	
	// Segmenter
	public void setReadBytes(long _readBytes){
		this.readBytes = _readBytes;
	}
	
	public void setSeqTs(int _seqTs){
		this.seqTs = _seqTs;
	}
	
	public void setSeqTsEnc(int _seqTsEnc){
		this.seqTsEnc = _seqTsEnc;
	}
	
	public void setSeqTsCapturedTimeShifted(int _seqTsCapturedTimeShifted){
		this.seqTsCapturedTimeShifted = _seqTsCapturedTimeShifted;
	}
	
	public void setSeqTsLast(int _seqTsLast){
		this.seqTsLast = _seqTsLast;
	}
	
	public void setFlagSegFullDuration(int _flag){
		if(_flag == 0){
			this.flagSegFullDuration = false;
		}else{
			this.flagSegFullDuration = true;
		}
	}
	
	public void setFlagLastTs(int _flag){
		if(_flag == 0){
			this.flagLastTs = false;
		}else{
			this.flagLastTs = true;
		}
	}
	
	public void setInitPcrSecond(BigDecimal _initPcrSecond){
		this.initPcrSecond = _initPcrSecond;
	}
	
	public void setLastPcrSecond(BigDecimal _lastPcrSecond){
		this.lastPcrSecond = _lastPcrSecond;
	}
	
	public void setDiffPcrSecond(BigDecimal _diffPcrSecond){
		this.diffPcrSecond = _diffPcrSecond;
	}
	
	public void setLastPcrSec(BigDecimal _lastPcrSec){
		this.lastPcrSec = _lastPcrSec;
	}
	
	// Encrypter
	public void addKeyArrayList(String _key){
		this.keyArrayList.add(_key);
	}
	
	public void addIvArrayList(String _key){
		this.ivArrayList.add(_key);
	}
	
	// Playlister
	public void setSeqPl(int _seqPl){
		this.seqPl = _seqPl;
	}
	
	public void setNamePl(String _namePl){
		this.namePl = _namePl;
	}
	
	public void setFlagLastPl(int _flag){
		if(_flag == 0){
			this.flagLastPl = false;
		}else{
			this.flagLastPl = true;
		}
	}
	
	// Flag for Timer
	public void setFlagTimerSegmenter(int _flag){
		if(_flag == 0){
			this.flagTimerSegmenter = false;
		}else{
			this.flagTimerSegmenter = true;
		}
	}
	
	public void setFlagTimerPlaylister(int _flag){
		if(_flag == 0){
			this.flagTimerPlaylister = false;
		}else{
			this.flagTimerPlaylister = true;
		}
	}
	
	// Remover
	public void setFlagRemoveFile(int _flag){
		if(_flag == 0){
			this.flagRemoveFile = false;
		}else{
			this.flagRemoveFile = true;
		}
	}
	
	// ===== GETTER ========================================
	
	// Configuration
	public String getModeLive(){
		return this.modeLive;
	}
	
	public String getStreamPathRoot(){
		return this.streamPathRoot;
	}
	
	public String getVideoBitrate(){
		return this.videoBitrate;
	}
	
	// Segmenter
	public long getReadBytes(){
		return this.readBytes;
	}
	
	public int getSeqTs(){
		return this.seqTs;
	}
	
	public int getSeqTsEnc(){
		return this.seqTsEnc;
	}
	
	public int getSeqTsCapturedTimeShifted(){
		return this.seqTsCapturedTimeShifted;
	}
	
	public int getSeqTsLast(){
		return this.seqTsLast;
	}
	
	public boolean getFlagSegFullDuration(){
		return this.flagSegFullDuration;
	}
	
	public boolean getFlagLastTs(){
		return this.flagLastTs;
	}
	
	public BigDecimal getInitPcrSecond(){
		return this.initPcrSecond;
	}
	
	public BigDecimal getLastPcrSecond(){
		return this.lastPcrSecond;
	}
	
	public BigDecimal getDiffPcrSecond(){
		return this.diffPcrSecond;
	}
	
	public BigDecimal getLastPcrSec(){
		return this.lastPcrSec;
	}
	
	// Encrypter
	public String getKeyArrayList(int _pos){
		return this.keyArrayList.get(_pos);
	}
	
	public String getIvArrayList(int _pos){
		return this.ivArrayList.get(_pos);
	}
	
	// Playlister
	public int getSeqPl(){
		return this.seqPl;
	}
	
	public String getNamePl(){
		return this.namePl;
	}
	
	public boolean getFlagLastPl(){
		return this.flagLastPl;
	}
	
	// Flag for Timer
	public boolean getFlagTimerSegmenter(){
		return this.flagTimerSegmenter;
	}
	
	public boolean getFlagTimerPlaylister(){
		return this.flagTimerPlaylister;
	}
	
	// Remover
	public boolean getFlagRemoveFile(){
		return this.flagRemoveFile;
	}

} // class