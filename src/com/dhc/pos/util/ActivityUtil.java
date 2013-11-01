package com.dhc.pos.util;

import java.util.Stack;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.dhc.pos.R;

public class ActivityUtil {
	
	private static Stack<Activity> activityStack = new Stack<Activity>();
	
	public static void shake(Context context,View v){
		Animation shake = AnimationUtils.loadAnimation(context, R.anim.shake);
		// 震动并获得焦点
        v.startAnimation(shake);
        v.requestFocus();
	}
	
	/*******
	 * 
	public static void pushActivity(Activity activity){
		activityStack.add(activity);
	}
	
	public static Activity peekActivity(){
		return activityStack.peek();
	}
	
	public static Activity popActivity(){
		return activityStack.pop();
	}
	
	******/

}
