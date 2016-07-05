/**
 * @aim GsptPlayMediaManager��
 * ʵ�ֶ���Ϸ�����Ĳ��Ź���������������Ч
 * MediaPlayer���ű��������ļ���soundPlayer������ЧС�ļ�
 * MediaPlayerһ��ʵ��ֻ�ܲ���һ���������Ƚ�����
 * �������Ź���Ĳ��ԣ�
 * 1��ʹ��һ��ר�ŵ�MediaPlayerʵ�����ű�������
 * 2����������ʵ���򲻹̶���Ҫ������һ�����ס��һʵ�����ر�����Ҳ�����
 * 3����ʾ�Ϸ���ȷ���ϷŴ�����������⣬����ͬһʱ��ֻ����һ��
 * 4����ʹ��MediaPlayer���ţ�û�в���soundPlayer����
 * ����ID�����ݶ�Ӧ��ϵ��
 * R.raw.gspt_game_bg						��Ϸ������
 * R.raw.gspt_gsjm_00						ԭ��������պ����˾�����ʯͷ��˯���ˣ���С�ı�С�������˻��ǡ�
 * R.raw.gspt_gsjm_01						ԭ��������վ����˺󣬷����Լ���ڤ�磬��������𹿰�һƬ�Ҵ�
 * R.raw.gspt_gsjm_02						ԭ����Сʯ��ͻ���ɽ��С������������Ϸ��
 * R.raw.gspt_gsjm_03						ԭ����С�ӱ�����ռ���ˣ��������������һ���취����ʯͷ�������㡣
 * R.raw.gspt_gsjm_04						ԭ����С����������С����׽�㡣
 * R.raw.gspt_gsjm_05						ԭ����ʯ���С��������ˮ����������ˣ��
 * R.raw.gspt_gsjm_06						ԭ������һֻ���������ˣ�ʯ��ͺ��������ĵؿ��ˣ�
 * R.raw.gspt_gsjm_07						ԭ���Ǻ�������һ����ľ����
 * R.raw.gspt_gsjm_08						ԭ���Ǻ������ڰ�ʯ��ժˮ����
 * R.raw.gspt_gsjm_09						ԭ������ɮ�������ȡ������·��������������
 * R.raw.gspt_gsjm_10						ԭ�������׼�������ʱ�����������ˣ�������հ����Ḩ����ɮȡ����
 * R.raw.gspt_gsjm_11						ԭ�������׷���˽��ˣ�׼����ʰ����
 * R.raw.gspt_gsjm_12						ԭ���ǰ˽����ɮΪʦ��
 * R.raw.gspt_gsjm_13						ԭ������ɮʦͽ���˱���ɳ�ӵ�ס��ȥ·��
 * R.raw.gspt_gsjm_14						ԭ����ɳɮ����ɮΪʦ��
 * R.raw.gspt_gsjm_15						ԭ�������������ͥ������������
 * R.raw.gspt_gsjm_16						ԭ��������պ�����Ů��һ��ժ���ң�Ϊ��һ���׼����
 * R.raw.gspt_gsjm_17						ԭ����ʦͽ������ȡ��·�϶��ˣ������ȥ�ﱸ�Եġ�
 * R.raw.gspt_okjm_ts1						С���ѣ��ð�Ŷ�����ɹ����һ��ͼƬ�ˣ�����
 * R.raw.gspt_okjm_ts2						ƴͼ�ɹ�����
 * R.raw.gspt_xtjm_ts1						С���ѣ���ЩͼƬ�������ַ���ס�ˣ�������ȥ�����ǽ��ɣ�����
 * R.raw.gspt_xtjm_ts2						����һ��
 * R.raw.gspt_yxjm_by1						С���ѣ�������������ڽ�����һ����ߣ�������Ŭ��Ŷ����
 * R.raw.gspt_yxjm_by2						С���ѣ����ò��������ڽ�����һ����ߣ�������Ŭ��Ŷ����
 * R.raw.gspt_yxjm_by3						С���ѣ����ò��������ڽ�����һ����ߣ����ٽ���������
 * R.raw.gspt_yxjm_fail1					��������Ŷ��������ϸ�۲�Ŷ����
 * R.raw.gspt_yxjm_right1					�ð�������ȫ��ȷ��
 * R.raw.gspt_yxjm_right2					���ò���
 * R.raw.gspt_yxjm_right3					�Լ��ˣ�����������
 * R.raw.gspt_yxjm_right4					���úã�
 * R.raw.gspt_yxjm_right5					�ٽ�������
 * R.raw.gspt_yxjm_right6					��̫���ˣ���
 * R.raw.gspt_yxjm_right7					�����ˣ���
 * R.raw.gspt_yxjm_right8					�ɵúã���
 * R.raw.gspt_yxjm_right9					����Ŭ������
 * R.raw.gspt_yxjm_tc						�˳�
 * R.raw.gspt_yxjm_ts1						С���ѣ����ұߵ�ѡ���������ƴͼ������ȷ��λ���ϰɣ�����
 * R.raw.gspt_yxjm_xyf						��һ��
 * R.raw.gspt_zjm_ts1						С���ѣ�������Ҳһ��ȥ���ΰɣ�����
 * 
 * @time 2013.08.10;
 * @author divhee
 * @modify by
 */
