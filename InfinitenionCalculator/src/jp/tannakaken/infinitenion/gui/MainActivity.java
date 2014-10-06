package jp.tannakaken.infinitenion.gui;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CountDownLatch;

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
 * �d���ʁB
 * @author tannakaken
 *
 */
public class MainActivity extends ActionBarActivity implements OnClickListener {
	/**
	 * ���̓��͗��B
	 */
	private EditText mInputText;
	/**
	 * �v�Z�J�n�̃{�^���B
	 */
	private Button mCalcButton;
	/**
	 * �v�Z���ʂ̏o�͗��B
	 */
	private TextView mOutputText;
	/**
	 * �o�͗��̃X�N���[���B
	 */
	private ScrollView mScroll;
	
	/**
	 * �R�}���h�̗����B
	 */
	private LinkedList<String> mCommandHistory = new LinkedList<String>();
	/**
	 * �R�}���h�����̃C�e���[�^�B
	 */
	private ListIterator<String> mHistoryIterator;
	/**
	 * ���ݗ����������̂ڂ蒆���ǂ����B
	 */
	private boolean mIsGoingUp;
	/**
	 * �����A�F���A�S�Ă̓����B��������42�ɂ��킹�邽�߂ɁAlife��The���t���Ă���B
	 */
	private static final String ANSWER = "TheAnswerToTheLifeTheUniverseAndEverything";
	
	/**
	 * �v�Z����������C���X�^���X�B
	 */
	private Calculator mCalc;
	/**
	 * �񓯊������̏I����ʒm���邽�߂�{@link CountDownLatch}�̃��X�g�B<br>
	 * ���Ƃ��΃e�X�g�ȂǂɎg���B
	 */
	private List<CountDownLatch> mLatchList = new ArrayList<CountDownLatch>();
	/**
	 * �񓯊������̏I����ʒm���邽�߂�{@link CountDownLatch}�����X�g�ɉ�����B<br>
	 * �e�X�g�ȂǂɎg���B
	 * @param aLatch ��������{@link CountDownLatch}
	 */
	public final void addLatch(final CountDownLatch aLatch) {
		mLatchList.add(aLatch);
	}
	/**
	 * �񓯊������̏I����ʒm���邽�߂�{@link CountDownLatch}�����X�g���炳�쏜����B
	 * @param aLatch �폜����{@link CountDownLatch}
	 * @return aLatch�����X�g���ɂ����true�A�Ȃ����false��Ԃ��B
	 */
	public final boolean removeLatch(final CountDownLatch aLatch) {
		return mLatchList.remove(aLatch);
	}
	
