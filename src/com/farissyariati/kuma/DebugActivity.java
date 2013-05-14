package com.farissyariati.kuma;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class DebugActivity extends Activity {

	private Thread animationThread;
	private Button btAnimationStart;
	private ImageView ivProjectProgress;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.debug_activity_layout);
		initComps();
	}

	private void initComps() {
		this.ivProjectProgress = (ImageView) findViewById(R.id.iv_animation_project_progress);
		this.btAnimationStart = (Button) findViewById(R.id.bt_start_animation);
		btAnimationStart.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				fAnimate();
			}
		});
	}

	private void fAnimate() {
		this.animationThread = new Thread(new Runnable() {
			@SuppressWarnings("static-access")
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try{
					for(int i = 0; i < 10; i++){
						progressHandler.sendMessage(Message.obtain(progressHandler, i+1));
						animationThread.sleep(100);
					}
				}
				catch(Exception e){
					e.printStackTrace();
				}
				
			}
		});
		animationThread.start();
	}

	Handler progressHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				ivProjectProgress.setImageResource(R.drawable.percent_ten);
				break;
			case 2:
				ivProjectProgress.setImageResource(R.drawable.percent_twenty);
				break;
			case 3:
				ivProjectProgress.setImageResource(R.drawable.percent_thirty);
				break;
			case 4:
				ivProjectProgress.setImageResource(R.drawable.percent_fourty);
				break;
			case 5:
				ivProjectProgress.setImageResource(R.drawable.percent_fifty);
				break;
			case 6:
				ivProjectProgress.setImageResource(R.drawable.percent_sixty);
				break;
			case 7:
				ivProjectProgress.setImageResource(R.drawable.percent_seventy);
				break;
			case 8:
				ivProjectProgress.setImageResource(R.drawable.percent_eighty);
				break;
			case 9:
				ivProjectProgress.setImageResource(R.drawable.percent_ninety);
				break;
			case 10:
				ivProjectProgress.setImageResource(R.drawable.percent_hundred);
				break;

			default:
				break;
			}
		}
	};

}
