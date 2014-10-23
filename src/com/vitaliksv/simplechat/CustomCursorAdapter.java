package com.vitaliksv.simplechat;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class CustomCursorAdapter extends CursorAdapter {
	
	public static final int MY_MESSAGE = 1;
	public static final int FOREIGN_MESSAGE = 0;
	

	public CustomCursorAdapter(Context context, Cursor c) {
		super(context, c);
		// TODO Auto-generated constructor stub
	}
	
	private int getItemViewType(Cursor cursor){
		
		int type = cursor.getInt(cursor.getColumnIndex(cursor.getColumnName(3)));
		if (type == 0){
			return 0;
		}
		else{
			return 1;
		}
	}
	@Override
	public int getItemViewType(int position){
		Cursor cursor = (Cursor) getItem(position);
		return getItemViewType(cursor);
	}
	
	@Override
	public int getViewTypeCount(){
		return 2;
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		// TODO Auto-generated method stub
		// when the view will be created for first time,
		// we need to tell the adapters, how each item will look

		final int position = cursor.getPosition();

		final int type = getItemViewType(position);
		
		View retView;
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		if(type == MY_MESSAGE){
			retView = inflater.inflate(R.layout.message_mine_row, parent, false);
		}
		else{
			retView = inflater.inflate(R.layout.message_foreign_row, parent, false);
		}
		return retView;
	}
	

	
	private SpannableString hashTagSearching(String stringToProcess) {	
		
		int startIndex = 0;
		int stopIndex = 0;
		
		SpannableString spannedPermissions = new SpannableString(stringToProcess);

			while (((stringToProcess.indexOf("#", startIndex)) != -1)) {
				
				startIndex = stringToProcess.indexOf("#", startIndex);
				
				if (stringToProcess.indexOf(" ", startIndex) != -1) {
					stopIndex = stringToProcess.indexOf(" ", startIndex);
				}
				else{
					stopIndex = stringToProcess.length();
				}
				spannedPermissions.setSpan(new ForegroundColorSpan(Color.RED), startIndex, stopIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				startIndex = stopIndex;
			}

		return spannedPermissions;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		// TODO Auto-generated method stub
		// here we are setting our data
		// that means, take the data from the cursor and put it in views

		TextView messageText = (TextView) view.findViewById(R.id.message_text);
		messageText.setText(hashTagSearching(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(1)))));

		if (cursor.getInt(cursor.getColumnIndex(cursor.getColumnName(3))) == 0) {

			TextView messageTime = (TextView) view.findViewById(R.id.txtTime);
			messageTime.setText(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(2))));

		} else {

			TextView messageTime = (TextView) view.findViewById(R.id.txtTime);
			messageTime.setText(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(2))));

		}

	}

}
