/**
 * @aim OperateAdapter�࣬ʵ�ּǼ����û�����������Ŀ
 * 			�û��������������ݵ�ʱ��������Ӧ�����������ݣ�������������ֺС������¡��˽�ѧ��ʫ
 * 			������������Ŀ��Adapter����
 * 
 * ʵ�֣�
 * Adapter�ص�����ȡ��ǰ�����ͣ�Ȼ����ز�ͬ�Ĳ���
 * 
 * 
 * @time 2014.04.10;
 * @author divhee
 * @modify by 
 */
package com.readboy.Q.Shares;

import java.util.List;
import java.util.Map;

import com.readboy.Q.Gspt.R;

import android.content.Context;
import android.graphics.drawable.StateListDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

public class GsptGridViewAdapter extends BaseAdapter{
	
	/**
	 * ����ÿ����Ŀ��layout��
	 */
	private LayoutInflater listenInflater = null;
	
	/**
	 * ϵͳ��context
	 */
	private Context listenContext = null;
	
	/**
	 * ����list map
	 */
	private List<Map<String, Object>> listenListItems = null;

	/**
	 * ��������Ļص�
	 */
	private OnClickListener onClickListener = null;
	
	/**
	 * �б���Ϣ
	 */
	private StoryGridViewInfo listInfo = null;
	
	/**
	 * @aim �޸�List��Style
	 * @param style
	 */
	public void setListenAdapterInfo(StoryGridViewInfo setInfo) {
		listInfo = setInfo;
	}
	
	public interface StoryGridViewInfo {
		/**
		 * @aim �жϵ�ǰ��ť��״̬���������߿��ŵ�
		 * @index ��ǰ�����
		 * @return true ���� false �Ѿ���
		 */
		public boolean bBtnOfGridItemPassState(int index);
		
		/**
		 * @aim ��ȡ��ť��״̬ͼƬStateListDrawable
		 * @param index ��ť���
		 * @return ״̬ͼƬStateListDrawable
		 */
		public StateListDrawable getShowStateListDrawable(int index);
		
	}	
	
	@SuppressWarnings("unchecked")
	public GsptGridViewAdapter(Context context, List<? extends Map<String, ?>> data, OnClickListener clickListener) {
		this.listenContext = context;
		this.listenListItems = (List<Map<String, Object>>) data;
		this.listenInflater = (LayoutInflater) listenContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.onClickListener = clickListener;
	}
	
	@Override
	public int getCount() {
		return listenListItems.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder viewHolder = null;
		// ÿһ�����°�ť
		if(convertView == null){
			viewHolder = new ViewHolder();
			convertView = listenInflater.inflate(R.layout.gspt_gridview_item, null);
			viewHolder.btnStoryIcon = (Button) convertView.findViewById(R.id.btn_story_icon);
			viewHolder.btnStoryIcon.setOnClickListener(onClickListener);
			convertView.setTag(R.id.tag_grid_holder, viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag(R.id.tag_grid_holder);
		}
		int gridItemId = (Integer) listenListItems.get(position).get("gridItemId");
		viewHolder.btnStoryIcon.setSelected((listInfo != null) ? listInfo.bBtnOfGridItemPassState(gridItemId) : false);
		viewHolder.btnStoryIcon.setTag(R.id.tag_grid_index, gridItemId);
		int gridItemDb = (Integer) listenListItems.get(position).get("gridItemDb");
//		viewHolder.btnStoryIcon.setBackgroundResource(gridItemDb);
		viewHolder.btnStoryIcon.setBackgroundDrawable((listInfo != null) ? listInfo.getShowStateListDrawable(gridItemDb) : null);

		return convertView;
	}
	
	/**
	 * @aim GridViewÿ����Ŀ��ʾ��ͼƬ�Լ��ļ����Ŀؼ�ʵ����ֻ��Ҫ��ȡһ��
	 * @author Administrator
	 *
	 */
	private class ViewHolder {
		public Button btnStoryIcon = null;
	}
}	