package com.readboy.Q.Shares;

import com.readboy.Q.Activity.GsptMainActivity;
import com.readboy.Q.Gspt.R;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.SoundPool;
import android.util.Log;

public class GsptPlayMediaManager {

	/**
	 * �������� �������� 
	 * true ʹ�ܲ��� 
	 * false ��ֹ����
	 */
	private boolean blnOnOffBgSound = false;

	/**
	 * ��Ϸ���������������Ϸ��߹̶���Ψһ
	 */
	private MediaPlayer bgMediaPlayer = null;

	/**
	 * ��������ID����¼��ǰ���ŵı���������һ��
	 * �����ѭ������һ�������������²���
	 */
	private int bgMediaPlayId = 0;

	/**
	 * ��Ϸ����ʾ���Ĳ���MediaPlayer
	 */
	private MediaPlayer userIngameMediaPlayer = null;
	
	/**
	 * �����ť���������̶̵���Ч
	 */
	private SoundPool btnSoundPool = null;
	
	/**
	 * ���صĵ�����������
	 */
	private int loadSoundPoolId = 0;
	
	/**
	 * ���ź�ķ���ֵ
	 */
	private int playSoundPoolId = 0;
	
	/**
	 * ��������
	 */
	private AudioManager amSoundPool = null;
	
	/**
	 * ������С
	 */
	private int currentVolume = 0;
	
	/**
	 * ������Context
	 */
	private Context mpManagerContext = null;
	
	/**
	 * ��Ϸʤ���󲥷ŵ�����
	 */
	private MediaPlayer mediaPlayWin = null;
	
	/**
	 * ��������ʵ��
	 */
	private static GsptPlayMediaManager mediaManager = null;
	
	
	/**
	 * �����ʼ��
	 */
	private GsptPlayMediaManager(Context context) {
		mpManagerContext = context;
		btnSoundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 100);
		loadSoundPoolId = btnSoundPool.load(mpManagerContext, R.raw.gspt_btn_soundpool, 1);
	
