package jp.tannakaken.infinitenion.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import jp.tannakaken.infinitenion.calculator.BackgroundProcessCancelledException;
import jp.tannakaken.infinitenion.calculator.CalculatingException;
import jp.tannakaken.infinitenion.calculator.Calculator;
import jp.tannakaken.infinitenion.calculator.CalculatorParseException;
import jp.tannakaken.infinitenion.operand.VariableFactory;
import jp.tannakaken.infinitenion.R;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.ScrollView;
import android.widget.TextView;
/**
 * 計算をbackgroundで非同期に行い、時間が掛かりすぎる場合には、キャンセルが出来るようにする。<br>
 * そのために、{@link Calc}を{@link AsyncTask}のサブクラスでラッピングする。
 * 
 * @author tannakaken
 *
 */
class AsyncCalculatingTask extends AsyncTask<String, Void, String> 
								  implements OnCancelListener {
	/**
	 * 計算を処理するインスタンス。
	 */
	private Calculator mCalc;
	/**
	 * この非同期タスクを開始した、{@link MainActivity}のインスタンスを格納する。
	 */
	private Context mContext;
	/**
	 * タスクの進行中であることを表す{@link android.app.Dialog}。
	 */
	private ProgressDialog mDialog;
	/**
	 * 結果の出力欄。
	 */
	private TextView mOutputText;
	/**
	 * 出力欄のスクロール。
	 */
	private ScrollView mScroll;
	/**
	 * 入力されたコマンド。
	 */
	private String mCommand;
	/**
	 * エラーが起きたかどうか。
	 */
	private boolean mErrorOccured = false;
	/**
	 * 非同期処理の終了を通知するための{@link CountDownLatch}のリスト。<br>
	 * たとえばテストなどに使う。
	 */
	private List<CountDownLatch> mLatchList = new ArrayList<CountDownLatch>();
	/**
	 * 計算の開始時間。
	 */
	private long mStart;
	/**
	 * ミリ秒と秒の比率。
	 */
	private static final float MILLISECOND_TO_SECOND = 1000f;
	/**
	 * この環境での改行。
	 */
	private static final String NEW_LINE = System.getProperty("line.separator"); 
	/**
	 * @param aContext この非同期タスクを開始した、{@link MainActivity}のインスタンス
	 * @param aOutputText 計算の結果を表示する{@link TextView}
	 * @param aScroll 計算結果を表示するaOutputTextを一番下まで移動させる
	 * @param aCalc バックグラウンドで計算を実行する{@link Calculator}
	 * @param aLatchList 非同期処理の終了を通知するための{@link CountDownLatch}のリスト
	 */
	public AsyncCalculatingTask(final Context aContext, final TextView aOutputText, final ScrollView aScroll,
								final Calculator aCalc, final List<CountDownLatch> aLatchList) {
		mContext = aContext;
		mOutputText = aOutputText;
		mScroll = aScroll;
		mCalc = aCalc;
		mLatchList = aLatchList;
	}
	
	@Override
	protected final void onPreExecute() {
	    mDialog = new ProgressDialog(mContext);
	    mDialog.setMessage(mContext.getText(R.string.calculation_running_label));
	    mDialog.setIndeterminateDrawable(mContext.getResources().getDrawable(R.drawable.progress_bar));
	    mDialog.setOnCancelListener(this);
	    mDialog.setButton(DialogInterface.BUTTON_NEGATIVE, mContext.getText(R.string.calculation_cancel_label),
	            new DialogInterface.OnClickListener() {
	                public void onClick(final DialogInterface aDialog, final int aWhich) {
	                	cancel(true);
	                }
	            });
	    mDialog.show();
	  }
	
	@Override
	protected final String doInBackground(final String... aParams) {
		mCommand = aParams[0];
		mStart = System.currentTimeMillis();
		try {
			return mCalc.calc(mCommand, this);
		} catch (CalculatorParseException e) {
			mErrorOccured = true;
			return mContext.getString(R.string.parse_error_message) + ": " + e.getMessage();
		} catch (CalculatingException e) {
			mErrorOccured = true;
			return mContext.getString(R.string.calculation_error_message) + ": " + e.getMessage();
		} catch (BackgroundProcessCancelledException e) {
			return null;
		}
	}

	@Override
	protected final void onPostExecute(final String aResult) {
		if (mErrorOccured) {
			VariableFactory.getInstance().cancelSubstitution();
			mOutputText.setTextColor(Color.RED);
		} else {
			VariableFactory.getInstance().settleSubstitution();
			mOutputText.setTextColor(Color.GREEN);
		}
		mOutputText.append(mCommand + NEW_LINE + "=> ");
		mOutputText.append(aResult + NEW_LINE
				+ "(" + getElapsedTime() + " " + mContext.getString(R.string.second) + ")" + NEW_LINE);
	    scrolldown();
	    // 登録したCountDownLatchに終了を通知
	    for (CountDownLatch tLatch: mLatchList) {
			tLatch.countDown();
		}
	    mDialog.dismiss();
	}
	@Override
	protected final void onCancelled() {
		VariableFactory.getInstance().cancelSubstitution();
		mOutputText.setTextColor(Color.YELLOW);
		mOutputText.append(mCommand + NEW_LINE + "=> ");
		mOutputText.append(mContext.getString(R.string.calculation_canceled_message) + NEW_LINE
				+ "(" + getElapsedTime() + " " + mContext.getString(R.string.second) + ")" + NEW_LINE);
		scrolldown();
		// 登録したCountDownLatchに終了を通知
	    for (CountDownLatch tLatch: mLatchList) {
			tLatch.countDown();
		}
	    mDialog.dismiss();
	}
	
	
	@Override
	public final void onCancel(final DialogInterface aDialog) {
		cancel(true);
	}
	/**
	 * 
	 * @return 計算に掛かった時間（秒）
	 */
	private float getElapsedTime() {
		return (System.currentTimeMillis() - mStart) / MILLISECOND_TO_SECOND;
	}

	/**
     * 出力欄の画面を下までスクロールさせる。
     */
    private void scrolldown() {
    	mScroll.post(new Runnable() {
		    @Override
		    public void run() {
		        mScroll.fullScroll(ScrollView.FOCUS_DOWN);
		    }
		});
    }
	
}
