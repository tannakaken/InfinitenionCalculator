package jp.tannakaken.infinitenion.gui;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CountDownLatch;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import jp.tannakaken.infinitenion.calculator.Calculator;
import jp.tannakaken.infinitenion.calculator.CalculatorException;
import jp.tannakaken.infinitenion.operand.Factory;
import jp.tannakaken.infinitenion.operand.VariableFactory;
import jp.tannakaken.infinitenion.R;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
/**
 * 電卓画面。
 * @author tannakaken
 *
 */
public class MainActivity extends ActionBarActivity implements OnClickListener {
	/**
	 * インタースティシャル広告。
	 */
	private InterstitialAd mInterstitialAd;
	/**
	 * 式の入力欄。
	 */
	private EditText mInputText;
	/**
	 * 計算開始のボタン。
	 */
	private Button mCalcButton;
	/**
	 * 計算結果の出力欄。
	 */
	private TextView mOutputText;
	/**
	 * 出力欄のスクロール。
	 */
	private ScrollView mScroll;
	
	/**
	 * コマンドの履歴。
	 */
	private LinkedList<String> mCommandHistory = new LinkedList<String>();
	/**
	 * コマンド履歴のイテレータ。
	 */
	private ListIterator<String> mHistoryIterator;
	/**
	 * 現在履歴をさかのぼり中かどうか。
	 */
	private boolean mIsGoingUp;
	/**
	 * 生命、宇宙、全ての答え。文字数を42にあわせるために、lifeにTheが付いている。
	 */
	private static final String ANSWER = "TheAnswerToTheLifeTheUniverseAndEverything";
	
