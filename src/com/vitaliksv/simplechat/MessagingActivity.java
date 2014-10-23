package com.vitaliksv.simplechat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import com.vitaliksv.simplechat.database.Message;
import com.vitaliksv.simplechat.database.MyDBAdapter;

import android.app.ListActivity;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;


public class MessagingActivity extends ListActivity implements OnClickListener, OnLoadCompleteListener{
	
	final int MAX_STREAMS = 5;

	SoundPool sp;
	int soundIdMessage;

	final String LOG_TAG = "myLogs";

	private static final int DEFAUTL_MESSAGE_DELAY = 5000;
	private static final int MESSAGE_MAX_DELAY_TIME = 35000;
	
	private final String[] comments = new String[] { "Cool",
			"Very nice",
			"Hate it",
			"Привет!",
			"Как у тебя дела?",
			"Что делаешь?",
			"Я сегодня занят. Давай встретимся в пятницу. Ок?",
			"Смотри, какой у меня есть тег #java. Нравится?",
			"Хочешь, я расскажу тебе новый анекдот?",
			"Лови новый тег #stackoverflow",
			"Сегодня отличная погода, не правда ли?",
			"Новые теги #peace и #world",
			"Как тебе сегодняшние новости?",
			"Я собираюсь прийти #сегодня к тебе в гости. Ты не против?",
			"Видел сегодня Аню. Она передавала тебе привет )))",
			"Когда ты планируешь отправиться в отпуск?",
			"Как твоё путешествие в Европу? Понравилось?",
			"Так...",
			"Вчера рассказали новый анекдот, но я его забыл...",
			"Скоро будет проходить конференция, посвящённая мобильной разработке.",
			"Ну и погодка сегодня",
			"Вот это новость!!! )))",
			"Передавай привет своей сестре! )))",
			"Сколько будет 2 + 2?",
			"Какие цветы любит твоя сестра?",
			"Когда твой день рождения?",
			"#music",
			"Я собираюсь завтра сходить в кино. Пойдёшь со мной?",
			"Я уже давно небыл на рыбалке #fish !!!",
			"Какой сейчас курс доллара?",
			"Да!",
			"Я не согласен! )))",
			"Ты на верном пути )))",
			"Какая у тебя марка телефона?",
			"А ты как думашь?",
			"#looksery отличный проект!",
			"Я ничего тебе не скажу",
			"Ну ты и отличился вчера! Молодец! )))",
			"я занят. напишу позже.",
			"Через месяц будет концерт #scorpions . Ты идёшь?",
			"Какая чудесная новость!",
			"А я собираюсь купить себе новый ноутбук!",
			"Я наконец-то купил себе ноутбук! )))",
			"Хочу купить себе какую-нибудь книгу почитать. Может что-то посоветуешь?",
			"Завтра обещают 24 градуса тепла )",
			"Поехали сегодня на пляж?"
			};
	
	private boolean listIsTouched = false;

	Button btnSend;
	ListView lvData;
	EditText etmyMessage;

	private CustomCursorAdapter customAdapter;
	MyDBAdapter mydatbase;
	Cursor cursor;
	
	private Handler mHandler = new Handler();
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		sp = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
		sp.setOnLoadCompleteListener(this);
		soundIdMessage = sp.load(this, R.raw.message, 1);
		
		setContentView(R.layout.messaging_screen);
		btnSend = (Button) findViewById(R.id.btnSend);
		btnSend.setOnClickListener(this);
		lvData = (ListView) findViewById(android.R.id.list);
		etmyMessage = (EditText) findViewById(R.id.text);

		mydatbase = new MyDBAdapter(this);
		mydatbase.open();
		
		customAdapter = new CustomCursorAdapter(MessagingActivity.this, mydatbase.getAllEntries());
		lvData.setAdapter(customAdapter);		
		getListView().setSelection(mydatbase.getNumberOfMessages()-1);
		
		mHandler.removeCallbacks(IncomingMessager);
		mHandler.postDelayed(IncomingMessager, DEFAUTL_MESSAGE_DELAY + new Random().nextInt(MESSAGE_MAX_DELAY_TIME));
		
		lvData.setOnScrollListener(new OnScrollListener() {
		      public void onScrollStateChanged(AbsListView view, int scrollState) {
		    	  if((scrollState == 1)|(scrollState == 2)){
		    		  listIsTouched = true;
		    	  }  
		      }

			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

			}
		});
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.messaging, menu);
		return true;
	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_clear_history) {
			listIsTouched = false;
			mydatbase.clearHistory();
			customAdapter = new CustomCursorAdapter(MessagingActivity.this,	mydatbase.getAllEntries());
			lvData.setAdapter(customAdapter);
			customAdapter.notifyDataSetChanged();
			getListView().setSelection(mydatbase.getNumberOfMessages() - 1);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	
	private String getDateTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		Date date = new Date();
		return dateFormat.format(date);
	}

	
	public Message createMessage(String text, int user) {

		Message mes = new Message(text, getDateTime(), user);
		return mes;
	}

	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnSend:
			// TODO Call second activity
			String myMessageText = etmyMessage.getText().toString().trim();
			if (myMessageText.length() > 0) {
				etmyMessage.setText("");
				listIsTouched = false;
				addNewMessage(myMessageText, 1);
				
			}
			break;
		default:
			break;
		}
	}

	
	@Override
	protected void onResume() {
		Log.d(LOG_TAG, "onResume");
		mydatbase.open();
		super.onResume();
		mHandler.postDelayed(IncomingMessager, DEFAUTL_MESSAGE_DELAY + new Random().nextInt(MESSAGE_MAX_DELAY_TIME));
	}

	
	@Override
	protected void onPause() {
		Log.d(LOG_TAG, "onPause");
		mydatbase.close();
		mHandler.removeCallbacks(IncomingMessager);
		super.onPause();
	}

	
	public void addNewMessage(String m, int user) {

		int index = lvData.getFirstVisiblePosition();
		View v = lvData.getChildAt(0);
		int top = (v == null) ? 0 : v.getTop();

		mydatbase.addMessageToDB(createMessage(m, user));
		customAdapter = new CustomCursorAdapter(MessagingActivity.this,	mydatbase.getAllEntries());
		lvData.setAdapter(customAdapter);
		customAdapter.notifyDataSetChanged();
		
		 sp.play(soundIdMessage, 1, 1, 0, 0, 1);
		
		if (listIsTouched == false) {
			getListView().setSelection(mydatbase.getNumberOfMessages() - 1);

		} else {

			lvData.setSelectionFromTop(index, top);
		}

	}

	
	private Runnable IncomingMessager = new Runnable() {
		public void run() {

			addNewMessage(getRandomMessage(), 0);
			mHandler.postDelayed(this, DEFAUTL_MESSAGE_DELAY + new Random().nextInt(MESSAGE_MAX_DELAY_TIME));
		}
	};

	
	public String getRandomMessage() {

		int nextInt = new Random().nextInt(comments.length);
		return comments[nextInt];
	}


	@Override
	public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
		// TODO Auto-generated method stub
		
	}
}
