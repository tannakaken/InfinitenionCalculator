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
 * �v�Z��background�Ŕ񓯊��ɍs���A���Ԃ��|���肷����ꍇ�ɂ́A�L�����Z�����o����悤�ɂ���B<br>
 * ���̂��߂ɁA{@link Calc}��{@link AsyncTask}�̃T�u�N���X�Ń��b�s���O����B
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
	private Context mContext;
	/**
	 * �^�X�N�̐i�s���ł��邱�Ƃ�\��{@link android.app.Dialog}�B
	 */
	private ProgressDialog mDialog;
	/**
	 * ���ʂ̏o�͗��B
	 */
	private TextView mOutputText;
	/**
	 * �o�͗��̃X�N���[���B
	 */
	private ScrollView mScroll;
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
	 * @param aContext ���̔񓯊��^�X�N���J�n�����A{@link MainActivity}�̃C���X�^���X
	 * @param aOutputText �v�Z�̌��ʂ�\������{@link TextView}
	 * @param aScroll �v�Z���ʂ�\������aOutputText����ԉ��܂ňړ�������
	 * @param aCalc �o�b�N�O���E���h�Ōv�Z�����s����{@link Calculator}
	 * @param aLatchList �񓯊������̏I����ʒm���邽�߂�{@link CountDownLatch}�̃��X�g
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
	    // �o�^����CountDownLatch�ɏI����ʒm
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
		// �o�^����CountDownLatch�ɏI����ʒm
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
	 * @return �v�Z�Ɋ|���������ԁi�b�j
	 */
	private float getElapsedTime() {
		return (System.currentTimeMillis() - mStart) / MILLISECOND_TO_SECOND;
	}

	/**
     * �o�͗��̉�ʂ����܂ŃX�N���[��������B
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
