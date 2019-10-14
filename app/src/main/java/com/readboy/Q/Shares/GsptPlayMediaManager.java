/**
 * @aim GsptPlayMediaManager类
 * 实现对游戏声音的播放管理，包括声音和音效
 * MediaPlayer播放背景音大文件，soundPlayer播放音效小文件
 * MediaPlayer一个实例只能播放一个声音，比较慢。
 * 声音播放管理的策略：
 * 1、使用一个专门的MediaPlayer实例播放背景音，
 * 2、其他声音实例则不固定，要播放哪一个则记住哪一实例，关闭声音也是如此
 * 3、提示拖放正确和拖放错误的声音互斥，两个同一时间只播放一个
 * 4、都使用MediaPlayer播放，没有采用soundPlayer播放
 * 声音ID与内容对应关系：
 * R.raw.gspt_game_bg						游戏背景音
 * R.raw.gspt_gsjm_00						原来是孙悟空喝醉了酒躺在石头上睡觉了，不小心被小鬼招走了魂魄。
 * R.raw.gspt_gsjm_01						原来是孙悟空酒醒了后，发现自己在冥界，气得拿起金箍棒一片乱打。
 * R.raw.gspt_gsjm_02						原来是小石猴和花果山的小猴子们在玩游戏。
 * R.raw.gspt_gsjm_03						原来是小河被鳄鱼占领了！！于是悟空想了一个办法，用石头赶跑鳄鱼。
 * R.raw.gspt_gsjm_04						原来是小猴子们在在小河里捉鱼。
 * R.raw.gspt_gsjm_05						原来是石猴和小猴子们在水帘洞外面玩耍。
 * R.raw.gspt_gsjm_06						原来是有一只猴子老死了，石猴和猴子们伤心地哭了！
 * R.raw.gspt_gsjm_07						原来是猴子们在一起造木筏。
 * R.raw.gspt_gsjm_08						原来是猴子们在帮石猴摘水果。
 * R.raw.gspt_gsjm_09						原来是唐僧和悟空在取西经的路上遇到条白龙。
 * R.raw.gspt_gsjm_10						原来是悟空准备打白龙时，菩萨出现了，告诉悟空白龙会辅助唐僧取经。
 * R.raw.gspt_gsjm_11						原来是悟空追到八戒了，准备收拾他。
 * R.raw.gspt_gsjm_12						原来是八戒拜唐僧为师。
 * R.raw.gspt_gsjm_13						原来是唐僧师徒三人被流沙河挡住了去路。
 * R.raw.gspt_gsjm_14						原来是沙僧拜唐僧为师。
 * R.raw.gspt_gsjm_15						原来是孙悟空在天庭做弼马温养马。
 * R.raw.gspt_gsjm_16						原来是孙悟空和七仙女在一起摘仙桃，为蟠桃会作准备。
 * R.raw.gspt_gsjm_17						原来是师徒四人在取经路上饿了，让悟空去筹备吃的。
 * R.raw.gspt_okjm_ts1						小朋友，好棒哦！！成功解封一张图片了！！！
 * R.raw.gspt_okjm_ts2						拼图成功！！
 * R.raw.gspt_xtjm_ts1						小朋友，这些图片都被妖怪封锁住了，让我们去把它们解封吧！！！
 * R.raw.gspt_xtjm_ts2						再玩一次
 * R.raw.gspt_yxjm_by1						小朋友，你真棒！！现在奖励你一个玩具！！继续努力哦！！
 * R.raw.gspt_yxjm_by2						小朋友，做得不错！！现在奖励你一个玩具！！继续努力哦！！
 * R.raw.gspt_yxjm_by3						小朋友，做得不错！！现在奖励你一个玩具！！再接再励！！
 * R.raw.gspt_yxjm_fail1					不是这里哦！！再仔细观察哦！！
 * R.raw.gspt_yxjm_right1					好棒！！完全正确！
 * R.raw.gspt_yxjm_right2					做得不错！
 * R.raw.gspt_yxjm_right3					对极了，就是这样！
 * R.raw.gspt_yxjm_right4					做得好！
 * R.raw.gspt_yxjm_right5					再接再厉！
 * R.raw.gspt_yxjm_right6					你太棒了！！
 * R.raw.gspt_yxjm_right7					棒极了！！
 * R.raw.gspt_yxjm_right8					干得好！！
 * R.raw.gspt_yxjm_right9					继续努力！！
 * R.raw.gspt_yxjm_tc						退出
 * R.raw.gspt_yxjm_ts1						小朋友，在右边的选择栏里面把拼图拉到正确的位置上吧！！！
 * R.raw.gspt_yxjm_xyf						下一幅
 * R.raw.gspt_zjm_ts1						小朋友，让我们也一起去西游吧！！！
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
	 * 声音开关 背景音乐 
	 * true 使能播放 
	 * false 禁止播放
	 */
	private boolean blnOnOffBgSound = false;

	/**
	 * 游戏背景音，在这个游戏里边固定且唯一
	 */
	private MediaPlayer bgMediaPlayer = null;

	/**
	 * 背景音的ID，记录当前播放的背景音是哪一个
	 * 如果在循环播放一个背景音则不重新播放
	 */
	private int bgMediaPlayId = 0;

	/**
	 * 游戏中提示音的播放MediaPlayer
	 */
	private MediaPlayer userIngameMediaPlayer = null;
	
	/**
	 * 点击按钮的声音，短短的音效
	 */
	private SoundPool btnSoundPool = null;
	
	/**
	 * 加载的点击的声音序号
	 */
	private int loadSoundPoolId = 0;
	
	/**
	 * 播放后的返回值
	 */
	private int playSoundPoolId = 0;
	
	/**
	 * 音量控制
	 */
	private AudioManager amSoundPool = null;
	
	/**
	 * 音量大小
	 */
	private int currentVolume = 0;
	
	/**
	 * 上下文Context
	 */
	private Context mpManagerContext = null;
	
	/**
	 * 游戏胜利后播放的声音
	 */
	private MediaPlayer mediaPlayWin = null;
	
	/**
	 * 声音管理实例
	 */
	private static GsptPlayMediaManager mediaManager = null;
	
	
	/**
	 * 构造初始化
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
	 * @aim 播放按钮音效
	 * @param 无
	 * @return 无
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
	 * @aim 释放soundpool加载的音效资源
	 * @param 无
	 * @return 无
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
	 * @aim 获取MediaManager的实例，唯一实例
	 * @param 无
	 * @return GsptPlayMediaManager 唯一声音播放实例
	 */
	public static GsptPlayMediaManager getInstance(Context context) {
		if (mediaManager == null) {
			mediaManager = new GsptPlayMediaManager(context);
		}
		return mediaManager;
	}

	/**
	 * @aim 设置使能或禁止背景音播放
	 * @param bgSound
	 *            true 使能播放
	 *            false 禁止播放
	 * @return 无
	 */
	public void setOnOffBgState(boolean bgSound) {

		blnOnOffBgSound = bgSound;
		if (bgMediaPlayer != null) {
			if (!bgSound) {
				try {
					// 禁止播放的时候设置背景音暂停播放
					bgMediaPlayer.pause();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * @aim 是否开启前景背景音乐循环播放
	 * @param bgLooping
	 *            设置背景音循环播放
	 *            true 循环
	 *            false 不循环
	 * @return 无
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
	 * @aim 通过资源ID号来播放一个背景音
	 * @param mediaResId
	 *            当前要播放的背景音的声音资源ID号
	 * @param bgLooping
	 *            是否循环播放
	 *            true 循环
	 *            false 不循环
	 * @return 无
	 */
	public void playBgMedia(int mediaResId, boolean bgLooping) {

		// 校验是否使能了播放，如果是禁止播放背景音则推出
		if (!blnOnOffBgSound) {
			return;
		}
		
		// 如果该背景音乐是循环播放的，再次播放则不重新播放了，直接退出
		if (bgMediaPlayer != null && bgMediaPlayId == mediaResId){
			if (!bgMediaPlayer.isPlaying()){
				bgMediaPlayer.start();
			}
			return;
		}
		
		// 播放新的背影音乐
		try {
			// 先停止当前播放的背景音
			// MediaPlayer 一次只能播放一种声音
			stopBgMedia();
			
			// 记录背景音的ID号，开始加载背景音
			bgMediaPlayId = mediaResId;
			bgMediaPlayer = MediaPlayer.create(mpManagerContext, mediaResId);
			
			// 设置是否需要循环播放
			bgMediaPlayer.setLooping(bgLooping);
			
			// 设置资源是否准备好的监听准备好了再开始播放
			bgMediaPlayer.setOnPreparedListener(new OnPreparedListener() {

				@Override
				public void onPrepared(MediaPlayer mp) {
					// 已经准备好了播放，正式开始播放
					mp.start();
					// 这里为什么要这么做呢，因为ingameactivity在onResumed之后才播放声音，否则不行的。
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
	 * @aim 暂停背景MediaPlayer的播放
	 * @param 无
	 * @return 无
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
	 * @aim 停止背景MediaPlayer的播放，并且释放资源
	 * @param 无
	 * @return 无
	 */
	public void stopBgMedia() {

		if (bgMediaPlayer != null) {
			try {
				// 停止背景音播放并释放资源
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
	 * @aim 通过声音ID号来播放MediaPlayer音乐
	 * @param mediaResId
	 *            声音资源的ID
	 * @param onlyLooping
	 *            声音播放是否循环
	 *            true 循环
	 *            false 不循环
	 * @return MediaPlayer 播放声音的实例
	 */
	public MediaPlayer playMediaOnlyById(int mediaResId, boolean onlyLooping) {

//		Log.w("divhee_edugame", "playMediaOnlyById=====" + mediaResId);

		// 开始播放声音
		try {
			// 通过资源ID号加载资源
			MediaPlayer mOnlyPlayer = MediaPlayer.create(mpManagerContext, mediaResId);

			// 循环播放与否 true 循环 false 不循环
			mOnlyPlayer.setLooping(onlyLooping);
			
			// 设置正常播放完成的监听
			mOnlyPlayer.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					// 正常播放结束，相应的处理
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
			
			// 设置播放出错的监听
			mOnlyPlayer.setOnErrorListener(new OnErrorListener() {

				@Override
				public boolean onError(MediaPlayer mp, int what, int extra) {
					// 操作错误或其他原因导致的错误会在这里被通知
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
			
			// 设置加载与否是否准备好开放播放的监听
			mOnlyPlayer.setOnPreparedListener(new OnPreparedListener() {

				@Override
				public void onPrepared(MediaPlayer mp) {
					// 一切就绪可以开放播放了，正式开始播放
					mp.start();
					// 这里为什么要这么做呢，因为ingameactivity在onResumed之后才播放声音，否则不行的。
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
	 * @aim 停止MediaPlayer播放的声音或者音效并且释放资源
	 * @param mOnlyPlayer
	 *            MediaPlayer声音播放的实例
	 * @return 无
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
	 * @aim 获取当前正在后台播放的背景音乐的声音资源ID号
	 * @param 无
	 * @return int 当前播放的后台资源的ID号(bgMediaPlayId)
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
	 * @aim 停止IngameActivity中指定提示音的播放
	 * @param 无
	 * @return 无
	 */
	public void IngameStopMediaPlayer() {
		
		// 停止声音播放，并且清空userIngameMediaPlayer
		if (mediaManager != null) {
			if (userIngameMediaPlayer != null) {
				stopMediaOnly(userIngameMediaPlayer);
				userIngameMediaPlayer = null;
			}
		}
	}

	/**
	 * @aim 拼图游戏中，通过声音的ID号播放相应的提示音
	 * @param srcid 需要播放的声音的ID号
	 * @return 无
	 */
	public void IngamePlayMediaPlayer(int srcid) {
		
		// 停止正在播放的声音
		IngameStopMediaPlayer();
		
		// 播放需要播放的声音
		if (mediaManager != null) {
			userIngameMediaPlayer = playMediaOnlyById(srcid, false);
		}
	}
	
	/**
	 * @aim 停止IngameActivity中指定提示音的播放
	 * @param 无
	 * @return 无
	 */
	public void winStopMediaPlayer() {
		// 停止声音播放，并且清空userIngameMediaPlayer
		if (mediaManager != null) {
			if (mediaPlayWin != null) {
				stopMediaOnly(mediaPlayWin);
				mediaPlayWin = null;
			}
		}
	}
	
	/**
	 * @aim 暂停IngameActivity中指定提示音的播放
	 * @param 无
	 * @return 无
	 */
	public void winPauseMediaPlayer() {
		// 暂停声音播放
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
	 * @aim 停止IngameActivity中指定提示音的播放
	 * @param 无
	 * @return 无
	 */
	public void winRestartMediaPlayer() {
		// 重新开始声音播放
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
	 * @aim 拼图游戏中，通过声音的ID号播放相应的提示音
	 * @param srcid 需要播放的声音的ID号
	 * @return 无
	 */
	public void winPlayMediaPlayer(int srcid) {
		// 停止正在播放的声音
		winStopMediaPlayer();
		
		// 播放需要播放的声音
		if (mediaManager != null) {
			mediaPlayWin = playMediaOnlyById(srcid, false);
		}
	}	
	
	/**
	 * GsptPlayMediaManager定义结束
	 */

}
