package jp.tannakaken.infinitenion.operand;

import java.math.BigInteger;
import java.util.regex.Pattern;

import jp.tannakaken.infinitenion.calculator.BackgroundProcessCancelledException;

/**
 * �g�[�N�����璴���f���̋����P�ʂ𐶐�����Factory�B<br>
 * ������K�v�Ȃ��̂ŁASingleton�p�^�[�����g���B<br>
 * �K��{@link Factory#setContext(Context)}���ŁA{@link ImaginaryFactory#setContexttoConstant(Context)}�����邱�ƁB<br>
 * �����ӂ����ꍇ�̋����͕ۏ؂��Ȃ��B
 * 
 * @author tannakaken
 * @see 
 * <a href="http://en.wikipedia.org/wiki/Factory_method_pattern">
 * http://en.wikipedia.org/wiki/Factory_method_pattern</a><br>
 * <a href="http://en.wikipedia.org/wiki/Singleton_pattern">http://en.wikipedia.org/wiki/Singleton_pattern</a>
 */
public final class ImaginaryFactory extends Factory {
	/**
	 * �����ȊO��Constant������킷�L�����ǂ����𔻒肷�鐳�K�\���B
	 */
	private static String mRegex = "^E[1-9][0-9]*$";
	/**
	 * ���K�\�����R���p�C���B
	 */
	private static Pattern mPattern = Pattern.compile(mRegex);
	/**
	 * 1�𐶐����邽�߂�Factory�B
	 */
	private static BaseFieldFactory mRealFactory = BaseFieldFactory.getInstance();
	/**
	 * {@link ImaginaryFactroy}��<a href="http://en.wikipedia.org/wiki/Singleton_pattern">singleton</a>�B
	 * 
	 */
	private static ImaginaryFactory mSingleton = new ImaginaryFactory();
	/**
	 * {@link CayleyDickson}����邽�߂́A�󂯓���\�ȃg�[�N���B
	 */
	private String mToken;
	/**
	 * �O����C���X�^���X�������Ȃ��B
	 */
	private ImaginaryFactory() { }
	/**
	 * 
	 * @return {@link ImaginaryFactory}��<a href="http://en.wikipedia.org/wiki/Singleton_pattern">singleton</a>
	 * 
	 */
	public static ImaginaryFactory getInstance() {
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
	public void calc() throws BackgroundProcessCancelledException {
		getStack().push(buildConstant(number(mToken)));
	}
	/**
	 * 
	 * @param aToken {@link Constant}�Ɖ��߂����g�[�N���B
	 * @return ���̃g�[�N�����\���Ă��鋕���P�ʂ̔ԍ��B
	 */
	private BigInteger number(final String aToken) {
		return new BigInteger(aToken.substring(1));
	}
	/**
	 * �����P�ʂ̔ԍ�����A�؍\�����v�Z���A���̋����P�ʂ�\��{@link CayleyDickson}�̃C���X�^���X��Ԃ��B
	 * @param aNumber �����P�ʂ̔ԍ��B
	 * @return aNumber�Ԗڂ̋����P�ʁB
	 * @throws BackgroundProcessCancelledException �o�b�N�O���E���h�������L�����Z�����ꂽ�Ƃ��̗�O�B 
	 */
	private Constant buildConstant(final BigInteger aNumber) throws BackgroundProcessCancelledException {
		if (super.isCanceled()) {
			throw new BackgroundProcessCancelledException();
		}
		// �ړI�̎}�ɂ��ǂ蒅������A�W����1�����B
		if (aNumber.equals(BigInteger.ZERO)) {
			return mRealFactory.getOne();
		// �ړI�̎}�ɂ��ǂ蒅���܂ŁAaNumber���i�W�J���Ă����B
		} else {
			int tHeight = calculateHeight(aNumber);
			return new CayleyDickson(Zero.ZERO,
									 buildConstant(aNumber.subtract(Constant.TWO.pow(tHeight - 1))),
									 tHeight);
		}
	}
	/**
	 * 
	 * @param aNumber �����P�ʂ̔ԍ��B
	 * @return ���̋����P�ʂ�������2^n������n
	 */
	private int calculateHeight(final BigInteger aNumber) {
		int tHeight = 0;
		BigInteger tNum = BigInteger.ONE;
		while (tNum.compareTo(aNumber) <= 0) {
			tHeight++;
			tNum = tNum.multiply(Constant.TWO);
		}
		return tHeight;
	}
	/**
	 * {@link CayleyDickson}�̃C���X�^���X���ė��p���邱�ƂɂȂ����Ƃ��̂��߂ɁA���̃��\�b�h��p�ӂ��Ă���B
	 * @param aReal ������
	 * @param aImag ��������
	 * @param aHeight ����
	 * @return �\������A�œK�����ꂽ{@link Constant}
	 */
	Constant mixRealAndImaginary(final Constant aReal, final Constant aImag, final int aHeight) {
		return (new CayleyDickson(aReal, aImag, aHeight)).drop();
	}
}
