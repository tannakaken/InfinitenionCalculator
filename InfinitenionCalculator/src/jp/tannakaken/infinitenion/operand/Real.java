package jp.tannakaken.infinitenion.operand;

import java.math.BigDecimal;
import java.math.BigInteger;

import jp.tannakaken.infinitenion.gui.Prefs;

/**
 * ���������b�s���O���āA{@link Constant}�^�ɓK��������Adapter�p�^�[���B<br>
 * �܂��AComposite�p�^�[����p���č\�������؍\��������{@link Constant}�N���X�̗t�̖����������Ă���B
 * 
 * @author tannakaken
 *
 * @see
 * <a href="http://en.wikipedia.org/wiki/Adapter_pattern">http://en.wikipedia.org/wiki/Adapter_pattern</a><br>
 * <a href="http://en.wikipedia.org/wiki/Composite_pattern">http://en.wikipedia.org/wiki/Composite_pattern</a>
 */
class Real extends Constant {

	/**
	 * ���̃C���X�^���X���\�����B
	 */
	private final BigDecimal mNumber;
	/**
	 * 
	 * @param aNumber ���b�s���O���������^�B
	 */
	Real(final BigDecimal aNumber) {
		mNumber = aNumber.setScale(getCalculationScale(), BigDecimal.ROUND_DOWN);
	}
	/**
	 * �X�P�[����V�����ݒ肵�āA�C���X�^���X�����B
	 * @param aNumber ���b�s���O���������^
	 * @param aScale �V�����ݒ肳���X�P�[���B
	 */
	Real(final BigDecimal aNumber, final int aScale) {
		mNumber = aNumber.setScale(aScale, BigDecimal.ROUND_DOWN);
	}
	@Override
	public final Constant getReal() {
		throw new UnsupportedOperationException("getReal�͍����P�ȏ��Constant�Ɏ�������܂��B");
	}
	@Override
	public final Constant getImag() {
		throw new UnsupportedOperationException("getImag�͍����P�ȏ��Constant�Ɏ�������܂��B");
	}

	@Override
	public final Constant add(final Operand aOperand) {
		Real tReal = (Real) aOperand.getInterior();
		return (new Real(mNumber.add(tReal.mNumber).setScale(getCalculationScale(), BigDecimal.ROUND_DOWN))).drop();
	}
	@Override
	public final Constant mul(final Operand aOperand) {
		Real tReal = (Real) aOperand.getInterior();
		return new Real(mNumber.multiply(tReal.mNumber).setScale(getCalculationScale(), BigDecimal.ROUND_DOWN));
	}
	@Override
	public final Constant negate() {
		return new Real(mNumber.negate());
	}
	@Override
	public final Constant div(final Operand aOperand) {
		Real tReal = (Real) aOperand.getInterior();
		return new Real(mNumber.divide(tReal.mNumber, getCalculationScale(), BigDecimal.ROUND_DOWN));
	}
	@Override
	public final Constant inv() {
		return new Real(BigDecimal.ONE.divide(mNumber, getCalculationScale(), BigDecimal.ROUND_DOWN));
	}

	@Override
	public final boolean isZero() {
		return mNumber.equals(BigDecimal.ZERO.setScale(getCalculationScale()));
	}
	@Override
	final boolean isOne() {
		return mNumber.equals(BigDecimal.ONE.setScale(getCalculationScale()));
	}

	@Override
	public final int getHeight() {
		return 0;
	}
	@Override
	final Constant drop() {
		if (isZero()) {
			return Zero.ZERO;
		}
		return this;
	}
	@Override
	public final boolean isInteger() {
		return isInteger(mNumber); 
	}
	/**
	 * {@link Real#isInteger()}�̓����ł̎葱���B<br>
	 * {@link Real}�ł͂Ȃ��A{@link BigDecimal}�ɒ��ړK�p�������Ƃ��Ɏg���B
	 * 
	 * @param aDecimal �������ǂ������肳���{@link BigDecimal}
	 * @return aDecimal���������ǂ����B
	 */
	private static boolean isInteger(final BigDecimal aDecimal) {
		return aDecimal.compareTo(aDecimal.setScale(0, BigDecimal.ROUND_DOWN)) == 0; 
	}
	
	@Override
	public final BigInteger getInteger() {
		return mNumber.toBigInteger();
	}

	@Override
	public final String toString() {
		if (isInteger(mNumber)) {
			return mNumber.setScale(0).toString();
		} else {
			return mNumber.setScale(getOutputScale(), BigDecimal.ROUND_DOWN).toString();
		}
	}
	@Override
	public final boolean equals(final Object aOther) {
		if (this == aOther) {
			return true;
		}
		if (!(aOther instanceof Real)) {
			return false;
		}
		Real tOtherReal = (Real) aOther;
		return mNumber == tOtherReal.mNumber;
	}
	@Override
	public final int hashCode() {
		return mNumber.hashCode();
	}
	
	/**
	 * 
	 * @return �v�Z���̎������x�B�o�͎������������߂ɂƂ�B
	 */
	private static int getCalculationScale() {
		return Prefs.getScale(getContext()) + 1;
	}
	/**
	 * 
	 * @return �o�͎��̎������x
	 */
	private static int getOutputScale() {
		return Prefs.getScale(getContext());
	}
}
