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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

class Encrypter implements Runnable{
	
	// XXX BC の AES/CBC/PKCS7Padding ではなく，Java の標準 Cipher クラスの AES/CBC/PKCS5Padding で良いかも．
	
	static final String FILE_SEPARATOR = System.getProperty("file.separator");
	
	private static Logger log = LoggerFactory.getLogger(Encrypter.class);
	private static Marker MARKER_Encrypter = MarkerFactory.getMarker("Encrypter");
	
	private String streamPath;
	private String TEMP_PATH_FOR_ENC;
	
	private String modeLive;
	private int abs;
	
	private int TS_PACKET_LENGTH;
	
	Encrypter(
			
		String _streamPath,
		String _TEMP_PATH_FOR_ENC,
		
		String _modeLive,
		int _abs,
		
		int _TS_PACKET_LENGTH
		){
		
		this.streamPath = _streamPath;
		this.TEMP_PATH_FOR_ENC = _TEMP_PATH_FOR_ENC;
		
		this.modeLive = _modeLive;
		this.abs = _abs;
		
		this.TS_PACKET_LENGTH = _TS_PACKET_LENGTH;
		
		log.debug(MARKER_Encrypter, "{} Construcor of Encrypter : {}", Thread.currentThread().getStackTrace()[1].getMethodName() ,abs);
		
	} // Constructor
	
	@SuppressWarnings("resource")
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
		
		int seqTsEnc = 0; //info.getSeqTsEnc();
		if(!modeLive.equals("capturedTimeShifted")){
			if((abs == 0) && (info != null)){
				seqTsEnc = info.getSeqTsEnc();
			}else if((abs == 1) && (info2 != null)){
				seqTsEnc = info2.getSeqTsEnc();
			}else if((abs == 2) && (info3 != null)){
				seqTsEnc = info3.getSeqTsEnc();
			}
		}else if(modeLive.equals("capturedTimeShifted")){
			if((abs == 0) && (info != null)){
				seqTsEnc = info.getSeqTsCapturedTimeShifted();
			}else if((abs == 1) && (info2 != null)){
				seqTsEnc = info2.getSeqTsCapturedTimeShifted();
			}else if((abs == 2) && (info3 != null)){
				seqTsEnc = info3.getSeqTsCapturedTimeShifted();
			}
		}
		
		if((abs == 0) && (info != null) && info.getFlagLastTs()){
			seqTsEnc = info.getSeqTsLast();
		}else if((abs == 1) && (info2 != null) && info2.getFlagLastTs()){
			seqTsEnc = info2.getSeqTsLast();
		}else if((abs == 2) && (info3 != null) && info3.getFlagLastTs()){
			seqTsEnc = info3.getSeqTsLast();
		}
		
		log.debug(MARKER_Encrypter, "{} Begin : Encryption of seqTsEnc : {}", Thread.currentThread().getStackTrace()[1].getMethodName(), seqTsEnc);
		
		Key sKey;
		Cipher c;
		FileOutputStream keyOut;
		FileWriter ivOut;
		FileInputStream fis;
		BufferedInputStream bis;
		FileOutputStream fos;
		CipherOutputStream cos;
		