		amSoundPool = (AudioManager) mpManagerContext.getSystemService(Context.AUDIO_SERVICE);
		//maxVol = soundpoolam.getStreamMaxVolume(AudioManager.STREAM_MUSIC);				
		//currentVolume = soundpoolam.getStreamVolume(AudioManager.STREAM_MUSIC);
		currentVolume = amSoundPool.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		//amSoundPool.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, AudioManager.FLAG_PLAY_SOUND);
	}

	/**
	 * @aim ���Ű�ť��Ч
	 * @param ��
	 * @return ��
	 */
	public void playBtnSoundPool() {
		try {
			if (btnSoundPool != null){
				playSoundPoolId = btnSoundPool.play(loadSoundPoolId, currentVolume, currentVolume, 1, 0, 1f);
				//Log.w("edugame", "===playBtnSoundPool===" + playSoundPoolId + "," + loadSoundPoolId);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @aim �ͷ�soundpool���ص���Ч��Դ
	 * @param ��
	 * @return ��
	 */
	public void releaseBtnSoundPool() {
		try {
			if (btnSoundPool != null){
				if (playSoundPoolId != 0){
					btnSoundPool.stop(playSoundPoolId);
				}
				btnSoundPool.release();
				btnSoundPool = null;
				loadSoundPoolId = 0;
				playSoundPoolId = 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/***
	 * @aim ��ȡMediaManager��ʵ����Ψһʵ��
	 * @param ��
	 * @return GsptPlayMediaManager Ψһ��������ʵ��
	 */
	public static GsptPlayMediaManager getInstance(Context context) {
		if (mediaManager == null) {
			mediaManager = new GsptPlayMediaManager(context);
		}
		return mediaManager;
	}

	/**
	 * @aim ����ʹ�ܻ��ֹ����������
	 * @param bgSound
	 *            true ʹ�ܲ���
	 *            false ��ֹ����
	 * @return ��
	 */
	public void setOnOffBgState(boolean bgSound) {

		blnOnOffBgSound = bgSound;
		if (bgMediaPlayer != null) {
			if (!bgSound) {
				try {
					// ��ֹ���ŵ�ʱ�����ñ�������ͣ����
					bgMediaPlayer.pause();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * @aim �Ƿ���ǰ����������ѭ������
	 * @param bgLooping
	 *            ���ñ�����ѭ������
	 *            true ѭ��
	 *            false ��ѭ��
	 * @return ��
	 */
	public void setLoopingBgState(boolean bgLooping) {

		if (bgMediaPlayer != null) {
			try {
				bgMediaPlayer.setLooping(bgLooping);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @aim ͨ����ԴID��������һ��������
	 * @param mediaResId
	 *            ��ǰҪ���ŵı�������������ԴID��
	 * @param bgLooping
	 *            �Ƿ�ѭ������
	 *            true ѭ��
	 *            false ��ѭ��
	 * @return ��
	 */
	public void playBgMedia(int mediaResId, boolean bgLooping) {

		// У���Ƿ�ʹ���˲��ţ�����ǽ�ֹ���ű��������Ƴ�
		if (!blnOnOffBgSound) {
			return;
		}
		
		// ����ñ���������ѭ�����ŵģ��ٴβ��������²����ˣ�ֱ���˳�
		if (bgMediaPlayer != null && bgMediaPlayId == mediaResId){
			if (!bgMediaPlayer.isPlaying()){
				bgMediaPlayer.start();
			}
			return;
		}
		
		// �����µı�Ӱ����
		try {
			// ��ֹͣ��ǰ���ŵı�����
			// MediaPlayer һ��ֻ�ܲ���һ������
			stopBgMedia();
			
			// ��¼��������ID�ţ���ʼ���ر�����
			bgMediaPlayId = mediaResId;
			bgMediaPlayer = MediaPlayer.create(mpManagerContext, mediaResId);
			
			// �����Ƿ���Ҫѭ������
			bgMediaPlayer.setLooping(bgLooping);
			
			// ������Դ�Ƿ�׼���õļ���׼�������ٿ�ʼ����
			bgMediaPlayer.setOnPreparedListener(new OnPreparedListener() {

				@Override
				public void onPrepared(MediaPlayer mp) {
					// �Ѿ�׼�����˲��ţ���ʽ��ʼ����
					mp.start();
					// ����ΪʲôҪ��ô���أ���Ϊingameactivity��onResumed֮��Ų��������������еġ�
					if (!GsptRunDataFrame.bMainCurrentOnResumed && !GsptRunDataFrame.bIngameCurrentOnResumed){
						mp.pause();
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @aim ��ͣ����MediaPlayer�Ĳ���
	 * @param ��
	 * @return ��
	 */
	public void pauseBgMedia() {
		try {
			if (bgMediaPlayer != null && bgMediaPlayer.isPlaying()) {
				bgMediaPlayer.pause();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @aim ֹͣ����MediaPlayer�Ĳ��ţ������ͷ���Դ
	 * @param ��
	 * @return ��
	 */
	public void stopBgMedia() {

		if (bgMediaPlayer != null) {
			try {
				// ֹͣ���������Ų��ͷ���Դ
				bgMediaPlayer.setLooping(false);
				if (bgMediaPlayer.isPlaying()) {
					bgMediaPlayer.stop();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				bgMediaPlayer.release();
				bgMediaPlayer = null;
				bgMediaPlayId = 0;
			}
		}
	}	

	/**
	 * @aim ͨ������ID��������MediaPlayer����
	 * @param mediaResId
	 *            ������Դ��ID
	 * @param onlyLooping
	 *            ���������Ƿ�ѭ��
	 *            true ѭ��
	 *            false ��ѭ��
	 * @return MediaPlayer ����������ʵ��
	 */
	public MediaPlayer playMediaOnlyById(int mediaResId, boolean onlyLooping) {

//		Log.w("divhee_edugame", "playMediaOnlyById=====" + mediaResId);

		// ��ʼ��������
		try {
			// ͨ����ԴID�ż�����Դ
			MediaPlayer mOnlyPlayer = MediaPlayer.create(mpManagerContext, mediaResId);

			// ѭ��������� true ѭ�� false ��ѭ��
			mOnlyPlayer.setLooping(onlyLooping);
			
			// ��������������ɵļ���
			mOnlyPlayer.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					// �������Ž�������Ӧ�Ĵ���
					try {
						if (mediaPlayWin == mp){
							mediaPlayWin.release();
							mediaPlayWin = null;
							GsptRunDataFrame gsptRunData = GsptRunDataFrame.getInstance(mpManagerContext);
							if (gsptRunData != null){
								gsptRunData.setGsptMediaPlayEnd();
							} else {
								Log.w("edugame", "===onCompletion=error===");
							}
						} else if (userIngameMediaPlayer == mp) {
							userIngameMediaPlayer.release();
							userIngameMediaPlayer = null;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			// ���ò��ų���ļ���
			mOnlyPlayer.setOnErrorListener(new OnErrorListener() {

				@Override
				public boolean onError(MediaPlayer mp, int what, int extra) {
					// �������������ԭ���µĴ���������ﱻ֪ͨ
					try {
						Log.w("edugame", "===setOnErrorListener====");
						if (mediaPlayWin == mp){
							mediaPlayWin.release();
							mediaPlayWin = null;
							GsptRunDataFrame gsptRunData = GsptRunDataFrame.getInstance(mpManagerContext);
							if (gsptRunData != null){
								gsptRunData.setGsptMediaPlayEnd();
							} else {
								Log.w("edugame", "===setOnErrorListener=error===");
							}
						} else if (userIngameMediaPlayer == mp) {
							userIngameMediaPlayer.release();
							userIngameMediaPlayer = null;
						}						
					} catch (Exception e) {
						e.printStackTrace();
					}
					return true;
				}
			});
			
			// ���ü�������Ƿ�׼���ÿ��Ų��ŵļ���
			mOnlyPlayer.setOnPreparedListener(new OnPreparedListener() {

				@Override
				public void onPrepared(MediaPlayer mp) {
					// һ�о������Կ��Ų����ˣ���ʽ��ʼ����
					mp.start();
					// ����ΪʲôҪ��ô���أ���Ϊingameactivity��onResumed֮��Ų��������������еġ�
					if (!GsptRunDataFrame.bMainCurrentOnResumed && !GsptRunDataFrame.bIngameCurrentOnResumed){
						mp.pause();
					}
				}
			});

			return mOnlyPlayer;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * @aim ֹͣMediaPlayer���ŵ�����������Ч�����ͷ���Դ
	 * @param mOnlyPlayer
	 *            MediaPlayer�������ŵ�ʵ��
	 * @return ��
	 */
	public void stopMediaOnly(MediaPlayer mOnlyPlayer) {
		if (mOnlyPlayer != null) {
			try {
				mOnlyPlayer.setLooping(false);
				if (mOnlyPlayer.isPlaying()) {
					mOnlyPlayer.stop();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				mOnlyPlayer.release();
				mOnlyPlayer = null;
			}
		}
	}
	
	/**
	 * @aim ��ȡ��ǰ���ں�̨���ŵı������ֵ�������ԴID��
	 * @param ��
	 * @return int ��ǰ���ŵĺ�̨��Դ��ID��(bgMediaPlayId)
	 */
	public int getMediaPlayId() {
		if (bgMediaPlayer != null) {
			if (bgMediaPlayer.isPlaying()) {
				return bgMediaPlayId;
			}
		}
		return 0;
	}
	
	/**
	 * @aim ֹͣIngameActivity��ָ����ʾ���Ĳ���
	 * @param ��
	 * @return ��
	 */
	public void IngameStopMediaPlayer() {
		
		// ֹͣ�������ţ��������userIngameMediaPlayer
		if (mediaManager != null) {
			if (userIngameMediaPlayer != null) {
				stopMediaOnly(userIngameMediaPlayer);
				userIngameMediaPlayer = null;
			}
		}
	}

	/**
	 * @aim ƴͼ��Ϸ�У�ͨ��������ID�Ų�����Ӧ����ʾ��
	 * @param srcid ��Ҫ���ŵ�������ID��
	 * @return ��
	 */
	public void IngamePlayMediaPlayer(int srcid) {
		
		// ֹͣ���ڲ��ŵ�����
		IngameStopMediaPlayer();
		
		// ������Ҫ���ŵ�����
		if (mediaManager != null) {
			userIngameMediaPlayer = playMediaOnlyById(srcid, false);
		}
	}
	
	/**
	 * @aim ֹͣIngameActivity��ָ����ʾ���Ĳ���
	 * @param ��
	 * @return ��
	 */
	public void winStopMediaPlayer() {
		// ֹͣ�������ţ��������userIngameMediaPlayer
		if (mediaManager != null) {
			if (mediaPlayWin != null) {
				stopMediaOnly(mediaPlayWin);
				mediaPlayWin = null;
			}
		}
	}
	
	/**
	 * @aim ��ͣIngameActivity��ָ����ʾ���Ĳ���
	 * @param ��
	 * @return ��
	 */
	public void winPauseMediaPlayer() {
		// ��ͣ��������
		try {
			if (mediaManager != null) {
				if (mediaPlayWin != null && mediaPlayWin.isPlaying()) {
					mediaPlayWin.pause();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	
	/**
	 * @aim ֹͣIngameActivity��ָ����ʾ���Ĳ���
	 * @param ��
	 * @return ��
	 */
	public void winRestartMediaPlayer() {
		// ���¿�ʼ��������
		try {
			if (mediaManager != null) {
				if (mediaPlayWin != null && !mediaPlayWin.isPlaying()) {
					mediaPlayWin.start();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * @aim ƴͼ��Ϸ�У�ͨ��������ID�Ų�����Ӧ����ʾ��
	 * @param srcid ��Ҫ���ŵ�������ID��
	 * @return ��
	 */
	public void winPlayMediaPlayer(int srcid) {
		// ֹͣ���ڲ��ŵ�����
		winStopMediaPlayer();
		
		// ������Ҫ���ŵ�����
		if (mediaManager != null) {
			mediaPlayWin = playMediaOnlyById(srcid, false);
		}
	}	
	
	/**
	 * GsptPlayMediaManager�������
	 */

}
