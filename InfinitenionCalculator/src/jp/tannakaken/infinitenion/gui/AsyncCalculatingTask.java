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
 * �v�Z��background�Ŕ񓯊��ɍs���A���Ԃ��|���肷����ꍇ�ɂ́A�L�����Z�����o����悤�ɂ���B<br>
 * ���̂��߂ɁA{@link Calculator}��{@link AsyncTask}�̃T�u�N���X�Ń��b�s���O����B<br>
 * {@link SryncTask#onCancelled()}�͎g�킸�A�L�����Z������シ���ɁA
 * {@link ProgressDialog}�������A�L�����Z�����s�����|�̃��b�Z�[�W��\������B<br>
 * {@link java.math.BigInteger}��{@link java.math.BigDecimal}�̉��Z�ȂǁA
 * ������ō�����t���O�̓͂��Ȃ��ꏊ�Ōv�Z�Ɏ��Ԃ��|�����Ă���ꍇ�A
 * �L�����Z�������Ă��A���̃X���b�h�͂����ɂ͏I���Ȃ��B<br>
 * �����ŁA�����ɃL�����Z�������s���ꂽ�U������邱�ƂŁA���[�U�[�ɃX�g���X��^���Ȃ��I���������B<br>
 * ���ۂɂ̓L�����Z������Ă��Ȃ��X���b�h���ʂɏd�˂邱�Ƃɂ��A���ׂ������邱�Ƃ��\��������Ȃ��̂ŁA
 * �蓮�ŗǂ��̂ŁA���׃e�X�g�����s���Ă������ƁB
 * 
 * @author tannakaken
 *
 */
class AsyncCalculatingTask extends AsyncTask<String, Void, String> 
								  implements OnCancelListener {
	/**
	 * �v�Z����������C���X�^���X�B
	 */
	private Calculator mCalc;
	/**
	 * ���̔񓯊��^�X�N���J�n�����A{@link MainActivity}�̃C���X�^���X���i�[����B
	 */
	private MainActivity mMain;
	/**
	 * �^�X�N�̐i�s���ł��邱�Ƃ�\��{@link android.app.Dialog}�B
	 */
	private ProgressDialog mDialog;
	/**
	 * ���͂��ꂽ�R�}���h�B
	 */
	private String mCommand;
	/**
	 * �G���[���N�������ǂ����B
	 */
	private boolean mErrorOccured = false;
	/**
	 * �񓯊������̏I����ʒm���邽�߂�{@link CountDownLatch}�̃��X�g�B<br>
	 * ���Ƃ��΃e�X�g�ȂǂɎg���B
	 */
	private List<CountDownLatch> mLatchList = new ArrayList<CountDownLatch>();
	/**
	 * �v�Z�̊J�n���ԁB
	 */
	private long mStart;
	/**
	 * �~���b�ƕb�̔䗦�B
	 */
	private static final float MILLISECOND_TO_SECOND = 1000f;
	/**
	 * ���̊��ł̉��s�B
	 */
	private static final String NEW_LINE = System.getProperty("line.separator"); 
	/**
	 * @param aMain ���̔񓯊��^�X�N���J�n�����A{@link MainActivity}�̃C���X�^���X
	 * @param aCalc �o�b�N�O���E���h�Ōv�Z�����s����{@link Calculator}
	 * @param aLatchList �񓯊������̏I����ʒm���邽�߂�{@link CountDownLatch}�̃��X�g
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
	    // �o�^����CountDownLatch�ɏI����ʒm
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
	 * @return �v�Z�Ɋ|���������ԁi�b�j
	 */
	private float getElapsedTime() {
		return (System.currentTimeMillis() - mStart) / MILLISECOND_TO_SECOND;
	}
	/**
	 * �L�����Z���������s���B
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
		// �o�^����CountDownLatch�ɏI����ʒm
	    for (CountDownLatch tLatch: mLatchList) {
			tLatch.countDown();
		}
	    mDialog.dismiss();
	}
}
