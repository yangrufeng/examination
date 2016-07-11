package org.chm.popupwindow;

import android.content.Context;
import android.view.View;
import android.widget.PopupWindow;


public class NavPopupWindow extends PopupWindow {

	public NavPopupWindow(Context context, View contentView,
			int width, int height) {
		super(contentView, width, height, false);
	}
}
