package jp.tannakaken.infinitenion.operand;

import java.math.BigInteger;

import jp.tannakaken.infinitenion.R;
import jp.tannakaken.infinitenion.calculator.CalculatingException;
/**
 * 0��\���N���X�B<br>
 * �ȃ��������̂��߂ɁA0�͑S�āA���̃N���X�̗B��̃C���X�^���X�ɂ���ĕ\���B<br>
 * ���R�ASingleton�p�^�[�����g���B
 * @author tannakaken
 *�@
 *�@@see 
 * <a href="http://en.wikipedia.org/wiki/Singleton_pattern">http://en.wikipedia.org/wiki/Singleton_pattern</a>
 */
final class Zero extends Constant {
	/**
	 * �O������̓C���X�^���X�������Ȃ��B
	 */
	private Zero() {	
	}
	
	/**
	 * �B���0�B
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
		throw new UnsupportedOperationException("getImag�͍����P�ȏ��Constant�Ɏ�������܂��B");
	}

	@Override
	public Constant getImag() {
		throw new UnsupportedOperationException("getImag�͍����P�ȏ��Constant�Ɏ�������܂��B");
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
