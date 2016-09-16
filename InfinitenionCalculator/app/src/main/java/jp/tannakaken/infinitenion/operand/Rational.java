package jp.tannakaken.infinitenion.operand;

import java.math.BigDecimal;
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
	private final BigDecimal mNumerator;
	/**
	 * 分母。省メモリ化のためにfinalにはしない。
	 */
	private final BigDecimal mDenominator;

	static private BigDecimal gcd(BigDecimal a,BigDecimal b) {
		if (b.equals(BigDecimal.ZERO)) {
			return a;
		} else {
			return gcd(b, a.remainder(b));
		}
	}

	/**
	 * 与えられた分子と分母を約分して収納する。符号は分子につける。
	 * @param aNumerator 分子。
	 * @param aDenominator 分母。
	 * 
	 */
	Rational(final BigDecimal aNumerator, final BigDecimal aDenominator) throws CalculatingException {
		if (isOverLimitWithoutScale(aNumerator) || isOverLimitWithoutScale(aDenominator)) {
			throw new CalculatingException(R.string.too_big);
		}
		if (aDenominator.equals(BigDecimal.ZERO)) {
			throw new IllegalArgumentException("分母が0の有理数は作成できません。");
		}
		if (aNumerator.equals(BigDecimal.ZERO)) {
			mNumerator = BigDecimal.ZERO;
			mDenominator = BigDecimal.ONE;
			return;
		}
		BigDecimal tNum = aNumerator.abs();
		BigDecimal tDen = aDenominator.abs();
		BigDecimal tCommon = gcd(aNumerator,aDenominator);
		// tSigはこの分数の符号。
		BigDecimal tSig = BigDecimal.valueOf(aNumerator.signum() * aDenominator.signum());
		// 分子に符号をつけ、分母分子を約分する。 
		mNumerator = tSig.multiply(tNum).divide(tCommon,0,BigDecimal.ROUND_DOWN);
		mDenominator = tDen.divide(tCommon,0,BigDecimal.ROUND_DOWN);
	}
	/**
	 * 
	 * @param aInteger 有理数とみなされる整数。
	 */
	Rational(final BigDecimal aInteger) throws CalculatingException {
		if (isOverLimitWithoutScale(aInteger)) {
			throw new CalculatingException(R.string.too_big);
		}
		mNumerator = aInteger;
		mDenominator = BigDecimal.ONE;
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
		return mNumerator.compareTo(BigDecimal.ZERO) == 0;
	}

	@Override
	final boolean isOne() {
		return mNumerator.multiply(mDenominator).compareTo(BigDecimal.ONE) == 0;
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
		return this.mNumerator.equals(tOtherRational.mNumerator)
					&& this.mDenominator.equals(tOtherRational.mDenominator);
	}
	@Override
	public final int hashCode() {
		return mNumerator.add(mDenominator).add(mNumerator.multiply(mDenominator)).hashCode();
	}
	@Override
	public final String toString() {
		if (mNumerator.equals(BigDecimal.ZERO)) {
			return "0";
		}
		if (mDenominator.equals(BigDecimal.ONE)) {
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
		return mDenominator.equals(BigDecimal.ONE);
	}
	@Override
	public final BigInteger getInteger() {
		return mNumerator.toBigInteger();
	}
	
}