	@Override
	protected final void onCreate(final Bundle aSavedInstanceState) {
		super.onCreate(aSavedInstanceState);
		setContentView(R.layout.activity_main);
		
		Factory.setContext(this);
		CalculatorException.setContext(this);
		
		mInputText = (EditText) findViewById(R.id.input_text);
		mCalcButton = (Button) findViewById(R.id.calculate_button);
		mOutputText = (TextView) findViewById(R.id.output_text);
		
		mScroll = (ScrollView) findViewById(R.id.scroll);
		
		mCalc = new Calculator();
		
		mCalcButton.setOnClickListener(this);
		// �\�t�g�L�[�{�[�h�͏o���Ȃ��B
		mInputText.setRawInputType(InputType.TYPE_NULL); 
		mInputText.setCursorVisible(true);
		//�@�����̏���
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
				if (mIsGoingUp) { // �����������̂ڂ蒆�̂Ƃ��͈���o��B
					mInputText.setText(mHistoryIterator.next());
					mInputText.setSelection(mInputText.length());
					mIsGoingUp = true;
					return;
				}
				// �������~��Ă����Ƃ��ɁA�܂�Ԃ��ēo��Ƃ��́A�����o���Γ�����̂ڂ�B
				mIsGoingUp = true;
				mHistoryIterator.next();
				if (mHistoryIterator.hasNext()) {
					mInputText.setText(mHistoryIterator.next());
					mInputText.setSelection(mInputText.length());
					return;
				}
			}
			// ��̒[�ł͐����A�F���A�S�Ă̓�����\���B���~���Ƃ��́A���̓����̓X�L�b�v����B
			mInputText.setText(ANSWER);
			mInputText.setSelection(mInputText.length());
			return;
		case R.id.keypad_down:
		case R.id.keypad_down_2:
			if (mHistoryIterator.hasPrevious()) {
				if (!mIsGoingUp) { // �������~��Ă���r���Ȃ�A����~���B
					mInputText.setText(mHistoryIterator.previous());
					mInputText.setSelection(mInputText.length());
					mIsGoingUp = false;
					return;
				}
				// �����������̂ڂ�r���Ő܂�Ԃ��Ƃ��́A�����~����Γ�~���B
				mHistoryIterator.previous();
				if (mHistoryIterator.hasPrevious()) {
					mInputText.setText(mHistoryIterator.previous());
					mInputText.setSelection(mInputText.length());
					mIsGoingUp = false;
					return;
				}
			}
			// ���̒[�ł͋󔒂�\���@���͓o�邵���Ȃ�
			mIsGoingUp = true;
			mInputText.setText("");
			return;
		case R.id.keypad_next:
			getSupportFragmentManager().beginTransaction()
			.remove(getSupportFragmentManager().getFragments().get(0))
			.add(R.id.container, new KeypadSecond())
			.commitAllowingStateLoss(); // �L�[�p�b�h�͏��������Ȃ��̂ŁAState��ۑ�����K�v�Ȃ��B
			return;
		case R.id.keypad_prev:
			getSupportFragmentManager().beginTransaction()
			.remove(getSupportFragmentManager().getFragments().get(0))
			.add(R.id.container, new KeypadFirst())
			.commitAllowingStateLoss(); // �L�[�p�b�h�͏��������Ȃ��̂ŁAState��ۑ�����K�v�Ȃ��B
			return;
		default:
			throw new IllegalStateException("Unknown view exists.");
		}
	}

	/**
	 * �R�}���h����͂���B<br>
	 * �L�����Z�����ꂽ�R�}���h�̏������I�����Ȃ��Ă��A���̃R�}���h�����s�ł���悤�ɁA
	 * {@link AsyncTask#THREAD_POOL_EXECUTOR}���w�肷��B
	 * @param aCommand �R�}���h�B 
	 */
	private void command(final String aCommand) {
		(new AsyncCalculatingTask(this, mCalc, mLatchList)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, aCommand);
	}
	/**
	 * ��ڂ̃L�[�p�b�h��\���N���X�B<br>
	 * android�̒�΂ł́A�����static�N���X�ɂ���B
	 * @author tannakaken
	 *
	 */
	public static class KeypadFirst extends Fragment {
		/**
		 * ����Fragment����������{@link MainActivity}�B
		 */
		private MainActivity mMain;
		
		/**
		 * �N���X�������ɂ͉������Ȃ��B
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
		 * �{�^����View���擾���AListener��set���Ă������\�b�h�B
		 * @param aRootView �e��View�B
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
	 * ��ڂ̃L�[�p�b�h��\���N���X�B<br>
	 * android�̒�΂ł́A�����static�N���X�ɂ���B
	 * @author tannakaken
	 *
	 */
	public static class KeypadSecond extends Fragment {
		/**
		 * ����Fragment����������{@link MainActivity}�B
		 */
		private MainActivity mMain;
		/**
		 * �������ɉ������Ȃ��B
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
		 * �{�^����View���擾���AListener��set���Ă������\�b�h�B
		 * @param aRootView �e��View�B
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
	 * @param aOutput �o�͂���镶����
	 * @param aColor �����̐F
	 */
	final void output(final String aOutput, final int aColor) {
		mOutputText.setTextColor(aColor);
		mOutputText.append(aOutput);
	}
    /**
     * �o�͗��̉�ʂ����܂ŃX�N���[��������B
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
