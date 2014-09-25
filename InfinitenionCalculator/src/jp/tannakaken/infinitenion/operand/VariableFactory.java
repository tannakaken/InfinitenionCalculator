package jp.tannakaken.infinitenion.operand;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

import jp.tannakaken.infinitenion.R;
import jp.tannakaken.infinitenion.calculator.BackgroundProcessCancelledException;
import android.content.Context;

/**
 * �g�[�N������{@link Variable}�𐶐����ēo�^����Factory�B<br>
 * ���̍ہA�g�[�N���̑啶���������͋�ʂ��Ȃ��B<br>
 * �g�[�N���̌��������Ă���C���X�^���X���\�����闬���Factory Method�p�^�[���𗘗p���č\���B<br>
 * �o�^�̂�������FlyWeight�p�^�[�����g���B<br>
 * ������C���X�^���X���K�v�Ȃ��̂ŁASingleton�p�^�[�����g���B
 * 
 * @author tannakaken
 *�@
 * @see 
 * <a href="http://en.wikipedia.org/wiki/Factory_method_pattern">http://en.wikipedia.org/wiki/Factory_method_pattern</a>
 * <a href="http://en.wikipedia.org/wiki/Flyweight_pattern">http://en.wikipedia.org/wiki/Flyweight_pattern</a>
 * <a href="http://en.wikipedia.org/wiki/Singleton_pattern">http://en.wikipedia.org/wiki/Singleton_pattern</a>
 *
 */
public final class VariableFactory extends Factory {
	/**
	 * ���̊��ł̉��s�B
	 */
	private static final String NEW_LINE = System.getProperty("line.separator"); 
	/**
	 * ������K�v�Ȃ��̂�<a href="http://en.wikipedia.org/wiki/Singleton_pattern">Singleton</a>�B
	 * 
	 */
	private static VariableFactory mSingleton = new VariableFactory();
	
	/**
	 * {@link Variable}���i�[���Ă����v�[���B
	 * {@link Variable}��\���g�[�N���uX��������Xn�in�͐��̐���)�v�̓Y�����ɂ�鏇���Ő��񂳂���B
	 */
	private static Map<String, Variable> mPool = new TreeMap<String, Variable>(new Comparator<String>() {
		@Override
		public int compare(final String aLeft, final String aRight) {
			int tLeftLength = aLeft.length();
			int tRightLength = aRight.length();
			if (tLeftLength > tRightLength) {
				return 1;
			} else if (tLeftLength < tRightLength) {
				return -1;
			} else {
				return aLeft.compareTo(aRight);
			}
		}
	});
	/**
	 * {@link Variable}�̃g�[�N���̈�ʌ`������킷���K�\���B
	 */
	private static String mRegex = "^X$|^X[1-9][0-9]*$";
	/**
	 * {@link Variable}���ϐ����ǂ����𔻒肷�鐳�K�\���̃p�^�[���B
	 */
	private static Pattern mPattern = Pattern.compile(mRegex);
	/**
	 * {@link Variable}����邽�߂́A�󂯓���\�ȃg�[�N���B
	 */
	private static String mToken;
	/**
	 * �O������C���X�^���X���ł��Ȃ��悤�ɂ��Ă����B
	 */
	private VariableFactory() { }
	
	/**
	 * 
	 * @return {@link VariableFactory}��<a href="http://en.wikipedia.org/wiki/Singleton_pattern">Singleton</a>�B
	 * 
	 */
	public static VariableFactory getInstance() {
		return mSingleton;
	}
	@Override
	public boolean getReady(final String aToken) throws BackgroundProcessCancelledException {
		if (super.isCanceled()) {
			throw new BackgroundProcessCancelledException();
		}
		if (mPattern.matcher(aToken).find()) {
			mToken = aToken;
			return true;
		}
		return false;
	}
	@Override
	public void calc() {
		Variable tVariable = mPool.get(mToken);
		if (tVariable == null) {
			tVariable = new Variable();
			mPool.put(mToken, tVariable);
		}
		getStack().push(tVariable);
	}
	
	/**
	 * ��������{@link Variable}��S�ăN���A����B
	 */
	public void clearVariable() {
		mPool.clear();
	}
	
	/**
	 * ��������ׂăL�����Z������B
	 */
	public void cancelSubstitution() {
		for (Variable tVariable : mPool.values()) {
			tVariable.cancel();
		}
	}
	/**
	 * ��������ׂĊm�肷��B
	 */
	public void settleSubstitution() {
		for (Variable tVariable : mPool.values()) {
			tVariable.settle();
		}
	}
	/**
	 * ��x�ȏ�g�p���邱�Ƃɂ��A���ݓo�^���ꂽ{@link Variable}��Y�����̏��Ԃňꗗ��\������B
	 * @param aContext �\�����郁�b�Z�[�W���擾���邽�߂�{@link Context}�BLocalization�̂��߂ɕK�v�B
	 * @return mPool�̒��g�𕶎���ɂ������́B
	 */
	public String variablesToString(final Context aContext) {
		StringBuilder tBuffer = new StringBuilder();
		tBuffer.append(mPool.size() + aContext.getString(R.string.the_number_of_variables) + NEW_LINE);
		for (Map.Entry<String, Variable> tEntry : mPool.entrySet()) {
			tBuffer.append(tEntry.getKey());
			tBuffer.append(" : ");
			tBuffer.append(tEntry.getValue());
			tBuffer.append(NEW_LINE);
		}
		return tBuffer.toString();
	}
}
