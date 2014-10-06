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
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Color;
import android.os.AsyncTask;
/**
 * 計算をbackgroundで非同期に行い、時間が掛かりすぎる場合には、キャンセルが出来るようにする。<br>
 * そのために、{@link Calculator}を{@link AsyncTask}のサブクラスでラッピングする。<br>
 * {@link SryncTask#onCancelled()}は使わず、キャンセル操作後すぐに、
 * {@link ProgressDialog}を消し、キャンセルを行った旨のメッセージを表示する。<br>
 * {@link java.math.BigInteger}や{@link java.math.BigDecimal}の演算など、
 * こちらで作ったフラグの届かない場所で計算に時間が掛かっている場合、
 * キャンセルをしても、このスレッドはすぐには終わらない。<br>
 * そこで、すぐにキャンセルが実行された振りをすることで、ユーザーにストレスを与えない選択をした。<br>
 * 実際にはキャンセルされていないスレッドを大量に重ねることにより、負荷をかけることが可能かもしれないので、
 * 手動で良いので、負荷テストを実行しておくこと。
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
	private MainActivity mMain;
	/**
	 * タスクの進行中であることを表す{@link android.app.Dialog}。
	 */
	private ProgressDialog mDialog;
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
	 * @param aMain この非同期タスクを開始した、{@link MainActivity}のインスタンス
	 * @param aCalc バックグラウンドで計算を実行する{@link Calculator}
	 * @param aLatchList 非同期処理の終了を通知するための{@link CountDownLatch}のリスト
	 */
	public AsyncCalculatingTask(final MainActivity aMain,
								final Calculator aCalc,
								final List<CountDownLatch> aLatchList) {
		mMain = aMain;
		mCalc = aCalc;
		mLatchList = aLatchList;
	}
	
	@Override
	protected final void onPreExecute() {
	    mDialog = new ProgressDialog(mMain);
	    mDialog.setMessage(mMain.getText(R.string.calculation_running_label));
	    mDialog.setIndeterminateDrawable(mMain.getResources().getDrawable(R.drawable.progress_bar));
	    mDialog.setOnCancelListener(this);
	    mDialog.setButton(DialogInterface.BUTTON_NEGATIVE, mMain.getText(R.string.calculation_cancel_label),
	            new DialogInterface.OnClickListener() {
	                public void onClick(final DialogInterface aDialog, final int aWhich) {
	                	cancel();
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
			return mMain.getString(R.string.parse_error_message) + ": " + e.getMessage();
		} catch (CalculatingException e) {
			mErrorOccured = true;
			return mMain.getString(R.string.calculation_error_message) + ": " + e.getMessage();
		} catch (BackgroundProcessCancelledException e) {
			return null;
		}
	}

	@Override
	protected final void onPostExecute(final String aResult) {
		int tColor;
		if (mErrorOccured) {
			VariableFactory.getInstance().cancelSubstitution();
			tColor = Color.RED;
		} else {
			VariableFactory.getInstance().settleSubstitution();
			tColor = Color.GREEN;
		}
		mMain.output(
				mCommand + NEW_LINE
				+ "=> " + aResult + NEW_LINE
				+ "(" + getElapsedTime() + " " + mMain.getString(R.string.second) + ")" + NEW_LINE,
				tColor);
	    mMain.scrolldown();
	    // 登録したCountDownLatchに終了を通知
	    for (CountDownLatch tLatch: mLatchList) {
			tLatch.countDown();
		}
	    mDialog.dismiss();
	}
	
	@Override
	public final void onCancel(final DialogInterface aDialog) {
		cancel();
	}
	/**
	 * 
	 * @return 計算に掛かった時間（秒）
	 */
	private float getElapsedTime() {
		return (System.currentTimeMillis() - mStart) / MILLISECOND_TO_SECOND;
	}
	/**
	 * キャンセル処理を行う。
	 */
	private void cancel() {
		cancel(true);
		VariableFactory.getInstance().cancelSubstitution();
		mMain.output(
				mCommand + NEW_LINE
				+ "=> " + mMain.getString(R.string.calculation_canceled_message) + NEW_LINE
				+ "(" + getElapsedTime() + " " + mMain.getString(R.string.second) + ")" + NEW_LINE,
				Color.YELLOW);
		mMain.scrolldown();
		// 登録したCountDownLatchに終了を通知
	    for (CountDownLatch tLatch: mLatchList) {
			tLatch.countDown();
		}
	    mDialog.dismiss();
	}
}
