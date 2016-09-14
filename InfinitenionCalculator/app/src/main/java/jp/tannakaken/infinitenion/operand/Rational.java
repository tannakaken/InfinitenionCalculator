package jp.tannakaken.infinitenion.operand;

import java.math.BigInteger;

import jp.tannakaken.infinitenion.R;
import jp.tannakaken.infinitenion.calculator.CalculatingException;

/**
 * 有理数を表すクラス。<br>
 * {@link Constant}の成す二分木の葉を成している。<br>
 * 木構造をjavaで実装する定石により、Compositeパターンを使用している。
 * 
 * @author tannakaken
 *
 * @see <a href="http://en.wikipedia.org/wiki/Composite_pattern">http://en.wikipedia.org/wiki/Composite_pattern</a>
 */
class Rational extends Constant {

	/**
	 * 分子。省メモリ化のためにfinalにはしない。
	 */
	private final BigInteger mNumerator;
	/**
	 * 分母。省メモリ化のためにfinalにはしない。
	 */
	private final BigInteger mDenominator;
	/**
	 * 上限。
	 */
	private static final BigInteger MAX = new BigInteger(MAX_NUM);
	
	/**
	 * 与えられた分子と分母を約分して収納する。符号は分子につける。
	 * @param aNumerator 分子。
	 * @param aDenominator 分母。
	 * 
	 */
	Rational(final BigInteger aNumerator, final BigInteger aDenominator) throws CalculatingException {
		if (aNumerator.compareTo(MAX) > 0 || aDenominator.compareTo(MAX) > 0) {
			throw new CalculatingException(R.string.too_big);
		}
		if (aDenominator.equals(BigInteger.ZERO)) {
			throw new IllegalArgumentException("分母が0の有理数は作成できません。");
		}
		if (aNumerator.equals(BigInteger.ZERO)) {
			mNumerator = BigInteger.ZERO;
			mDenominator = BigInteger.ONE;
			return;
		}
		BigInteger tNum = aNumerator.abs();
		BigInteger tDen = aDenominator.abs();
		BigInteger tCommon = aNumerator.gcd(aDenominator);
		// tSigはこの分数の符号。
		BigInteger tSig = BigInteger.valueOf(aNumerator.signum() * aDenominator.signum());
		// 分子に符号をつけ、分母分子を約分する。 
		mNumerator = tSig.multiply(tNum).divide(tCommon);
		mDenominator = tDen.divide(tCommon);
	}
	/**
	 * 
	 * @param aInteger 有理数とみなされる整数。
	 */
	Rational(final BigInteger aInteger) throws CalculatingException {
		if (aInteger.compareTo(MAX) > 0) {
			throw new CalculatingException(R.string.too_big);
		}
		mNumerator = aInteger;
		mDenominator = BigInteger.ONE;
	}
	
	@Override
	public final Constant add(final Operand aOperand) throws CalculatingException {
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
	public final Constant mul(final Operand aOperand) throws CalculatingException {
		if (aOperand.isZero()) {
			return Zero.ZERO;
		} else {
			Rational tRational = (Rational) aOperand.getInterior();
			return new Rational(mNumerator.multiply(tRational.mNumerator),
	    						mDenominator.multiply(tRational.mDenominator));
		}
	}
	@Override
	public final Constant negate() throws  CalculatingException {
		return new Rational(mNumerator.negate(), mDenominator);
	}
	@Override
	public final Constant div(final Operand aOperand) throws CalculatingException {
		Rational tRational = (Rational) aOperand.getInterior();
		return new Rational(mNumerator.multiply(tRational.mDenominator),
    						mDenominator.multiply(tRational.mNumerator));
	}
	@Override
	public final Constant inv() throws CalculatingException {
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
