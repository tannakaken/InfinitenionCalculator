package jp.tannakaken.infinitenion.calculator;

import android.content.Context;

/**
 * �v�Z�̓r���Ŕ��������O�̐e�N���X�B
 * @author tannakaken
 *
 */
public abstract class CalculatorException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * �o�͂���G���[���b�Z�[�W���擾���邽�߂́A{@link Context}�B
	 */
	private static Context mContext;
	/**
	 * �K��{@link jp.tannakaken.infinitenion.gui.MainActivity}�̋N�����ɂ��̃��\�b�h�ŏ��������邱�ƁB�B
	 * @param aContext �o�͂���G���[���b�Z�[�W���擾���邽�߂́A{@link Context}�B
	 */
	public static void setContext(final Context aContext) {
		mContext = aContext;
	}
	/**
	 * 
	 * @param aMessageId �\������G���[���b�Z�[�W���擾���邽�߂̃��\�[�XID�B
	 */
	public CalculatorException(final int aMessageId) {
		super(mContext.getString(aMessageId));
	}
	/**
	 * 
	 * @param aMessageId �\������G���[���b�Z�[�W���擾���邽�߂̃��\�[�XID�B
	 * @param aCause �G���[�̌����ƂȂ����g�[�N���B
	 */
	public CalculatorException(final int aMessageId, final Object aCause) {
		super(mContext.getString(aMessageId) + ":" + aCause);
	}
	
}
