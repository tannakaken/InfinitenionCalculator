package jp.tannakaken.infinitenion.operand;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.regex.Pattern;

import jp.tannakaken.infinitenion.R;
import jp.tannakaken.infinitenion.calculator.BackgroundProcessCancelledException;
import jp.tannakaken.infinitenion.calculator.CalculatorParseException;
import jp.tannakaken.infinitenion.gui.Prefs;
/**
 * �g�[�N�����琔�𐶐�����Factory�B<br/>
 * �K���g�[�N�������v�\���������Ă���A�C���X�^���X�𐶐�����H������{�����邽�߂ɁAFactory Method�p�^�[�����g�p�B<br/>
 * ������K�v�Ȃ��̂ŁASingleton�p�^�[�����g���B
 * @author tannakaken
 * @see
 * <a href="http://en.wikipedia.org/wiki/Factory_method_pattern">
 * http://en.wikipedia.org/wiki/Factory_method_pattern</a><br/>
 * <a href="http://en.wikipedia.org/wiki/Singleton_pattern">http://en.wikipedia.org/wiki/Singleton_pattern</a>
 *
 */
public final class BaseFieldFactory extends Factory {

	/**
	 * ������\�����K�\���B
	 */
	private static String mIntegerRegexp = "^0$|^-?[1-9]\\d*$";
	/**
	 * ������\���p�^�[���B
	 */
	private static Pattern mIntegerPattern = Pattern.compile(mIntegerRegexp);
	/**
	 * ������\�����K�\���B
	 */
	private static String mRealRegexp = "^(0|(-?[1-9]\\d*))(\\.\\d*)?$";
	/**
	 * ������\���p�^�[���B
	 */
	private static Pattern mRealPattern = Pattern.compile(mRealRegexp);
	/**
	 * {@link BaseFieldFactory}��<a href="http://en.wikipedia.org/wiki/Singleton_pattern">singleton</a>�B
	 */
	private static BaseFieldFactory mSingleton = new BaseFieldFactory();
	/**
	 * {@link Rational}�܂���{@link Real}����邽�߂́A�󂯓���\�ȃg�[�N���B
	 */
	private String mToken;
	/**
	 * �������[�h�̂Ƃ��A�^����ꂽ�g�[�N�����������ǂ����𔻒�B
	 */
	private boolean mIsInteger;
	/**
	 * 
	 * @return {@link BaseFieldFactory}��<a href="http://en.wikipedia.org/wiki/Singleton_pattern">singleton</a>
	 */
	public static BaseFieldFactory getInstance() {
		return mSingleton;
	}
	
	@Override
	public boolean getReady(final String aToken)
			throws CalculatorParseException, BackgroundProcessCancelledException {
		if (super.isCanceled()) {
			throw new BackgroundProcessCancelledException();
		}
		if (aToken.equals("TheAnswerToTheLifeTheUniverseAndEverything")) {
			mToken = String.valueOf(aToken.length());
			mIsInteger = true;
			return true;
		}
		// �����̏��Ԃɒ��Ӂ@���R�����ǂ������`�F�b�N���Ă���A�������ǂ������`�F�b�N����B
		if (isReal()) {
			if (mIntegerPattern.matcher(aToken).find()) {
				mIsInteger = true;
				mToken = aToken;
				return true;
			}
			if (mRealPattern.matcher(aToken).find()) {
				mIsInteger = false;
				mToken = aToken;
				return true;
			}
			return false;
		} else {
			if (mIntegerPattern.matcher(aToken).find()) {
				mToken = aToken;
				return true;
			}
			if (mRealPattern.matcher(aToken).find()) {
				throw new CalculatorParseException(R.string.radix_point_in_rational_mode, aToken);
			}
			return false;
		}
	}

	@Override
	public void calc() {
		if (isReal()) {
			if (mIsInteger) {
				getStack().push(new Real(new BigDecimal(mToken)));
			} else {
				getStack().push(new Real(new BigDecimal(mToken), Prefs.getScale(getContext())));
			}
		} else {
			getStack().push(new Rational(new BigInteger(mToken)));
		}
	}
	
	/**
	 * 
	 * @return ���݂̃��[�h��1�B
	 */
	Constant getOne() {
		if (isReal()) {
			return new Real(BigDecimal.ONE);
		} else {
			return new Rational(BigInteger.ONE);
			
		}
	}
	/**
	 * 
	 * @return ���ݗL�������[�h���ǂ����B
	 */
	private static boolean isReal() {
		return Prefs.getIsReal(getContext());
	}
	
	
	
}
