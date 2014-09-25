package jp.tannakaken.infinitenion.operand;


import java.util.ArrayDeque;
import java.util.Deque;

import android.content.Context;
import android.os.AsyncTask;
import jp.tannakaken.infinitenion.calculator.BackgroundProcessCancelledException;
import jp.tannakaken.infinitenion.calculator.CalculatingException;
import jp.tannakaken.infinitenion.calculator.CalculatorParseException;

/**
 * �g�[�N�����������āA������e�\�Ȃ�{@link Operand}�����H������{�����邽�߂́AFactory Method�p�^�[���B<br>
 * �v�Z�@�̌v�Z���[�h��{@link android.preference.PreferenceManager}����擾���邽�߂ɁA
 * �v���O�����̊J�n�����ɁA{@link Factory#setContext(Context)}�����邱�ƁB<br>
 * �����ӂ����ꍇ�̋����͈�ؕۏ؂��Ȃ��B
 * 
 * @author tannakaken
 * @see
 * <a href="http://en.wikipedia.org/wiki/Factory_method_pattern">
 * http://en.wikipedia.org/wiki/Factory_method_pattern</a><br/>
 *
 */
public abstract class Factory {
	/**
	 * ���݂̃��[�h���擾���邽�߂�{@link Context}�B
	 */
	private static Context mContext;
	/**
	 * Factory��product��{@link Context}�𒍓����Ȃ��Ă͂����Ȃ��Ȃ�����A
	 * �����̃N���X�ɃN���X���\�b�h�Ƃ���setter���`���āA�����ŌĂяo���B
	 * @param aContext ���݂�{@link Context}�B
	 */
	public static void setContext(final Context aContext) {
		mContext = aContext;
		Constant.setContext(aContext);
	}
	/**
	 * �v���O�����̊J�n�����ɁA{@link Factory#setContext(Context)}�����邱�ƁB<br>
	 * �����ӂ����ꍇ�̋����͈�ؕۏ؂��Ȃ��B
	 * @return ���݂̃��[�h���擾���邽�߂�{@link Context}�B
	 */
	static Context getContext() {
		return mContext;
	}
	/**
	 * �����{@link Operand}��ۊǂ��Ă������߂ɁA{@link Deque}���X�^�b�N�Ƃ��Ďg���B
	 */
	private static Deque<Operand> mStack = new ArrayDeque<Operand>();
	/**
	 *  �v���O�����̊J�n�����ɁA{@link Factory#setStack(Deque<Operand>)}�����邱�ƁB<br>
	 *  �����ӂ����ꍇ�̋����͈�ؕۏ؂��Ȃ��B
	 * @return {@link Operand}�𒙑����邽�߂̃X�^�b�N�B 
	 */
	static Deque<Operand> getStack() {
		return mStack;
	}
	/**
	 * �v�Z�����s���Ă���񓯊��^�X�N�B
	 */
	private static AsyncTask<String, Void, String> mTask;
	/**
	 * 
	 * @param aTask �v�Z�����s���Ă���񓯊��^�X�N�B
	 */
	public static final void setTask(final AsyncTask<String, Void, String> aTask) {
		mTask = aTask;
	}
	/**
	 * 
	 * @return �񓯊��^�X�N���L�����Z�������ƁA���̃��\�b�h�̕Ԃ茌��true�ɂȂ�B
	 */
	public static final boolean isCanceled() {
		return mTask.isCancelled();
	}
	/**
	 * �K��{@link Factory#getOperand}����O�ɁA���̃��\�b�h�Ō������邱�ƁB<br>
	 * �����A���ꂪ����Factory�ɂ���āA{@link Operand}�ɕϊ��ł���Ȃ�A
	 * ���̃��\�b�h�ɂ���āAFactory��������Ԃɓ���A
	 * {@link Factory#calc()}�ɂ���āA{@link Operand}����������A
	 * {@link Factory#mStack}�ɒǉ������B<br>
	 * ��������Ȃ������ꍇ�̋����͈�ؕۏ؂��Ȃ��B
	 * @param aToken ���������g�[�N���B
	 * @return ���̃g�[�N�������v�\���ǂ����B
	 * @throws CalculatorParseException ���݂̃��[�h�ł͎d���Ȃ��g�[�N�����g�p����邱�Ƃɂ���O�B
	 * @throws BackgroundProcessCancelledException �o�b�N�O���E���h�������L�����Z�����ꂽ�Ƃ��̗�O�B
	 */
	public abstract boolean getReady(String aToken)
			throws CalculatorParseException, BackgroundProcessCancelledException;
	
	/**
	 * �K�����̃��\�b�h���g���O�ɁA{@link Factory#getReady(String)}�Ō������邱�ƁB<br>
	 * �����A���ꂪ����Factory�ɂ���āA{@link Operand}�ɕϊ��ł���Ȃ�A
	 * ����ɂ��AFactory��������Ԃɓ���A
	 * ���̃��\�b�h�ɂ���āA�g�[�N�����\��{@link Operand}�A
	 * �������̓g�[�N���̕\���I�y���[�^��{@link Factory#mStack}������o����{@link Operand}�ɓK�p����{@link Operand}����������A
	 * {@link Factory#mStack}�ɒǉ������B<br>
	 * ��������Ȃ������ꍇ�̋����͈�ؕۏ؂��Ȃ��B
	 * @throws CalculatingException �I�y���[�^�̗v�����镶�@�̈ᔽ�ɂ���O�B
	 * @throws BackgroundProcessCancelledException �o�b�N�O���E���h�������L�����Z�����ꂽ�Ƃ��̗�O�B
	 */
	public abstract void calc() throws CalculatingException, BackgroundProcessCancelledException;
	
	/**
	 * 
	 * @return �X�^�b�N�Ɋi�[���ꂽ�v�Z���ʂ𕶎���ɂ���B
	 */
	public static String getResult() {
		StringBuilder tResult = new StringBuilder();
		for (Operand tOperand : mStack) {
			tResult.insert(0, ", " + tOperand);
		}
		return tResult.toString().replaceFirst(",", "").trim();
	}
	/**
	 * �X�^�b�N���N���A����B
	 */
	public static void clearStack() {
		mStack.clear();
	}
}
