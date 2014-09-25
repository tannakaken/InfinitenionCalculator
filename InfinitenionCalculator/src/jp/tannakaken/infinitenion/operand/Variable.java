package jp.tannakaken.infinitenion.operand;

import java.math.BigInteger;

import jp.tannakaken.infinitenion.calculator.CalculatingException;

/**
 * {@link Operand}を収納する変数。<br>
 * このクラスのインスタンスを収納しないように注意。無限ループが起こる可能性がある。<br>
 * メンバーとして{@link Operand}のインスタンス{@link Variable#mInterior}を一つ持ち、
 *　substitutionすなわち代入以外の演算子に関しては、その{@link Variable#mInterior}に委譲する。<br>
 * 他の{@link Operand}と同一視しながら、いろいろな値をとりうる機能を、委譲とDecoratorパターンを使って実現。
 * @author tannakaken。
 *
 * @see
 * <a href="http://en.wikipedia.org/wiki/Decorator_pattern">http://en.wikipedia.org/wiki/Decorator_pattern</a><br>
 * <a href="http://en.wikipedia.org/wiki/Delegation_(programming)">
 * http://en.wikipedia.org/wiki/Delegation_(programming)</a>
 *
 */
class Variable implements Operand {
	
	/**
	 * 変数の中身。初期値は0。
	 */
	private Constant mInterior = Zero.ZERO;
	/**
	 * 計算がキャンセルされた時のために、以前の状態を保存しておく。
	 */
	private Constant mFormerInterior;
	
	@Override
	public void setInterior(final Constant aInterior) {
		if (mFormerInterior == null) {
			mFormerInterior = mInterior;
		}
		mInterior = aInterior;
	}
	@Override
	public Constant getInterior() {
		return mInterior;
	}
	/**
	 * 代入をキャンセルする。
	 */
	public void cancel() {
		if (mFormerInterior != null) {
			mInterior = mFormerInterior;
			mFormerInterior = null;
		}
	}
	/**
	 * 代入を確定する。
	 */
	public void settle() {
		mFormerInterior = null;
	}
	@Override
	public Constant getReal() {
		return mInterior.getReal();
	}

	@Override
	public Constant getImag() {
		return mInterior.getImag();
	}
	
	@Override
	public int getHeight() {
		return mInterior.getHeight();
	}
	
	@Override
	public Constant add(final Operand aOperand) {
		return mInterior.add(aOperand);
	}
	
	@Override
	public Constant mul(final Operand aOperand) {
		return mInterior.mul(aOperand);
	}
	
	@Override
	public Constant negate() {
		return mInterior.negate();
	}
	@Override
	public Constant div(final Operand aOperand) {
		return mInterior.div(aOperand);
	}
	@Override
	public Constant inv() throws CalculatingException {
		return mInterior.inv();
	}
	
	@Override
	public boolean isZero() {
		return mInterior.isZero();
	}
	
	@Override
	public boolean isInteger() {
		return mInterior.isInteger();
	}

	@Override
	public BigInteger getInteger() {
		return mInterior.getInteger();
	}
	/**
	 * @return {@link Variable#mInterior}の文字列表現
	 */
	@Override
	public String toString() {
		return mInterior.toString();
	}
}