		try {
			
			Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
			
			sKey = makeKey(128); // Key length is 128bit
			c = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
//			log.debug(MARKER_Encrypter, "{} [c.getAlgorithm()] {}", Thread.currentThread().getStackTrace()[1].getMethodName(), c.getAlgorithm());
			c.init(Cipher.ENCRYPT_MODE, sKey);
			
			// Set Key File Name at random
			String keyPre = RandomStringUtils.randomAlphabetic(10);
			keyOut = new FileOutputStream(streamPath + FILE_SEPARATOR + keyPre + seqTsEnc + ".key");

			if((abs == 0) && (info != null)){
				info.addKeyArrayList(keyPre);
			}else if((abs == 1) && (info2 != null)){
				info2.addKeyArrayList(keyPre);
			}else if((abs == 2) && (info3 != null)){
				info3.addKeyArrayList(keyPre);
			}

			byte[] keyOutByte = sKey.getEncoded();
			keyOut.write(keyOutByte);
			keyOut.close();
			
			byte[] iv = c.getIV();
//			log.debug(MARKER_Encrypter, "{} [iv.length] {} [byte]", Thread.currentThread().getStackTrace()[1].getMethodName(), iv.length);
			
			String ivHex = "";
			for(int i = 0; i < iv.length; i++){
				String ivHexTmp = String.format("%02x", iv[i]).toUpperCase();
				ivHex = ivHex + ivHexTmp;
			}
			
			String ivPre = RandomStringUtils.randomAlphabetic(10);
			ivOut = new FileWriter(streamPath + FILE_SEPARATOR + ivPre + seqTsEnc + ".iv");
			ivOut.write(ivHex);
			ivOut.close();
			
//			log.debug(MARKER_Encrypter, "{} [iv] {}", Thread.currentThread().getStackTrace()[1].getMethodName(), ivHex);
			
			if((abs == 0) && (info != null)){
				info.addIvArrayList(ivHex);
			}else if((abs == 1) && (info2 != null)){
				info2.addIvArrayList(ivHex);
			}else if((abs == 2) && (info3 != null)){
				info3.addIvArrayList(ivHex);
			}
			
			fis = new FileInputStream(TEMP_PATH_FOR_ENC + FILE_SEPARATOR + "fileSequence" + seqTsEnc + ".ts");
			bis = new BufferedInputStream(fis);
			fos = new FileOutputStream(streamPath + FILE_SEPARATOR + "fileSequenceEnc" + seqTsEnc + ".ts");
			cos = new CipherOutputStream(fos, c);
			if(modeLive.equals("capturedTimeShifted")){
				fis = new FileInputStream(TEMP_PATH_FOR_ENC + FILE_SEPARATOR + "fileSequenceEncoded" + seqTsEnc + ".ts");
                bis = new BufferedInputStream(fis);
                fos = new FileOutputStream(streamPath + FILE_SEPARATOR + "fileSequenceEnc" + seqTsEnc + ".ts");
                cos = new CipherOutputStream(fos, c);
			}
			
			byte[] buf = new byte[TS_PACKET_LENGTH]; 
			
			int ch;
			while((ch = bis.read(buf)) != -1){	
				cos.write(buf, 0, ch);
			}
			cos.close();
			fos.close();
			bis.close();
			fis.close();
			
			log.debug(MARKER_Encrypter, "{} End : Encryption of seqTsEnc : {}", Thread.currentThread().getStackTrace()[1].getMethodName(), seqTsEnc);
			
			if((abs == 0) && (info != null) && info.getFlagLastTs()){
				log.debug(MARKER_Encrypter, "{} ALL ENCRYPTION FINISHED!!! {}", Thread.currentThread().getStackTrace()[1].getMethodName(), abs);
			}else if((abs == 1) && (info2 != null) && info2.getFlagLastTs()){
				log.debug(MARKER_Encrypter, "{} ALL ENCRYPTION FINISHED!!! {}", Thread.currentThread().getStackTrace()[1].getMethodName(), abs);
			}else if((abs == 2) && (info3 != null) && info3.getFlagLastTs()){
				log.debug(MARKER_Encrypter, "{} ALL ENCRYPTION FINISHED!!! {}", Thread.currentThread().getStackTrace()[1].getMethodName(), abs);
			}
		
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} // try
	} // run
	
	// Generate Random Key
	static Key makeKey(int keyBit) throws NoSuchAlgorithmException{
		
		KeyGenerator kg = KeyGenerator.getInstance("AES");	
		SecureRandom rd = SecureRandom.getInstance("SHA1PRNG");
		kg.init(keyBit, rd);
		Key key = kg.generateKey();
		return key;
		
	} // makeKey
	
} // class Encrypter