	/**
	 * 計算を処理するインスタンス。
	 */
	private Calculator mCalc;
	/**
	 * 非同期処理の終了を通知するための{@link CountDownLatch}のリスト。<br>
	 * たとえばテストなどに使う。
	 */
	private List<CountDownLatch> mLatchList = new ArrayList<CountDownLatch>();
	/**
	 * 非同期処理の終了を通知するための{@link CountDownLatch}をリストに加える。<br>
	 * テストなどに使う。
	 * @param aLatch 加えられる{@link CountDownLatch}
	 */
	public final void addLatch(final CountDownLatch aLatch) {
		mLatchList.add(aLatch);
	}
	/**
	 * 非同期処理の終了を通知するための{@link CountDownLatch}をリストからさ駆除する。
	 * @param aLatch 削除する{@link CountDownLatch}
	 * @return aLatchがリスト内にあればtrue、なければfalseを返す。
	 */
	public final boolean removeLatch(final CountDownLatch aLatch) {
		return mLatchList.remove(aLatch);
	}
	
	
	/**
	 * インタースティシャル広告を表示する。
	 */
	private void showInterstitial() {
		// Show the ad if it's ready. Otherwise toast and restart the game.
		if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
			mInterstitialAd.show();
		}
	}
	
	@Override
	protected final void onCreate(final Bundle aSavedInstanceState) {
		super.onCreate(aSavedInstanceState);
		setContentView(R.layout.activity_main);
		
		 // Create the interstitial.
	    mInterstitialAd = new InterstitialAd(this);
	    mInterstitialAd.setAdUnitId(getString(R.string.ad_unit_id));

	    // Create ad request.
	    AdRequest adRequest = new AdRequest.Builder()
	    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
	    .build();

	    // Begin loading your interstitial.
	    mInterstitialAd.loadAd(adRequest);
	    
		Factory.setContext(this);
		CalculatorException.setContext(this);
		
		mInputText = (EditText) findViewById(R.id.input_text);
		mCalcButton = (Button) findViewById(R.id.calculate_button);
		mOutputText = (TextView) findViewById(R.id.output_text);
		
		mScroll = (ScrollView) findViewById(R.id.scroll);
		
		mCalc = new Calculator();
		
		mCalcButton.setOnClickListener(this);
		// ソフトキーボードは出さない。
		mInputText.setRawInputType(InputType.TYPE_NULL); 
		mInputText.setCursorVisible(true);
		//　履歴の準備
		mCommandHistory.addFirst(ANSWER);
		mHistoryIterator = mCommandHistory.listIterator();
		mIsGoingUp = true;

		if (aSavedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new KeypadFirst()).commit();
		}
	}

	@Override
	public final boolean onCreateOptionsMenu(final Menu aMenu) {
		getMenuInflater().inflate(R.menu.main, aMenu);
		return true;
	}

	@Override
	public final boolean onOptionsItemSelected(final MenuItem aItem) {
		showInterstitial();
		Intent i;
		switch (aItem.getItemId()) {
		case R.id.action_settings:
			startActivity(new Intent(this, Prefs.class));
			return true;
		case R.id.action_show_variables:
			output(VariableFactory.getInstance().variablesToString(this), Color.GREEN);
			scrolldown();
			return true;
		case R.id.action_about:
			i = new Intent(this, About.class);
			startActivity(i);
			return true;
		case R.id.action_help:
			Text.setResource(TextResource.HELP);
			i = new Intent(this, Text.class);
			startActivity(i);
			return true;
		case R.id.action_problem:
			Text.setResource(TextResource.PROBLEM);
			i = new Intent(this, Text.class);
			startActivity(i);
			return true;
		case R.id.action_story:
			Web.setResource(WebResource.STORY);
			i = new Intent(this, Web.class);
			startActivity(i);
			return true;
		case R.id.action_finish:
			android.os.Process.killProcess(android.os.Process.myPid());
		default:
			return super.onOptionsItemSelected(aItem);
		}
	}

	@Override
	public final void onClick(final View aView) {
		switch (aView.getId()) {
		case R.id.calculate_button:
			String tFormula = mInputText.getText().toString();
			command(tFormula);
			mInputText.setText("");
			if (!tFormula.isEmpty() && !mCommandHistory.getFirst().equals(tFormula)) {
				mCommandHistory.addFirst(tFormula);
			}
			mHistoryIterator = mCommandHistory.listIterator();
			mIsGoingUp = true;
			return;
		case R.id.keypad_div:
		case R.id.keypad_mul:
		case R.id.keypad_sub:
		case R.id.keypad_add:
		case R.id.keypad_pow:
		case R.id.keypad_1:
		case R.id.keypad_2:
		case R.id.keypad_3:
		case R.id.keypad_X:
		case R.id.keypad_substitution:
		case R.id.keypad_4:
		case R.id.keypad_5:
		case R.id.keypad_6:
		case R.id.keypad_E:
		case R.id.keypad_sp:
		case R.id.keypad_7:
		case R.id.keypad_8:
		case R.id.keypad_9:
		case R.id.keypad_0:
		case R.id.keypad_dot:
		case R.id.keypad_negate:
		case R.id.keypad_inv:
		case R.id.keypad_conj:
		case R.id.keypad_norm:
		case R.id.keypad_commu:
		case R.id.keypad_assoc:
		case R.id.keypad_normed:
		case R.id.keypad_sp_2:
			mInputText.append(((Button) aView).getText());
			return;
		case R.id.keypad_C:
			mInputText.setText("");
			return;
		case R.id.keypad_bs:
		case R.id.keypad_bs_2:
			int tStart = mInputText.getSelectionStart();
			if (tStart > 0) {
				mInputText.getText().delete(tStart - 1, tStart);
			}
			return;
		case R.id.keypad_up:
		case R.id.keypad_up_2:
			if (mHistoryIterator.hasNext()) {
				if (mIsGoingUp) { // 履歴をさかのぼり中のときは一つずつ登る。
					mInputText.setText(mHistoryIterator.next());
					mInputText.setSelection(mInputText.length());
					mIsGoingUp = true;
					return;
				}
				// 履歴を降りていくときに、折り返して登るときは、もし登れれば二つさかのぼる。
				mIsGoingUp = true;
				mHistoryIterator.next();
				if (mHistoryIterator.hasNext()) {
					mInputText.setText(mHistoryIterator.next());
					mInputText.setSelection(mInputText.length());
					return;
				}
			}
			// 上の端では生命、宇宙、全ての答えを表示。次降りるときは、その答えはスキップする。
			mInputText.setText(ANSWER);
			mInputText.setSelection(mInputText.length());
			return;
		case R.id.keypad_down:
		case R.id.keypad_down_2:
			if (mHistoryIterator.hasPrevious()) {
				if (!mIsGoingUp) { // 履歴を降りている途中なら、一つずつ降りる。
					mInputText.setText(mHistoryIterator.previous());
					mInputText.setSelection(mInputText.length());
					mIsGoingUp = false;
					return;
				}
				// 履歴をさかのぼる途中で折り返すときは、もし降りれれば二つ降りる。
				mHistoryIterator.previous();
				if (mHistoryIterator.hasPrevious()) {
					mInputText.setText(mHistoryIterator.previous());
					mInputText.setSelection(mInputText.length());
					mIsGoingUp = false;
					return;
				}
			}
			// 下の端では空白を表示　次は登るしかない
			mIsGoingUp = true;
			mInputText.setText("");
			return;
		case R.id.keypad_next:
			getSupportFragmentManager().beginTransaction()
			.remove(getSupportFragmentManager().getFragments().get(0))
			.add(R.id.container, new KeypadSecond())
			.commitAllowingStateLoss(); // キーパッドは情報を持たないので、Stateを保存する必要なし。
			return;
		case R.id.keypad_prev:
			getSupportFragmentManager().beginTransaction()
			.remove(getSupportFragmentManager().getFragments().get(0))
			.add(R.id.container, new KeypadFirst())
			.commitAllowingStateLoss(); // キーパッドは情報を持たないので、Stateを保存する必要なし。
			return;
		default:
			throw new IllegalStateException("Unknown view exists.");
		}
	}

	/**
	 * コマンドを入力する。<br>
	 * キャンセルされたコマンドの処理が終了しなくても、次のコマンドが実行できるように、
	 * {@link AsyncTask#THREAD_POOL_EXECUTOR}を指定する。
	 * @param aCommand コマンド。 
	 */
	private void command(final String aCommand) {
		(new AsyncCalculatingTask(this, mCalc, mLatchList)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, aCommand);
	}
	/**
	 * 一つ目のキーパッドを表すクラス。<br>
	 * androidの定石では、これはstaticクラスにする。
	 * @author tannakaken
	 *
	 */
	public static class KeypadFirst extends Fragment {
		/**
		 * このFragmentを所持する{@link MainActivity}。
		 */
		private MainActivity mMain;
		
		/**
		 * クラス生成時には何もしない。
		 */
		public KeypadFirst() {
		}
		@Override
		public final void onAttach(final Activity aActivity) {
			super.onAttach(aActivity);
			if (aActivity instanceof MainActivity) {
				mMain = (MainActivity) aActivity;
			} else {
				throw new IllegalStateException("This Fragment must attach MainActivity.");
			}
		}
		
		@Override
		public final View onCreateView(final LayoutInflater aInflater, final ViewGroup aContainer,
				final Bundle aSavedInstanceState) {
			View rootView = aInflater.inflate(R.layout.keypad, aContainer,
					false);
			setListeners(rootView);
			mMain.scrolldown();
			return rootView;
		}
		
		/**
		 * ボタンのViewを取得し、Listenerをsetしていくメソッド。
		 * @param aRootView 親のView。
		 */
		private void setListeners(final View aRootView) {
			aRootView.findViewById(R.id.keypad_div).setOnClickListener(mMain);
			aRootView.findViewById(R.id.keypad_mul).setOnClickListener(mMain);
			aRootView.findViewById(R.id.keypad_sub).setOnClickListener(mMain);
			aRootView.findViewById(R.id.keypad_add).setOnClickListener(mMain);
			aRootView.findViewById(R.id.keypad_pow).setOnClickListener(mMain);
			aRootView.findViewById(R.id.keypad_1).setOnClickListener(mMain);
			aRootView.findViewById(R.id.keypad_2).setOnClickListener(mMain);
			aRootView.findViewById(R.id.keypad_3).setOnClickListener(mMain);
			aRootView.findViewById(R.id.keypad_X).setOnClickListener(mMain);
			aRootView.findViewById(R.id.keypad_substitution).setOnClickListener(mMain);
			aRootView.findViewById(R.id.keypad_4).setOnClickListener(mMain);
			aRootView.findViewById(R.id.keypad_5).setOnClickListener(mMain);
			aRootView.findViewById(R.id.keypad_6).setOnClickListener(mMain);
			aRootView.findViewById(R.id.keypad_E).setOnClickListener(mMain);
			aRootView.findViewById(R.id.keypad_sp).setOnClickListener(mMain);
			aRootView.findViewById(R.id.keypad_7).setOnClickListener(mMain);
			aRootView.findViewById(R.id.keypad_8).setOnClickListener(mMain);
			aRootView.findViewById(R.id.keypad_9).setOnClickListener(mMain);
			aRootView.findViewById(R.id.keypad_0).setOnClickListener(mMain);
			aRootView.findViewById(R.id.keypad_dot).setOnClickListener(mMain);
			aRootView.findViewById(R.id.keypad_C).setOnClickListener(mMain);
			aRootView.findViewById(R.id.keypad_bs).setOnClickListener(mMain);
			aRootView.findViewById(R.id.keypad_up).setOnClickListener(mMain);
			aRootView.findViewById(R.id.keypad_down).setOnClickListener(mMain);
			aRootView.findViewById(R.id.keypad_next).setOnClickListener(mMain);
		}
	}
	/**
	 * 二つ目のキーパッドを表すクラス。<br>
	 * androidの定石では、これはstaticクラスにする。
	 * @author tannakaken
	 *
	 */
	public static class KeypadSecond extends Fragment {
		/**
		 * このFragmentを所持する{@link MainActivity}。
		 */
		private MainActivity mMain;
		/**
		 * 生成時に何もしない。
		 */
		public KeypadSecond() {
		}
		@Override
		public final void onAttach(final Activity aActivity) {
			super.onAttach(aActivity);
			if (aActivity instanceof MainActivity) {
				mMain = (MainActivity) aActivity;
			} else {
				throw new IllegalStateException("This Fragment must attach MainActivity.");
			}
		}
		@Override
		public final View onCreateView(final LayoutInflater aInflater, final ViewGroup aContainer,
				final Bundle aSavedInstanceState) {
			View rootView = aInflater.inflate(R.layout.keypad_2, aContainer,
					false);
			setListeners(rootView);
			return rootView;
		}
		
		/**
		 * ボタンのViewを取得し、Listenerをsetしていくメソッド。
		 * @param aRootView 親のView。
		 */
		private void setListeners(final View aRootView) {
			aRootView.findViewById(R.id.keypad_negate).setOnClickListener(mMain);
			aRootView.findViewById(R.id.keypad_inv).setOnClickListener(mMain);
			aRootView.findViewById(R.id.keypad_conj).setOnClickListener(mMain);
			aRootView.findViewById(R.id.keypad_norm).setOnClickListener(mMain);
			aRootView.findViewById(R.id.keypad_commu).setOnClickListener(mMain);
			aRootView.findViewById(R.id.keypad_assoc).setOnClickListener(mMain);
			aRootView.findViewById(R.id.keypad_normed).setOnClickListener(mMain);
			aRootView.findViewById(R.id.keypad_sp_2).setOnClickListener(mMain);
			aRootView.findViewById(R.id.keypad_bs_2).setOnClickListener(mMain);
			aRootView.findViewById(R.id.keypad_up_2).setOnClickListener(mMain);
			aRootView.findViewById(R.id.keypad_down_2).setOnClickListener(mMain);
			aRootView.findViewById(R.id.keypad_prev).setOnClickListener(mMain);
			
		}
	}
	/**
	 * 
	 * @param aOutput 出力される文字列
	 * @param aColor 文字の色
	 */
	final void output(final String aOutput, final int aColor) {
		mOutputText.setTextColor(aColor);
		mOutputText.append(aOutput);
	}
    /**
     * 出力欄の画面を下までスクロールさせる。
     */
    final void scrolldown() {
    	mScroll.post(new Runnable() {
		    @Override
		    public void run() {
		        mScroll.fullScroll(ScrollView.FOCUS_DOWN);
		    }
		});
    }
}
