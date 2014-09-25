package jp.tannakaken.infinitenion.operand;

import java.math.BigInteger;

/**
 * �L������\���N���X�B<br>
 * {@link Constant}�̐����񕪖؂̗t�𐬂��Ă���B<br>
 * �؍\����java�Ŏ��������΂ɂ��AComposite�p�^�[�����g�p���Ă���B
 * 
 * @author tannakaken
 *
 * @see <a href="http://en.wikipedia.org/wiki/Composite_pattern">http://en.wikipedia.org/wiki/Composite_pattern</a>
 */
class Rational extends Constant {

	/**
	 * ���q�B�ȃ��������̂��߂�final�ɂ͂��Ȃ��B
	 */
	private final BigInteger mNumerator;
	/**
	 * ����B�ȃ��������̂��߂�final�ɂ͂��Ȃ��B
	 */
	private final BigInteger mDenominator;
	
	/**
	 * �^����ꂽ���q�ƕ����񕪂��Ď��[����B�����͕��q�ɂ���B
	 * @param aNumerator ���q�B
	 * @param aDenominator ����B
	 * 
	 */
	Rational(final BigInteger aNumerator, final BigInteger aDenominator) {
		if (aDenominator.equals(BigInteger.ZERO)) {
			throw new IllegalArgumentException("���ꂪ0�̗L�����͍쐬�ł��܂���B");
		}
		if (aNumerator.equals(BigInteger.ZERO)) {
			mNumerator = BigInteger.ZERO;
			mDenominator = BigInteger.ONE;
			return;
		}
		BigInteger tNum = aNumerator.abs();
		BigInteger tDen = aDenominator.abs();
		BigInteger tCommon = aNumerator.gcd(aDenominator);
		// tSig�͂��̕����̕����B
		BigInteger tSig = BigInteger.valueOf(aNumerator.signum() * aDenominator.signum());
		// ���q�ɕ��������A���ꕪ�q��񕪂���B 
		mNumerator = tSig.multiply(tNum).divide(tCommon);
		mDenominator = tDen.divide(tCommon);
	}
	/**
	 * 
	 * @param aInteger �L�����Ƃ݂Ȃ���鐮���B
	 */
	Rational(final BigInteger aInteger) {
		mNumerator = aInteger;
		mDenominator = BigInteger.ONE;
	}
	
	@Override
	public final Constant add(final Operand aOperand) {
		if (aOperand.isZero()) {
			return this;
		} else {
			Rational tRational = (Rational) aOperand.getInterior();
			return (new Rational(mNumerator.multiply(tRational.mDenominator)
								.add(tRational.mNumerator.multiply(mDenominator)),
								mDenominator.multiply(tRational.mDenominator))).drop();
		}
	}

	@Override
	public final Constant mul(final Operand aOperand) {
		if (aOperand.isZero()) {
			return Zero.ZERO;
		} else {
			Rational tRational = (Rational) aOperand.getInterior();
			return new Rational(mNumerator.multiply(tRational.mNumerator),
	    						mDenominator.multiply(tRational.mDenominator));
		}
	}
	@Override
	public final Constant negate() {
		return new Rational(mNumerator.negate(), mDenominator);
	}
	@Override
	public final Constant div(final Operand aOperand) {
		Rational tRational = (Rational) aOperand.getInterior();
		return new Rational(mNumerator.multiply(tRational.mDenominator),
    						mDenominator.multiply(tRational.mNumerator));
	}
	@Override
	public final Constant inv() {
		return new Rational(mDenominator, mNumerator);
	}
	@Override
	public final int getHeight() {
		return 0;
	}
	@Override
	public final boolean isZero() {
		return mNumerator.equals(BigInteger.ZERO);
	}

	@Override
	final boolean isOne() {
		return mNumerator.multiply(mDenominator).equals(BigInteger.ONE);
	}
	
	@Override
	public final boolean equals(final Object aOther) {
		if (this == aOther) {
			return true;
		}
		if (!(aOther instanceof Rational)) {
			return false;
		}
		Rational tOtherRational = (Rational) aOther;
		if (this.mNumerator.equals(tOtherRational.mNumerator)
				&& this.mDenominator.equals(tOtherRational.mDenominator)) {
			return true;
		}
		return false;
	}
	@Override
	public final int hashCode() {
		return mNumerator.add(mDenominator).add(mNumerator.multiply(mDenominator)).hashCode();
	}
	@Override
	public final String toString() {
		if (mNumerator.equals(BigInteger.ZERO)) {
			return "0";
		}
		if (mDenominator.equals(BigInteger.ONE)) {
			return "" + mNumerator;
		}
		return mNumerator + " " + mDenominator + " /";
	}
	
	
	@Override
	final Constant drop() {
		if (isZero()) {
			return Zero.ZERO;
		}
		return this;
	}

	@Override
	public final Constant getReal() {
		return this;
	}
	@Override
	public final Constant getImag() {
		return Zero.ZERO;
	}
	@Override
	public final boolean isInteger() {
		return mDenominator.equals(BigInteger.ONE);
	}
	@Override
	public final BigInteger getInteger() {
		return mNumerator;
	}
	
}
