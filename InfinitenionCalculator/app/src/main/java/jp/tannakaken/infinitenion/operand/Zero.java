package jp.tannakaken.infinitenion.operand;

import java.math.BigInteger;

import jp.tannakaken.infinitenion.R;
import jp.tannakaken.infinitenion.calculator.CalculatingException;
/**
 * 0を表すクラス。<br>
 * 省メモリ化のために、0は全て、このクラスの唯一のインスタンスによって表す。<br>
 * 当然、Singletonパターンを使う。
 * @author tannakaken
 *　
 *　@see 
 * <a href="http://en.wikipedia.org/wiki/Singleton_pattern">http://en.wikipedia.org/wiki/Singleton_pattern</a>
 */
final class Zero extends Constant {
	/**
	 * 外部からはインスタンス化させない。
	 */
	private Zero() {	
	}
	
	/**
	 * 唯一の0。
	 */
	public static final Zero ZERO = new Zero();
	
	@Override
	public boolean isInteger() {
		return true;
	}

	@Override
	public BigInteger getInteger() {
		return BigInteger.ZERO;
	}

	@Override
	public Constant getReal() {
		throw new UnsupportedOperationException("getImagは高さ１以上のConstantに実装されます。");
	}

	@Override
	public Constant getImag() {
		throw new UnsupportedOperationException("getImagは高さ１以上のConstantに実装されます。");
	}
	
	@Override
	public Constant add(final Operand aOperand) {
		return aOperand.getInterior();
	}

	@Override
	public Constant mul(final Operand aOperand) {
		return this;
	}

	@Override
	public Constant negate() {
		return this;
	}
	@Override
	public Constant div(final Operand aOperand) {
		return this;
	}
	@Override
	public Constant inv() throws CalculatingException {
		throw new CalculatingException(R.string.divide_by_zero);
	}

	@Override
	public boolean isZero() {
		return true;
	}

	@Override
	boolean isOne() {
		return false;
	}

	@Override
	public int getHeight() {
		return 0;
	}

	@Override
	Constant drop() {
		return this;
	}
	@Override
	public String toString() {
		return "0";
	}

}
