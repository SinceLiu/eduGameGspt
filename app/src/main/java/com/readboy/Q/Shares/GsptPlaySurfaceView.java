/**
 * @aim GsptPlaySurfaceView类 
 * 实现游戏动画部分更新刷图用，不用全屏更新，多个动画可以一起刷新
 * GsptPlaySurfaceView的实现：
 * 1、使用线程循环刷新动画
 * 2、while(iRun)通过iRun控制循环结束与否
 * 3、在初始化的时候要设置callback函数，这个是真正刷新动画的函数
 * 没有该函数，动画不会刷新，多个GsptPlaySurfaceView创建后也可以
 * 由于callback设置的不一样，刷新的动画不一样
 * 4、很重要一点，在网络上找到的都没有提及的一点
 * 线程中threadHolder.unlockCanvasAndPost(cvs);的释放最好在sleep之前，否则比较慢
 * 
 * @time 2013.08.10;
 * @author divhee
 * @modify by
 */
package com.readboy.Q.Shares;

import com.readboy.Q.Activity.GsptMainActivity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ReadboySurfaceView;
import android.view.SurfaceHolder;

public class GsptPlaySurfaceView extends ReadboySurfaceView implements
		SurfaceHolder.Callback {

	/**
	 * SurfaceHolder是一个接口，其作用就像一个关于SurfaceView的监听器
	 */
	private SurfaceHolder myHolder = null;

	/**
	 * 自己定义的SurfaceView动画刷新线程
	 * SurfaceView刷新有两种方法 
	 * 1、通过线程来刷新。
	 * 2、通过定时器来刷新
	 * 我们这里选用了线程来刷新动画
	 */
	private SurfaceViewPaintThread myThread = null;

	public GsptPlaySurfaceView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		myHolder = this.getSurface();
		myHolder.addCallback(this);

		// 设置画布 背景透明
		this.setZOrderOnTop(true);

		// TRANSLUCENT 设置背景透明
		this.getSurface().setFormat(PixelFormat.TRANSLUCENT);
	}

	public GsptPlaySurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		myHolder = this.getSurface();
		myHolder.addCallback(this);

		// 设置画布 背景透明
		this.setZOrderOnTop(true);

		// TRANSLUCENT 设置背景透明
		this.getSurface().setFormat(PixelFormat.TRANSLUCENT);
	}

	public GsptPlaySurfaceView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		myHolder = this.getSurface();
		myHolder.addCallback(this);

		// 设置画布 背景透明
		this.setZOrderOnTop(true);

		// TRANSLUCENT 设置背景透明
		this.getSurface().setFormat(PixelFormat.TRANSLUCENT);
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		// 创建一个绘图线程
		try {
			if (GsptRunDataFrame.bMainCurrentOnResumed && myThread == null) {
			Log.w("divhee_edugame", "surfaceCreated====in===");
				myThread = new SurfaceViewPaintThread(myHolder, GsptRunDataFrame.myThreadDelayTime);
				myThread.start();
			}			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
//		myThread.isRun = false;
		if (myThread != null){
			Log.w("divhee_edugame", "surfaceDestroyed====exit===");
			myThread.mThOnResumeCount = 0;
		}
	}
	
	/**
	 * @aim 不马上刷新
	 * @param 无
	 * @return 无
	 */
	public void surfaceNotPaintCurrent(int value){
		try {
			if (myThread != null) {
				myThread.mThOnResumeCount = value;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	
	/**
	 * @aim 退出函数接口
	 * @param 无
	 * @return 无
	 */
	public void surfaceRealExit(){
		try {
			if (myThread != null) {
				myThread.mThOnResumeCount = 0;
				myThread.isRun = false;
				myThread = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @aim SurfaceView刷新界面在线程当中的回调函数 用户使用SurfaceView需要设置这个
	 *          否则不会刷新界面，在线程中会调用这个回调
	 * @param cvs
	 *            Canvas用于用户绘制图片到这个上边
	 * @return 无
	 */
	private SurfaceViewCallBack rePaintCallBack = null;

	/**
	 * 定义SurfaceView刷新界面的interface接口
	 * 
	 * @author divhee
	 * 
	 */
	public interface SurfaceViewCallBack {
		public void UpdataStateRepaint(Canvas cvs);
	}

	/**
	 * @aim 设置Thread的回调函数
	 * @param callBack
	 *            SurfaceViewCallBack类型的回调函数
	 * @return 无
	 */
	public void setCallBack(SurfaceViewCallBack callBack) {
		rePaintCallBack = callBack;
	}

	/**
	 * @aim 使用回调函数，刷新界面
	 * @param cvs
	 *            Canvas用于用户绘制图片到这个上边
	 * @return 无
	 */
	public void rePaintSurfaceViewByCallBack(Canvas cvs) {
		if (rePaintCallBack != null) {
			rePaintCallBack.UpdataStateRepaint(cvs);
		}
	}

	/**
	 * @aim 刷新线程，线程内部类 
	 *          先同步synchronized(holder)
	 *          再锁定画布Canvas
	 *          再刷新界面
	 *          然后更新到屏幕上去
	 *          最后线程睡眠一段时间然后再重新循环
	 * @author divhee
	 */
	class SurfaceViewPaintThread extends Thread {

		/**
		 * 控制PlayThread线程是否继续循环 true 继续运行 false 退出循环
		 */
		public boolean isRun = false;

		/**
		 * 延时计数，因为发现HOME以后再次进入比较慢
		 * 一个小小的延时等待，进入模块后不马上动画
		 */
		public int mThOnResumeCount = 0;
		
		/**
		 * 控制PlayThread刷新的快慢，延时时间长度 
		 * threadDelayTime 越大，刷新越慢，但必须大于 0
		 */
		public int threadDelayTime = 100;

		/**
		 * 本SurfaceView要更新界面所需要的同步变量
		 */
		public SurfaceHolder threadHolder = null;

		/**
		 * @aim 线程初始化，初始化线程的一些变量
		 * @param holder
		 *            同步信息变量
		 * @param Delay
		 *            通过延时时长控制动画刷新的快慢
		 * @return 无
		 */
		public SurfaceViewPaintThread(SurfaceHolder holder, int Delay) {
			// Log.w("divhee_edugame", "PlayThread =111==" );
			this.threadHolder = holder;
			threadDelayTime = (Delay > 0) ? Delay : 12;
			isRun = true;
			mThOnResumeCount = 0;
//			Log.w("divhee_edugame", "SurfaceViewPaintThread====in===");
		}

		@Override
		public void run() {

			while (isRun) {
				Canvas cvs = null;
				try {
					synchronized (threadHolder) {
						// 延时计数，因为发现HOME以后再次进入比较慢
						if (isRun && GsptRunDataFrame.bMainCurrentOnResumed){
							if (mThOnResumeCount > 6) {
								// 锁定画布，一般在锁定后就可以通过其返回的画布对象Canvas，
								// 在其上面画图等操作了。
								cvs = threadHolder.lockCanvas();
								
								// 这一句很重要，真正背景透明
								cvs.drawColor(Color.TRANSPARENT, Mode.CLEAR);
								
								// 刷新动画的回调
								rePaintSurfaceViewByCallBack(cvs);
								
								// 结束锁定画面，关键点在这里
								if (cvs != null) {
									// 结束锁定画图，并提交改变
									threadHolder.unlockCanvasAndPost(cvs);
									cvs = null;
								}
								
								// 睡眠时间为threadDelayTime设置时长
								Thread.sleep(threadDelayTime);
							} else {
								mThOnResumeCount++;
								Thread.sleep(8);
							}
						} else {
							mThOnResumeCount = 0;
							// 睡眠时间为threadDelayTime设置时长
							Thread.sleep(8);
						}
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				} finally {
					if (cvs != null) {
						// 结束锁定画图，并提交改变
						threadHolder.unlockCanvasAndPost(cvs);
						cvs = null;
					}
				}
			}
		}
	}

	/**
	 * SurfaceViewPaintThread内部类定义结束 end
	 */
}
