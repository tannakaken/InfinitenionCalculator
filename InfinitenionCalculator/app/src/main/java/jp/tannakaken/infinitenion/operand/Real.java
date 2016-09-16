package jp.tannakaken.infinitenion.operand;

import java.math.BigDecimal;
import java.math.BigInteger;

import jp.tannakaken.infinitenion.R;
import jp.tannakaken.infinitenion.calculator.CalculatingException;
import jp.tannakaken.infinitenion.gui.Prefs;

/**
 * 実数をラッピングして、{@link Constant}型に適合させるAdapterパターン。<br>
 * また、Compositeパターンを用いて構成される木構造を持つ{@link Constant}クラスの葉の役割を持っている。
 * 
 * @author tannakaken
 *
 * @see
 * <a href="http://en.wikipedia.org/wiki/Adapter_pattern">http://en.wikipedia.org/wiki/Adapter_pattern</a><br>
 * <a href="http://en.wikipedia.org/wiki/Composite_pattern">http://en.wikipedia.org/wiki/Composite_pattern</a>
 */
class Real extends Constant {

	/**
	 * このインスタンスが表す数。
	 */
	private final BigDecimal mNumber;

	/**
	 * 
	 * @param aNumber ラッピングされる実数型。
	 */
	Real(final BigDecimal aNumber) throws CalculatingException {
		if (isOverLimit(aNumber)) {
			throw new CalculatingException(R.string.too_big);
		}
		mNumber = aNumber.setScale(getCalculationScale(), BigDecimal.ROUND_DOWN);
	}
	/**
	 * スケールを新しく設定して、インスタンスを作る。
	 * @param aNumber ラッピングされる実数型
	 * @param aScale 新しく設定されるスケール。
	 */
	Real(final BigDecimal aNumber, final int aScale) throws CalculatingException {
		if (isOverLimit(aNumber)) {
			throw new CalculatingException(R.string.too_big);
		}
		mNumber = aNumber.setScale(aScale, BigDecimal.ROUND_DOWN);
	}
	@Override
	public final Constant getReal() {
		throw new UnsupportedOperationException("getRealは高さ１以上のConstantに実装されます。");
	}
	@Override
	public final Constant getImag() {
		throw new UnsupportedOperationException("getImagは高さ１以上のConstantに実装されます。");
	}

	@Override
	public final Constant add(final Operand aOperand) throws CalculatingException {
		Real tReal = (Real) aOperand.getInterior();
		return (new Real(mNumber.add(tReal.mNumber).setScale(getCalculationScale(), BigDecimal.ROUND_DOWN))).drop();
	}
	@Override
	public final Constant mul(final Operand aOperand)throws CalculatingException {
		Real tReal = (Real) aOperand.getInterior();
		return new Real(mNumber.multiply(tReal.mNumber).setScale(getCalculationScale(), BigDecimal.ROUND_DOWN));
	}
	@Override
	public final Constant negate() throws CalculatingException {
		return new Real(mNumber.negate());
	}
	@Override
	public final Constant div(final Operand aOperand) throws  CalculatingException {
		Real tReal = (Real) aOperand.getInterior();
		return new Real(mNumber.divide(tReal.mNumber, getCalculationScale(), BigDecimal.ROUND_DOWN));
	}
	@Override
	public final Constant inv() throws CalculatingException {
		return new Real(BigDecimal.ONE.divide(mNumber, getCalculationScale(), BigDecimal.ROUND_DOWN));
	}

	@Override
	public final boolean isZero() {
		return mNumber.compareTo(BigDecimal.ZERO) == 0;
	}
	@Override
	final boolean isOne() {
		return mNumber.compareTo(BigDecimal.ONE) == 0;
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
	 * {@link Real#isInteger()}の内部での手続き。<br>
	 * {@link Real}ではなく、{@link BigDecimal}に直接適用したいときに使う。
	 * 
	 * @param aDecimal 整数かどうか判定される{@link BigDecimal}
	 * @return aDecimalが整数かどうか。
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
			return mNumber.setScale(0,BigDecimal.ROUND_DOWN).toString();
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
		return mNumber.equals(tOtherReal.mNumber);
	}
	@Override
	public final int hashCode() {
		return mNumber.hashCode();
	}
	
	/**
	 * 
	 * @return 計算時の実数精度。出力時よりも少し多めにとる。
	 */
	private static int getCalculationScale() {
		return Prefs.getScale(getContext()) + 1;
	}
	/**
	 * 
	 * @return 出力時の実数精度
	 */
	private static int getOutputScale() {
		return Prefs.getScale(getContext());
	}
}
