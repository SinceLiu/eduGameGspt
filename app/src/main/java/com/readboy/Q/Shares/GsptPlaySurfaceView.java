/**
 * @aim GsptPlaySurfaceView�� 
 * ʵ����Ϸ�������ָ���ˢͼ�ã�����ȫ�����£������������һ��ˢ��
 * GsptPlaySurfaceView��ʵ�֣�
 * 1��ʹ���߳�ѭ��ˢ�¶���
 * 2��while(iRun)ͨ��iRun����ѭ���������
 * 3���ڳ�ʼ����ʱ��Ҫ����callback���������������ˢ�¶����ĺ���
 * û�иú�������������ˢ�£����GsptPlaySurfaceView������Ҳ����
 * ����callback���õĲ�һ����ˢ�µĶ�����һ��
 * 4������Ҫһ�㣬���������ҵ��Ķ�û���ἰ��һ��
 * �߳���threadHolder.unlockCanvasAndPost(cvs);���ͷ������sleep֮ǰ������Ƚ���
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
	 * SurfaceHolder��һ���ӿڣ������þ���һ������SurfaceView�ļ�����
	 */
	private SurfaceHolder myHolder = null;

	/**
	 * �Լ������SurfaceView����ˢ���߳�
	 * SurfaceViewˢ�������ַ��� 
	 * 1��ͨ���߳���ˢ�¡�
	 * 2��ͨ����ʱ����ˢ��
	 * ��������ѡ�����߳���ˢ�¶���
	 */
	private SurfaceViewPaintThread myThread = null;

	public GsptPlaySurfaceView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		myHolder = this.getSurface();
		myHolder.addCallback(this);

		// ���û��� ����͸��
		this.setZOrderOnTop(true);

		// TRANSLUCENT ���ñ���͸��
		this.getSurface().setFormat(PixelFormat.TRANSLUCENT);
	}

	public GsptPlaySurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		myHolder = this.getSurface();
		myHolder.addCallback(this);

		// ���û��� ����͸��
		this.setZOrderOnTop(true);

		// TRANSLUCENT ���ñ���͸��
		this.getSurface().setFormat(PixelFormat.TRANSLUCENT);
	}

	public GsptPlaySurfaceView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		myHolder = this.getSurface();
		myHolder.addCallback(this);

		// ���û��� ����͸��
		this.setZOrderOnTop(true);

		// TRANSLUCENT ���ñ���͸��
		this.getSurface().setFormat(PixelFormat.TRANSLUCENT);
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		// ����һ����ͼ�߳�
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
	 * @aim ������ˢ��
	 * @param ��
	 * @return ��
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
	 * @aim �˳������ӿ�
	 * @param ��
	 * @return ��
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
	 * @aim SurfaceViewˢ�½������̵߳��еĻص����� �û�ʹ��SurfaceView��Ҫ�������
	 *          ���򲻻�ˢ�½��棬���߳��л��������ص�
	 * @param cvs
	 *            Canvas�����û�����ͼƬ������ϱ�
	 * @return ��
	 */
	private SurfaceViewCallBack rePaintCallBack = null;

	/**
	 * ����SurfaceViewˢ�½����interface�ӿ�
	 * 
	 * @author divhee
	 * 
	 */
	public interface SurfaceViewCallBack {
		public void UpdataStateRepaint(Canvas cvs);
	}

	/**
	 * @aim ����Thread�Ļص�����
	 * @param callBack
	 *            SurfaceViewCallBack���͵Ļص�����
	 * @return ��
	 */
	public void setCallBack(SurfaceViewCallBack callBack) {
		rePaintCallBack = callBack;
	}

	/**
	 * @aim ʹ�ûص�������ˢ�½���
	 * @param cvs
	 *            Canvas�����û�����ͼƬ������ϱ�
	 * @return ��
	 */
	public void rePaintSurfaceViewByCallBack(Canvas cvs) {
		if (rePaintCallBack != null) {
			rePaintCallBack.UpdataStateRepaint(cvs);
		}
	}

	/**
	 * @aim ˢ���̣߳��߳��ڲ��� 
	 *          ��ͬ��synchronized(holder)
	 *          ����������Canvas
	 *          ��ˢ�½���
	 *          Ȼ����µ���Ļ��ȥ
	 *          ����߳�˯��һ��ʱ��Ȼ��������ѭ��
	 * @author divhee
	 */
	class SurfaceViewPaintThread extends Thread {

		/**
		 * ����PlayThread�߳��Ƿ����ѭ�� true �������� false �˳�ѭ��
		 */
		public boolean isRun = false;

		/**
		 * ��ʱ��������Ϊ����HOME�Ժ��ٴν���Ƚ���
		 * һ��СС����ʱ�ȴ�������ģ������϶���
		 */
		public int mThOnResumeCount = 0;
		
		/**
		 * ����PlayThreadˢ�µĿ�������ʱʱ�䳤�� 
		 * threadDelayTime Խ��ˢ��Խ������������� 0
		 */
		public int threadDelayTime = 100;

		/**
		 * ��SurfaceViewҪ���½�������Ҫ��ͬ������
		 */
		public SurfaceHolder threadHolder = null;

		/**
		 * @aim �̳߳�ʼ������ʼ���̵߳�һЩ����
		 * @param holder
		 *            ͬ����Ϣ����
		 * @param Delay
		 *            ͨ����ʱʱ�����ƶ���ˢ�µĿ���
		 * @return ��
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
						// ��ʱ��������Ϊ����HOME�Ժ��ٴν���Ƚ���
						if (isRun && GsptRunDataFrame.bMainCurrentOnResumed){
							if (mThOnResumeCount > 6) {
								// ����������һ����������Ϳ���ͨ���䷵�صĻ�������Canvas��
								// �������滭ͼ�Ȳ����ˡ�
								cvs = threadHolder.lockCanvas();
								
								// ��һ�����Ҫ����������͸��
								cvs.drawColor(Color.TRANSPARENT, Mode.CLEAR);
								
								// ˢ�¶����Ļص�
								rePaintSurfaceViewByCallBack(cvs);
								
								// �����������棬�ؼ���������
								if (cvs != null) {
									// ����������ͼ�����ύ�ı�
									threadHolder.unlockCanvasAndPost(cvs);
									cvs = null;
								}
								
								// ˯��ʱ��ΪthreadDelayTime����ʱ��
								Thread.sleep(threadDelayTime);
							} else {
								mThOnResumeCount++;
								Thread.sleep(8);
							}
						} else {
							mThOnResumeCount = 0;
							// ˯��ʱ��ΪthreadDelayTime����ʱ��
							Thread.sleep(8);
						}
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				} finally {
					if (cvs != null) {
						// ����������ͼ�����ύ�ı�
						threadHolder.unlockCanvasAndPost(cvs);
						cvs = null;
					}
				}
			}
		}
	}

	/**
	 * SurfaceViewPaintThread�ڲ��ඨ����� end
	 */
}
