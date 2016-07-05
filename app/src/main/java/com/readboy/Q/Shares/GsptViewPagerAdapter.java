package com.readboy.Q.Shares;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

public class GsptViewPagerAdapter extends PagerAdapter {
	
	/**
	 * �б���Ϣ
	 */
	private DataViewPagerInfo dataInfo = null;	
	
	/**
	 * View�б�
	 */
	private View[] pagerArray = null;
	
	public GsptViewPagerAdapter(DataViewPagerInfo dvpInfo) {
		this.dataInfo = dvpInfo;
		this.pagerArray = new View [GsptRunDataFrame.getPagerTotal()];
	}
	
	@Override
	public int getCount() {
		return GsptRunDataFrame.getPagerTotal();//Integer.MAX_VALUE;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public void destroyItem(View container, int position, Object object) {
		 Log.e("elabookcase", "destroyItem is called " + position);
		 if (pagerArray[position] != null){
			 ((ViewPager) container).removeView(pagerArray[position]);
			 pagerArray[position] = null;
		 }
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return super.getPageTitle(position);
	}

	@Override
	public Object instantiateItem(View container, int position) {
		try {
			if (pagerArray[position] == null && dataInfo != null){
				pagerArray[position] = dataInfo.onCreateView(position);
			}
			((ViewPager) container).addView(pagerArray[position], 0);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pagerArray[position];
	}

	/**
	 * @aim �û���ȡһЩ�û�����
	 * @author Administrator
	 *
	 */
	public interface DataViewPagerInfo {

		/**
		 * @aim ��ȡ��ǰ�ڵ��Viewʵ��
		 * @return View ����ֵ
		 */
		public View onCreateView(int position);
	}
	
}


